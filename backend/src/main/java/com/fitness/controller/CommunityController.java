package com.fitness.controller;

import com.fitness.dto.ApiResponse;
import com.fitness.entity.CommunityPost;
import com.fitness.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @GetMapping("/posts")
    public ResponseEntity<List<CommunityPost>> getPosts() {
        List<CommunityPost> posts = communityService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse<CommunityPost>> createPost(@RequestBody CommunityPost post,
                                                                   Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        post.setUserId(userId);
        post.setLikes(0);

        CommunityPost saved = communityService.createPost(post);
        return ResponseEntity.ok(ApiResponse.success("发布成功", saved));
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<ApiResponse<CommunityPost>> likePost(@PathVariable Long postId) {
        CommunityPost post = communityService.likePost(postId);
        return ResponseEntity.ok(ApiResponse.success("点赞成功", post));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        communityService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }
}
