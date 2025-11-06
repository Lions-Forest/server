package com.example.lionsforest.domain.group;

import com.example.lionsforest.domain.comment.Comment;
import com.example.lionsforest.domain.review.Review;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "`Group`")
public class Group extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 63)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupCategory category;

    @Column(nullable = false)
    private Integer count;

    @Column(nullable = false)
    private LocalDateTime meeting_at;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupState state;

    //모임장
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader;

    //참여자들
    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Participation> participations = new ArrayList<>();

    //모임 사진
    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<GroupPhoto> photos = new ArrayList<>();

    //모임 댓글
    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    //모임 후기
    @Builder.Default
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Review> reviews = new ArrayList<>();

}
