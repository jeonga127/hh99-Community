package com.sparta.hanghaememo.controller;

import com.sparta.hanghaememo.dto.board.BoardRequestDto;
import com.sparta.hanghaememo.security.UserDetailsImpl;
import com.sparta.hanghaememo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시글 작성 기능
    @PostMapping("/api/boards")
    public ResponseEntity createBoard(@RequestBody BoardRequestDto board,
                                @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.createBoard(board, userDetails.getUser());
    }

    // 게시글 수정 기능
    @PutMapping("/api/boards/{id}")
    public ResponseEntity updateBoard(@PathVariable Long id,
                                     @RequestBody BoardRequestDto boardDTO,
                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.updateBoard(id, boardDTO, userDetails.getUser());
    }

    // 게시글 삭제 기능
    @DeleteMapping("/api/boards/{id}")
    public ResponseEntity deleteBoard(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.deleteBoard(id, userDetails.getUser());
    }

    // 게시글 좋아요 기능
    @PutMapping("/api/likes/board/{id}")
    public ResponseEntity updateLikes(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails){
        return boardService.updateLikes(id, userDetails.getUser());
    }
}
