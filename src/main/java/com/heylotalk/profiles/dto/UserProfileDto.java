package com.heylotalk.profiles.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String profileImageUrl;
}
