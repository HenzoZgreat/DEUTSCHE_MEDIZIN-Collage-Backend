package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramLevelCreateDto {

    private String code;

    private String name;

    private String remark;
}