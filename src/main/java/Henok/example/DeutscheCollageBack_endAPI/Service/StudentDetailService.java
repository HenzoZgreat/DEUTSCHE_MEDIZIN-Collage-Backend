package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.StudentRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.UserRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Students.StudentDetailsDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Students.StudentUpdateDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.Students.StudentListDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.*;
import Henok.example.DeutscheCollageBack_endAPI.Enums.DocumentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentDetailService {

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private WoredaRepository woredaRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ImpairmentRepository impairmentRepository;

    @Autowired
    private SchoolBackgroundRepository schoolBackgroundRepository;

    @Autowired
    private BatchClassYearSemesterRepo batchClassYearSemesterRepository;

    @Autowired
    private StudentStatusRepo studentStatusRepository;

    @Autowired
    private DepartmentRepo departmentRepository;

    @Autowired
    private ProgramModalityRepository programModalityRepository;

    @Autowired
    private NotificationService notificationService;

    // Registers a new student with the provided details and files
    // Why: Handles student registration with multipart form data, validates inputs, and ensures data integrity
    @Transactional(rollbackFor = Exception.class)
    public StudentDetails registerStudent(StudentRegisterRequest request, MultipartFile studentPhoto, MultipartFile document) {
        // Validate required fields
        validateRegistrationRequest(request);

        // Check for duplicate phone number
        if (studentDetailsRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already in use");
        }
        // Check for duplicate exitExamUserID if provided
        if (request.getExitExamUserID() != null && !request.getExitExamUserID().isEmpty()
                && studentDetailsRepository.existsByExitExamUserID(request.getExitExamUserID())) {
            throw new IllegalArgumentException("Exit exam user ID already in use");
        }

        // Validate file sizes
        if (studentPhoto != null && !studentPhoto.isEmpty() && studentPhoto.getSize() > 2_000_000) { // 2MB limit
            throw new IllegalArgumentException("Student photo size exceeds 2MB limit");
        }
        if (document != null && !document.isEmpty() && document.getSize() > 10_000_000) { // 10MB limit
            throw new IllegalArgumentException("Document size exceeds 10MB limit");
        }

        // Register user with STUDENT role
        UserRegisterRequest userRequest = new UserRegisterRequest();
        userRequest.setUsername(request.getUsername());
        userRequest.setPassword(request.getPassword());
        userRequest.setRole(Role.STUDENT);
        User user;
        try {
            user = userService.registerUser(userRequest);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("User registration failed: " + e.getMessage());
        }

        // Map request to StudentDetails entity
        StudentDetails student;
        try {
            student = mapRequestToEntity(request, user, studentPhoto, document);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Failed to map request: " + e.getMessage());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process file uploads: " + e.getMessage());
        }

        // Save student
        try {
            StudentDetails newStudent = studentDetailsRepository.save(student);
            notificationService.createNotification(Arrays.asList(
                    Role.GENERAL_MANAGER, Role.DEAN, Role.VICE_DEAN, Role.DEPARTMENT_HEAD, Role.REGISTRAR),
                    null, Role.REGISTRAR,
                    "New Student Registered : " + newStudent.getFirstNameAMH() + " " + newStudent.getFatherNameAMH());
            return newStudent;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Failed to register student due to duplicate entry or constraint violation: " + e.getMessage());
        }
    }

    // Retrieves all students as DTOs, including disabled ones
    // Why: For admin/registrar to view all student records, regardless of enabled status
    public List<StudentListDTO> getAllStudents() {
        return studentDetailsRepository.findAll().stream()
                .map(student -> {
                    StudentListDTO dto = new StudentListDTO();
                    dto.setId(student.getId());
                    dto.setUsername(student.getUser().getUsername());
                    dto.setAccountStatus(student.getUser().isEnabled() ? "ENABLED" : "DISABLED");

                    dto.setFirstNameAMH(student.getFirstNameAMH());
                    dto.setFirstNameENG(student.getFirstNameENG());
                    dto.setFatherNameAMH(student.getFatherNameAMH());
                    dto.setFatherNameENG(student.getFatherNameENG());
                    dto.setGrandfatherNameAMH(student.getGrandfatherNameAMH());
                    dto.setGrandfatherNameENG(student.getGrandfatherNameENG());

                    dto.setStudentRecentStatus(student.getStudentRecentStatus().getStatusName());
                    dto.setDepartmentEnrolled(student.getDepartmentEnrolled().getDeptName());
                    dto.setBatchClassYearSemester(student.getBatchClassYearSemester().getDisplayName());

                    dto.setStudentPhoto(student.getStudentPhoto());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Retrieves a student by ID as a DTO, ensuring they are active
// Why: Returns DTO for detailed views, includes all fields, respects enabled flag
    public StudentDetailsDTO getStudentById(Long id) {
        try {
            StudentDetails student = studentDetailsRepository.findByIdAndUserEnabledTrue(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Active student not found with id: " + id));
            return mapToDTO(student);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve student with id " + id + ": " + e.getMessage());
        }
    }


    // Updates a student's details with optional file uploads
    // Why: Allows modification with multipart form data, respects enabled status
    // Updates a student's details with optional file uploads and returns DTO
// Why: Allows modification with multipart form data, returns updated DTO
    @Transactional(rollbackFor = Exception.class)
    public StudentDetailsDTO updateStudent(Long id, StudentUpdateDTO dto, MultipartFile studentPhoto, MultipartFile document) {
        // Validate DTO
        if (dto == null) {
            throw new IllegalArgumentException("Update request cannot be null");
        }

        // Fetch existing student
        StudentDetails existing;
        try {
            existing = studentDetailsRepository.findByIdAndUserEnabledTrue(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Active student not found with id: " + id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch student for update: " + e.getMessage());
        }

        // Check for duplicate phone number
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()
                && !dto.getPhoneNumber().equals(existing.getPhoneNumber())
                && studentDetailsRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already in use");
        }
        // Check for duplicate exitExamUserID if provided
        if (dto.getExitExamUserID() != null && !dto.getExitExamUserID().isEmpty()
                && !dto.getExitExamUserID().equals(existing.getExitExamUserID())
                && studentDetailsRepository.existsByExitExamUserID(dto.getExitExamUserID())) {
            throw new IllegalArgumentException("Exit exam user ID already in use");
        }

        // Validate file sizes
        if (studentPhoto != null && !studentPhoto.isEmpty() && studentPhoto.getSize() > 2_000_000) { // 2MB limit
            throw new IllegalArgumentException("Student photo size exceeds 2MB limit");
        }
        if (document != null && !document.isEmpty() && document.getSize() > 10_000_000) { // 10MB limit
            throw new IllegalArgumentException("Document size exceeds 10MB limit");
        }

        // Update fields if provided
        try {
            updateStudentFields(existing, dto, studentPhoto, document);
            StudentDetails updated = studentDetailsRepository.save(existing);
            return mapToDTO(updated);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Failed to update student due to duplicate entry or constraint violation: " + e.getMessage());
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process file uploads: " + e.getMessage());
        }
    }

    // Enables a student by enabling their user account
    // Why: Activates account for login, sets all flags to active
    public void enableStudent(Long studentId) {
        try {
            StudentDetails student = studentDetailsRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
            userService.enableUser(student.getUser().getId());
            notificationService.createNotification(Arrays.asList(
                            Role.GENERAL_MANAGER, Role.DEAN, Role.VICE_DEAN, Role.DEPARTMENT_HEAD, Role.FINANCIAL_STAFF),
                    null, Role.REGISTRAR,
                    "Student " + student.getUser().getUsername() + " Can access his Account");
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to enable student with id " + studentId + ": " + e.getMessage());
        }
    }

    // Disables a student by disabling their user account
    // Why: Suspends account without deletion, respects dependencies
    public void disableStudent(Long studentId) {
        try {
            StudentDetails student = studentDetailsRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
            userService.disableUser(student.getUser().getId());
            notificationService.createNotification(Arrays.asList(
                            Role.GENERAL_MANAGER, Role.DEAN, Role.VICE_DEAN, Role.DEPARTMENT_HEAD, Role.FINANCIAL_STAFF),
                    null, Role.REGISTRAR,
                    "Student " + student.getUser().getUsername() + " Cannot access his Account any more");
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to disable student with id " + studentId + ": " + e.getMessage());
        }
    }

    // Validates registration request fields
    // Why: Ensures all required fields are present and valid before processing
    private void validateRegistrationRequest(StudentRegisterRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (request.getFirstNameAMH() == null || request.getFirstNameAMH().isEmpty()) {
            throw new IllegalArgumentException("First name (Amharic) cannot be empty");
        }
        if (request.getFirstNameENG() == null || request.getFirstNameENG().isEmpty()) {
            throw new IllegalArgumentException("First name (English) cannot be empty");
        }
        if (request.getFatherNameAMH() == null || request.getFatherNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Father name (Amharic) cannot be empty");
        }
        if (request.getFatherNameENG() == null || request.getFatherNameENG().isEmpty()) {
            throw new IllegalArgumentException("Father name (English) cannot be empty");
        }
        if (request.getGrandfatherNameAMH() == null || request.getGrandfatherNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Grandfather name (Amharic) cannot be empty");
        }
        if (request.getGrandfatherNameENG() == null || request.getGrandfatherNameENG().isEmpty()) {
            throw new IllegalArgumentException("Grandfather name (English) cannot be empty");
        }
        if (request.getMotherNameAMH() == null || request.getMotherNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Mother name (Amharic) cannot be empty");
        }
        if (request.getMotherNameENG() == null || request.getMotherNameENG().isEmpty()) {
            throw new IllegalArgumentException("Mother name (English) cannot be empty");
        }
        if (request.getMotherFatherNameAMH() == null || request.getMotherFatherNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Mother's father name (Amharic) cannot be empty");
        }
        if (request.getMotherFatherNameENG() == null || request.getMotherFatherNameENG().isEmpty()) {
            throw new IllegalArgumentException("Mother's father name (English) cannot be empty");
        }
        if (request.getGender() == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        if (request.getAge() == null || request.getAge() < 0) {
            throw new IllegalArgumentException("Age must be a non-negative integer");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        if (request.getDateOfBirthEC() == null || request.getDateOfBirthEC().isEmpty()) {
            throw new IllegalArgumentException("Date of birth (EC) cannot be empty");
        }
        if (request.getDateOfBirthGC() == null) {
            throw new IllegalArgumentException("Date of birth (GC) cannot be null");
        }
        if (request.getPlaceOfBirthWoredaCode() == null) {
            throw new IllegalArgumentException("Place of birth Woreda code cannot be null");
        }
        if (request.getPlaceOfBirthZoneCode() == null) {
            throw new IllegalArgumentException("Place of birth Zone code cannot be null");
        }
        if (request.getPlaceOfBirthRegionCode() == null) {
            throw new IllegalArgumentException("Place of birth Region code cannot be null");
        }
        if (request.getCurrentAddressWoredaCode() == null) {
            throw new IllegalArgumentException("Current address Woreda code cannot be null");
        }
        if (request.getCurrentAddressZoneCode() == null) {
            throw new IllegalArgumentException("Current address Zone code cannot be null");
        }
        if (request.getCurrentAddressRegionCode() == null) {
            throw new IllegalArgumentException("Current address Region code cannot be null");
        }
        if (request.getMaritalStatus() == null) {
            throw new IllegalArgumentException("Marital status cannot be null");
        }
        if (request.getSchoolBackgroundId() == null) {
            throw new IllegalArgumentException("School background cannot be null");
        }
        if (request.getContactPersonFirstNameAMH() == null || request.getContactPersonFirstNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Contact person first name (Amharic) cannot be empty");
        }
        if (request.getContactPersonFirstNameENG() == null || request.getContactPersonFirstNameENG().isEmpty()) {
            throw new IllegalArgumentException("Contact person first name (English) cannot be empty");
        }
        if (request.getContactPersonLastNameAMH() == null || request.getContactPersonLastNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Contact person last name (Amharic) cannot be empty");
        }
        if (request.getContactPersonLastNameENG() == null || request.getContactPersonLastNameENG().isEmpty()) {
            throw new IllegalArgumentException("Contact person last name (English) cannot be empty");
        }
        if (request.getContactPersonPhoneNumber() == null || request.getContactPersonPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Contact person phone number cannot be empty");
        }
        if (request.getDateEnrolledEC() == null || request.getDateEnrolledEC().isEmpty()) {
            throw new IllegalArgumentException("Date enrolled (EC) cannot be empty");
        }
        if (request.getDateEnrolledGC() == null) {
            throw new IllegalArgumentException("Date enrolled (GC) cannot be null");
        }
        if (request.getBatchClassYearSemesterId() == null) {
            throw new IllegalArgumentException("Batch class year semester cannot be null");
        }
        if (request.getStudentRecentStatusId() == null) {
            throw new IllegalArgumentException("Student recent status cannot be null");
        }
        if (request.getDepartmentEnrolledId() == null) {
            throw new IllegalArgumentException("Department enrolled cannot be null");
        }
        if (request.getProgramModalityCode() == null) {
            throw new IllegalArgumentException("Program modality cannot be null");
        }
    }

    // Updates student fields from DTO and file uploads
    // Why: Centralizes update logic, only updates provided fields and files
    private void updateStudentFields(StudentDetails student, StudentUpdateDTO dto, MultipartFile studentPhoto, MultipartFile document) throws IOException {
        if (dto.getFirstNameAMH() != null && !dto.getFirstNameAMH().isEmpty()) {
            student.setFirstNameAMH(dto.getFirstNameAMH());
        }
        if (dto.getFirstNameENG() != null && !dto.getFirstNameENG().isEmpty()) {
            student.setFirstNameENG(dto.getFirstNameENG());
        }
        if (dto.getFatherNameAMH() != null && !dto.getFatherNameAMH().isEmpty()) {
            student.setFatherNameAMH(dto.getFatherNameAMH());
        }
        if (dto.getFatherNameENG() != null && !dto.getFatherNameENG().isEmpty()) {
            student.setFatherNameENG(dto.getFatherNameENG());
        }
        if (dto.getGrandfatherNameAMH() != null && !dto.getGrandfatherNameAMH().isEmpty()) {
            student.setGrandfatherNameAMH(dto.getGrandfatherNameAMH());
        }
        if (dto.getGrandfatherNameENG() != null && !dto.getGrandfatherNameENG().isEmpty()) {
            student.setGrandfatherNameENG(dto.getGrandfatherNameENG());
        }
        if (dto.getMotherNameAMH() != null && !dto.getMotherNameAMH().isEmpty()) {
            student.setMotherNameAMH(dto.getMotherNameAMH());
        }
        if (dto.getMotherNameENG() != null && !dto.getMotherNameENG().isEmpty()) {
            student.setMotherNameENG(dto.getMotherNameENG());
        }
        if (dto.getMotherFatherNameAMH() != null && !dto.getMotherFatherNameAMH().isEmpty()) {
            student.setMotherFatherNameAMH(dto.getMotherFatherNameAMH());
        }
        if (dto.getMotherFatherNameENG() != null && !dto.getMotherFatherNameENG().isEmpty()) {
            student.setMotherFatherNameENG(dto.getMotherFatherNameENG());
        }
        if (dto.getGender() != null) {
            student.setGender(dto.getGender());
        }
        if (dto.getAge() != null && dto.getAge() >= 0) {
            student.setAge(dto.getAge());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty()) {
            student.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getDateOfBirthEC() != null && !dto.getDateOfBirthEC().isEmpty()) {
            student.setDateOfBirthEC(dto.getDateOfBirthEC());
        }
        if (dto.getDateOfBirthGC() != null) {
            student.setDateOfBirthGC(dto.getDateOfBirthGC());
        }
        if (dto.getPlaceOfBirthWoredaCode() != null) {
            Woreda woreda = woredaRepository.findById(dto.getPlaceOfBirthWoredaCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Woreda not found with code: " + dto.getPlaceOfBirthWoredaCode()));
            student.setPlaceOfBirthWoreda(woreda);
        }
        if (dto.getPlaceOfBirthZoneCode() != null) {
            Zone zone = zoneRepository.findById(dto.getPlaceOfBirthZoneCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Zone not found with code: " + dto.getPlaceOfBirthZoneCode()));
            student.setPlaceOfBirthZone(zone);
        }
        if (dto.getPlaceOfBirthRegionCode() != null) {
            Region region = regionRepository.findById(dto.getPlaceOfBirthRegionCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Region not found with code: " + dto.getPlaceOfBirthRegionCode()));
            student.setPlaceOfBirthRegion(region);
        }
        if (dto.getCurrentAddressWoredaCode() != null) {
            Woreda woreda = woredaRepository.findById(dto.getCurrentAddressWoredaCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Woreda not found with code: " + dto.getCurrentAddressWoredaCode()));
            student.setCurrentAddressWoreda(woreda);
        }
        if (dto.getCurrentAddressZoneCode() != null) {
            Zone zone = zoneRepository.findById(dto.getCurrentAddressZoneCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Zone not found with code: " + dto.getCurrentAddressZoneCode()));
            student.setCurrentAddressZone(zone);
        }
        if (dto.getCurrentAddressRegionCode() != null) {
            Region region = regionRepository.findById(dto.getCurrentAddressRegionCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Region not found with code: " + dto.getCurrentAddressRegionCode()));
            student.setCurrentAddressRegion(region);
        }
        if (dto.getEmail() != null) {
            student.setEmail(dto.getEmail());
        }
        if (dto.getMaritalStatus() != null) {
            student.setMaritalStatus(dto.getMaritalStatus());
        }
        if (dto.getImpairmentCode() != null) {
            Impairment impairment = impairmentRepository.findById(dto.getImpairmentCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Impairment not found with code: " + dto.getImpairmentCode()));
            student.setImpairment(impairment);
        }
        if (dto.getSchoolBackgroundId() != null) {
            SchoolBackground background = schoolBackgroundRepository.findById(dto.getSchoolBackgroundId())
                    .orElseThrow(() -> new ResourceNotFoundException("School background not found with id: " + dto.getSchoolBackgroundId()));
            student.setSchoolBackground(background);
        }
        if (studentPhoto != null && !studentPhoto.isEmpty()) {
            student.setStudentPhoto(studentPhoto.getBytes());
        }
        if (dto.getContactPersonFirstNameAMH() != null && !dto.getContactPersonFirstNameAMH().isEmpty()) {
            student.setContactPersonFirstNameAMH(dto.getContactPersonFirstNameAMH());
        }
        if (dto.getContactPersonFirstNameENG() != null && !dto.getContactPersonFirstNameENG().isEmpty()) {
            student.setContactPersonFirstNameENG(dto.getContactPersonFirstNameENG());
        }
        if (dto.getContactPersonLastNameAMH() != null && !dto.getContactPersonLastNameAMH().isEmpty()) {
            student.setContactPersonLastNameAMH(dto.getContactPersonLastNameAMH());
        }
        if (dto.getContactPersonLastNameENG() != null && !dto.getContactPersonLastNameENG().isEmpty()) {
            student.setContactPersonLastNameENG(dto.getContactPersonLastNameENG());
        }
        if (dto.getContactPersonPhoneNumber() != null && !dto.getContactPersonPhoneNumber().isEmpty()) {
            student.setContactPersonPhoneNumber(dto.getContactPersonPhoneNumber());
        }
        if (dto.getContactPersonRelation() != null) {
            student.setContactPersonRelation(dto.getContactPersonRelation());
        }
        if (dto.getDateEnrolledEC() != null && !dto.getDateEnrolledEC().isEmpty()) {
            student.setDateEnrolledEC(dto.getDateEnrolledEC());
        }
        if (dto.getDateEnrolledGC() != null) {
            student.setDateEnrolledGC(dto.getDateEnrolledGC());
        }
        if (dto.getBatchClassYearSemesterId() != null) {
            BatchClassYearSemester bcys = batchClassYearSemesterRepository.findById(dto.getBatchClassYearSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + dto.getBatchClassYearSemesterId()));
            student.setBatchClassYearSemester(bcys);
//            student.setStudentRecentBatch(bcys);
        }
        if (dto.getStudentRecentStatusId() != null) {
            StudentStatus status = studentStatusRepository.findById(dto.getStudentRecentStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student status not found with id: " + dto.getStudentRecentStatusId()));
            student.setStudentRecentStatus(status);
        }
        if (dto.getDepartmentEnrolledId() != null) {
            Department dept = departmentRepository.findById(dto.getDepartmentEnrolledId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + dto.getDepartmentEnrolledId()));
            student.setDepartmentEnrolled(dept);
//            student.setStudentRecentDepartment(dept);
        }
        if (dto.getProgramModalityCode() != null) {
            ProgramModality modality = programModalityRepository.findById(dto.getProgramModalityCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Program modality not found with code: " + dto.getProgramModalityCode()));
            student.setProgramModality(modality);
        }
        if (document != null && !document.isEmpty()) {
            student.setDocument(document.getBytes());
        }
        if (dto.getDocumentStatus() != null) {
            student.setDocumentStatus(dto.getDocumentStatus());
        }
        if (dto.getRemark() != null) {
            student.setRemark(dto.getRemark());
        }
        if (dto.getIsTransfer() != null) {
            student.setTransfer(dto.getIsTransfer());
        }
        if (dto.getExitExamUserID() != null) {
            student.setExitExamUserID(dto.getExitExamUserID());
        }
        if (dto.getExitExamScore() != null) {
            student.setExitExamScore(dto.getExitExamScore());
        }
        if (dto.getIsStudentPassExitExam() != null) {
            student.setStudentPassExitExam(dto.getIsStudentPassExitExam());
        }
        if (dto.getGrade12Result() != null) {
            student.setGrade12Result(dto.getGrade12Result());
        }
    }

    // Maps registration request to StudentDetails entity with file uploads
    // Why: Centralizes mapping logic, handles file conversion and relationships
    private StudentDetails mapRequestToEntity(StudentRegisterRequest request, User user, MultipartFile studentPhoto, MultipartFile document) throws IOException {
        StudentDetails student = new StudentDetails();
        student.setUser(user);
        student.setFirstNameAMH(request.getFirstNameAMH());
        student.setFirstNameENG(request.getFirstNameENG());
        student.setFatherNameAMH(request.getFatherNameAMH());
        student.setFatherNameENG(request.getFatherNameENG());
        student.setGrandfatherNameAMH(request.getGrandfatherNameAMH());
        student.setGrandfatherNameENG(request.getGrandfatherNameENG());
        student.setMotherNameAMH(request.getMotherNameAMH());
        student.setMotherNameENG(request.getMotherNameENG());
        student.setMotherFatherNameAMH(request.getMotherFatherNameAMH());
        student.setMotherFatherNameENG(request.getMotherFatherNameENG());
        student.setGender(request.getGender());
        student.setAge(request.getAge());
        student.setPhoneNumber(request.getPhoneNumber());
        student.setDateOfBirthEC(request.getDateOfBirthEC());
        student.setDateOfBirthGC(request.getDateOfBirthGC());
        student.setPlaceOfBirthWoreda(woredaRepository.findById(request.getPlaceOfBirthWoredaCode())
                .orElseThrow(() -> new ResourceNotFoundException("Woreda not found with code: " + request.getPlaceOfBirthWoredaCode())));
        student.setPlaceOfBirthZone(zoneRepository.findById(request.getPlaceOfBirthZoneCode())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with code: " + request.getPlaceOfBirthZoneCode())));
        student.setPlaceOfBirthRegion(regionRepository.findById(request.getPlaceOfBirthRegionCode())
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with code: " + request.getPlaceOfBirthRegionCode())));
        student.setCurrentAddressWoreda(woredaRepository.findById(request.getCurrentAddressWoredaCode())
                .orElseThrow(() -> new ResourceNotFoundException("Woreda not found with code: " + request.getCurrentAddressWoredaCode())));
        student.setCurrentAddressZone(zoneRepository.findById(request.getCurrentAddressZoneCode())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with code: " + request.getCurrentAddressZoneCode())));
        student.setCurrentAddressRegion(regionRepository.findById(request.getCurrentAddressRegionCode())
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with code: " + request.getCurrentAddressRegionCode())));
        student.setEmail(request.getEmail());
        student.setMaritalStatus(request.getMaritalStatus());
        if (request.getImpairmentCode() != null && !request.getImpairmentCode().isEmpty()) {
            student.setImpairment(impairmentRepository.findById(request.getImpairmentCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Impairment not found with code: " + request.getImpairmentCode())));
        }
        student.setSchoolBackground(schoolBackgroundRepository.findById(request.getSchoolBackgroundId())
                .orElseThrow(() -> new ResourceNotFoundException("School background not found with id: " + request.getSchoolBackgroundId())));
        if (studentPhoto != null && !studentPhoto.isEmpty()) {
            student.setStudentPhoto(studentPhoto.getBytes());
        }
        student.setContactPersonFirstNameAMH(request.getContactPersonFirstNameAMH());
        student.setContactPersonFirstNameENG(request.getContactPersonFirstNameENG());
        student.setContactPersonLastNameAMH(request.getContactPersonLastNameAMH());
        student.setContactPersonLastNameENG(request.getContactPersonLastNameENG());
        student.setContactPersonPhoneNumber(request.getContactPersonPhoneNumber());
        student.setContactPersonRelation(request.getContactPersonRelation());
        student.setDateEnrolledEC(request.getDateEnrolledEC());
        student.setDateEnrolledGC(request.getDateEnrolledGC());
        BatchClassYearSemester bcys = batchClassYearSemesterRepository.findById(request.getBatchClassYearSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + request.getBatchClassYearSemesterId()));
        student.setBatchClassYearSemester(bcys);
//        student.setStudentRecentBatch(bcys);
        student.setStudentRecentStatus(studentStatusRepository.findById(request.getStudentRecentStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Student status not found with id: " + request.getStudentRecentStatusId())));
        Department dept = departmentRepository.findById(request.getDepartmentEnrolledId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentEnrolledId()));
        student.setDepartmentEnrolled(dept);
//        student.setStudentRecentDepartment(dept);
        student.setProgramModality(programModalityRepository.findById(request.getProgramModalityCode())
                .orElseThrow(() -> new ResourceNotFoundException("Program modality not found with code: " + request.getProgramModalityCode())));
        if (document != null && !document.isEmpty()) {
            student.setDocument(document.getBytes());
        }
        student.setDocumentStatus(request.getDocumentStatus() != null ? request.getDocumentStatus() : DocumentStatus.INCOMPLETE);
        student.setRemark(request.getRemark());
        student.setTransfer(request.getIsTransfer() != null ? request.getIsTransfer() : false);
        student.setExitExamUserID(request.getExitExamUserID());
        student.setExitExamScore(request.getExitExamScore());
        student.setStudentPassExitExam(request.getIsStudentPassExitExam() != null ? request.getIsStudentPassExitExam() : false);
        student.setGrade12Result(request.getGrade12Result());
        return student;
    }

    // Maps StudentDetails entity to StudentDetailsDTO
    // Why: Converts entity to DTO to include all fields safely
    private StudentDetailsDTO mapToDTO(StudentDetails student) {
        StudentDetailsDTO dto = new StudentDetailsDTO();
        dto.setId(student.getId());
        dto.setUserId(student.getUser().getId());
        dto.setFirstNameAMH(student.getFirstNameAMH());
        dto.setFirstNameENG(student.getFirstNameENG());
        dto.setFatherNameAMH(student.getFatherNameAMH());
        dto.setFatherNameENG(student.getFatherNameENG());
        dto.setGrandfatherNameAMH(student.getGrandfatherNameAMH());
        dto.setGrandfatherNameENG(student.getGrandfatherNameENG());
        dto.setMotherNameAMH(student.getMotherNameAMH());
        dto.setMotherNameENG(student.getMotherNameENG());
        dto.setMotherFatherNameAMH(student.getMotherFatherNameAMH());
        dto.setMotherFatherNameENG(student.getMotherFatherNameENG());
        dto.setGender(student.getGender().name());
        dto.setAge(student.getAge());
        dto.setPhoneNumber(student.getPhoneNumber());
        dto.setDateOfBirthEC(student.getDateOfBirthEC());
        dto.setDateOfBirthGC(student.getDateOfBirthGC());
        dto.setPlaceOfBirthWoredaCode(student.getPlaceOfBirthWoreda().getWoredaCode());
        dto.setPlaceOfBirthZoneCode(student.getPlaceOfBirthZone().getZoneCode());
        dto.setPlaceOfBirthRegionCode(student.getPlaceOfBirthRegion().getRegionCode());
        dto.setCurrentAddressWoredaCode(student.getCurrentAddressWoreda().getWoredaCode());
        dto.setCurrentAddressZoneCode(student.getCurrentAddressZone().getZoneCode());
        dto.setCurrentAddressRegionCode(student.getCurrentAddressRegion().getRegionCode());
        dto.setEmail(student.getEmail());
        dto.setMaritalStatus(student.getMaritalStatus().name());
        dto.setImpairmentCode(student.getImpairment() != null ? student.getImpairment().getImpairmentCode() : null);
        dto.setSchoolBackgroundId(student.getSchoolBackground().getId());
        dto.setStudentPhoto(student.getStudentPhoto());
        dto.setContactPersonFirstNameAMH(student.getContactPersonFirstNameAMH());
        dto.setContactPersonFirstNameENG(student.getContactPersonFirstNameENG());
        dto.setContactPersonLastNameAMH(student.getContactPersonLastNameAMH());
        dto.setContactPersonLastNameENG(student.getContactPersonLastNameENG());
        dto.setContactPersonPhoneNumber(student.getContactPersonPhoneNumber());
        dto.setContactPersonRelation(student.getContactPersonRelation());
        dto.setDateEnrolledEC(student.getDateEnrolledEC());
        dto.setDateEnrolledGC(student.getDateEnrolledGC());
        dto.setBatchClassYearSemesterId(student.getBatchClassYearSemester().getBcysID());
        dto.setStudentRecentStatusId(student.getStudentRecentStatus().getId());
        dto.setDepartmentEnrolledId(student.getDepartmentEnrolled().getDptID());
        dto.setProgramModalityCode(student.getProgramModality().getModalityCode());
        dto.setDocument(student.getDocument());
        dto.setDocumentStatus(student.getDocumentStatus().name());
        dto.setRemark(student.getRemark());
        dto.setIsTransfer(student.isTransfer());
        dto.setExitExamUserID(student.getExitExamUserID());
        dto.setExitExamScore(student.getExitExamScore());
        dto.setIsStudentPassExitExam(student.isStudentPassExitExam());
        dto.setGrade12Result(student.getGrade12Result());
        return dto;
    }
}