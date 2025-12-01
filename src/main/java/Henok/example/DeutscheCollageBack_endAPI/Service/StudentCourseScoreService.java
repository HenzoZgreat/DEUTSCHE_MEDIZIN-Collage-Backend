package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.GradeDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseScore.StudentCourseScoreDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseScore.StudentCourseScoreResponseDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseScore.StudentCourseScoreBulkUpdateDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentCourseScoreService {

    @Autowired
    private StudentCourseScoreRepo studentCourseScoreRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private BatchClassYearSemesterRepo batchClassYearSemesterRepo;

    @Autowired
    private CourseSourceRepo courseSourceRepo;

    @Autowired
    private GradingSystemService gradingSystemService;

    @Autowired
    private StudentDetailsRepository studentDetailsRepository; // Assume exists for fetching student details

    public void addCourse(StudentCourseScoreDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Student course score DTO cannot be null");
        }

        User student = userRepo.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + dto.getStudentId()));
        Course course = courseRepo.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + dto.getCourseId()));
        BatchClassYearSemester bcys = batchClassYearSemesterRepo.findById(dto.getBatchClassYearSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + dto.getBatchClassYearSemesterId()));
        CourseSource courseSource = courseSourceRepo.findById(dto.getSourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Course source not found with id: " + dto.getSourceId()));

        if (studentCourseScoreRepo.existsByStudentAndCourseAndBatchClassYearSemester(student, course, bcys)) {
            throw new IllegalArgumentException("Student is already enrolled in this course for the given semester");
        }

        for (Course prereq : course.getPrerequisites()) {
            if (!studentCourseScoreRepo.findByStudentAndCourseAndBatchClassYearSemester(student, prereq, null)
                    .map(sc -> sc.getScore() != null && sc.getScore() >= 50).orElse(false)) {
                throw new IllegalArgumentException("Prerequisite course " + prereq.getCCode() + " not completed with passing score");
            }
        }

        StudentCourseScore studentCourseScore = new StudentCourseScore(null, student, course, bcys, courseSource, null, false);
        studentCourseScoreRepo.save(studentCourseScore);
    }

    public void updateScore(Long studentId, Long courseId, Long batchClassYearSemesterId, Double score) {
        if (score != null && (score < 0 || score > 100)) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }

        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        BatchClassYearSemester bcys = batchClassYearSemesterRepo.findById(batchClassYearSemesterId)
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + batchClassYearSemesterId));

        StudentCourseScore studentCourseScore = studentCourseScoreRepo.findByStudentAndCourseAndBatchClassYearSemester(student, course, bcys)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student " + studentId + " and course " + courseId));

        studentCourseScore.setScore(score);
        studentCourseScoreRepo.save(studentCourseScore);
    }

    public void releaseScore(Long studentId, Long courseId, Long batchClassYearSemesterId, boolean isReleased) {
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        BatchClassYearSemester bcys = batchClassYearSemesterRepo.findById(batchClassYearSemesterId)
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + batchClassYearSemesterId));

        StudentCourseScore studentCourseScore = studentCourseScoreRepo.findByStudentAndCourseAndBatchClassYearSemester(student, course, bcys)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student " + studentId + " and course " + courseId));

        studentCourseScore.setReleased(isReleased);
        studentCourseScoreRepo.save(studentCourseScore);
    }

    public List<StudentCourseScore> getStudentScores(Long studentId) {
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        List<StudentCourseScore> scores = studentCourseScoreRepo.findByStudentAndIsReleasedTrue(student);
        if (scores.isEmpty()) {
            throw new ResourceNotFoundException("No released scores found for student " + studentId);
        }
        return scores;
    }

    /**
     * Computes the grade for a student course score based on the student's recent department.
     * @param scoreId The score ID.
     * @return GradeDTO with letter grade and GPA value.
     * @throws ResourceNotFoundException if score or student details not found.
     * @throws IllegalStateException if grading system or interval missing.
     */
    public GradeDTO getGrade(Long scoreId) {
        StudentCourseScore score = studentCourseScoreRepo.findById(scoreId)
                .orElseThrow(() -> new ResourceNotFoundException("Score not found with id: " + scoreId));

        // Get student details to fetch recent department
        StudentDetails studentDetails = studentDetailsRepository.findByUser(score.getStudent())
                .orElseThrow(() -> new ResourceNotFoundException("Student details not found for student id: " + score.getStudent().getId()));

        Department department = studentDetails.getDepartmentEnrolled();

        // Resolve latest applicable grading system (department-specific or global fallback)
        GradingSystem gs = gradingSystemService.findApplicableGradingSystem(department);

        // Find matching interval
        MarkInterval interval = gs.getIntervals().stream()
                .filter(i -> score.getScore() >= i.getMin() && score.getScore() <= i.getMax())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No matching interval for score: " + score.getScore()));

        return new GradeDTO(interval.getGradeLetter(), interval.getGivenValue());
    }

    // Explanation: Computes grade dynamically using student's recent department for grading system resolution.
    // Why: Handles department-specific grading with global fallback; uses latest system as per requirement.
    // Additional methods: For score CRUD as per existing system.

    public List<StudentCourseScoreResponseDTO> getAllStudentCourseScores() {
        List<StudentCourseScore> scores = studentCourseScoreRepo.findAll();
        return scores.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private StudentCourseScoreResponseDTO mapToResponseDTO(StudentCourseScore score) {
        StudentCourseScoreResponseDTO dto = new StudentCourseScoreResponseDTO();
        dto.setId(score.getId());
        dto.setStudentId(score.getStudent().getId());
        
        StudentCourseScoreResponseDTO.CourseInfo courseInfo = new StudentCourseScoreResponseDTO.CourseInfo(
                score.getCourse().getCID(),
                score.getCourse().getCTitle()
        );
        dto.setCourse(courseInfo);
        
        StudentCourseScoreResponseDTO.BCYSInfo bcysInfo = new StudentCourseScoreResponseDTO.BCYSInfo(
                score.getBatchClassYearSemester().getBcysID(),
                score.getBatchClassYearSemester().getDisplayName()
        );
        dto.setBcys(bcysInfo);
        
        StudentCourseScoreResponseDTO.CourseSourceInfo courseSourceInfo = new StudentCourseScoreResponseDTO.CourseSourceInfo(
                score.getCourseSource().getSourceID(),
                score.getCourseSource().getSourceName()
        );
        dto.setCourseSource(courseSourceInfo);
        
        dto.setScore(score.getScore());
        dto.setIsReleased(score.isReleased());
        
        return dto;
    }

    public void bulkUpdateStudentCourseScores(StudentCourseScoreBulkUpdateDTO bulkUpdateDTO) {
        if (bulkUpdateDTO == null || bulkUpdateDTO.getUpdates() == null || bulkUpdateDTO.getUpdates().isEmpty()) {
            throw new IllegalArgumentException("Bulk update DTO cannot be null or empty");
        }

        for (StudentCourseScoreBulkUpdateDTO.StudentCourseScoreUpdateRequest updateRequest : bulkUpdateDTO.getUpdates()) {
            if (updateRequest.getId() == null) {
                throw new IllegalArgumentException("ID is required for each update request");
            }

            StudentCourseScore studentCourseScore = studentCourseScoreRepo.findById(updateRequest.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student course score not found with id: " + updateRequest.getId()));

            // Update score only if provided (not null)
            if (updateRequest.getScore() != null) {
                if (updateRequest.getScore() < 0 || updateRequest.getScore() > 100) {
                    throw new IllegalArgumentException("Score must be between 0 and 100 for record with id: " + updateRequest.getId());
                }
                studentCourseScore.setScore(updateRequest.getScore());
            }

            // Update isReleased only if provided (not null)
            if (updateRequest.getIsReleased() != null) {
                studentCourseScore.setReleased(updateRequest.getIsReleased());
            }

            studentCourseScoreRepo.save(studentCourseScore);
        }
    }

}
