package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {

    private String cTitle;
    private String cCode;
    private Integer theoryHrs;
    private Integer labHrs;
    private Long cCatagoryID;
    private Long departmentID;
    private List<Long> prerequisiteIds;
}
