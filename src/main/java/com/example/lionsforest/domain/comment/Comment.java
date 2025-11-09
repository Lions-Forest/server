package com.example.lionsforest.domain.comment;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String content;

    //이 댓글을 좋아요한 유저
    @Builder.Default
    @ManyToMany(mappedBy = "liked_comments")
    private Set<User> liked_by_users = new HashSet<>();
}
