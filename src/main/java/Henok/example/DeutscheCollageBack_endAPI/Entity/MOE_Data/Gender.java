package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Gender_T")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Gender {

    @Id
    @Column(length = 10)
    private String id; // e.g. "M", "F", "O"

    @Column(nullable = false)
    private String gender;
}
