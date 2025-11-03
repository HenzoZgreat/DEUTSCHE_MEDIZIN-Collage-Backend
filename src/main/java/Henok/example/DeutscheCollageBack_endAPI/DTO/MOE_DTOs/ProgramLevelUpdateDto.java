package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// src/main/java/com/yourorg/dto/ProgramLevelUpdateDto.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramLevelUpdateDto {

    private String name;

    private String remark;

    private Boolean active;
}