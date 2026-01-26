package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssessmentScoresResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarDashboardDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.RegistrarRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.UserRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.AcademicYear;
import Henok.example.DeutscheCollageBack_endAPI.Enums.*;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Error.BadRequestException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.AcademicYearRepo;
import Henok.example.DeutscheCollageBack_endAPI.Service.Utility.AcademicYearUtilityService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrarService {

    @Autowired private final UserService userService;
    @Autowired private final RegistrarDetailRepository registrarDetailRepository;
    @Autowired private final AppliedStudentRepository appliedStudentRepository;
    @Autowired private final StudentDetailsRepository studentDetailsRepository;
    @Autowired private final StudentCourseScoreRepo studentCourseScoreRepository;
    @Autowired private final DepartmentRepo departmentRepository;
    @Autowired private final StudentStatusRepo studentStatusRepository;
    @Autowired private final AcademicYearRepo academicYearRepository; // New: to fetch all academic years
    @Autowired private final AcademicYearUtilityService academicYearUtilityService; // New: injected for date checking
    @Autowired private final DepartmentHeadRepository departmentHeadRepository;
    @Autowired private final CourseRepo courseRepo;
    @Autowired private final AssessmentRepo assessmentRepository;
    @Autowired private final StudentCourseScoreRepo studentCourseScoreRepo;
    @Autowired private final StudentAssessmentRepo studentAssessmentRepository;
    @Autowired private final TeacherCourseAssignmentRepository assignmentRepository;
    @Autowired private final NotificationService notificationService;
    @Autowired private EntityManager entityManager;



    @Transactional
    public RegistrarDetail registerRegistrar(RegistrarRegisterRequest request, MultipartFile nationalIdImage, MultipartFile photograph) {
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
        if (registrarDetailRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Create and save User with REGISTRAR role
        UserRegisterRequest userRequest = new UserRegisterRequest();
        userRequest.setUsername(request.getUsername());
        userRequest.setPassword(request.getPassword());
        userRequest.setRole(Role.REGISTRAR);
        User user = userService.registerUser(userRequest);

        // Flush to ensure User is persisted
        entityManager.flush();

        // Check for existing RegistrarDetail with the same user
        if (registrarDetailRepository.findByUser(user).isPresent()) {
            throw new IllegalArgumentException("Registrar detail already exists for user: " + user.getUsername());
        }

        // Create RegistrarDetail
        RegistrarDetail registrarDetail = new RegistrarDetail();
        registrarDetail.setUser(user);
        registrarDetail.setFirstNameAmharic(request.getFirstNameAmharic());
        registrarDetail.setLastNameAmharic(request.getLastNameAmharic());
        registrarDetail.setFirstNameEnglish(request.getFirstNameEnglish());
        registrarDetail.setLastNameEnglish(request.getLastNameEnglish());
        registrarDetail.setEmail(request.getEmail());
        registrarDetail.setPhoneNumber(request.getPhoneNumber());

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
        registrarDetail.setNationalIdImage(nationalIdImageBytes);
        registrarDetail.setPhotograph(photographBytes);

        // Clear persistence context to avoid stale entity issues
        entityManager.clear();

        return registrarDetailRepository.save(registrarDetail);
    }

    // -- New helper methods for controller endpoints --
    @Transactional(readOnly = true)
    public java.util.List<Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarResponse> getAllUserEnabledRegistrars() {
        return registrarDetailRepository.findAll().stream()
                .filter(rd -> rd.getUser() != null && rd.getUser().isEnabled() && rd.getUser().getRole() == Role.REGISTRAR)
                .map(rd -> {
                    Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarResponse dto = new Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarResponse();
                    dto.setId(rd.getId());
                    dto.setUsername(rd.getUser().getUsername());
                    dto.setFirstNameAmharic(rd.getFirstNameAmharic());
                    dto.setLastNameAmharic(rd.getLastNameAmharic());
                    dto.setFirstNameEnglish(rd.getFirstNameEnglish());
                    dto.setLastNameEnglish(rd.getLastNameEnglish());
                    dto.setEmail(rd.getEmail());
                    dto.setPhoneNumber(rd.getPhoneNumber());
                    dto.setHasPhoto(rd.getPhotograph() != null && rd.getPhotograph().length > 0);
                    dto.setHasNationalId(rd.getNationalIdImage() != null && rd.getNationalIdImage().length > 0);
                    dto.setEnabled(rd.getUser().isEnabled());
                    return dto;
                }).toList();
    }

    @Transactional(readOnly = true)
    public Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarResponse getProfileByUser(User user) {
        RegistrarDetail rd = registrarDetailRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Registrar profile not found for user: " + user.getUsername()));

        Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarResponse dto = new Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarResponse();
        dto.setId(rd.getId());
        dto.setUsername(rd.getUser().getUsername());
        dto.setFirstNameAmharic(rd.getFirstNameAmharic());
        dto.setLastNameAmharic(rd.getLastNameAmharic());
        dto.setFirstNameEnglish(rd.getFirstNameEnglish());
        dto.setLastNameEnglish(rd.getLastNameEnglish());
        dto.setEmail(rd.getEmail());
        dto.setPhoneNumber(rd.getPhoneNumber());
        dto.setHasPhoto(rd.getPhotograph() != null && rd.getPhotograph().length > 0);
        dto.setHasNationalId(rd.getNationalIdImage() != null && rd.getNationalIdImage().length > 0);
        dto.setEnabled(rd.getUser().isEnabled());
        return dto;
    }

    @Transactional(readOnly = true)
    public byte[] getPhotographById(Long id) {
        RegistrarDetail rd = registrarDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registrar not found with id: " + id));
        byte[] photo = rd.getPhotograph();
        if (photo == null || photo.length == 0) throw new ResourceNotFoundException("Photograph not available for registrar id: " + id);
        return photo;
    }

    @Transactional(readOnly = true)
    public byte[] getNationalIdById(Long id) {
        RegistrarDetail rd = registrarDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registrar not found with id: " + id));
        byte[] nat = rd.getNationalIdImage();
        if (nat == null || nat.length == 0) throw new ResourceNotFoundException("National ID not available for registrar id: " + id);
        return nat;
    }


    @Transactional
    public RegistrarDetail updateProfileByUser(User user, Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarUpdateRequest req,
                                            MultipartFile nationalIdImage,
                                            MultipartFile photograph) {
        // Validate file sizes
        validateFileSize(nationalIdImage, "National ID Image");
        validateFileSize(photograph, "Photograph");
        
        RegistrarDetail rd = registrarDetailRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Registrar profile not found for user: " + user.getUsername()));

        if (req.getFirstNameAmharic() != null) rd.setFirstNameAmharic(req.getFirstNameAmharic());
        if (req.getLastNameAmharic() != null) rd.setLastNameAmharic(req.getLastNameAmharic());
        if (req.getFirstNameEnglish() != null) rd.setFirstNameEnglish(req.getFirstNameEnglish());
        if (req.getLastNameEnglish() != null) rd.setLastNameEnglish(req.getLastNameEnglish());
        if (req.getEmail() != null) rd.setEmail(req.getEmail());
        if (req.getPhoneNumber() != null) rd.setPhoneNumber(req.getPhoneNumber());

        try {
            if (photograph != null && !photograph.isEmpty()) {
                rd.setPhotograph(photograph.getBytes());
            }
            if (nationalIdImage != null && !nationalIdImage.isEmpty()) {
                rd.setNationalIdImage(nationalIdImage.getBytes());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process uploaded files: " + e.getMessage());
        }

        return registrarDetailRepository.save(rd);
    }

    @Transactional
    public RegistrarDetail updateProfileById(Long id, Henok.example.DeutscheCollageBack_endAPI.DTO.Registrar.RegistrarUpdateRequest req,
                                            MultipartFile nationalIdImage,
                                            MultipartFile photograph) {
        // Validate file sizes
        validateFileSize(nationalIdImage, "National ID Image");
        validateFileSize(photograph, "Photograph");
        
        RegistrarDetail rd = registrarDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registrar not found with id: " + id));

        if (req.getFirstNameAmharic() != null) rd.setFirstNameAmharic(req.getFirstNameAmharic());
        if (req.getLastNameAmharic() != null) rd.setLastNameAmharic(req.getLastNameAmharic());
        if (req.getFirstNameEnglish() != null) rd.setFirstNameEnglish(req.getFirstNameEnglish());
        if (req.getLastNameEnglish() != null) rd.setLastNameEnglish(req.getLastNameEnglish());
        if (req.getEmail() != null) rd.setEmail(req.getEmail());
        if (req.getPhoneNumber() != null) rd.setPhoneNumber(req.getPhoneNumber());

        try {
            if (photograph != null && !photograph.isEmpty()) {
                rd.setPhotograph(photograph.getBytes());
            }
            if (nationalIdImage != null && !nationalIdImage.isEmpty()) {
                rd.setNationalIdImage(nationalIdImage.getBytes());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process uploaded files: " + e.getMessage());
        }

        return registrarDetailRepository.save(rd);
    }

    // Helper method to validate file size
    private void validateFileSize(MultipartFile file, String fieldName) {
        if (file != null && !file.isEmpty()) {
            long maxSize = 2 * 1024 * 1024; // 2MB in bytes
            
            if (file.getSize() > maxSize) {
                throw new IllegalArgumentException(
                    fieldName + " size exceeds maximum allowed size of 2MB. Current size: " + 
                    (file.getSize() / (1024.0 * 1024.0)) + "MB"
                );
            }
            
            // Optional: Add minimum size check if needed
            long minSize = 1024; // 1KB minimum (optional)
            if (file.getSize() < minSize) {
                throw new IllegalArgumentException(
                    fieldName + " size is too small. Minimum size is 1KB."
                );
            }
        }
    }

    @Transactional
    public void deleteRegistrarById(Long id) {
        RegistrarDetail rd = registrarDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registrar not found with id: " + id));

        long enabledCount = registrarDetailRepository.findAll().stream()
                .filter(r -> r.getUser() != null && r.getUser().isEnabled())
                .count();

        if (enabledCount <= 1 && rd.getUser() != null && rd.getUser().isEnabled()) {
            throw new BadRequestException("Cannot delete the last enabled registrar account");
        }

        // Delete registrar detail first to avoid FK constraint on user
        registrarDetailRepository.delete(rd);

        if (rd.getUser() != null) {
            userService.deleteUserById(rd.getUser().getId());
        }
    }

    // Aggregates dashboard data.
    // Why: Central method to fetch and compute all metrics in one go, minimizing DB calls.
    // Handles: Defaults to 0/null for no-data cases; throws RuntimeException for critical errors (e.g., DB failure).
    public RegistrarDashboardDTO getDashboardData() {
        RegistrarDashboardDTO dto = new RegistrarDashboardDTO();

        // Cards - safe counts
        dto.setTotalApplicants(appliedStudentRepository.count());
        dto.setPendingApplicants(appliedStudentRepository.countByApplicationStatus(ApplicationStatus.PENDING));
        dto.setRegisteredStudents(studentDetailsRepository.count());
        dto.setTotalDepartments(departmentRepository.count());

        StudentStatus activeStatus = studentStatusRepository.findByStatusName("ACTIVE")
                .orElseThrow(() -> new ResourceNotFoundException("Student status 'ACTIVE' not found in database"));
        dto.setActiveStudents(studentDetailsRepository.countByStudentRecentStatus(activeStatus));

        dto.setIncompleteDocuments(studentDetailsRepository.countByDocumentStatus(DocumentStatus.INCOMPLETE));

        // === SAFE HANDLING FOR APPLICANT GENDER DISTRIBUTION ===
        // Why: The previous countByGenderGrouped() may fail due to empty results, null gender, or unsupported return type.
        // Solution: Use simple, safe derived query methods + manual mapping.
        try {
            Map<Gender, Long> genderDist = new EnumMap<>(Gender.class);
            for (Gender g : Gender.values()) {
                long count = appliedStudentRepository.countByGender(g);
                genderDist.put(g, count);
            }
            dto.setApplicantGenderDistribution(genderDist);
        } catch (Exception e) {
            // Prevent crash - return empty map if anything goes wrong
            dto.setApplicantGenderDistribution(Collections.emptyMap());
        }

        // === SAFE HANDLING FOR ENROLLMENT BY DEPARTMENT ===
        // Why: countByDepartmentEnrolledGrouped() likely fails for similar reasons (unsupported Map return or join issues).
        // Solution: Use countByDepartmentEnrolled(Department department) derived queries + manual aggregation.
        try {
            // Fetch all departments first (usually small number)
            List<Department> allDepts = departmentRepository.findAll();
            Map<String, Long> enrollmentByDept = new LinkedHashMap<>();
            for (Department dept : allDepts) {
                long count = studentDetailsRepository.countByDepartmentEnrolled(dept);
                if (count > 0) { // Only include departments with students
                    enrollmentByDept.put(dept.getDeptName(), count);
                }
            }
            dto.setEnrollmentByDepartment(enrollmentByDept);
        } catch (Exception e) {
            // Prevent crash
            dto.setEnrollmentByDepartment(Collections.emptyMap());
        }

        // Average scores by department - keep safe empty fallback (already stable from previous fix)
        try {
            List<Object[]> rawAvg = studentCourseScoreRepository.findRawAverageScoresByDepartment();
            Map<String, Double> avgScoresByDept = rawAvg.stream()
                    .filter(row -> row[0] != null && row[1] != null)
                    .collect(Collectors.toMap(
                            row -> (String) row[0],
                            row -> ((Number) row[1]).doubleValue()
                    ));
            dto.setAverageScoresByDepartment(avgScoresByDept);
        } catch (Exception e) {
            dto.setAverageScoresByDepartment(Collections.emptyMap());
        }

        // Academic year trends - safe (in-memory)
        List<AcademicYear> allAcademicYears = academicYearRepository.findAll();
        if (allAcademicYears.isEmpty()) {
            dto.setEnrollmentTrendsByAcademicYear(Collections.emptyList());
        } else {
            Map<String, Long> trendsMap = studentDetailsRepository.findAll()
                    .stream()
                    .filter(sd -> sd.getDateEnrolledGC() != null)
                    .map(sd -> academicYearUtilityService.findAcademicYearByDate(sd.getDateEnrolledGC(), allAcademicYears))
                    .filter(Objects::nonNull)
                    .map(AcademicYear::getAcademicYearGC)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            List<RegistrarDashboardDTO.AcademicYearEnrollment> trends = trendsMap.entrySet().stream()
                    .map(entry -> {
                        RegistrarDashboardDTO.AcademicYearEnrollment aye = new RegistrarDashboardDTO.AcademicYearEnrollment();
                        aye.setAcademicYearGC(entry.getKey());
                        aye.setCount(entry.getValue());
                        return aye;
                    })
                    .sorted(Comparator.comparing(RegistrarDashboardDTO.AcademicYearEnrollment::getAcademicYearGC))
                    .collect(Collectors.toList());

            dto.setEnrollmentTrendsByAcademicYear(trends);
        }

        // Recent applicants - safe with null check
        List<AppliedStudent> recent = appliedStudentRepository.findTop10ByOrderByIdDesc();
        List<RegistrarDashboardDTO.RecentApplicantDTO> recentDTOs = recent.stream()
                .map(as -> {
                    RegistrarDashboardDTO.RecentApplicantDTO rad = new RegistrarDashboardDTO.RecentApplicantDTO();
                    rad.setId(as.getId());
                    rad.setFirstNameENG(as.getFirstNameENG());
                    rad.setApplicationStatus(as.getApplicationStatus());
                    rad.setDepartmentEnrolled(as.getDepartmentEnrolled() != null ? as.getDepartmentEnrolled().getDeptName() : "N/A");
                    return rad;
                })
                .collect(Collectors.toList());
        dto.setRecentApplicants(recentDTOs);

        // Low score alerts: only students with statusName = 'ACTIVE' (case-insensitive)
        // Uses username as studentId (String)
        List<Object[]> rawAlerts = studentCourseScoreRepository.findRawLowAverageActiveStudents(50.0);

        List<RegistrarDashboardDTO.StudentAlertDTO> alerts = rawAlerts.stream()
                .map(row -> {
                    RegistrarDashboardDTO.StudentAlertDTO alert = new RegistrarDashboardDTO.StudentAlertDTO();
                    alert.setStudentId((String) row[0]);           // username (String)
                    alert.setFullName((String) row[1]);
                    alert.setAvgScore(((Number) row[2]).doubleValue());
                    return alert;
                })
                .collect(Collectors.toList());

        dto.setLowScoreAlerts(alerts);

        return dto;
    }

    // Updated Service methods for registrar
    /**
     * Retrieves scores grid for all assessments that have been APPROVED by the dean
     * (deanApproval = ACCEPTED) across ALL departments.
     *
     * Why this method:
     * - Registrars have university-wide access → they can see dean-approved assessments from every department
     * - No department filtering needed
     * - Shows full grid for registrar to review before final release
     *
     * @param authenticatedUser The authenticated registrar
     * @return List of AssessmentScoresResponse (one per teacher course assignment)
     */
    @Transactional(readOnly = true)
    public List<AssessmentScoresResponse> getDeanApprovedAssessmentScoresForRegistrar(User authenticatedUser) {

        // Optional: Verify registrar profile if you have a RegistrarDetails entity
        // If not, just rely on Role.REGISTRAR checked in SecurityConfig
        // Example if you have one:
        // RegistrarDetails registrar = registrarRepository.findByUser(authenticatedUser)
        //         .orElseThrow(() -> new ResourceNotFoundException("Registrar profile not found"));

        // Get ALL assessments where deanApproval = ACCEPTED (no department filter)
        List<Assessment> deanApprovedAssessments = assessmentRepository.findAll().stream()
                .filter(a -> a.getDeanApproval() == AssessmentStatus.ACCEPTED)
                .collect(Collectors.toList());

        if (deanApprovedAssessments.isEmpty()) {
            return Collections.emptyList(); // Clean empty response
        }

        // Group by teacher course assignment
        Map<TeacherCourseAssignment, List<Assessment>> byAssignment = deanApprovedAssessments.stream()
                .collect(Collectors.groupingBy(Assessment::getTeacherCourseAssignment));

        // Build responses for each assignment
        List<AssessmentScoresResponse> responses = new ArrayList<>();
        for (Map.Entry<TeacherCourseAssignment, List<Assessment>> entry : byAssignment.entrySet()) {
            TeacherCourseAssignment tca = entry.getKey();
            List<Assessment> assessments = entry.getValue();

            AssessmentScoresResponse response = new AssessmentScoresResponse();
            response.setTeacherCourseAssignmentId(tca.getId());
            response.setTeacherName(tca.getTeacher().getFirstNameEnglish() + " " + tca.getTeacher().getLastNameEnglish());
            response.setCourseCode(tca.getCourse().getCCode());
            response.setCourseTitle(tca.getCourse().getCTitle());
            response.setBatchClassYearSemester(tca.getBcys().getDisplayName());

            // Build assessment infos – sorted by creation date
            List<AssessmentScoresResponse.AssessmentInfo> assessmentInfos = assessments.stream()
                    .sorted((a1, a2) -> a1.getCreatedAt().compareTo(a2.getCreatedAt()))
                    .map(a -> {
                        AssessmentScoresResponse.AssessmentInfo info = new AssessmentScoresResponse.AssessmentInfo();
                        info.setAssessmentId(a.getAssID());
                        info.setTitle(a.getAssTitle());
                        info.setMaxScore(a.getMaxScore());
                        info.setDueDate(a.getDueDate());
                        info.setStatus(a.getAssStatus());
                        info.setHeadApproval(a.getHeadApproval());
                        info.setRegistrarApproval(a.getRegistrarApproval());
                        return info;
                    })
                    .collect(Collectors.toList());
            response.setAssessments(assessmentInfos);

            // Get enrolled students for this course + BCYS
            List<StudentCourseScore> courseScores = studentCourseScoreRepo.findByCourseAndBatchClassYearSemester(
                    tca.getCourse(), tca.getBcys());

            Set<User> uniqueUsers = courseScores.stream()
                    .map(StudentCourseScore::getStudent)
                    .collect(Collectors.toSet());

            // Build student score views
            List<AssessmentScoresResponse.StudentScoreView> studentViews = uniqueUsers.stream()
                    .map(user -> {
                        StudentDetails student = studentDetailsRepository.findByUser(user).orElse(null);
                        if (student == null) return null;

                        AssessmentScoresResponse.StudentScoreView view = new AssessmentScoresResponse.StudentScoreView();
                        view.setStudentId(student.getId());
                        view.setStudentIdNumber(user.getUsername());
                        view.setFullNameENG(student.getFirstNameENG() + " " + student.getFatherNameENG() + " " + student.getGrandfatherNameENG());
                        view.setFullNameAMH(student.getFirstNameAMH() + " " + student.getFatherNameAMH() + " " + student.getGrandfatherNameAMH());

                        // Scores for each assessment (in same order as assessmentInfos)
                        List<AssessmentScoresResponse.SingleScore> scores = assessments.stream()
                                .map(ass -> {
                                    StudentAssessmentKey key = new StudentAssessmentKey(student.getId(), ass.getAssID());
                                    Double score = studentAssessmentRepository.findById(key)
                                            .map(StudentAssessment::getScore)
                                            .orElse(null);
                                    return new AssessmentScoresResponse.SingleScore(ass.getAssID(), score);
                                })
                                .collect(Collectors.toList());
                        view.setScores(scores);
                        return view;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            response.setStudents(studentViews);
            responses.add(response);
        }

        return responses;
    }

    /**
     * Registrar approves or rejects all assessments in a course assignment.
     *
     * - Checks all are dean-approved first
     * - On APPROVED: sets registrarApproval = ACCEPTED, updates StudentCourseScore totals, notifies Deans "Assessment has been approved"
     * - On REJECTED: sets registrarApproval = REJECTED, reverts deanApproval to PENDING, notifies Deans "Assessment has been rejected"
     *
     * @param authenticatedUser The authenticated registrar
     * @param teacherCourseAssignmentId The assignment to process
     * @param status ACCEPTED or REJECTED
     * @return List of updated assessments
     */
    @Transactional
    public List<Assessment> registrarApproveOrRejectAllAssessments(
            User authenticatedUser,
            Long teacherCourseAssignmentId,
            AssessmentStatus status) {

        // Validate status
        if (status != AssessmentStatus.ACCEPTED && status != AssessmentStatus.REJECTED) {
            throw new IllegalArgumentException("Status must be ACCEPTED or REJECTED");
        }

        // Optional: Verify registrar profile

        // Load TeacherCourseAssignment
        TeacherCourseAssignment tca = assignmentRepository.findById(teacherCourseAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher course assignment not found"));

        // Get all assessments under this assignment
        List<Assessment> assessments = assessmentRepository.findByTeacherCourseAssignment(tca);

        if (assessments.isEmpty()) {
            throw new ResourceNotFoundException("No assessments found for this course assignment");
        }

        // Check all are dean-approved before registrar can act
        for (Assessment a : assessments) {
            if (a.getDeanApproval() != AssessmentStatus.ACCEPTED) {
                throw new IllegalArgumentException(
                        "All assessments must be approved by dean first. Assessment '" + a.getAssTitle() + "' is pending dean approval."
                );
            }
        }

        List<Assessment> updatedAssessments = new ArrayList<>();

        // Step 1: Update registrarApproval on all assessments
        for (Assessment assessment : assessments) {
            assessment.setRegistrarApproval(status);
            if (status == AssessmentStatus.REJECTED) {
                assessment.setDeanApproval(AssessmentStatus.PENDING); // Revert dean approval on reject
            }
            updatedAssessments.add(assessmentRepository.save(assessment));
        }

        // Step 2: If registrar APPROVES → calculate total score per student and update StudentCourseScore
        if (status == AssessmentStatus.ACCEPTED) {
            Course course = tca.getCourse();
            BatchClassYearSemester bcys = tca.getBcys();

            // Get all enrolled students for this course + BCYS
            List<StudentCourseScore> enrollments = studentCourseScoreRepository
                    .findByCourseAndBatchClassYearSemester(course, bcys);

            if (!enrollments.isEmpty()) {
                // Pre-load all student scores for these assessments to avoid N+1
                List<Long> assessmentIds = assessments.stream()
                        .map(Assessment::getAssID)
                        .toList();

                List<StudentAssessment> allScores = studentAssessmentRepository
                        .findByAssessmentInAndStudentIn(
                                assessments,
                                enrollments.stream()
                                        .map(StudentCourseScore::getStudent)
                                        .map(user -> studentDetailsRepository.findByUser(user).orElse(null))
                                        .filter(Objects::nonNull)
                                        .toList()
                        );

                // Map: studentId → total score
                Map<Long, Double> studentTotalScores = new HashMap<>();

                for (StudentAssessment sa : allScores) {
                    Long studentId = sa.getStudent().getId();
                    Double score = sa.getScore() != null ? sa.getScore() : 0.0;
                    studentTotalScores.merge(studentId, score, Double::sum);
                }

                // Update each StudentCourseScore with calculated total
                for (StudentCourseScore scs : enrollments) {
                    Long studentId = studentDetailsRepository.findByUser(scs.getStudent())
                            .map(StudentDetails::getId)
                            .orElse(null);

                    if (studentId != null) {
                        Double total = studentTotalScores.getOrDefault(studentId, 0.0);
                        scs.setScore(total);
                        // scs.setIsReleased(true); // Final score is now officially released
                    }
                    studentCourseScoreRepository.save(scs);
                }
            }
        }

        // Step 3: Notify Deans of registrar's action
        String actionMessage = status == AssessmentStatus.ACCEPTED ? "approved" : "rejected";
        String message = createNotificationMessage(tca.getCourse(), tca, assessments.size(), actionMessage);

        notificationService.createNotification(
                List.of(Role.DEAN),
                null,
                Role.REGISTRAR,
                message
        );

        return updatedAssessments;
    }

    private String createNotificationMessage(Course course, TeacherCourseAssignment tca, int count, String action) {
        String courseCode = course.getCCode();
        String courseTitle = course.getCTitle();
        String teacherName = tca.getTeacher().getFirstNameEnglish() + " " + tca.getTeacher().getLastNameEnglish();

        return String.format(
                "Registrar has %s all %d assessments for course %s (%s) taught by %s. Please review if needed.",
                action,
                count,
                courseCode,
                courseTitle,
                teacherName
        );
    }

}