package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity
@Table(name = "woreda")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Woreda {
    @Id
    private String woredaCode;

    private String woreda;

    @ManyToOne
    @JoinColumn(name = "zone_code", nullable = false)
    private Zone zone;
}
