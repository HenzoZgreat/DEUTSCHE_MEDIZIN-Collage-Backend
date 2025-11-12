package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramModalityDTO {
    private String modalityCode;
    private String modality;
    private String programLevelCode;
}