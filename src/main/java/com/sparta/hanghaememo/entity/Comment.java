package com.sparta.hanghaememo.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.hanghaememo.dto.comment.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Comment extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentId")
    private Long id;

    @Column(nullable = false)
    private String contents;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boardId", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;

    @Column(nullable = false)
    private Long likes;

    public Comment(Users user, Board board, CommentRequestDto commentRequestDto){
        this.user = user;
        this.board = board;
        this.contents =commentRequestDto.getContents();
        this.likes = 0L;
    }

    public void update(CommentRequestDto commentRequestDto){
        this.contents=commentRequestDto.getContents();
    }

    public void updatelikes(Long likes){
        this.likes = likes;
    }
}

