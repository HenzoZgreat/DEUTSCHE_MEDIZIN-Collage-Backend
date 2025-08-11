package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "students_eheece_g12")
@Data
public class EHEECE_G12 {
    @Id
    @Column(name = "student_id")
    private Long studentId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(name = "preparatory_school_stream")
    private String preparatorySchoolStream;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "town")
    private String town;

    @Column(name = "english")
    private Double english;

    @Column(name = "maths")
    private Double maths;

    @Column(name = "aptitude")
    private Double aptitude;

    @Column(name = "physics")
    private Double physics;

    @Column(name = "chemistry")
    private Double chemistry;

    @Column(name = "civics")
    private Double civics;

    @Column(name = "biology")
    private Double biology;

    @Column(name = "geo_science")
    private Double geoScience;

    @Column(name = "total")
    private Double total;

    @Column(name = "year_of_exam")
    private Integer yearOfExam;

    @Column(name = "national_exam_id")
    private String nationalExamId;
}