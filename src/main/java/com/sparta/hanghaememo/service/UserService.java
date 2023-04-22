package com.sparta.hanghaememo.service;

import com.sparta.hanghaememo.dto.ResponseDto;
import com.sparta.hanghaememo.dto.user.LoginRequestDto;
import com.sparta.hanghaememo.dto.user.SignupRequestDto;
import com.sparta.hanghaememo.entity.ErrorCode;
import com.sparta.hanghaememo.entity.Token;
import com.sparta.hanghaememo.entity.UserRoleEnum;
import com.sparta.hanghaememo.entity.Users;
import com.sparta.hanghaememo.jwt.JwtUtil;
import com.sparta.hanghaememo.repository.TokenRepository;
import com.sparta.hanghaememo.repository.UserRepository;
import com.sparta.hanghaememo.security.CustomException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private static final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

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

        Users user = new Users(username, password, role);
        userRepository.save(user);
        ResponseDto responseDTO = ResponseDto.setSuccess("회원가입 성공", null);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

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

        String refreshtoken = jwtUtil.createToken(user.getUsername(), user.getRole(),"REFRESH_HEADER");
        String accesstoken = jwtUtil.createToken(user.getUsername(), user.getRole(), "ACCESS_HEADER");

        if(tokenRepository.existsByUsername(username)){
            Token token = tokenRepository.findByUsername(username);
            token.update(new Token(username, accesstoken, refreshtoken ));
        } else tokenRepository.save(new Token(username, accesstoken, refreshtoken));

        response.addHeader(jwtUtil.ACCESS_HEADER, accesstoken);
        ResponseDto responseDTO = ResponseDto.setSuccess("로그인 성공", user);
        return new ResponseEntity(responseDTO, HttpStatus.OK);
    }

    private Users userCheck(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(ErrorCode.NON_LOGIN)
        );
    }

}