package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AppliedStudentResponseDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.AppliedStudentRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.AppliedStudent;
import Henok.example.DeutscheCollageBack_endAPI.Enums.ApplicationStatus;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.AppliedStudentRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.ClassYearRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.MOE_Repos.*;
import Henok.example.DeutscheCollageBack_endAPI.Repository.SchoolBackgroundRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppliedStudentService {

    @Autowired
    private AppliedStudentRepository appliedStudentRepository;

    @Autowired
    private WoredaRepository woredaRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private SchoolBackgroundRepository schoolBackgroundRepository;

    @Autowired
    private DepartmentRepo departmentRepository;

    @Autowired
    private ProgramModalityRepository programModalityRepository;

    @Autowired
    private ClassYearRepository classYearRepository;

    @Autowired
    private SemesterRepo semesterRepository;

    @Autowired
    private ImpairmentRepository impairmentRepository;

    /**
     * Registers a new applicant with the provided details and uploaded document.
     * @param request The DTO containing applicant details.
     * @param document The uploaded document (optional).
     * @return The saved AppliedStudent entity.
     * @throws IllegalArgumentException if input validation fails.
     * @throws ResourceNotFoundException if referenced entities are not found.
     */
    @Transactional
    public AppliedStudent registerApplicant(AppliedStudentRegisterRequest request, MultipartFile document) {
        // Validate required fields
        validateRequest(request);

        // Check for unique phone number
        if (appliedStudentRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number already exists: " + request.getPhoneNumber());
        }

        // Map DTO to entity
        AppliedStudent applicant = new AppliedStudent();
        applicant.setFirstNameAMH(request.getFirstNameAMH());
        applicant.setFirstNameENG(request.getFirstNameENG());
        applicant.setFatherNameAMH(request.getFatherNameAMH());
        applicant.setFatherNameENG(request.getFatherNameENG());
        applicant.setGrandfatherNameAMH(request.getGrandfatherNameAMH());
        applicant.setGrandfatherNameENG(request.getGrandfatherNameENG());
        applicant.setMotherNameAMH(request.getMotherNameAMH());
        applicant.setMotherNameENG(request.getMotherNameENG());
        applicant.setMotherFatherNameAMH(request.getMotherFatherNameAMH());
        applicant.setMotherFatherNameENG(request.getMotherFatherNameENG());
        applicant.setGender(request.getGender());
        applicant.setAge(request.getAge());
        applicant.setPhoneNumber(request.getPhoneNumber());
        applicant.setDateOfBirthEC(request.getDateOfBirthEC());
        applicant.setDateOfBirthGC(request.getDateOfBirthGC());
        applicant.setEmail(request.getEmail());
        applicant.setMaritalStatus(request.getMaritalStatus());
        applicant.setContactPersonFirstNameAMH(request.getContactPersonFirstNameAMH());
        applicant.setContactPersonFirstNameENG(request.getContactPersonFirstNameENG());
        applicant.setContactPersonLastNameAMH(request.getContactPersonLastNameAMH());
        applicant.setContactPersonLastNameENG(request.getContactPersonLastNameENG());
        applicant.setContactPersonPhoneNumber(request.getContactPersonPhoneNumber());
        applicant.setContactPersonRelation(request.getContactPersonRelation());

        // Set foreign key references
        applicant.setPlaceOfBirthWoreda(woredaRepository.findById(request.getPlaceOfBirthWoredaCode())
                .orElseThrow(() -> new ResourceNotFoundException("Woreda not found with code: " + request.getPlaceOfBirthWoredaCode())));
        applicant.setPlaceOfBirthZone(zoneRepository.findById(request.getPlaceOfBirthZoneCode())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with code: " + request.getPlaceOfBirthZoneCode())));
        applicant.setPlaceOfBirthRegion(regionRepository.findById(request.getPlaceOfBirthRegionCode())
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with code: " + request.getPlaceOfBirthRegionCode())));
        applicant.setCurrentAddressWoreda(woredaRepository.findById(request.getCurrentAddressWoredaCode())
                .orElseThrow(() -> new ResourceNotFoundException("Woreda not found with code: " + request.getCurrentAddressWoredaCode())));
        applicant.setCurrentAddressZone(zoneRepository.findById(request.getCurrentAddressZoneCode())
                .orElseThrow(() -> new ResourceNotFoundException("Zone not found with code: " + request.getCurrentAddressZoneCode())));
        applicant.setCurrentAddressRegion(regionRepository.findById(request.getCurrentAddressRegionCode())
                .orElseThrow(() -> new ResourceNotFoundException("Region not found with code: " + request.getCurrentAddressRegionCode())));
        applicant.setSchoolBackground(schoolBackgroundRepository.findById(request.getSchoolBackgroundId())
                .orElseThrow(() -> new ResourceNotFoundException("SchoolBackground not found with id: " + request.getSchoolBackgroundId())));
        applicant.setDepartmentEnrolled(departmentRepository.findById(request.getDepartmentEnrolledId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + request.getDepartmentEnrolledId())));
        applicant.setProgramModality(programModalityRepository.findById(request.getProgramModalityCode())
                .orElseThrow(() -> new ResourceNotFoundException("ProgramModality not found with code: " + request.getProgramModalityCode())));
        applicant.setClassYear(classYearRepository.findById(request.getClassYearId())
                .orElseThrow(() -> new ResourceNotFoundException("ClassYear not found with id: " + request.getClassYearId())));
        applicant.setSemester(semesterRepository.findById(request.getSemesterCode())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with code: " + request.getSemesterCode())));

        // Set optional impairment
        if (request.getImpairmentCode() != null && !request.getImpairmentCode().isEmpty()) {
            applicant.setImpairment(impairmentRepository.findById(request.getImpairmentCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Impairment not found with code: " + request.getImpairmentCode())));
        }

        // Set document if provided
        if (document != null && !document.isEmpty()) {
            try {
                applicant.setDocument(document.getBytes());
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to process document: " + e.getMessage());
            }
        }

        // Default status is PENDING (set in entity)
        return appliedStudentRepository.save(applicant);
    }

    /**
     * Updates the application status of an applicant.
     * @param applicantId The ID of the applicant.
     * @param newStatus The new status (PENDING, ACCEPTED, REJECTED).
     * @return The updated AppliedStudent entity.
     * @throws ResourceNotFoundException if the applicant is not found.
     * @throws IllegalArgumentException if the status is invalid.
     */
    @Transactional
    public AppliedStudent updateApplicationStatus(Long applicantId, ApplicationStatus newStatus) {
        AppliedStudent applicant = appliedStudentRepository.findById(applicantId)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found with id: " + applicantId));

        if (newStatus == null) {
            throw new IllegalArgumentException("Application status cannot be null");
        }

        applicant.setApplicationStatus(newStatus);
        return appliedStudentRepository.save(applicant);
    }

    /**
     * Retrieves all applicants.
     * @return A list of AppliedStudentResponseDTOs.
     * @throws ResourceNotFoundException if no applicants are found.
     */
    public List<AppliedStudentResponseDTO> getAllApplicants() {
        List<AppliedStudent> applicants = appliedStudentRepository.findAll();
        if (applicants.isEmpty()) {
            throw new ResourceNotFoundException("No applicants found");
        }
        return applicants.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an applicant by ID.
     * @param id The ID of the applicant.
     * @return The AppliedStudentResponseDTO for the applicant.
     * @throws ResourceNotFoundException if the applicant is not found.
     */
    public AppliedStudentResponseDTO getApplicantById(Long id) {
        AppliedStudent applicant = appliedStudentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Applicant not found with id: " + id));
        return mapToResponseDTO(applicant);
    }

    /**
     * Maps an AppliedStudent entity to its response DTO.
     * @param applicant The AppliedStudent entity.
     * @return The corresponding AppliedStudentResponseDTO.
     */
    private AppliedStudentResponseDTO mapToResponseDTO(AppliedStudent applicant) {
        AppliedStudentResponseDTO dto = new AppliedStudentResponseDTO();
        dto.setId(applicant.getId());
        dto.setFirstNameAMH(applicant.getFirstNameAMH());
        dto.setFirstNameENG(applicant.getFirstNameENG());
        dto.setFatherNameAMH(applicant.getFatherNameAMH());
        dto.setFatherNameENG(applicant.getFatherNameENG());
        dto.setGrandfatherNameAMH(applicant.getGrandfatherNameAMH());
        dto.setGrandfatherNameENG(applicant.getGrandfatherNameENG());
        dto.setMotherNameAMH(applicant.getMotherNameAMH());
        dto.setMotherNameENG(applicant.getMotherNameENG());
        dto.setMotherFatherNameAMH(applicant.getMotherFatherNameAMH());
        dto.setMotherFatherNameENG(applicant.getMotherFatherNameENG());
        dto.setGender(applicant.getGender());
        dto.setAge(applicant.getAge());
        dto.setPhoneNumber(applicant.getPhoneNumber());
        dto.setDateOfBirthEC(applicant.getDateOfBirthEC());
        dto.setDateOfBirthGC(applicant.getDateOfBirthGC());
        dto.setPlaceOfBirthWoredaCode(applicant.getPlaceOfBirthWoreda().getWoredaCode());
        dto.setPlaceOfBirthZoneCode(applicant.getPlaceOfBirthZone().getZoneCode());
        dto.setPlaceOfBirthRegionCode(applicant.getPlaceOfBirthRegion().getRegionCode());
        dto.setCurrentAddressWoredaCode(applicant.getCurrentAddressWoreda().getWoredaCode());
        dto.setCurrentAddressZoneCode(applicant.getCurrentAddressZone().getZoneCode());
        dto.setCurrentAddressRegionCode(applicant.getCurrentAddressRegion().getRegionCode());
        dto.setEmail(applicant.getEmail());
        dto.setMaritalStatus(applicant.getMaritalStatus());
        dto.setImpairmentCode(applicant.getImpairment() != null ? applicant.getImpairment().getImpairmentCode() : null);
        dto.setSchoolBackgroundId(applicant.getSchoolBackground().getId());
        dto.setContactPersonFirstNameAMH(applicant.getContactPersonFirstNameAMH());
        dto.setContactPersonFirstNameENG(applicant.getContactPersonFirstNameENG());
        dto.setContactPersonLastNameAMH(applicant.getContactPersonLastNameAMH());
        dto.setContactPersonLastNameENG(applicant.getContactPersonLastNameENG());
        dto.setContactPersonPhoneNumber(applicant.getContactPersonPhoneNumber());
        dto.setContactPersonRelation(applicant.getContactPersonRelation());
        dto.setDepartmentEnrolledId(applicant.getDepartmentEnrolled().getDptID());
        dto.setProgramModalityCode(applicant.getProgramModality().getModalityCode());
        dto.setClassYearId(applicant.getClassYear().getId());
        dto.setSemesterCode(applicant.getSemester().getAcademicPeriodCode());
        dto.setApplicationStatus(applicant.getApplicationStatus());
        dto.setHasDocument(applicant.getDocument() != null);
        return dto;
    }

    /**
     * Validates the registration request for required fields.
     * @param request The DTO to validate.
     * @throws IllegalArgumentException if validation fails.
     */
    private void validateRequest(AppliedStudentRegisterRequest request) {
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
        if (request.getPlaceOfBirthWoredaCode() == null || request.getPlaceOfBirthWoredaCode().isEmpty()) {
            throw new IllegalArgumentException("Place of birth woreda cannot be empty");
        }
        if (request.getPlaceOfBirthZoneCode() == null || request.getPlaceOfBirthZoneCode().isEmpty()) {
            throw new IllegalArgumentException("Place of birth zone cannot be empty");
        }
        if (request.getPlaceOfBirthRegionCode() == null || request.getPlaceOfBirthRegionCode().isEmpty()) {
            throw new IllegalArgumentException("Place of birth region cannot be empty");
        }
        if (request.getCurrentAddressWoredaCode() == null || request.getCurrentAddressWoredaCode().isEmpty()) {
            throw new IllegalArgumentException("Current address woreda cannot be empty");
        }
        if (request.getCurrentAddressZoneCode() == null || request.getCurrentAddressZoneCode().isEmpty()) {
            throw new IllegalArgumentException("Current address zone cannot be empty");
        }
        if (request.getCurrentAddressRegionCode() == null || request.getCurrentAddressRegionCode().isEmpty()) {
            throw new IllegalArgumentException("Current address region cannot be empty");
        }
        if (request.getSchoolBackgroundId() == null) {
            throw new IllegalArgumentException("School background cannot be null");
        }
        if (request.getDepartmentEnrolledId() == null) {
            throw new IllegalArgumentException("Department cannot be null");
        }
        if (request.getProgramModalityCode() == null || request.getProgramModalityCode().isEmpty()) {
            throw new IllegalArgumentException("Program modality cannot be empty");
        }
        if (request.getClassYearId() == null) {
            throw new IllegalArgumentException("Class year cannot be null");
        }
        if (request.getSemesterCode() == null || request.getSemesterCode().isEmpty()) {
            throw new IllegalArgumentException("Semester cannot be empty");
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
    }
}
