package com.bstu.UniversityIIT.entity;

import com.bstu.UniversityIIT.entity.enums.Discipline;
import com.bstu.UniversityIIT.entity.enums.FileType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private long size;
    private String httpContentType;
    private String name;
    private String filePath;

    @Enumerated(EnumType.STRING)
    private Discipline discipline;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
}
