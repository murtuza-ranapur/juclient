package org.test.pack;

import lombok.Data;

@Data
public class StudentCourseViewDTO {
    private Long studentId;
    private String name;
    private String courseCode;
    private String courseName;
}
