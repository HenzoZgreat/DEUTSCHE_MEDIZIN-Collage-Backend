package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssignTeacherCoursesRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.UserRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherCourseResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherResponseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Impairment;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Region;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ImpairmentRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.RegionRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.WoredaRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.ZoneRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final UserService userService;
    private final TeacherRepository teacherRepository;
    private final TeacherCourseAssignmentRepository assignmentRepository;

    private final DepartmentRepo departmentRepository;
    private final WoredaRepository woredaRepository;
    private final ZoneRepository zoneRepository;
    private final RegionRepository regionRepository;
    private final ImpairmentRepository impairmentRepository;

    private final CourseRepo courseRepository;
    private final BatchClassYearSemesterRepo bcysRepository;

    private final EntityManager entityManager;

    // ==================== REGISTER TEACHER + ASSIGN COURSES ====================
    @Transactional
    public TeacherDetail registerTeacher(TeacherRegisterRequest request,
                                         MultipartFile photograph,
                                         MultipartFile document) {

        // --- VALIDATION (same as before) ---
        validateRegistrationRequest(request);

        // --- CREATE USER ---
        UserRegisterRequest userReq = new UserRegisterRequest();
        userReq.setUsername(request.getUsername());
        userReq.setPassword(request.getPassword());
        userReq.setRole(Role.TEACHER);
        User user = userService.registerUser(userReq);
        entityManager.flush();

        if (teacherRepository.findByUser(user).isPresent())
            throw new IllegalArgumentException("Teacher detail already exists for user: " + user.getUsername());

        // --- BUILD TEACHER DETAIL ---
        TeacherDetail teacher = buildTeacherDetail(request, user, photograph, document);
        teacher = teacherRepository.save(teacher);
        entityManager.flush();

        // --- ASSIGN COURSES (if any) ---
        if (request.getCourseAssignments() != null && !request.getCourseAssignments().isEmpty()) {
            assignCoursesToTeacher(teacher.getId(), request.getCourseAssignments());
        }

        entityManager.clear();
        return teacher;
    }

    // Reusable method for assigning courses
    private void assignCoursesToTeacher(Long teacherId, List<AssignTeacherCoursesRequest> requests) {
        TeacherDetail teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalStateException("Teacher not found after save"));

        for (AssignTeacherCoursesRequest req : requests) {
            Course course = courseRepository.findById(req.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("Course not found: " + req.getCourseId()));

            BatchClassYearSemester bcys = bcysRepository.findById(req.getBcysId())
                    .orElseThrow(() -> new IllegalArgumentException("BCYS not found: " + req.getBcysId()));

            if (assignmentRepository.existsByTeacherAndCourseAndBcys(teacher, course, bcys)) {
                throw new IllegalArgumentException(
                        "Teacher already assigned to course " + course.getCTitle() +
                                " in " + bcys.getDisplayName());
            }

            TeacherCourseAssignment assignment = new TeacherCourseAssignment();
            assignment.setTeacher(teacher);
            assignment.setCourse(course);
            assignment.setBcys(bcys);
            assignment.setAssignedAt(LocalDateTime.now());

            assignmentRepository.save(assignment);
        }
    }

    // Extracted validation
    private void validateRegistrationRequest(TeacherRegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty())
            throw new IllegalArgumentException("Username cannot be empty");
        if (request.getPassword() == null || request.getPassword().isEmpty())
            throw new IllegalArgumentException("Password cannot be empty");
        if (request.getFirstNameAmharic() == null || request.getFirstNameAmharic().isEmpty())
            throw new IllegalArgumentException("First name in Amharic cannot be empty");
        if (request.getLastNameAmharic() == null || request.getLastNameAmharic().isEmpty())
            throw new IllegalArgumentException("Last name in Amharic cannot be empty");
        if (request.getFirstNameEnglish() == null || request.getFirstNameEnglish().isEmpty())
            throw new IllegalArgumentException("First name in English cannot be empty");
        if (request.getLastNameEnglish() == null || request.getLastNameEnglish().isEmpty())
            throw new IllegalArgumentException("Last name in English cannot be empty");
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty())
            throw new IllegalArgumentException("Phone number cannot be empty");
        if (request.getEmail() == null || request.getEmail().isEmpty())
            throw new IllegalArgumentException("Email cannot be empty");
        if (request.getDepartmentId() == null)
            throw new IllegalArgumentException("Department is required");

        if (teacherRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent())
            throw new IllegalArgumentException("Phone number already exists");
    }

    // Extracted teacher building
    private TeacherDetail buildTeacherDetail(TeacherRegisterRequest request, User user,
                                             MultipartFile photograph, MultipartFile document) {
        TeacherDetail teacher = new TeacherDetail();
        teacher.setUser(user);

        teacher.setFirstNameAmharic(request.getFirstNameAmharic());
        teacher.setLastNameAmharic(request.getLastNameAmharic());
        teacher.setFirstNameEnglish(request.getFirstNameEnglish());
        teacher.setLastNameEnglish(request.getLastNameEnglish());
        teacher.setGender(request.getGender());
        teacher.setDateOfBirthGC(request.getDateOfBirthGC());
        teacher.setDateOfBirthEC(request.getDateOfBirthEC());
        teacher.setPhoneNumber(request.getPhoneNumber());
        teacher.setEmail(request.getEmail());

        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid department ID"));
        teacher.setDepartment(dept);

        teacher.setHireDateGC(request.getHireDateGC());
        teacher.setHireDateEC(request.getHireDateEC());
        teacher.setTitle(request.getTitle());
        teacher.setYearsOfExperience(request.getYearsOfExperience());

        if (request.getImpairmentCode() != null && !request.getImpairmentCode().isEmpty()) {
            Impairment imp = impairmentRepository.findById(request.getImpairmentCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid impairment code"));
            teacher.setImpairment(imp);
        }
        teacher.setMaritalStatus(request.getMaritalStatus());

        if (request.getCurrentAddressWoredaCode() != null && !request.getCurrentAddressWoredaCode().isEmpty()) {
            Woreda w = woredaRepository.findById(request.getCurrentAddressWoredaCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid woreda code"));
            teacher.setCurrentAddressWoreda(w);
        }
        if (request.getCurrentAddressZoneCode() != null && !request.getCurrentAddressZoneCode().isEmpty()) {
            Zone z = zoneRepository.findById(request.getCurrentAddressZoneCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid zone code"));
            teacher.setCurrentAddressZone(z);
        }
        if (request.getCurrentAddressRegionCode() != null && !request.getCurrentAddressRegionCode().isEmpty()) {
            Region r = regionRepository.findById(request.getCurrentAddressRegionCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid region code"));
            teacher.setCurrentAddressRegion(r);
        }

        try {
            if (photograph != null && !photograph.isEmpty())
                teacher.setPhotograph(photograph.getBytes());
            if (document != null && !document.isEmpty())
                teacher.setDocuments(document.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process files: " + e.getMessage());
        }

        return teacher;
    }

    // ==================== GET ALL ====================
    @Transactional(readOnly = true)
    public List<TeacherResponseDTO> getAllTeachers() {
        return teacherRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    // ==================== GET BY ID ====================
    @Transactional(readOnly = true)
    public TeacherResponseDTO getTeacherById(Long id) {
        TeacherDetail t = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + id));
        return toResponseDto(t);
    }

    // ==================== UPDATE (partial) ====================
    @Transactional
    public TeacherDetail updateTeacher(Long id, TeacherRegisterRequest request,
                                       MultipartFile photograph, MultipartFile document) {
        TeacherDetail teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + id));

        if (request.getUsername() != null && !request.getUsername().equals(teacher.getUser().getUsername()))
            throw new IllegalArgumentException("Cannot change username");

        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(teacher.getPhoneNumber())) {
            if (teacherRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent())
                throw new IllegalArgumentException("Phone number already in use");
        }

        ofNullable(request.getFirstNameAmharic()).filter(s -> !s.isEmpty()).ifPresent(teacher::setFirstNameAmharic);
        ofNullable(request.getLastNameAmharic()).filter(s -> !s.isEmpty()).ifPresent(teacher::setLastNameAmharic);
        ofNullable(request.getFirstNameEnglish()).filter(s -> !s.isEmpty()).ifPresent(teacher::setFirstNameEnglish);
        ofNullable(request.getLastNameEnglish()).filter(s -> !s.isEmpty()).ifPresent(teacher::setLastNameEnglish);
        ofNullable(request.getGender()).ifPresent(teacher::setGender);
        ofNullable(request.getDateOfBirthGC()).ifPresent(teacher::setDateOfBirthGC);
        ofNullable(request.getDateOfBirthEC()).filter(s -> !s.isEmpty()).ifPresent(teacher::setDateOfBirthEC);
        ofNullable(request.getPhoneNumber()).filter(s -> !s.isEmpty()).ifPresent(teacher::setPhoneNumber);
        ofNullable(request.getEmail()).ifPresent(e -> teacher.setEmail(e.isEmpty() ? null : e));

        ofNullable(request.getDepartmentId()).ifPresent(deptId -> {
            Department d = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID"));
            teacher.setDepartment(d);
        });

        ofNullable(request.getHireDateGC()).ifPresent(teacher::setHireDateGC);
        ofNullable(request.getHireDateEC()).filter(s -> !s.isEmpty()).ifPresent(teacher::setHireDateEC);
        ofNullable(request.getTitle()).ifPresent(t -> teacher.setTitle(t.isEmpty() ? null : t));
        ofNullable(request.getYearsOfExperience()).ifPresent(teacher::setYearsOfExperience);

        ofNullable(request.getImpairmentCode()).ifPresent(code -> {
            if (code.isEmpty()) teacher.setImpairment(null);
            else {
                Impairment imp = impairmentRepository.findById(code)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid impairment code"));
                teacher.setImpairment(imp);
            }
        });

        ofNullable(request.getMaritalStatus()).ifPresent(teacher::setMaritalStatus);

        ofNullable(request.getCurrentAddressWoredaCode()).ifPresent(code -> {
            if (code.isEmpty()) teacher.setCurrentAddressWoreda(null);
            else {
                Woreda w = woredaRepository.findById(code)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid woreda code"));
                teacher.setCurrentAddressWoreda(w);
            }
        });
        ofNullable(request.getCurrentAddressZoneCode()).ifPresent(code -> {
            if (code.isEmpty()) teacher.setCurrentAddressZone(null);
            else {
                Zone z = zoneRepository.findById(code)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid zone code"));
                teacher.setCurrentAddressZone(z);
            }
        });
        ofNullable(request.getCurrentAddressRegionCode()).ifPresent(code -> {
            if (code.isEmpty()) teacher.setCurrentAddressRegion(null);
            else {
                Region r = regionRepository.findById(code)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid region code"));
                teacher.setCurrentAddressRegion(r);
            }
        });

        try {
            if (photograph != null && !photograph.isEmpty())
                teacher.setPhotograph(photograph.getBytes());
            if (document != null && !document.isEmpty())
                teacher.setDocuments(document.getBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process files: " + e.getMessage());
        }

        return teacherRepository.save(teacher);
    }

    // ==================== DELETE ====================
    @Transactional
    public void deleteTeacher(Long id) {
        TeacherDetail teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + id));

        assignmentRepository.findByTeacher(teacher).forEach(assignmentRepository::delete);
//        userService.deleteUser(teacher.getUser().getId());
        teacherRepository.delete(teacher);
    }

    // ==================== MAPPER ====================
    public TeacherResponseDTO toResponseDto(TeacherDetail t) {
        TeacherResponseDTO dto = new TeacherResponseDTO();
        dto.setId(t.getId());  // ← now the PK
        dto.setUsername(t.getUser().getUsername());

        dto.setFirstNameAmharic(t.getFirstNameAmharic());
        dto.setLastNameAmharic(t.getLastNameAmharic());
        dto.setFirstNameEnglish(t.getFirstNameEnglish());
        dto.setLastNameEnglish(t.getLastNameEnglish());

        dto.setGender(t.getGender());
        dto.setDateOfBirthGC(t.getDateOfBirthGC());
        dto.setDateOfBirthEC(t.getDateOfBirthEC());

        dto.setPhoneNumber(t.getPhoneNumber());
        dto.setEmail(t.getEmail());

        dto.setDepartmentName(t.getDepartment().getDeptName());
        dto.setHireDateGC(t.getHireDateGC());
        dto.setHireDateEC(t.getHireDateEC());
        dto.setTitle(t.getTitle());
        dto.setYearsOfExperience(t.getYearsOfExperience());

        dto.setImpairment(t.getImpairment() != null ? t.getImpairment().getImpairment() : null);
        dto.setMaritalStatus(t.getMaritalStatus());

        StringBuilder addr = new StringBuilder();
        if (t.getCurrentAddressWoreda() != null) addr.append(t.getCurrentAddressWoreda().getWoreda());
        if (t.getCurrentAddressZone() != null) addr.append(", ").append(t.getCurrentAddressZone().getZone());
        if (t.getCurrentAddressRegion() != null) addr.append(", ").append(t.getCurrentAddressRegion().getRegion());
        dto.setCurrentAddress(addr.length() > 0 ? addr.toString() : null);

        if (t.getPhotograph() != null && t.getPhotograph().length > 0)
            dto.setPhotographBase64(Base64.getEncoder().encodeToString(t.getPhotograph()));

        return dto;
    }

    // ====================  ====================
    // Inside TeacherService class

    @Transactional(readOnly = true)
    public List<TeacherCourseResponse> getTeacherCourses(Long teacherId) {
        TeacherDetail teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + teacherId));

        List<TeacherCourseAssignment> assignments = assignmentRepository.findByTeacher(teacher);

        return assignments.stream()
                .map(assignment -> {
                    Course course = assignment.getCourse();
                    BatchClassYearSemester bcys = assignment.getBcys();

                    // Count all students in this BCYS (for now)
                    Long studentCount = assignmentRepository.countEnrolledStudents(course, bcys);

                    return new TeacherCourseResponse(
                            course.getCTitle(),
                            course.getCCode(),
                            course.getTheoryHrs() + course.getLabHrs(),
                            studentCount,
                            bcys.getDisplayName()
                    );
                })
                .sorted(Comparator.comparing(TeacherCourseResponse::getCourseCode))
                .toList();
    }
}