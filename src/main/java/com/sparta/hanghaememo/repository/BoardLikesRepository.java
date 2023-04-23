package com.sparta.hanghaememo.repository;

import com.sparta.hanghaememo.entity.BoardLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardLikesRepository extends JpaRepository<BoardLikes, Long> {
    boolean existsByBoardIdAndUserId(Long boardId, Long userId);

    BoardLikes findByBoardIdAndUserId(Long boardId, Long userId);
    Long countByBoardId(Long boardId);
    void deleteAllByUserId(long userId);
}
