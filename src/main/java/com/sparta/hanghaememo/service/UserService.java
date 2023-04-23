package com.sparta.hanghaememo.service;

import com.sparta.hanghaememo.dto.ResponseDto;
import com.sparta.hanghaememo.dto.user.LoginRequestDto;
import com.sparta.hanghaememo.dto.user.SignupRequestDto;
import com.sparta.hanghaememo.entity.*;
import com.sparta.hanghaememo.jwt.JwtUtil;
import com.sparta.hanghaememo.repository.*;
import com.sparta.hanghaememo.security.CustomException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    private final BoardRepository boardRepository;
    private final BoardLikesRepository boardLikesRepository;
    private final CommentRepository commentRepository;
    private final CommentLikesRepository commentLikesRepository;
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    // 회원 가입
    @Transactional
    public ResponseEntity signup(SignupRequestDto signupRequestDto) {
        String username = signupRequestDto.getUsername();
        String password = passwordEncoder.encode(signupRequestDto.getPassword());

        // 회원 중복 확인
        Optional<Users> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            return new ResponseEntity(ResponseDto.setFail("중복된 username 입니다"), HttpHeaders.EMPTY, HttpStatus.BAD_REQUEST);
        }

        // 사용자 role 확인
        UserRoleEnum role = UserRoleEnum.USER;
        if (signupRequestDto.isAdmin()) {
            if (!signupRequestDto.getAdminToken().equals(ADMIN_TOKEN)) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRoleEnum.ADMIN;
        }
        
        // 새로운 사용자 정보 저장
        Users user = new Users(username, password, role);
        userRepository.save(user);
        ResponseDto responseDTO = ResponseDto.setSuccess("회원가입 성공", null);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    // 로그인
    @Transactional
    public ResponseEntity login(LoginRequestDto loginRequestDto, HttpServletResponse response) throws Exception{
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        // 사용자 유무 확인
        Users user = userCheck(username);

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.NON_LOGIN);
        }

        // 사용자에게 Refresh 토큰 & Access 토큰 발급
        String refreshtoken = jwtUtil.createToken(user.getUsername(), user.getRole(),"REFRESH_HEADER");
        String accesstoken = jwtUtil.createToken(user.getUsername(), user.getRole(), "ACCESS_HEADER");

        if(tokenRepository.existsByUsername(username)){
            Token token = tokenRepository.findByUsername(username);
            token.update(new Token(username, accesstoken, refreshtoken ));
        } else tokenRepository.save(new Token(username, accesstoken, refreshtoken));
         
        // Access 토큰만 헤더에 담아 보냄
        response.addHeader(jwtUtil.ACCESS_HEADER, accesstoken);
        ResponseDto responseDTO = ResponseDto.setSuccess("로그인 성공", user);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }
    
    // 회원 탈퇴
    @Transactional
    public ResponseEntity withdrawal(Users user) {
        long user_id = user.getId();

        // 사용자의 게시물, 댓글, 좋아요 흔적 모두 삭제
        commentLikesRepository.deleteAllByUserId(user_id);
        commentRepository.deleteAllByUserId(user_id);
        boardLikesRepository.deleteAllByUserId(user_id);
        boardRepository.deleteAllByUserId(user_id);

        // 사용자 정보 & 토큰 정보 모두 삭제
        tokenRepository.deleteByUsername(user.getUsername());
        userRepository.deleteById(user_id);

        ResponseDto responseDTO = ResponseDto.setSuccess("회원탈퇴 성공", null);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    private Users userCheck(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NON_LOGIN)
        );
    }
}