package com.example.lionsforest.domain.user;

import com.example.lionsforest.domain.comment.Comment;
import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.group.Participation;
import com.example.lionsforest.domain.notification.Notification;
import com.example.lionsforest.domain.radar.Radar;
import com.example.lionsforest.domain.user.dto.UserInfoResponseDTO;
import com.example.lionsforest.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`User`")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String name;

    @Column(nullable = false, length = 63)
    private String email;

    private String nickname;

    private String bio;

    private String profile_photo;

    //연관관계
    //내가 개설한 모임
    @Builder.Default
    @OneToMany(mappedBy = "leader")
    private List<Group> led_groups = new ArrayList<>();

    //내가 참여한 모임
    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Participation> participations = new ArrayList<>();

    //내가 작성한 댓글
    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    //내가 좋아요한 댓글
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "CommentLike", //연결 테이블 이름
            joinColumns = @JoinColumn(name = "user_id"), //내 FK
            inverseJoinColumns = @JoinColumn(name = "comment_id") //상대 테이블 FK
    )
    private Set<Comment> liked_comments = new HashSet<>();

    //내 레이더
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Radar radar;

    //내 알림
    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    //내가 준 레이더 좋아요
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "MessageLike",
            joinColumns = @JoinColumn(name = "sender_id"),
            inverseJoinColumns = @JoinColumn(name = "reciever_id")
    )
    private Set<User> liked_users = new HashSet<>();

    //내가 받은 레이더 좋아요
    @Builder.Default
    @ManyToMany(mappedBy = "liked_users")
    private Set<User> liked_by_users = new HashSet<>();

    //메서드
    // 유저 프로필 수정
    // 닉네임 & 바이오 - 항상 업데이트
    public void updateNicknameAndBio(String nickname, String bio) {
        this.nickname = nickname;
        this.bio = bio;
    }
    //profile_photo 업데이트 : @Setter의 setProfile_photo() 사용
}
