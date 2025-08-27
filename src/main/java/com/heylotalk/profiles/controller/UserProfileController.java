package com.heylotalk.profiles.controller;

import com.heylotalk.profiles.dto.UserProfileDto;
import com.heylotalk.profiles.service.S3Service;
import com.heylotalk.profiles.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/profile-service/v1/user-profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final S3Service s3Service;

    public UserProfileController(UserProfileService userProfileService, S3Service s3Service) {
        this.userProfileService = userProfileService;
        this.s3Service = s3Service;
    }

    @GetMapping("/{id}")
    public UserProfileDto getUserProfileById(@PathVariable("id") Long id) {
        return userProfileService.getUserProfileById(id);
    }

    @PostMapping
    public ResponseEntity<UserProfileDto> createUserProfile(@Valid @RequestBody UserProfileDto userProfileDto) {
        UserProfileDto createdProfile = userProfileService.createUserProfile(userProfileDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdProfile.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdProfile);
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Void> uploadProfileImage(
            @PathVariable("id") Long id,
            @RequestParam("image") MultipartFile file) throws IOException {

        userProfileService.uploadProfileImage(
                id,
                file.getBytes(),
                file.getContentType()
        );
        return ResponseEntity.noContent().build();
    }
}
