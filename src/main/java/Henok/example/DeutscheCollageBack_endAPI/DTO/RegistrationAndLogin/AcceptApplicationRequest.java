package Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AcceptApplicationRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String dateEnrolledEC;

    @NotNull(message = "Enrollment date (GC) is required")
    private LocalDate dateEnrolledGC;

    // Required: Academic year (can be null if optional)
    private String academicYearCode; // yearCode from AcademicYear table

    // Required: Batch class year semester ID
    @NotNull(message = "Batch class year semester ID is required")
    private Long batchClassYearSemesterId;

    // Required: Student recent status ID
    @NotNull(message = "Student recent status ID is required")
    private Long studentRecentStatusId;

    // Required: Transfer status
    @NotNull(message = "Transfer status is required")
    private Boolean isTransfer;

    // Optional: Exit exam info
    private String exitExamUserID;

    private Double exitExamScore;

    private Boolean isStudentPassExitExam;

    // Optional: Grade 12 result
    private Double grade12Result;

    // Optional: Remark for document status
    private String remark;
}