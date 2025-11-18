package com.example.lionsforest.domain.comment.service;

import com.example.lionsforest.domain.comment.Comment;
import com.example.lionsforest.domain.comment.dto.request.CommentRequestDto;
import com.example.lionsforest.domain.comment.dto.response.CommentLikeResponseDTO;
import com.example.lionsforest.domain.comment.dto.response.CommentResponseDto;
import com.example.lionsforest.domain.comment.repository.CommentRepository;
import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.GroupPhoto;
import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.group.repository.GroupPhotoRepository;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.group.repository.ParticipationRepository;
import com.example.lionsforest.domain.notification.Notification;
import com.example.lionsforest.domain.notification.TargetType;
import com.example.lionsforest.domain.notification.repository.NotificationRepository;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.exception.BusinessException;
import com.example.lionsforest.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final NotificationRepository notificationRepository;
    private final GroupPhotoRepository groupPhotoRepository;

    // 댓글 생성
    @Transactional
    public CommentResponseDto createComment(Long groupId, CommentRequestDto dto, Long userId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GROUP_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Comment comment = Comment.builder()
                .group(group)
                .content(dto.getContent())
                .user(user)
                .build();

        Comment saved = commentRepository.save(comment);

        // 알림 생성: 모임의 모든 참여자에게 새 댓글 알림
        String dateStr = group.getMeetingAt().format(DateTimeFormatter.ofPattern("yy.MM.dd"));
        String content = "💬 [" + dateStr + "] " + group.getTitle() + " 모임에 새로운 댓글이 달렸어요.";
        // 모임 첫 사진
        String photoPath = null;
        Optional<GroupPhoto> firstPhotoOpt = groupPhotoRepository.findFirstByGroupIdOrderByPhotoOrderAsc(groupId);
        if (firstPhotoOpt.isPresent()) {
            photoPath = firstPhotoOpt.get().getPhoto();
        }
        // 해당 모임의 전체 참여자 목록 (모임장 포함)
        List<Participation> participations = participationRepository.findByGroupId(groupId);
        for (Participation part : participations) {
            Long targetUserId = part.getUser().getId();
            if (!targetUserId.equals(userId)) {
                // 댓글 작성자 본인에게는 알림 보내지 않음
                Notification notification = Notification.builder()
                        .user(part.getUser())
                        .content(content)
                        .photo(photoPath)
                        .targetId(groupId)
                        .targetType(TargetType.GROUP)
                        .build();
                notificationRepository.save(notification);
            }
        }

        return CommentResponseDto.fromEntity(saved);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long userId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.COMMENT_PERMISSION_DENIED);
        }

        commentRepository.delete(comment);
    }

    // 모임별 댓글 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByGroupId(Long groupId){
        List<Comment> comments = commentRepository.findByGroupId(groupId);

        return comments.stream()
                .map(CommentResponseDto::fromEntity)
                .toList();
    }

    // 댓글 좋아요 생성/취소
    @Transactional
    public String toggleLike(Long commentId, Long userId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        Group group = comment.getGroup();

        // @ManyToMany의 주인(User) 엔티티를 가져와야 함
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // User 엔티티의 liked_comments Set을 가져옴
        Set<Comment> likedComments = user.getLiked_comments();

        if (likedComments.contains(comment)) {
            // 이미 좋아요 누름 -> 좋아요 취소
            likedComments.remove(comment);
            return "좋아요가 취소되었습니다.";
        } else {
            // 좋아요 안 누름 -> 좋아요 추가
            likedComments.add(comment);

            // 알림 생성: 댓글 작성자에게 좋아요 알림 보내기
            User author = comment.getUser();
            if (!author.getId().equals(userId)) {  // 본인의 댓글이 아닌 경우만
                String photoPath = null;
                Optional<GroupPhoto> firstPhotoOpt = groupPhotoRepository.findFirstByGroupIdOrderByPhotoOrderAsc(comment.getGroup().getId());
                if (firstPhotoOpt.isPresent()) {
                    photoPath = firstPhotoOpt.get().getPhoto();
                }

                String dateStr = group.getMeetingAt().format(DateTimeFormatter.ofPattern("yy.MM.dd"));
                String content = "♥️ ["+ dateStr + "] " + group.getTitle() + " 내가 작성한 댓글에 하트가 달렸어요.";

                Notification notification = Notification.builder()
                        .user(author)
                        .content(content)
                        .photo(photoPath)
                        .targetId(group.getId())
                        .targetType(TargetType.GROUP)
                        .build();
                notificationRepository.save(notification);
            }

            return "좋아요가 추가되었습니다.";
        }
    }

    //좋아요 눌렀는지 확인
    public CommentLikeResponseDTO isLiked(Long commentId, Long userId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(()->new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        // 현재 유저의 좋아요 상태 확인 (메서드 이름 쿼리 사용)
        boolean isLiked = commentRepository.existsByCommentIdAndLikedByUsers_Id(commentId, userId);

        return CommentLikeResponseDTO.of(isLiked);
    }

}
