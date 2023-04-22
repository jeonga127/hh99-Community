package com.sparta.hanghaememo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private String accessToken;
    @NotBlank
    private String refreshToken;

    public Token(String username, String accessToken, String refreshToken){
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public void update(Token token){
        this.refreshToken = token.getRefreshToken();
        this.accessToken = token.getAccessToken();
        this.username = token.getUsername();
    }
}