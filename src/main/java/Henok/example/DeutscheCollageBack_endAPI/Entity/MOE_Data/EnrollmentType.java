package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "enrollment_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentType {

    @Id
    private String enrollmentTypeCode; // from MOE Data

    private String enrollmentTypeName; // Example: "Regular", "Extension"
}

