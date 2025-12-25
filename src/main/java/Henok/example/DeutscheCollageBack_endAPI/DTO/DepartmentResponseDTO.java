package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentResponseDTO {

    private Long dptID;
    private String deptName;
    private Integer totalCrHr;
    private String departmentCode;

    // Nested DTO for ProgramModality (to avoid entity exposure)
    private ModalityDTO programModality;

    // Nested DTO for ProgramLevel (to avoid entity exposure)
    private LevelDTO programLevel;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModalityDTO {
        private String modalityCode;
        private String modality;
        private LevelDTO programLevel; // Nested if needed
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelDTO {
        private String code;
        private String name;
        private Boolean active;
    }
}