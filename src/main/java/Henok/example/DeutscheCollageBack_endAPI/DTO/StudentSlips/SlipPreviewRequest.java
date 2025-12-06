package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


// SlipPreviewRequest.java
// Why: Clean, validated input for previewing slips for one or many students
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SlipPreviewRequest {

    private List<Long> studentIds;

    private List<Long> courseIds;

    // Optional: override academic context if needed
    private Long batchClassYearSemesterId; // BCYS ID to force context
}