package com.example.lionsforest.domain.review;

import com.example.lionsforest.domain.group.Group;
import com.example.lionsforest.domain.user.User;
import com.example.lionsforest.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long review_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer score;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    //후기 사진
    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL)
    private List<ReviewPhoto> photos = new ArrayList<>();
}
