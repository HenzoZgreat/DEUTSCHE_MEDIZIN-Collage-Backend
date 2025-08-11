package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "area_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaType {
    @Id
    private String areaTypeCode;

    private String areaType;
}
