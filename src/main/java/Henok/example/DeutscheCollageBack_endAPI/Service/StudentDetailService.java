package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.StudentRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.UserRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Entity.MOE_Data.*;
import Henok.example.DeutscheCollageBack_endAPI.Enums.DocumentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentDetailService {

    @Autowired
    private UserService userService;

    @Autowired
    private StudentDetailsRepository studentDetailsRepository;

    @Autowired
    private WoredaRepository woredaRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private DepartmentRepo departmentRepository;

    @Autowired
    private ProgramModalityRepository programModalityRepository;

    @Autowired
    private BatchClassYearSemesterRepo batchClassYearSemesterRepository;

    @Autowired
    private SchoolBackgroundRepository schoolBackgroundRepository;

    @Autowired
    private StudentStatusRepo studentStatusRepository;

    @Autowired
    private ImpairmentRepository impairmentRepository;

    @Transactional
    public StudentDetails registerStudent(StudentRegisterRequest request) {
        // Validate required fields
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (request.getFirstNameAMH() == null || request.getFirstNameAMH().isEmpty()) {
            throw new IllegalArgumentException("First name in Amharic cannot be empty");
        }
        if (request.getFirstNameENG() == null || request.getFirstNameENG().isEmpty()) {
            throw new IllegalArgumentException("First name in English cannot be empty");
        }
        if (request.getFatherNameAMH() == null || request.getFatherNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Father's name in Amharic cannot be empty");
        }
        if (request.getFatherNameENG() == null || request.getFatherNameENG().isEmpty()) {
            throw new IllegalArgumentException("Father's name in English cannot be empty");
        }
        if (request.getGrandfatherNameAMH() == null || request.getGrandfatherNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Grandfather's name in Amharic cannot be empty");
        }
        if (request.getGrandfatherNameENG() == null || request.getGrandfatherNameENG().isEmpty()) {
            throw new IllegalArgumentException("Grandfather's name in English cannot be empty");
        }
        if (request.getMotherNameAMH() == null || request.getMotherNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Mother's name in Amharic cannot be empty");
        }
        if (request.getMotherNameENG() == null || request.getMotherNameENG().isEmpty()) {
            throw new IllegalArgumentException("Mother's name in English cannot be empty");
        }
        if (request.getMotherFatherNameAMH() == null || request.getMotherFatherNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Mother's father name in Amharic cannot be empty");
        }
        if (request.getMotherFatherNameENG() == null || request.getMotherFatherNameENG().isEmpty()) {
            throw new IllegalArgumentException("Mother's father name in English cannot be empty");
        }
        if (request.getGender() == null) {
            throw new IllegalArgumentException("Gender cannot be empty");
        }
        if (request.getAge() == null || request.getAge() <= 0) {
            throw new IllegalArgumentException("Age must be a positive integer");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        if (request.getDateOfBirthEC() == null || request.getDateOfBirthEC().isEmpty()) {
            throw new IllegalArgumentException("Date of birth (EC) cannot be empty");
        }
        if (request.getDateOfBirthGC() == null) {
            throw new IllegalArgumentException("Date of birth (GC) cannot be empty");
        }
        if (request.getPlaceOfBirthWoredaCode() == null || request.getPlaceOfBirthWoredaCode().isEmpty()) {
            throw new IllegalArgumentException("Place of birth woreda code cannot be empty");
        }
        if (request.getPlaceOfBirthZoneCode() == null || request.getPlaceOfBirthZoneCode().isEmpty()) {
            throw new IllegalArgumentException("Place of birth zone code cannot be empty");
        }
        if (request.getPlaceOfBirthRegionCode() == null || request.getPlaceOfBirthRegionCode().isEmpty()) {
            throw new IllegalArgumentException("Place of birth region code cannot be empty");
        }
        if (request.getCurrentAddressWoredaCode() == null || request.getCurrentAddressWoredaCode().isEmpty()) {
            throw new IllegalArgumentException("Current address woreda code cannot be empty");
        }
        if (request.getCurrentAddressZoneCode() == null || request.getCurrentAddressZoneCode().isEmpty()) {
            throw new IllegalArgumentException("Current address zone code cannot be empty");
        }
        if (request.getCurrentAddressRegionCode() == null || request.getCurrentAddressRegionCode().isEmpty()) {
            throw new IllegalArgumentException("Current address region code cannot be empty");
        }
        if (request.getMaritalStatus() == null) {
            throw new IllegalArgumentException("Marital status cannot be empty");
        }
        if (request.getSchoolBackgroundId() == null) {
            throw new IllegalArgumentException("School background ID cannot be empty");
        }
        if (request.getContactPersonFirstNameAMH() == null || request.getContactPersonFirstNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Contact person first name in Amharic cannot be empty");
        }
        if (request.getContactPersonFirstNameENG() == null || request.getContactPersonFirstNameENG().isEmpty()) {
            throw new IllegalArgumentException("Contact person first name in English cannot be empty");
        }
        if (request.getContactPersonLastNameAMH() == null || request.getContactPersonLastNameAMH().isEmpty()) {
            throw new IllegalArgumentException("Contact person last name in Amharic cannot be empty");
        }
        if (request.getContactPersonLastNameENG() == null || request.getContactPersonLastNameENG().isEmpty()) {
            throw new IllegalArgumentException("Contact person last name in English cannot be empty");
        }
        if (request.getContactPersonPhoneNumber() == null || request.getContactPersonPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Contact person phone number cannot be empty");
        }
        if (request.getDateEnrolledEC() == null || request.getDateEnrolledEC().isEmpty()) {
            throw new IllegalArgumentException("Date enrolled (EC) cannot be empty");
        }
        if (request.getDateEnrolledGC() == null) {
            throw new IllegalArgumentException("Date enrolled (GC) cannot be empty");
        }
        if (request.getBatchClassYearSemesterId() == null) {
            throw new IllegalArgumentException("Batch class year semester ID cannot be empty");
        }
        if (request.getDepartmentEnrolledId() == null) {
            throw new IllegalArgumentException("Department enrolled ID cannot be empty");
        }
        if (request.getProgramModalityCode() == null || request.getProgramModalityCode().isEmpty()) {
            throw new IllegalArgumentException("Program modality code cannot be empty");
        }
        if (request.getStudentRecentStatusId() == null) {
            throw new IllegalArgumentException("Student recent status ID cannot be empty");
        }

        // Check for unique phone number
        if (studentDetailsRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Create User with STUDENT role
        UserRegisterRequest userRequest = new UserRegisterRequest();
        userRequest.setUsername(request.getUsername());
        userRequest.setPassword(request.getPassword());
        userRequest.setRole(Role.STUDENT);
        User user = userService.registerUser(userRequest);

        // Validate and fetch related entities
        Woreda placeOfBirthWoreda = woredaRepository.findById(request.getPlaceOfBirthWoredaCode())
                .orElseThrow(() -> new ResourceNotFoundException("Woreda not found with code: " + request.getPlaceOfBirthWoredaCode()));
        Zone placeOfBirthZone = zoneRepository.findById(request.getPlaceOfBirthZoneCode())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with code: " + request.getPlaceOfBirthZoneCode()));
        Region placeOfBirthRegion = regionRepository.findById(request.getPlaceOfBirthRegionCode())
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with code: " + request.getPlaceOfBirthRegionCode()));
        Woreda currentAddressWoreda = woredaRepository.findById(request.getCurrentAddressWoredaCode())
                .orElseThrow(() -> new ResourceNotFoundException("Woreda not found with code: " + request.getCurrentAddressWoredaCode()));
        Zone currentAddressZone = zoneRepository.findById(request.getCurrentAddressZoneCode())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with code: " + request.getCurrentAddressZoneCode()));
        Region currentAddressRegion = regionRepository.findById(request.getCurrentAddressRegionCode())
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with code: " + request.getCurrentAddressRegionCode()));
        Department departmentEnrolled = departmentRepository.findById(request.getDepartmentEnrolledId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + request.getDepartmentEnrolledId()));
        ProgramModality programModality = programModalityRepository.findById(request.getProgramModalityCode())
                .orElseThrow(() -> new ResourceNotFoundException("Program modality not found with code: " + request.getProgramModalityCode()));
        BatchClassYearSemester batchClassYearSemester = batchClassYearSemesterRepository.findById(request.getBatchClassYearSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch class year semester not found with ID: " + request.getBatchClassYearSemesterId()));
        SchoolBackground schoolBackground = schoolBackgroundRepository.findById(request.getSchoolBackgroundId())
                .orElseThrow(() -> new ResourceNotFoundException("School background not found with ID: " + request.getSchoolBackgroundId()));
        StudentStatus studentStatus = studentStatusRepository.findById(request.getStudentRecentStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Student status not found with ID: " + request.getStudentRecentStatusId()));
        Impairment impairment = request.getImpairmentCode() != null ?
                impairmentRepository.findById(request.getImpairmentCode())
                        .orElseThrow(() -> new ResourceNotFoundException("Impairment not found with code: " + request.getImpairmentCode())) :
                null;

        // Create StudentDetails
        StudentDetails studentDetails = new StudentDetails();
        studentDetails.setUser(user);
        studentDetails.setUserId(user.getId());
        studentDetails.setFirstNameAMH(request.getFirstNameAMH());
        studentDetails.setFirstNameENG(request.getFirstNameENG());
        studentDetails.setFatherNameAMH(request.getFatherNameAMH());
        studentDetails.setFatherNameENG(request.getFatherNameENG());
        studentDetails.setGrandfatherNameAMH(request.getGrandfatherNameAMH());
        studentDetails.setGrandfatherNameENG(request.getGrandfatherNameENG());
        studentDetails.setMotherNameAMH(request.getMotherNameAMH());
        studentDetails.setMotherNameENG(request.getMotherNameENG());
        studentDetails.setMotherFatherNameAMH(request.getMotherFatherNameAMH());
        studentDetails.setMotherFatherNameENG(request.getMotherFatherNameENG());
        studentDetails.setGender(request.getGender());
        studentDetails.setAge(request.getAge());
        studentDetails.setPhoneNumber(request.getPhoneNumber());
        studentDetails.setDateOfBirthEC(request.getDateOfBirthEC());
        studentDetails.setDateOfBirthGC(request.getDateOfBirthGC());
        studentDetails.setPlaceOfBirthWoreda(placeOfBirthWoreda);
        studentDetails.setPlaceOfBirthZone(placeOfBirthZone);
        studentDetails.setPlaceOfBirthRegion(placeOfBirthRegion);
        studentDetails.setCurrentAddressWoreda(currentAddressWoreda);
        studentDetails.setCurrentAddressZone(currentAddressZone);
        studentDetails.setCurrentAddressRegion(currentAddressRegion);
        studentDetails.setEmail(request.getEmail());
        studentDetails.setMaritalStatus(request.getMaritalStatus());
        studentDetails.setImpairment(impairment);
        studentDetails.setSchoolBackground(schoolBackground);
        studentDetails.setStudentPhoto(request.getStudentPhoto());
        studentDetails.setContactPersonFirstNameAMH(request.getContactPersonFirstNameAMH());
        studentDetails.setContactPersonFirstNameENG(request.getContactPersonFirstNameENG());
        studentDetails.setContactPersonLastNameAMH(request.getContactPersonLastNameAMH());
        studentDetails.setContactPersonLastNameENG(request.getContactPersonLastNameENG());
        studentDetails.setContactPersonPhoneNumber(request.getContactPersonPhoneNumber());
        studentDetails.setContactPersonRelation(request.getContactPersonRelation());
        studentDetails.setDateEnrolledEC(request.getDateEnrolledEC());
        studentDetails.setDateEnrolledGC(request.getDateEnrolledGC());
        studentDetails.setBatchClassYearSemester(batchClassYearSemester);
        studentDetails.setStudentRecentBatch(batchClassYearSemester);
        studentDetails.setDepartmentEnrolled(departmentEnrolled);
        studentDetails.setStudentRecentDepartment(departmentEnrolled);
        studentDetails.setProgramModality(programModality);
        studentDetails.setStudentRecentStatus(studentStatus);
        studentDetails.setDocument(request.getDocument());
        studentDetails.setDocumentStatus(request.getDocument() != null ? DocumentStatus.COMPLETE : DocumentStatus.INCOMPLETE);
        studentDetails.setRemark(request.getDocument() != null ? null : "Document not provided");
        studentDetails.setTransfer(request.isTransfer());
        studentDetails.setExitExamUserID(request.getExitExamUserID());
        studentDetails.setExitExamScore(request.getExitExamScore());
        studentDetails.setStudentPassExitExam(request.isStudentPassExitExam());

        return studentDetailsRepository.save(studentDetails);
    }
}