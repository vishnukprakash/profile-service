package com.heylotalk.profiles.service;

import com.heylotalk.profiles.dto.UserProfileDto;
import com.heylotalk.profiles.entity.ProfileImage;
import com.heylotalk.profiles.entity.UserProfile;
import com.heylotalk.profiles.exception.ResourceNotFoundException;
import com.heylotalk.profiles.repository.ProfileImageRepository;
import com.heylotalk.profiles.repository.UserProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final S3Service s3Service;
    private final ProfileImageRepository profileImageRepository;

    public UserProfileService(UserProfileRepository userProfileRepository, S3Service s3Service, ProfileImageRepository profileImageRepository) {
        this.userProfileRepository = userProfileRepository;
        this.s3Service = s3Service;
        this.profileImageRepository = profileImageRepository;
    }

    public UserProfileDto createUserProfile(UserProfileDto userProfileDto) {
        log.info("createUserProfile invoked");
        UserProfile userProfile = UserProfile.builder()
                .name(userProfileDto.getName()).email(userProfileDto.getEmail())
                .phoneNumber(userProfileDto.getPhoneNumber())
                .build();
        UserProfile savedProfile = userProfileRepository.save(userProfile);
        return mapToUserProfileDto(savedProfile);
    }

    public UserProfileDto getUserProfileById(Long id) {
        log.info("getUserProfileById invoked for id: {}", id);
        return mapToUserProfileDto(this.fetchUserProfile(id));
    }

    private UserProfile fetchUserProfile(Long id) {
        Optional<UserProfile> userProfile = userProfileRepository.findById(id);
        return userProfile.orElseThrow(()-> new
                ResourceNotFoundException("User profile not found for id: " + id)
        );
    }

    public void uploadProfileImage(Long userId, byte[] imageBytes, String contentType) {
        log.info("uploadImage invoked for userId: {}", userId);
        UserProfile userProfile = this.fetchUserProfile(userId);
        String imageKey = s3Service.uploadImage(userId, imageBytes, contentType);
        ProfileImage profileImage = ProfileImage.builder()
                .imageUrl(imageKey).userProfile(userProfile)
                .isProfilePicture(false)
                .build();
        profileImageRepository.save(profileImage);
    }

    private UserProfileDto mapToUserProfileDto(UserProfile profile) {
        log.info("Mapping UserProfile to UserProfileDto for profile: {}", profile);

        String imageUrl = Optional.ofNullable(profile.getProfileImages()).flatMap(profileImages ->
                        profileImages.stream().filter(ProfileImage::getIsProfilePicture).findFirst()).map(ProfileImage::getImageUrl)
                .map(this.s3Service::generatePresignedUrl).orElse(null);

        return UserProfileDto.builder()
                .id(profile.getId())
                .name(profile.getName())
                .email(profile.getEmail())
                .phoneNumber(profile.getPhoneNumber())
                .profileImageUrl(imageUrl)
                .build();
    }
}
