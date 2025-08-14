package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "program_modality")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramModality {

    @Id
    private String modalityCode;

    private String modality;
}