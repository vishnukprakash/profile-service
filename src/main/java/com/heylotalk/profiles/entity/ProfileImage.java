package com.heylotalk.profiles.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "profile_image")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "is_profile_picture", nullable = false, columnDefinition = "boolean default false")
    private Boolean isProfilePicture;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id", nullable = false)
    private UserProfile userProfile;
}
