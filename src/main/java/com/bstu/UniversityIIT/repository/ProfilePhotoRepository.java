package com.bstu.UniversityIIT.repository;

import com.bstu.UniversityIIT.entity.ProfilePhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilePhotoRepository extends JpaRepository<ProfilePhoto, Integer> {
    Optional<ProfilePhoto> findByName(String filename);
}
