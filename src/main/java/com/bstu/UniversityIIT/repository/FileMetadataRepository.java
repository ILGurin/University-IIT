package com.bstu.UniversityIIT.repository;

import com.bstu.UniversityIIT.entity.FileMetadataEntity;
import com.bstu.UniversityIIT.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadataEntity, Integer> {

    Optional<FileMetadataEntity> findFileMetadataEntityById(Integer id);
    Optional<FileMetadataEntity> findFileMetadataEntityByFilePath(String filePath);

    @Query("SELECT f FROM FileMetadataEntity f WHERE f.fileType = 'MATERIAL'")
    List<FileMetadataEntity> findAllMaterials();

    @Query("SELECT f FROM FileMetadataEntity f WHERE f.fileType = 'EXAM_QUESTION'")
    List<FileMetadataEntity> findAllExamQuestions();

    @Query(" SELECT f FROM FileMetadataEntity f WHERE f.fileType = 'LAB_WORKS' ")
    List<FileMetadataEntity> findAllLabs();

    @Query(" SELECT f FROM FileMetadataEntity f WHERE f.fileType = 'MATERIAL' AND f.user = :user")
    List<FileMetadataEntity> findAllMaterialsByUser(User user);

    @Query(" SELECT f FROM FileMetadataEntity f WHERE f.fileType = 'EXAM_QUESTION' AND f.user = :user")
    List<FileMetadataEntity> findAllExamQuestionsByUser(User user);

    @Query(" SELECT f FROM FileMetadataEntity f WHERE f.fileType = 'LAB_WORKS' AND f.user = :user")
    List<FileMetadataEntity> findAllLabsByUser(User user);
}
