package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager.GeneralManagerDashboardDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager.GeneralManagerDetailDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager.GeneralManagerUpdateDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.GeneralManagerRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.UserRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.GeneralManagerDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.ApplicationStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.DocumentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AcademicYearRepo;
import Henok.example.DeutscheCollageBack_endAPI.Service.Utility.AcademicYearUtilityService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GeneralManagerService {

    @Autowired
    private UserService userService;
    @Autowired
    private GeneralManagerDetailRepository generalManagerDetailRepository;
    @Autowired
    private EntityManager entityManager;
    // Repositories for enrolled students and applicants
    private final StudentDetailsRepository studentDetailsRepository;
    private final AppliedStudentRepository appliedStudentRepository;

    // Repositories for staff detail entities (to count by role)
    private final TeacherRepository teacherDetailRepository;
    private final RegistrarDetailRepository registrarDetailRepository;
    private final DepartmentHeadRepository departmentHeadDetailsRepository;
    // Assume DeanViceDeanDetailRepository exists based on previous entities
    private final DeanViceDeanDetailsRepository deanViceDeanDetailRepository;

    // Repository for AcademicYear lookup
    private final AcademicYearRepo academicYearRepository;

    // Utility service to determine which academic year a date belongs to
    private final AcademicYearUtilityService academicYearUtilityService;


    @Transactional
    public GeneralManagerDetail registerGeneralManager(GeneralManagerRegisterRequest request, MultipartFile nationalIdImage, MultipartFile photograph) {
        // Validate required fields
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (request.getFirstNameAmharic() == null || request.getFirstNameAmharic().isEmpty()) {
            throw new IllegalArgumentException("First name in Amharic cannot be empty");
        }
        if (request.getLastNameAmharic() == null || request.getLastNameAmharic().isEmpty()) {
            throw new IllegalArgumentException("Last name in Amharic cannot be empty");
        }
        if (request.getFirstNameEnglish() == null || request.getFirstNameEnglish().isEmpty()) {
            throw new IllegalArgumentException("First name in English cannot be empty");
        }
        if (request.getLastNameEnglish() == null || request.getLastNameEnglish().isEmpty()) {
            throw new IllegalArgumentException("Last name in English cannot be empty");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        // Check for unique phone number
        if (generalManagerDetailRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Create and save User with GENERAL_MANAGER role
        UserRegisterRequest userRequest = new UserRegisterRequest();
        userRequest.setUsername(request.getUsername());
        userRequest.setPassword(request.getPassword());
        userRequest.setRole(Role.GENERAL_MANAGER);
        User user = userService.registerUser(userRequest);

        // Flush to ensure User is persisted
        entityManager.flush();

        // Check for existing GeneralManagerDetail with the same user
        if (generalManagerDetailRepository.findByUser(user).isPresent()) {
            throw new IllegalArgumentException("General Manager detail already exists for user: " + user.getUsername());
        }

        // Create GeneralManagerDetail
        GeneralManagerDetail generalManagerDetail = new GeneralManagerDetail();
        generalManagerDetail.setUser(user);
        generalManagerDetail.setFirstNameAmharic(request.getFirstNameAmharic());
        generalManagerDetail.setLastNameAmharic(request.getLastNameAmharic());
        generalManagerDetail.setFirstNameEnglish(request.getFirstNameEnglish());
        generalManagerDetail.setLastNameEnglish(request.getLastNameEnglish());
        generalManagerDetail.setEmail(request.getEmail());
        generalManagerDetail.setPhoneNumber(request.getPhoneNumber());

        // Convert MultipartFile to byte[]
        byte[] nationalIdImageBytes = null;
        byte[] photographBytes = null;
        try {
            if (nationalIdImage != null && !nationalIdImage.isEmpty()) {
                nationalIdImageBytes = nationalIdImage.getBytes();
            }
            if (photograph != null && !photograph.isEmpty()) {
                photographBytes = photograph.getBytes();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process image files: " + e.getMessage());
        }
        generalManagerDetail.setNationalIdImage(nationalIdImageBytes);
        generalManagerDetail.setPhotograph(photographBytes);

        // Clear persistence context to avoid stale entity issues
        entityManager.clear();

        return generalManagerDetailRepository.save(generalManagerDetail);
    }

    // Retrieves the profile of the currently authenticated general manager.
    // Throws ResourceNotFoundException if no detail record exists for the user.
    public GeneralManagerDetailDTO getProfileByUser(User user) {
        GeneralManagerDetail detail = generalManagerDetailRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("General manager profile not found"));

        return mapToDTO(detail);
    }

    // Performs a partial update on the general manager profile.
    // Only fields that are non-null in the updateDTO will be updated.
    // Why partial: Allows clients to update individual fields without sending the entire object.
    // Validation is handled by @Valid on the DTO fields.
    // Throws ResourceNotFoundException if profile does not exist.
    public GeneralManagerDetailDTO updateProfile(User user, GeneralManagerUpdateDTO updateDTO) {
        GeneralManagerDetail detail = generalManagerDetailRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("General manager profile not found"));

        // Partial update logic – only apply changes for non-null fields
        if (updateDTO.getFirstNameAmharic() != null) {
            detail.setFirstNameAmharic(updateDTO.getFirstNameAmharic());
        }
        if (updateDTO.getLastNameAmharic() != null) {
            detail.setLastNameAmharic(updateDTO.getLastNameAmharic());
        }
        if (updateDTO.getFirstNameEnglish() != null) {
            detail.setFirstNameEnglish(updateDTO.getFirstNameEnglish());
        }
        if (updateDTO.getLastNameEnglish() != null) {
            detail.setLastNameEnglish(updateDTO.getLastNameEnglish());
        }
        if (updateDTO.getEmail() != null) {
            detail.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPhoneNumber() != null) {
            detail.setPhoneNumber(updateDTO.getPhoneNumber());
        }
        if (updateDTO.getNationalIdImage() != null) {
            detail.setNationalIdImage(updateDTO.getNationalIdImage());
        }
        if (updateDTO.getPhotograph() != null) {
            detail.setPhotograph(updateDTO.getPhotograph());
        }

        // Save updated entity (transactional, so changes are persisted)
        GeneralManagerDetail updated = generalManagerDetailRepository.save(detail);

        return mapToDTO(updated);
    }

    // Helper method to map entity to DTO.
    // Why: Prevents exposing entity internals and blob data if not needed.
    private GeneralManagerDetailDTO mapToDTO(GeneralManagerDetail detail) {
        return GeneralManagerDetailDTO.builder()
                .id(detail.getId())
                .firstNameAmharic(detail.getFirstNameAmharic())
                .lastNameAmharic(detail.getLastNameAmharic())
                .firstNameEnglish(detail.getFirstNameEnglish())
                .lastNameEnglish(detail.getLastNameEnglish())
                .email(detail.getEmail())
                .phoneNumber(detail.getPhoneNumber())
                // Blob fields are returned only if client needs them (e.g., for display)
                .nationalIdImage(detail.getNationalIdImage())
                .photograph(detail.getPhotograph())
                .build();
    }


    public GeneralManagerDashboardDTO getDashboardData() {
        List<AcademicYear> allAcademicYears = academicYearRepository.findAll();

        return GeneralManagerDashboardDTO.builder()
                .studentOverview(buildStudentOverview())
                .applicationOverview(buildApplicationOverview())
                .staffOverview(buildStaffOverview())
                .departmentOverview(buildDepartmentOverview())
                .operationalAlerts(buildOperationalAlerts())
                .trends(buildTrends(allAcademicYears))
                .build();
    }

    private GeneralManagerDashboardDTO.StudentOverview buildStudentOverview() {
        // Use existing raw methods and map to nested DTOs
        List<GeneralManagerDashboardDTO.DepartmentCount> byDepartment = studentDetailsRepository.countStudentsPerDepartmentRaw().stream()
                .map(arr -> GeneralManagerDashboardDTO.DepartmentCount.builder()
                        .departmentName((String) arr[0])
                        .count((Long) arr[1])
                        .build())
                .toList();

        List<GeneralManagerDashboardDTO.ProgramModalityCount> byModality = studentDetailsRepository.countStudentsByModalityRaw().stream()
                .map(arr -> GeneralManagerDashboardDTO.ProgramModalityCount.builder()
                        .modality((String) arr[0])
                        .count((Long) arr[1])
                        .build())
                .toList();

        // Status count – assuming StudentStatus has a name field; adjust if needed
        List<GeneralManagerDashboardDTO.StudentStatusCount> byStatus = studentDetailsRepository.findAll().stream()
                .collect(Collectors.groupingBy(s -> s.getStudentRecentStatus().getStatusName(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> GeneralManagerDashboardDTO.StudentStatusCount.builder()
                        .status(e.getKey())
                        .count(e.getValue())
                        .build())
                .toList();

        long incompleteDocs = studentDetailsRepository.countByDocumentStatus(DocumentStatus.INCOMPLETE);

        return GeneralManagerDashboardDTO.StudentOverview.builder()
                .totalEnrolled(studentDetailsRepository.count())
                .byDepartment(byDepartment)
                .byProgramModality(byModality)
                .byStatus(byStatus)
                .incompleteDocuments(incompleteDocs)
                .build();
    }

    private GeneralManagerDashboardDTO.ApplicationOverview buildApplicationOverview() {
        List<GeneralManagerDashboardDTO.ApplicationStatusCount> byStatus = List.of(
                GeneralManagerDashboardDTO.ApplicationStatusCount.builder()
                        .status(ApplicationStatus.PENDING)
                        .count(appliedStudentRepository.countByApplicationStatus(ApplicationStatus.PENDING))
                        .build(),
                GeneralManagerDashboardDTO.ApplicationStatusCount.builder()
                        .status(ApplicationStatus.ACCEPTED)
                        .count(appliedStudentRepository.countByApplicationStatus(ApplicationStatus.ACCEPTED))
                        .build(),
                GeneralManagerDashboardDTO.ApplicationStatusCount.builder()
                        .status(ApplicationStatus.REJECTED)
                        .count(appliedStudentRepository.countByApplicationStatus(ApplicationStatus.REJECTED))
                        .build()
        );

        long pendingCount = appliedStudentRepository.countByApplicationStatus(ApplicationStatus.PENDING);

        // Department counts for applicants – using simple grouping (no raw query available yet)
        List<GeneralManagerDashboardDTO.DepartmentCount> byDepartment = appliedStudentRepository.findAll().stream()
                .collect(Collectors.groupingBy(a -> a.getDepartmentEnrolled().getDeptName(), Collectors.counting()))
                .entrySet().stream()
                .map(e -> GeneralManagerDashboardDTO.DepartmentCount.builder()
                        .departmentName(e.getKey())
                        .count(e.getValue())
                        .build())
                .toList();

        return GeneralManagerDashboardDTO.ApplicationOverview.builder()
                .totalApplied(appliedStudentRepository.count())
                .pendingCount(pendingCount)
                .byStatus(byStatus)
                .byDepartment(byDepartment)
                .build();
    }

    private GeneralManagerDashboardDTO.StaffOverview buildStaffOverview() {
        return GeneralManagerDashboardDTO.StaffOverview.builder()
                .totalTeachers(teacherDetailRepository.count())
                .totalRegistrars(registrarDetailRepository.count())
                .totalDepartmentHeads(departmentHeadDetailsRepository.count())
                .totalDeansViceDeans(deanViceDeanDetailRepository.count())
                .totalStaff(teacherDetailRepository.count() +
                        registrarDetailRepository.count() +
                        departmentHeadDetailsRepository.count() +
                        deanViceDeanDetailRepository.count())
                .build();
    }

    private GeneralManagerDashboardDTO.DepartmentOverview buildDepartmentOverview() {
        // Count distinct departments from enrolled students
        long totalDepartments = studentDetailsRepository.countDistinctDepartments();
        return GeneralManagerDashboardDTO.DepartmentOverview.builder()
                .totalDepartments(totalDepartments)
                .build();
    }

    private GeneralManagerDashboardDTO.OperationalAlerts buildOperationalAlerts() {
        long pendingApps = appliedStudentRepository.countByApplicationStatus(ApplicationStatus.PENDING);
        long studentsWithImpairments = studentDetailsRepository.countByImpairmentIsNotNull();

        return GeneralManagerDashboardDTO.OperationalAlerts.builder()
                .pendingApplications(pendingApps)
                .studentsWithImpairments(studentsWithImpairments)
                .build();
    }

    private GeneralManagerDashboardDTO.Trends buildTrends(List<AcademicYear> academicYears) {
        // Fetch only enrollment dates to avoid loading full entities
        List<LocalDate> enrollmentDates = studentDetailsRepository.findAllEnrollmentDates();

        Map<String, Long> countByAcademicYear = enrollmentDates.stream()
                .collect(Collectors.groupingBy(
                        date -> {
                            AcademicYear ay = academicYearUtilityService.findAcademicYearByDate(date, academicYears);
                            return ay != null ? ay.getAcademicYearGC() : "Unknown";
                        },
                        Collectors.counting()
                ));

        List<GeneralManagerDashboardDTO.EnrollmentTrend> trendList = countByAcademicYear.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> GeneralManagerDashboardDTO.EnrollmentTrend.builder()
                        .academicYear(e.getKey())
                        .count(e.getValue())
                        .build())
                .toList();

        return GeneralManagerDashboardDTO.Trends.builder()
                .enrollmentOverYears(trendList)
                .build();
    }
}