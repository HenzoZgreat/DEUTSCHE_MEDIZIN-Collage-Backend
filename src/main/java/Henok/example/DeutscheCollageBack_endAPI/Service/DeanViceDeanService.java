package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean.*;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.DeanViceDeanRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.UserRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.DeanViceDeanDetails;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Region;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Woreda;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.Zone;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DeanViceDeanDetailsRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.*;
import Henok.example.DeutscheCollageBack_endAPI.Repository.StudentDetailsRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

// DeanViceDeanService
// Handles registration logic for Dean and Vice-Dean.
// Keeps business logic separate from controller.
// Uses transactional behavior to ensure atomicity when creating User + DeanViceDeanDetails.
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DeanViceDeanService {

    private final UserService userService; // existing service for user registration
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final DeanViceDeanDetailsRepository deanViceDeanDetailsRepository;
    private final StudentDetailsRepository studentDetailsRepository;
    private final DepartmentRepo departmentRepository;
    private final ProgramModalityRepository programModalityRepository;
    private final ProgramLevelRepository programLevelRepository;
    private final WoredaRepository woredaRepository;
    private final ZoneRepository zoneRepository;
    private final RegionRepository regionRepository;
    private final PasswordEncoder passwordEncoder; // injected via Spring Security config

    // Common registration logic used by both Dean and Vice-Dean controllers
    // Why: Avoid code duplication while allowing different roles to be set externally.
    public Long registerDeanViceDean(DeanViceDeanRegisterRequest request, Role role,
                                     MultipartFile photograph, MultipartFile document) {

        // Step 1: Register the User with the specified role (DEAN or VICE_DEAN)
        UserRegisterRequest userReq = new UserRegisterRequest();
        userReq.setUsername(request.getUsername());
        userReq.setPassword(request.getPassword());
        userReq.setRole(role);

        User savedUser = userService.registerUser(userReq);

        // Step 2: Fetch location entities (with validation)
        Woreda woreda = woredaRepository.findById(request.getResidenceWoredaCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid residence Woreda code"));

        Zone zone = zoneRepository.findById(request.getResidenceZoneCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid residence Zone code"));

        Region region = regionRepository.findById(request.getResidenceRegionCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid residence Region code"));

        // Step 3: Convert MultipartFile to byte[] (handle nulls gracefully)
        byte[] photoBytes = null;
        if (photograph != null && !photograph.isEmpty()) {
            try {
                photoBytes = photograph.getBytes();
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to process photograph");
            }
        }

        byte[] documentBytes = null;
        if (document != null && !document.isEmpty()) {
            try {
                documentBytes = document.getBytes();
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to process document");
            }
        }

        // Step 4: Build and save DeanViceDeanDetails
        DeanViceDeanDetails details = DeanViceDeanDetails.builder()
                .user(savedUser)
                .firstNameAMH(request.getFirstNameAMH())
                .firstNameENG(request.getFirstNameENG())
                .fatherNameAMH(request.getFatherNameAMH())
                .fatherNameENG(request.getFatherNameENG())
                .grandfatherNameAMH(request.getGrandfatherNameAMH())
                .grandfatherNameENG(request.getGrandfatherNameENG())
                .gender(request.getGender())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .residenceWoreda(woreda)
                .residenceZone(zone)
                .residenceRegion(region)
                .hiredDateGC(request.getHiredDateGC())
                .title(request.getTitle())
                .remarks(request.getRemarks())
                .photo(photoBytes)
                .documents(documentBytes)
                .active(true)
                .build();

        DeanViceDeanDetails saved = deanViceDeanDetailsRepository.save(details);

        List<Role> targetRoles = Arrays.asList(
                Role.DEPARTMENT_HEAD,
                Role.REGISTRAR,
                Role.FINANCIAL_STAFF,
                Role.VICE_DEAN,
                Role.DEAN,
                Role.GENERAL_MANAGER
        );

        String message = saved.getUser().getRole().toString();
        if(saved.getUser().getRole() == Role.VICE_DEAN){
            message = "A new Vice-Dean has been registered: "
                    + saved.getFirstNameENG() + " "
                    + saved.getFatherNameENG() + " "
                    + saved.getGrandfatherNameENG();
        } else if (saved.getUser().getRole() == Role.DEAN) {
            message = "A new Vice-Dean has been registered: "
                    + saved.getFirstNameENG() + " "
                    + saved.getFatherNameENG() + " "
                    + saved.getGrandfatherNameENG();
        }
        notificationService.createNotification(
                targetRoles,    // roles to notify
                null,           // no single user
                Role.DEPARTMENT_HEAD,  // sender role (or Role.REGISTRAR if admin does this)
                message
        );

        return saved.getId(); // return the details ID for confirmation
    }


    // Fetch all active by role
    // Why: Retrieves list of active DeanViceDeanDetails filtered by User role.
    // Maps to DTO, computes hasDocument, fetches location names.
    @Transactional(readOnly = true)
    public List<DeanViceDeanListDTO> getAllActiveByRole(Role role) {
        List<DeanViceDeanDetails> detailsList = deanViceDeanDetailsRepository.findByUserRoleAndActiveTrue(role);
        return detailsList.stream().map(this::mapToListDTO).collect(Collectors.toList());
    }

    // Add these methods to DeanViceDeanService

    // Retrieve detailed information for a Dean/Vice-Dean by details ID.
// Throws exception if not found or role mismatch.
// Maps entity to DTO, computes hasPhoto/hasDocument flags.
    @Transactional(readOnly = true)
    public DeanViceDeanDetailDTO getDetailById(Long id, Role expectedRole) {
        DeanViceDeanDetails details = deanViceDeanDetailsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dean/Vice-Dean not found with ID: " + id));

        if (details.getUser().getRole() != expectedRole) {
            throw new IllegalArgumentException("Role mismatch: expected " + expectedRole);
        }

        if (!details.isActive()) {
            throw new IllegalArgumentException("This Dean/Vice-Dean is no longer active");
        }

        DeanViceDeanDetailDTO dto = new DeanViceDeanDetailDTO();
        dto.setId(details.getId());
        dto.setUserId(details.getUser().getId());
        dto.setUsername(details.getUser().getUsername());
        dto.setFirstNameAMH(details.getFirstNameAMH());
        dto.setFirstNameENG(details.getFirstNameENG());
        dto.setFatherNameAMH(details.getFatherNameAMH());
        dto.setFatherNameENG(details.getFatherNameENG());
        dto.setGrandfatherNameAMH(details.getGrandfatherNameAMH());
        dto.setGrandfatherNameENG(details.getGrandfatherNameENG());
        dto.setGender(details.getGender());
        dto.setEmail(details.getEmail());
        dto.setPhoneNumber(details.getPhoneNumber());

        // Residence details
        dto.setResidenceRegion(details.getResidenceRegion().getRegion());
        dto.setResidenceRegionCode(details.getResidenceRegion().getRegionCode());
        dto.setResidenceZone(details.getResidenceZone().getZone());
        dto.setResidenceZoneCode(details.getResidenceZone().getZoneCode());
        dto.setResidenceWoreda(details.getResidenceWoreda().getWoreda());
        dto.setResidenceWoredaCode(details.getResidenceWoreda().getWoredaCode());

        dto.setHiredDateGC(details.getHiredDateGC());
        dto.setTitle(details.getTitle());
        dto.setRemarks(details.getRemarks());
        dto.setHasPhoto(details.getPhoto() != null && details.getPhoto().length > 0);
        dto.setHasDocument(details.getDocuments() != null && details.getDocuments().length > 0);
        dto.setRole(details.getUser().getRole());
        dto.setActive(details.isActive());

        return dto;
    }

    // Helper mapper for ListDTO
    private DeanViceDeanListDTO mapToListDTO(DeanViceDeanDetails details) {
        DeanViceDeanListDTO dto = new DeanViceDeanListDTO();
        dto.setId(details.getId());
        dto.setUserId(details.getUser().getId());
        dto.setUsername(details.getUser().getUsername());
        dto.setFirstNameAMH(details.getFirstNameAMH());
        dto.setFirstNameENG(details.getFirstNameENG());
        dto.setFatherNameAMH(details.getFatherNameAMH());
        dto.setFatherNameENG(details.getFatherNameENG());
        dto.setGrandfatherNameAMH(details.getGrandfatherNameAMH());
        dto.setGrandfatherNameENG(details.getGrandfatherNameENG());
        dto.setGender(details.getGender());
        dto.setEmail(details.getEmail());
        dto.setPhoneNumber(details.getPhoneNumber());
        dto.setResidenceRegion(details.getResidenceRegion().getRegion());
        dto.setResidenceZone(details.getResidenceZone().getZone());
        dto.setResidenceWoreda(details.getResidenceWoreda().getWoreda());
        dto.setHiredDateGC(details.getHiredDateGC());
        dto.setTitle(details.getTitle());
        dto.setRemarks(details.getRemarks());
        dto.setHasDocument(details.getDocuments() != null && details.getDocuments().length > 0);
        dto.setPhoto(details.getPhoto());
        dto.setRole(details.getUser().getRole());
        return dto;
    }

    // Update by ID
    // Why: Partial update - only apply non-null/non-empty fields.
    // Cannot update username or documents (as per requirement).
    // Handles photo update separately.
    // Validates and updates locations if codes provided.
    public void updateDeanViceDean(Long id, DeanViceDeanUpdateRequest request, MultipartFile photo, MultipartFile document, Role expectedRole) {
        DeanViceDeanDetails details = deanViceDeanDetailsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dean/Vice-Dean not found with ID: " + id));

        if (details.getUser().getRole() != expectedRole) {
            throw new IllegalArgumentException("Role mismatch for update");
        }

        User user = details.getUser();

        // Update simple fields if not empty/null
        if (request.getFirstNameAMH() != null && !request.getFirstNameAMH().isEmpty()) {
            details.setFirstNameAMH(request.getFirstNameAMH());
        }
        if (request.getFirstNameENG() != null && !request.getFirstNameENG().isEmpty()) {
            details.setFirstNameENG(request.getFirstNameENG());
        }
        if (request.getFatherNameAMH() != null && !request.getFatherNameAMH().isEmpty()) {
            details.setFatherNameAMH(request.getFatherNameAMH());
        }
        if (request.getFatherNameENG() != null && !request.getFatherNameENG().isEmpty()) {
            details.setFatherNameENG(request.getFatherNameENG());
        }
        if (request.getGrandfatherNameAMH() != null && !request.getGrandfatherNameAMH().isEmpty()) {
            details.setGrandfatherNameAMH(request.getGrandfatherNameAMH());
        }
        if (request.getGrandfatherNameENG() != null && !request.getGrandfatherNameENG().isEmpty()) {
            details.setGrandfatherNameENG(request.getGrandfatherNameENG());
        }
        if (request.getGender() != null) {
            details.setGender(request.getGender());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            details.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            if (deanViceDeanDetailsRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), id)) {
                throw new IllegalArgumentException("Phone number already in use");
            }
            details.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getHiredDateGC() != null) {
            details.setHiredDateGC(request.getHiredDateGC());
        }
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            details.setTitle(request.getTitle());
        }
        if (request.getRemarks() != null && !request.getRemarks().isEmpty()) {
            details.setRemarks(request.getRemarks());
        }

        // Update locations if codes provided
        if (request.getResidenceWoredaCode() != null && !request.getResidenceWoredaCode().isEmpty()) {
            Woreda woreda = woredaRepository.findById(request.getResidenceWoredaCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid residence Woreda code"));
            details.setResidenceWoreda(woreda);
        }
        if (request.getResidenceZoneCode() != null && !request.getResidenceZoneCode().isEmpty()) {
            Zone zone = zoneRepository.findById(request.getResidenceZoneCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid residence Zone code"));
            details.setResidenceZone(zone);
        }
        if (request.getResidenceRegionCode() != null && !request.getResidenceRegionCode().isEmpty()) {
            Region region = regionRepository.findById(request.getResidenceRegionCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid residence Region code"));
            details.setResidenceRegion(region);
        }

        // Update photo if provided
        if (photo != null && !photo.isEmpty()) {
            try {
                details.setPhoto(photo.getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to process photograph");
            }
        }

        if (document != null && !document.isEmpty()) {
            try {
                details.setDocuments(document.getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to process document file");
            }
        }

        // Save updated user and details
        userRepository.save(user);
        deanViceDeanDetailsRepository.save(details);
    }
    
    // Self-update method
    // - Based on Authenticated User
    // - Restricted: Documents, HiredDate, UserInfo
    // - Allowed: Names, Gender, Phone, Email, Title, Remarks, Address, Photo
    public void updateSelf(User user, DeanViceDeanUpdateRequest request, MultipartFile photo) {
        DeanViceDeanDetails details = deanViceDeanDetailsRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user"));

        // Update simple fields if not empty/null
        if (request.getFirstNameAMH() != null && !request.getFirstNameAMH().isEmpty()) {
            details.setFirstNameAMH(request.getFirstNameAMH());
        }
        if (request.getFirstNameENG() != null && !request.getFirstNameENG().isEmpty()) {
            details.setFirstNameENG(request.getFirstNameENG());
        }
        if (request.getFatherNameAMH() != null && !request.getFatherNameAMH().isEmpty()) {
            details.setFatherNameAMH(request.getFatherNameAMH());
        }
        if (request.getFatherNameENG() != null && !request.getFatherNameENG().isEmpty()) {
            details.setFatherNameENG(request.getFatherNameENG());
        }
        if (request.getGrandfatherNameAMH() != null && !request.getGrandfatherNameAMH().isEmpty()) {
            details.setGrandfatherNameAMH(request.getGrandfatherNameAMH());
        }
        if (request.getGrandfatherNameENG() != null && !request.getGrandfatherNameENG().isEmpty()) {
            details.setGrandfatherNameENG(request.getGrandfatherNameENG());
        }
        if (request.getGender() != null) {
            details.setGender(request.getGender());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            details.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            // Check uniqueness if changed
            if (!request.getPhoneNumber().equals(details.getPhoneNumber())) {
                if (deanViceDeanDetailsRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber(), details.getId())) {
                    throw new IllegalArgumentException("Phone number already in use");
                }
                details.setPhoneNumber(request.getPhoneNumber());
            }
        }
        
        // HiredDate IS RESTRICTED
        
        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            details.setTitle(request.getTitle());
        }
        if (request.getRemarks() != null && !request.getRemarks().isEmpty()) {
            details.setRemarks(request.getRemarks());
        }

        // Update locations if codes provided
        if (request.getResidenceWoredaCode() != null && !request.getResidenceWoredaCode().isEmpty()) {
            Woreda woreda = woredaRepository.findById(request.getResidenceWoredaCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid residence Woreda code"));
            details.setResidenceWoreda(woreda);
        }
        if (request.getResidenceZoneCode() != null && !request.getResidenceZoneCode().isEmpty()) {
            Zone zone = zoneRepository.findById(request.getResidenceZoneCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid residence Zone code"));
            details.setResidenceZone(zone);
        }
        if (request.getResidenceRegionCode() != null && !request.getResidenceRegionCode().isEmpty()) {
            Region region = regionRepository.findById(request.getResidenceRegionCode())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid residence Region code"));
            details.setResidenceRegion(region);
        }

        // Update photo if provided 
        // Documents IS RESTRICTED (so we don't accept 'document' param here)
        if (photo != null && !photo.isEmpty()) {
            try {
                details.setPhoto(photo.getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to process photograph");
            }
        }

        // Save updated user and details
        deanViceDeanDetailsRepository.save(details);
    }

    public ResponseEntity<?> getDocumentByIdAndRole(Long id, Role expectedRole) {
        try {
            DeanViceDeanDetails details = deanViceDeanDetailsRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Dean/Vice-Dean not found with ID: " + id));

            if (details.getUser().getRole() != expectedRole) {
                throw new IllegalArgumentException("Role mismatch");
            }

            byte[] document = details.getDocuments();
            if (document == null || document.length == 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Document not found for ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            // Optional: set proper content type if you store filename/mime, here default octet-stream
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document_" + id + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(document);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to retrieve document");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Get profile for authenticated user
    // Why: Fetches details for the given user, maps to ProfileDTO without remarks.
    @Transactional(readOnly = true)
    public DeanViceDeanProfileDTO getProfile(User user) {
        DeanViceDeanDetails details = deanViceDeanDetailsRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found for user"));

        return mapToProfileDTO(details);
    }

    // Helper mapper for ProfileDTO
    private DeanViceDeanProfileDTO mapToProfileDTO(DeanViceDeanDetails details) {
        DeanViceDeanProfileDTO dto = new DeanViceDeanProfileDTO();
        dto.setId(details.getId());
        dto.setUsername(details.getUser().getUsername());
        dto.setFirstNameAMH(details.getFirstNameAMH());
        dto.setFirstNameENG(details.getFirstNameENG());
        dto.setFatherNameAMH(details.getFatherNameAMH());
        dto.setFatherNameENG(details.getFatherNameENG());
        dto.setGrandfatherNameAMH(details.getGrandfatherNameAMH());
        dto.setGrandfatherNameENG(details.getGrandfatherNameENG());
        dto.setGender(details.getGender());
        dto.setEmail(details.getEmail());
        dto.setPhoneNumber(details.getPhoneNumber());
        dto.setResidenceRegion(details.getResidenceRegion().getRegion());
        dto.setResidenceRegionCode(details.getResidenceRegion().getRegionCode());
        dto.setResidenceZone(details.getResidenceZone().getZone());
        dto.setResidenceZoneCode(details.getResidenceZone().getZoneCode());
        dto.setResidenceWoreda(details.getResidenceWoreda().getWoreda());
        dto.setResidenceWoredaCode(details.getResidenceWoreda().getWoredaCode());
        dto.setHiredDateGC(details.getHiredDateGC());
        dto.setTitle(details.getTitle());
        dto.setPhoto(details.getPhoto());
        dto.setRole(details.getUser().getRole());
        // Remarks excluded as per requirement
        return dto;
    }


    // Helper to retrieve User from saved DeanViceDeanDetails (needed for userId in response)
    // Why: We only return detailsId from registration, but response needs User.id
    @Transactional(readOnly = true)
    public User getUserByDetailsId(Long detailsId) {
        return deanViceDeanDetailsRepository.findById(detailsId)
                .orElseThrow(() -> new IllegalStateException("Dean/Vice-Dean details not found"))
                .getUser();
    }


    // Fetch dashboard data for Dean.
    // Why: Aggregates multiple queries into a single method for efficiency.
    // Uses custom repository queries for groupings/counts to avoid loading all entities.
    @Transactional(readOnly = true)
    public DeanDashboardDTO getDashboardData() {
        DeanDashboardDTO dto = new DeanDashboardDTO();

        // Total students: Simple count from StudentDetails.
        dto.setTotalStudents(studentDetailsRepository.count());

        // Total departments: Count from Department.
        dto.setTotalDepartments(departmentRepository.count());

        // Total department heads: Count users with ROLE_DEPARTMENT_HEAD.
        dto.setTotalDepartmentHeads(userRepository.countByRole(Role.DEPARTMENT_HEAD));

        // Active modalities: Count all ProgramModality (no active flag, assume all active).
        // If needed, filter if an active field is added later.
        dto.setActiveModalities(programModalityRepository.count());

        // Active levels: Count ProgramLevel where active = true.
        dto.setActiveLevels(programLevelRepository.countByActiveTrue());

        // Students by level: Group StudentDetails by programModality.programLevel.name.
        // Use custom query to get Map<levelName, count>.
        dto.setStudentsByLevel(studentDetailsRepository.countStudentsByProgramLevel());

        // Students by modality: Group by programModality.modality.
        dto.setStudentsByModality(studentDetailsRepository.countStudentsByModality());

        // Students per department: Group by departmentEnrolled.deptName.
        dto.setStudentsPerDepartment(studentDetailsRepository.countStudentsPerDepartment());

        // Enrollment trend: Group by academicYear.yearCode (assuming AcademicYear has yearCode).
        // If academicYear is null for some, fallback to year from dateEnrolledGC.
        dto.setEnrollmentTrend(studentDetailsRepository.getEnrollmentTrendByAcademicYear());

        // Gender distribution: Group by gender.
        dto.setGenderDistribution(studentDetailsRepository.countByGender());

        // Additional: Total teachers.
        dto.setTotalTeachers(userRepository.countByRole(Role.TEACHER));

        // Average Grade 12 result: Average of grade12Result (ignore nulls).
        dto.setAverageGrade12Result(studentDetailsRepository.getAverageGrade12Result().orElse(0.0));

        // Exit exam pass rate: (count where isStudentPassExitExam = true) / totalStudents * 100.
        long passCount = studentDetailsRepository.countByIsStudentPassExitExamTrue();
//        dto.setExitExamPassRate(dto.getTotalStudents > 0 ? (double) passCount / dto.getTotalStudents * 100 : 0.0);

        return dto;
    }

    @Transactional(readOnly = true)
    public byte[] getDeanViceDeanPhoto(Long id, Role expectedRole) {
        DeanViceDeanDetails details = deanViceDeanDetailsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dean/Vice-Dean not found with ID: " + id));
        if (details.getUser().getRole() != expectedRole) {
            throw new IllegalArgumentException("Role mismatch");
        }
        return details.getPhoto();
    }

    @Transactional(readOnly = true)
    public byte[] getDeanViceDeanDocument(Long id, Role expectedRole) {
        DeanViceDeanDetails details = deanViceDeanDetailsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dean/Vice-Dean not found with ID: " + id));
        if (details.getUser().getRole() != expectedRole) {
            throw new IllegalArgumentException("Role mismatch");
        }
        return details.getDocuments();
    }
}