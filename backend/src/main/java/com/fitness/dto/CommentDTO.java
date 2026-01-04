package com.fitness.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论返回DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    private String username;

    @JsonProperty("post_id")
    private Long postId;

    private String content;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
