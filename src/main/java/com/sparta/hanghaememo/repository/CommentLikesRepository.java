package com.sparta.hanghaememo.repository;

import com.sparta.hanghaememo.entity.CommentLikes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikesRepository extends JpaRepository<CommentLikes, Long> {
    boolean existsByCommentIdAndUserId(Long commentId, Long userId);
    CommentLikes findByCommentIdAndUserId(Long commentId, Long userId);
    Long countByCommentId(Long commentId);
    void deleteAllByUserId(Long userId);
}
