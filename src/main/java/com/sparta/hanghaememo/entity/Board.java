package com.sparta.hanghaememo.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sparta.hanghaememo.dto.board.BoardRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
public class Board extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boardId")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private Long likes;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;

    @JsonManagedReference
    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @OrderBy("createdAt desc")
    private List<Comment> commentList;


    public Board(String title, String contents, String category, Users user) {
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.category = category;
        this.likes = 0L;
    }

    public void update(BoardRequestDto boardRequestDto) {
        this.title = boardRequestDto.getTitle();
        this.contents = boardRequestDto.getContents();
        this.category = boardRequestDto.getCategory();
    }

    public void addComment(List<Comment> commentList){ this.commentList = commentList; }

    public void updatelikes(Long likes){ this.likes = likes; }
}
