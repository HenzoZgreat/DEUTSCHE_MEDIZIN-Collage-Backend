package Henok.example.DeutscheCollageBack_endAPI.Entity;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Semester;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Entity
@Table(name = "batch_class_year_semester")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchClassYearSemester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bcysID;

    @ManyToOne
    @JoinColumn(name = "batchID", nullable = false)
    private Batch batch;

    @Column(nullable = false)
    private Integer classYear;

    @ManyToOne
    @JoinColumn(name = "semester", nullable = false)
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "academic_year", nullable = false)
    private AcademicYear entryYear;

    @Column(nullable = false)
    private LocalDate classStart_GC;

    @Column(nullable = false)
    private String classStart_EC;

    @Column(nullable = false)
    private LocalDate classEnd_GC;

    @Column(nullable = false)
    private String classEnd_EC;
}