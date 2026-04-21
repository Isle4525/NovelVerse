package com.novelverse.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String username;
    private String email;
}
