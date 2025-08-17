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

    @ManyToOne
    @JoinColumn(name = "class_year_id", nullable = false)
    private ClassYear classYear;

    @ManyToOne
    @JoinColumn(name = "semester", nullable = false)
    private Semester semester;

    @ManyToOne
    @JoinColumn(name = "academic_year")
    private AcademicYear entryYear;

    @Column
    private LocalDate classStart_GC;

    @Column
    private String classStart_EC;

    @Column
    private LocalDate classEnd_GC;

    @Column
    private String classEnd_EC;
}