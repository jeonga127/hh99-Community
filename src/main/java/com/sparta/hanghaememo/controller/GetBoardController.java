package com.sparta.hanghaememo.controller;

import com.sparta.hanghaememo.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetBoardController {
    private final BoardService boardService;

    @GetMapping("/api/read")
    public ResponseEntity list(){
        return boardService.list();
    }

    @GetMapping("/api/read/{id}")
    public ResponseEntity listOne(@PathVariable Long id){
        return boardService.listOne(id);
    }
}
