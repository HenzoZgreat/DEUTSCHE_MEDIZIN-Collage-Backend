package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Impairment {
    @Id
    private String impairmentCode;

    @Column(nullable = false)
    private String impairment;
}