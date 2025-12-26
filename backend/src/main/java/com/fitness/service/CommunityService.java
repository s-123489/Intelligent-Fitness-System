package com.fitness.service;

import com.fitness.entity.CommunityPost;
import com.fitness.entity.PostComment;
import com.fitness.repository.CommunityPostRepository;
import com.fitness.repository.PostCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommunityService {

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    public List<CommunityPost> getAllPosts() {
        return communityPostRepository.findTop50ByOrderByCreatedAtDesc();
    }

    public CommunityPost createPost(CommunityPost post) {
        return communityPostRepository.save(post);
    }

    public void deletePost(Long id) {
        communityPostRepository.deleteById(id);
    }

    public CommunityPost likePost(Long postId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("动态不存在"));
        post.setLikes(post.getLikes() + 1);
        return communityPostRepository.save(post);
    }

    public List<PostComment> getComments(Long postId) {
        return postCommentRepository.findByPostIdOrderByCreatedAtDesc(postId);
    }

    public PostComment addComment(PostComment comment) {
        return postCommentRepository.save(comment);
    }
}
