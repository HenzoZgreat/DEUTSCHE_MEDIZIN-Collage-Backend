package Henok.example.DeutscheCollageBack_endAPI.DTO.StudentSlips;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// StudentIdsRequest.java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentIdsRequest {

    private List<Long> studentIds;
}