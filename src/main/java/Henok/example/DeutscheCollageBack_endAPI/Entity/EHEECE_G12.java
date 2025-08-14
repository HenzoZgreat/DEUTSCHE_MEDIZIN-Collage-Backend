package Henok.example.DeutscheCollageBack_endAPI.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "students_eheece_g12")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EHEECE_G12 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private StudentDetails student;

    private String nationalExamId;

    private Integer year;

    private String subject;

    private Double score;

    @Lob
    private byte[] photo;
}