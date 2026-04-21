package com.novelverse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NovelNotification {
    private String type;
    private Long novelId;
    private String title;
    private String message;
}
