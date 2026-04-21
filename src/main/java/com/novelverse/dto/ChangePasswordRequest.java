package com.novelverse.dto;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String newPassword;
    private String confirmPassword;
}
