package Henok.example.DeutscheCollageBack_endAPI.DTO;

import lombok.Data;

@Data
public class EHEECE_G12DTO {

    private Long studentId;

    private String nationalExamId;

    private Integer year;

    private String subject;

    private Double score;

    private byte[] photo;
}
