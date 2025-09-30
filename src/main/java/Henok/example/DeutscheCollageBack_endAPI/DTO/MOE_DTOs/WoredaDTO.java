package Henok.example.DeutscheCollageBack_endAPI.DTO.MOE_DTOs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WoredaDTO {

    private String woredaCode; // Unique code for the woreda
    private String woreda; // Name of the woreda
    private String zoneCode; // Code of the associated zone
}
