package Henok.example.DeutscheCollageBack_endAPI.Entity;

import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cohort")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cohort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The base academic structure (batch + year + semester)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bcys_id", nullable = false)
    private BatchClassYearSemester bcys;

    // The department this cohort belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    // Academic year this cohort is running in
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_code", nullable = false)
    private AcademicYear academicYear;

    // Time boundaries for this specific cohort
    @Column(name = "class_start_ec")
    private String classStartEC;

    @Column(name = "class_start_gc")
    private LocalDate classStartGC;

    @Column(name = "class_end_ec")
    private String classEndEC;

    @Column(name = "class_end_gc")
    private LocalDate classEndGC;

    // Optional: helpful derived field
    public String getDisplayName() {
        if (bcys == null || department == null) return "Unknown Cohort";

        String base = bcys.getDisplayName(); // assumes old display name without dept
        String dept = department.getDepartmentCode() != null ? department.getDepartmentCode() : department.getDeptName();
        return base + " - " + dept;
    }
}