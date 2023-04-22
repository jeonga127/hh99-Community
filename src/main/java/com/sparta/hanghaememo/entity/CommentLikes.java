package com.sparta.hanghaememo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class CommentLikes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentLikeId")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "commmentId", nullable = false)
    private Comment comment;

    public CommentLikes(Comment comment, Users user) {
        this.comment = comment;
        this.user = user;
    }
}
