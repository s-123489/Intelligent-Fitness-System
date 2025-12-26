package com.fitness.repository;

import com.fitness.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 评论Repository
 */
@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    List<PostComment> findByPostIdOrderByCreatedAtDesc(Long postId);

    Long countByPostId(Long postId);
}
