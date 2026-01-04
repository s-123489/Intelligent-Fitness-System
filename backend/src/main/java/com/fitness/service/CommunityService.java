package com.fitness.service;

import com.fitness.dto.CommentDTO;
import com.fitness.dto.PostDTO;
import com.fitness.entity.CommunityPost;
import com.fitness.entity.PostComment;
import com.fitness.entity.PostLike;
import com.fitness.entity.User;
import com.fitness.repository.CommunityPostRepository;
import com.fitness.repository.PostCommentRepository;
import com.fitness.repository.PostLikeRepository;
import com.fitness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommunityService {

    @Autowired
    private CommunityPostRepository communityPostRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PostDTO> getAllPosts(Long currentUserId) {
        List<CommunityPost> posts = communityPostRepository.findTop50ByOrderByCreatedAtDesc();

        // 获取所有动态的ID列表
        List<Long> postIds = posts.stream().map(CommunityPost::getId).collect(Collectors.toList());

        // 批量获取当前用户的点赞记录
        List<PostLike> userLikes = currentUserId != null ?
            postLikeRepository.findByUserIdAndPostIdIn(currentUserId, postIds) : new ArrayList<>();
        Map<Long, Boolean> likedMap = new HashMap<>();
        for (PostLike like : userLikes) {
            likedMap.put(like.getPostId(), true);
        }

        // 转换为DTO
        List<PostDTO> postDTOs = new ArrayList<>();
        for (CommunityPost post : posts) {
            PostDTO dto = new PostDTO();
            dto.setId(post.getId());
            dto.setUserId(post.getUserId());
            dto.setContent(post.getContent());
            dto.setImageUrl(post.getImageUrl());
            dto.setLikes(post.getLikes());
            dto.setCreatedAt(post.getCreatedAt());

            // 获取用户名
            User user = userRepository.findById(post.getUserId()).orElse(null);
            dto.setUsername(user != null ? user.getUsername() : "未知用户");

            // 获取评论数
            Long commentCount = postCommentRepository.countByPostId(post.getId());
            dto.setCommentCount(commentCount);

            // 设置是否已点赞
            dto.setIsLiked(likedMap.getOrDefault(post.getId(), false));

            postDTOs.add(dto);
        }

        return postDTOs;
    }

    public CommunityPost createPost(CommunityPost post) {
        return communityPostRepository.save(post);
    }

    public void deletePost(Long id) {
        communityPostRepository.deleteById(id);
    }

    @Transactional
    public Map<String, Object> toggleLike(Long postId, Long userId) {
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("动态不存在"));

        Map<String, Object> result = new HashMap<>();

        // 检查是否已点赞
        Optional<PostLike> existingLike = postLikeRepository.findByUserIdAndPostId(userId, postId);

        if (existingLike.isPresent()) {
            // 已点赞，取消点赞
            postLikeRepository.delete(existingLike.get());
            post.setLikes(Math.max(0, post.getLikes() - 1));
            result.put("is_liked", false);
            result.put("message", "取消点赞");
        } else {
            // 未点赞，添加点赞
            PostLike like = new PostLike();
            like.setUserId(userId);
            like.setPostId(postId);
            postLikeRepository.save(like);

            post.setLikes(post.getLikes() + 1);
            result.put("is_liked", true);
            result.put("message", "点赞成功");
        }

        communityPostRepository.save(post);
        result.put("likes", post.getLikes());

        return result;
    }

    public List<CommentDTO> getComments(Long postId) {
        List<PostComment> comments = postCommentRepository.findByPostIdOrderByCreatedAtDesc(postId);

        List<CommentDTO> commentDTOs = new ArrayList<>();
        for (PostComment comment : comments) {
            CommentDTO dto = new CommentDTO();
            dto.setId(comment.getId());
            dto.setUserId(comment.getUserId());
            dto.setPostId(comment.getPostId());
            dto.setContent(comment.getContent());
            dto.setCreatedAt(comment.getCreatedAt());

            // 获取用户名
            User user = userRepository.findById(comment.getUserId()).orElse(null);
            dto.setUsername(user != null ? user.getUsername() : "未知用户");

            commentDTOs.add(dto);
        }

        return commentDTOs;
    }

    public PostComment addComment(PostComment comment) {
        return postCommentRepository.save(comment);
    }
}
