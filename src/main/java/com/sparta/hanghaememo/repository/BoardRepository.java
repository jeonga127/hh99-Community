package com.sparta.hanghaememo.repository;

import com.sparta.hanghaememo.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllByOrderByCreatedAtDesc();
    List<Board> findAllByCategory(String category);
    boolean existsByCategory(String category);
    void deleteAllByUserId(Long userId);
}
