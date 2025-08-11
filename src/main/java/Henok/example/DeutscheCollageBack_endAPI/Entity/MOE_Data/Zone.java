package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "zone")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Zone {
    @Id
    private String zoneCode;

    private String zone;

    @ManyToOne
    @JoinColumn(name = "region_code", nullable = false)
    private Region region;
}
