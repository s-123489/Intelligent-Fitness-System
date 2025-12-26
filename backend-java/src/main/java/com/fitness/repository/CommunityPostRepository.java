package com.fitness.repository;

import com.fitness.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 社区动态Repository
 */
@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    List<CommunityPost> findTop50ByOrderByCreatedAtDesc();
}
