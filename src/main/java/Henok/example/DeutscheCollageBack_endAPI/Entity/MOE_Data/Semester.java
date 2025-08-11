package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "semester")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Semester {
    @Id
    private String academicPeriodCode; // from MOE

    private String academicPeriod;
}
