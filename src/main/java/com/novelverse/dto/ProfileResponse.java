package com.novelverse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String avatarUrl;
    private boolean verified;
    private LocalDateTime createdAt;
}
