package com.example.lionsforest.domain.review.service;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.repository.GroupRepository;
import com.example.lionsforest.domain.review.Review;
import com.example.lionsforest.domain.review.ReviewPhoto;
import com.example.lionsforest.domain.review.dto.request.ReviewRequestDto;
import com.example.lionsforest.domain.review.dto.request.ReviewUpdateRequestDto;
import com.example.lionsforest.domain.review.dto.response.ReviewGetResponseDto;
import com.example.lionsforest.domain.review.dto.response.ReviewResponseDto;
import com.example.lionsforest.domain.review.repository.ReviewPhotoRepository;
import com.example.lionsforest.domain.review.repository.ReviewRepository;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.domain.user.repository.UserRepository;
import com.example.lionsforest.global.common.S3UploadService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewPhotoRepository reviewPhotoRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;

    // 후기 생성
    @Transactional
    public ReviewResponseDto createReview(Long groupId,
                                          ReviewRequestDto dto,
                                          List<MultipartFile> photos,
                                          Long userId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 모임입니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Review review = Review.builder()
                .group(group)
                .score(dto.getScore())
                .content(dto.getContent())
                .user(user)
                .build();

        Review saved = reviewRepository.save(review);

        if (photos != null && !photos.isEmpty()) {
            List<ReviewPhoto> reviewPhotos = new ArrayList<>();
            for (int i = 0; i < photos.size(); i++) {
                MultipartFile photo = photos.get(i);

                // S3(또는 로컬)에 파일 업로드 -> URL 반환
                String photoUrl = s3UploadService.upload(photo, "review-photos");
                // ReviewPhoto 엔티티 생성
                ReviewPhoto reviewPhoto = ReviewPhoto.builder()
                        .review(saved)        // 저장된 Review 객체
                        .photo(photoUrl)     // S3에서 반환된 URL
                        .photo_order(i)      // 사진 순서 (0부터 시작)
                        .build();

                reviewPhotos.add(reviewPhoto);
            }
            // ReviewPhoto 리스트를 DB에 한 번에 저장 (Batch Insert)
            reviewPhotoRepository.saveAll(reviewPhotos);
        }

        return ReviewResponseDto.fromEntity(saved);
    }

    // 후기 삭제
    @Transactional
    public void deleteReview(Long reviewId, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Review review = reviewRepository
                .findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 후기입니다."));

        if(!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 후기를 삭제할 수 있습니다.");
        }

        if (review.getPhotos() != null) {
            review.getPhotos().forEach(p -> s3UploadService.delete(p.getPhoto()));
        }

        reviewRepository.delete(review);
    }

    //후기 수정
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId,
                                          ReviewUpdateRequestDto dto,
                                          List<MultipartFile> addPhotos,
                                          Long userId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 후기가 존재하지 않습니다."));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("작성자만 후기를 수정할 수 있습니다.");
        }

        if (dto.getContent() != null) {
            review.setContent(dto.getContent());
        }
        if (dto.getScore() != null) {
            review.setScore(dto.getScore());
        }

        // 사진 삭제 요청 처리 (S3/로컬 → 파일 먼저 삭제, 그 다음 DB 삭제)
        if (dto.getDeletePhotoIds() != null && !dto.getDeletePhotoIds().isEmpty()) {
            List<ReviewPhoto> toDelete = review.getPhotos().stream()
                    .filter(p -> dto.getDeletePhotoIds().contains(p.getId()))
                    .toList();

            // 소유 검증: 다른 후기 사진을 삭제하려는 경우 차단
            if (toDelete.size() != dto.getDeletePhotoIds().size()) {
                throw new IllegalArgumentException("삭제 대상에 포함된 사진 중 이 후기의 사진이 아닌 항목이 있습니다.");
            }

            // 스토리지 파일 삭제
            toDelete.forEach(p -> s3UploadService.delete(p.getPhoto()));

            // DB 삭제
            reviewPhotoRepository.deleteAllInBatch(toDelete);

            // 컬렉션 동기화(선택): 영속 컬렉션에서도 제거
            review.getPhotos().removeAll(toDelete);
        }

        // 사진 추가
        if (addPhotos != null && !addPhotos.isEmpty()) {
            // 현재 최대 photo_order 계산
            int nextOrder = review.getPhotos().stream()
                    .map(ReviewPhoto::getPhoto_order)
                    .max(Integer::compareTo)
                    .orElse(-1) + 1;

            List<ReviewPhoto> toAdd = new ArrayList<>();
            for (int i = 0; i < addPhotos.size(); i++) {
                MultipartFile file = addPhotos.get(i);
                String url = s3UploadService.upload(file, "review-photos");

                ReviewPhoto rp = ReviewPhoto.builder()
                        .review(review)
                        .photo(url)
                        .photo_order(nextOrder + i)
                        .build();
                toAdd.add(rp);
            }
            reviewPhotoRepository.saveAll(toAdd);

            // 컬렉션에도 추가(양방향 컬렉션 유지)
            review.getPhotos().addAll(toAdd);
        }

        // photo_order 정규화
        normalizePhotoOrders(review);

        return ReviewResponseDto.fromEntity(review);
    }


    // 개별 후기 조회
    @Transactional(readOnly = true)
    public ReviewGetResponseDto getReviewById(Long reviewId){
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("해당 후기가 존재하지 않습니다."));
        return ReviewGetResponseDto.fromEntity(review);
    }


    // 모임별 후기 전체 조회
    @Transactional(readOnly = true)
    public List<ReviewGetResponseDto> getReviewByGroupId(Long groupId){
        List<Review> reviews = reviewRepository.findByGroupId(groupId);

        return reviews.stream()
                .map(ReviewGetResponseDto::fromEntity)
                .toList();
    }

    // 특정 유저의 후기 전체 조회
    @Transactional(readOnly = true)
    public List<ReviewGetResponseDto> getReviewByUserId(Long userId){
        List<Review> reviews = reviewRepository.findByUserId(userId);

        return reviews.stream()
                .map(ReviewGetResponseDto::fromEntity)
                .toList();
    }

    private void normalizePhotoOrders(Review review) {
        review.getPhotos().stream()
                .sorted((a, b) -> Integer.compare(a.getPhoto_order(), b.getPhoto_order()))
                .forEachOrdered(new java.util.function.Consumer<ReviewPhoto>() {
                    int idx = 0;
                    @Override public void accept(ReviewPhoto p) { p.setPhoto_order(idx++); }
                });
    }


}
