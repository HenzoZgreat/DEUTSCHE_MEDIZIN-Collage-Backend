package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseScore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentCourseScoreBulkUpdateDTO {
    private List<StudentCourseScoreUpdateRequest> updates;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentCourseScoreUpdateRequest {
        private Long id; // Required - identifies which record to update
        private Double score; // Optional - only update if not null
        private Long courseSourceId; // Optional - only update if not null
        private Boolean isReleased; // Optional - only update if not null
    }
}

