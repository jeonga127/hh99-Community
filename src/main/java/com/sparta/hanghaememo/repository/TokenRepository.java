package com.sparta.hanghaememo.repository;

import com.sparta.hanghaememo.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Token findByAccessToken(String accessToken);
    boolean existsByAccessToken(String accessToken);
    Token findByUsername(String username);
    boolean existsByUsername(String username);
}
