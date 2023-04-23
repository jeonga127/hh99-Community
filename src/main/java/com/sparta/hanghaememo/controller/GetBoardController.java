package com.sparta.hanghaememo.controller;

import com.sparta.hanghaememo.service.BoardService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetBoardController {
    private final BoardService boardService;

    // 게시글 전체 조회 기능
    @GetMapping("/api/read")
    public ResponseEntity getAllBoard(){
        return boardService.getAllBoard();
    }

    // 게시글 선택 조회 기능
    @GetMapping("/api/read/{id}")
    public ResponseEntity getOneBoard(@PathVariable Long id){
        return boardService.getOneBoard(id);
    }

    // 게시글 카테고리별 조회 기능
    @GetMapping("/api/read")
    public ResponseEntity getCategorizedBoard(@PathParam("category") String category){
        return boardService.getCategorizedBoard(category);
    }
}
