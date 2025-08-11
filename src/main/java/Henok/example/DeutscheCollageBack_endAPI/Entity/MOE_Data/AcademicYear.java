package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "academic_year")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicYear {
    @Id
    private String yearCode; // from MOE

    @Column(name = "academic_year_gc")
    private String academicYearGC;

    @Column(name = "academic_year_ec")
    private String academicYearEC;
}