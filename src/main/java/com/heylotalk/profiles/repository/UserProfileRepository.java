package com.heylotalk.profiles.repository;

import com.heylotalk.profiles.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
