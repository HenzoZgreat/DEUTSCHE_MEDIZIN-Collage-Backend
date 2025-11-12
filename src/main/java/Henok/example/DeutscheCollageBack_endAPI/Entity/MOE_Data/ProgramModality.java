package Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "program_modality")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramModality {

    @Id
    private String modalityCode;

    private String modality;

    // Foreign key to ProgramLevel (the level this modality belongs to)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "program_level_code",
            nullable = true,
            referencedColumnName = "code",
            foreignKey = @ForeignKey(name = "fk_program_modality_program_level")
    )
    private ProgramLevel programLevel;
}