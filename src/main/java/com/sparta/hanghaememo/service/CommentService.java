package com.sparta.hanghaememo.service;

import com.sparta.hanghaememo.dto.ResponseDto;
import com.sparta.hanghaememo.dto.comment.CommentRequestDto;
import com.sparta.hanghaememo.dto.comment.CommentResponseDto;
import com.sparta.hanghaememo.entity.*;
import com.sparta.hanghaememo.repository.BoardRepository;
import com.sparta.hanghaememo.repository.CommentLikesRepository;
import com.sparta.hanghaememo.repository.CommentRepository;
import com.sparta.hanghaememo.security.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;

    @Transactional
    public ResponseEntity createComment(CommentRequestDto requestDto, Users user){
        // 게시글 존재 여부 확인
        Board board = checkBoard(requestDto.getBoard_id());
        Comment comment = new Comment(user, board, requestDto);

        List<Comment> commentList = board.getCommentList();
        commentList.add(comment);
        board.addComment(commentList);

        commentRepository.save(comment);
        boardRepository.save(board);
        ResponseDto responseDTO = ResponseDto.setSuccess("댓글 작성 성공", new CommentResponseDto(comment));
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity updateComment(Long id, CommentRequestDto requestDto, Users user) {
        // 댓글 존재 여부 확인
        Comment comment = checkComment(id);
        // 작성자 게시글 체크
        isCommentUsers(user,comment);
        comment.update(requestDto);
        ResponseDto responseDTO = ResponseDto.setSuccess("댓글 수정 성공", new CommentResponseDto(comment));
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity deleteComment(Long id, Users user) {
        // 댓글 존재 여부 확인
        Comment comment = checkComment(id);
        // 작성자 게시글 체크
        isCommentUsers(user,comment);

        commentRepository.deleteById(id);
        ResponseDto responseDTO = ResponseDto.setSuccess("댓글 삭제 성공", null);
        return new ResponseEntity(responseDTO , HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity updateLikes(Long id, Users user) {
        // 댓글 존재 여부 확인
        Comment comment = checkComment(id);
        ResponseDto responseDTO = new ResponseDto();

        // 댓글에 현재 유저의 좋아요 유무 확인
        if(commentLikesRepository.existsByCommentIdAndUserId(comment.getId(), user.getId())){
            // 좋아요가 있으면 삭제
            CommentLikes commentLikes = commentLikesRepository.findByCommentIdAndUserId(comment.getId(), user.getId());
            commentLikesRepository.delete(commentLikes);
            comment.updatelikes(false);
            responseDTO.setMessage("Comment 좋아요 감소");
        }else{ // 없으면 좋아요 +1
            commentLikesRepository.save(new CommentLikes(comment, user));
            comment.updatelikes(true);
            responseDTO.setMessage("Comment 좋아요 증가");
        }
        responseDTO.setStatus(StatusEnum.OK);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    private Board checkBoard(Long id){
        return boardRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NO_BOARD)
        );
    }

    private Comment checkComment(Long id){
        return commentRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.NO_COMMENT)
        );
    }

    private void isCommentUsers(Users users, Comment comment) {
        if (!comment.getUser().getUsername().equals(users.getUsername()) && !users.getRole().equals(UserRoleEnum.ADMIN)) {
            throw new CustomException(ErrorCode.NON_AUTHORIZATION);
        }
    }
}
