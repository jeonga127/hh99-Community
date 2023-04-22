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

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private Users user;

    @JsonManagedReference
    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @OrderBy("createdAt desc")
    private List<Comment> commentList;

    @Column(nullable = false)
    private int likes;

    public Board(String title, String contents, Users user) {
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.likes = 0;
    }

    public void update(BoardRequestDto boardDTO) {
        this.title = boardDTO.getTitle();
        this.contents = boardDTO.getContents();
    }

    public void addComment(List<Comment> commentList){ this.commentList = commentList; }

    public void updatelikes(boolean addOrNot){
        this.likes = addOrNot? this.likes+1 : this.likes-1;
    }
}
