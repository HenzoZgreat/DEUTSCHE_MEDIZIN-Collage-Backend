package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "region")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    @Id
    private String regionCode;

    private String region;

    private String regionType;
}
