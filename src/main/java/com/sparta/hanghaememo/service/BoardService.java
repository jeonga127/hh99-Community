package com.sparta.hanghaememo.service;

import com.sparta.hanghaememo.dto.board.BoardRequestDto;
import com.sparta.hanghaememo.dto.board.BoardResponseDto;
import com.sparta.hanghaememo.dto.ResponseDto;
import com.sparta.hanghaememo.entity.*;
import com.sparta.hanghaememo.repository.BoardLikesRepository;
import com.sparta.hanghaememo.repository.BoardRepository;
import com.sparta.hanghaememo.security.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final BoardLikesRepository boardLikesRepository;

    @Transactional
    public ResponseEntity write(BoardRequestDto boardRequestDTO , Users user){
        Board board = new Board(boardRequestDTO.getTitle(), boardRequestDTO.getContents(), user);

        boardRepository.save(board);
        ResponseDto responseDTO = ResponseDto.setSuccess("게시글 작성 성공", boardRequestDTO);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    // Board 객체를 BoardResponseDTO 타입으로 변경해 리스트 반환
    public ResponseEntity list() {
        List<BoardResponseDto> boardList = boardRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(BoardResponseDto::new)
                .collect(Collectors.toList());
        ResponseDto responseDTO = ResponseDto.setSuccess("게시글 목록 조회 성공", boardList);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    public ResponseEntity listOne(Long id) {
        // 게시글 존재여부 확인
        Board board = checkBoard(id);
        BoardResponseDto boardResponseDto = new BoardResponseDto(board);
        ResponseDto responseDTO = ResponseDto.setSuccess("게시글 조회 성공", boardResponseDto);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity update(Long id, BoardRequestDto boardRequestDTO, Users user) {
        // 게시글 존재여부 확인
        Board board = checkBoard(id);

        // 작성자 게시글 체크
        isBoardUsers(user, board);

        board.update(boardRequestDTO);
        ResponseDto responseDTO = ResponseDto.setSuccess("게시글 수정 성공",boardRequestDTO);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity delete(Long id, Users user) {
        // 게시글 존재여부 확인
        Board board = checkBoard(id);
        // 작성자 게시글 체크
        isBoardUsers(user, board);

        boardRepository.deleteById(id);
        ResponseDto responseDTO = ResponseDto.setSuccess("게시글 삭제 성공",null);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity updateLikes(Long id, Users user) {
        //게시글 존재여부 확인
        Board board = checkBoard(id);
        ResponseDto responseDTO = new ResponseDto();

        // 게시글에 현재 유저의 좋아요 유무 확인
        if(boardLikesRepository.existsByBoardIdAndUserId(board.getId(), user.getId())){
            // 좋아요가 있으면 삭제
            BoardLikes boardLikes = boardLikesRepository.findByBoardIdAndUserId(board.getId(), user.getId());
            boardLikesRepository.delete(boardLikes);
            board.updatelikes(false);
            responseDTO.setMessage("Board 좋아요 감소");
        }else{ // 없으면 좋아요 +1
            boardLikesRepository.save(new BoardLikes(board, user));
            board.updatelikes(true);
            responseDTO.setMessage("Board 좋아요 증가");
        }
        responseDTO.setStatus(StatusEnum.OK);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    private Board checkBoard(Long id){
        return boardRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NO_BOARD)
        );
    }

    private void isBoardUsers(Users users, Board board) {
        if (!board.getUser().getUsername().equals(users.getUsername()) && !users.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.NON_AUTHORIZATION);
        }
    }
}
