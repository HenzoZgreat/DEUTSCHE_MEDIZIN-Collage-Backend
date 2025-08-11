package Henok.example.DeutscheCollageBack_endAPI.DTO;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BatchDTO {
    @JsonProperty("batchName")
    @JsonAlias("Batch")
    private int batchName;
}