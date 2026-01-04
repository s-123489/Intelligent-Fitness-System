package com.fitness.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 社区动态返回DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    private String username;

    private String content;

    @JsonProperty("image_url")
    private String imageUrl;

    private Integer likes;

    @JsonProperty("comment_count")
    private Long commentCount;

    @JsonProperty("is_liked")
    private Boolean isLiked;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
