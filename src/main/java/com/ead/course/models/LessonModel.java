package com.ead.course.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "tb_lessons")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LessonModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID lessonId;

    @Column(nullable = false, unique = true, length = 150)
    private String title;

    @Column(nullable = false, unique = true, length = 250)
    private String description;

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime creationDate;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private ModuleModel module;

}
