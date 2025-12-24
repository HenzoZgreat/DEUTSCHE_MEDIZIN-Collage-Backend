package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.AssessmentScoresResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentScoreRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores.BulkStudentScoreRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherPortal.AssessmentsAndScores.BulkScoreRecordResponse;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// StudentAssessmentService
@Service
@RequiredArgsConstructor
public class StudentAssessmentService {

    private final AssessmentRepo assessmentRepository;
    private final StudentDetailsRepository studentDetailsRepository;
    private final StudentAssessmentRepo studentAssessmentRepository;
    private final TeacherCourseAssignmentRepository tcaRepository;
    private final TeacherRepository teacherDetailRepository;
    private final StudentCourseScoreRepo studentCourseScoreRepo;

    // Records a single student score for an assessment
    // Why: Ensures score ≤ maxScore and teacher owns the assessment
    @Transactional
    public StudentAssessment recordScore(User authenticatedUser, StudentScoreRequest request) {

        // Step 1: Get authenticated teacher
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        // Step 2: Load Assessment and validate ownership
        Assessment assessment = assessmentRepository.findById(request.getAssessmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (!assessment.getTeacherCourseAssignment().getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You are not authorized to grade this assessment");
        }

        // Step 3: Validate score ≤ maxScore
        if (request.getScore() > assessment.getMaxScore()) {
            throw new IllegalArgumentException(
                    "Score (" + request.getScore() + ") exceeds max score (" + assessment.getMaxScore() + ")"
            );
        }

        // Step 4: Load Student
        StudentDetails student = studentDetailsRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Step 5: Prevent duplicate score
        StudentAssessmentKey key = new StudentAssessmentKey(student.getId(), assessment.getAssID());
        if (studentAssessmentRepository.existsById(key)) {
            throw new IllegalArgumentException("Score already recorded for this student and assessment");
        }

        // Step 6: Save score
        StudentAssessment sa = new StudentAssessment();
        sa.setId(key);
        sa.setStudent(student);
        sa.setAssessment(assessment);
        sa.setScore(request.getScore());
        sa.setGradedAt(LocalDateTime.now());

        return studentAssessmentRepository.save(sa);
    }

    // Add this method to your StudentAssessmentService (or TeacherService)
    @Transactional
    public BulkScoreRecordResponse recordBulkScores(User authenticatedUser, List<BulkStudentScoreRequest.SingleScore> records) {

        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        BulkScoreRecordResponse response = new BulkScoreRecordResponse();
        int successCount = 0;

        for (BulkStudentScoreRequest.SingleScore record : records) {
            try {
                // Per-item validation
                if (record.getAssessmentId() == null || record.getStudentId() == null) {
                    throw new IllegalArgumentException("Assessment ID and Student ID are required");
                }
                if (record.getScore() == null || record.getScore() < 0) {
                    throw new IllegalArgumentException("Score must be non-negative");
                }

                // Load and validate assessment ownership
                Assessment assessment = assessmentRepository.findById(record.getAssessmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + record.getAssessmentId()));

                if (!assessment.getTeacherCourseAssignment().getTeacher().getId().equals(teacher.getId())) {
                    throw new IllegalArgumentException("Not authorized for assessment: " + record.getAssessmentId());
                }

                // Validate score against maxScore
                if (record.getScore() > assessment.getMaxScore()) {
                    throw new IllegalArgumentException(
                            "Score (" + record.getScore() + ") exceeds max score (" + assessment.getMaxScore() + ")");
                }

                // Load student
                StudentDetails student = studentDetailsRepository.findById(record.getStudentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + record.getStudentId()));

                // Prevent duplicate entry
                StudentAssessmentKey key = new StudentAssessmentKey(record.getStudentId(), record.getAssessmentId());
                if (studentAssessmentRepository.existsById(key)) {
                    throw new IllegalArgumentException("Score already recorded for student " + record.getStudentId() +
                            " on assessment " + record.getAssessmentId());
                }

                // Create and save new StudentAssessment
                StudentAssessment sa = new StudentAssessment();
                sa.setId(key);
                sa.setStudent(student);
                sa.setAssessment(assessment);
                sa.setScore(record.getScore());
                sa.setGradedAt(LocalDateTime.now());

                studentAssessmentRepository.save(sa);
                successCount++;

            } catch (Exception e) {
                // Collect failure, continue with others
                BulkScoreRecordResponse.FailedRecord failure = new BulkScoreRecordResponse.FailedRecord();
                failure.setAssessmentId(record.getAssessmentId());
                failure.setStudentId(record.getStudentId());
                failure.setReason(e.getMessage());
                response.getFailedRecords().add(failure);
            }
        }

        response.setRecordedCount(successCount);
        return response;
    }

    // Updates an existing score
    // Why: Ensures score ≤ maxScore, teacher ownership, and that the record exists
    @Transactional
    public StudentAssessment updateScore(User authenticatedUser, Long assessmentId, Long studentId, Double newScore) {

        // Step 1: Get authenticated teacher
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        // Step 2: Load Assessment and check ownership
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment not found"));

        if (!assessment.getTeacherCourseAssignment().getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You are not authorized to update scores for this assessment");
        }

        // Step 3: Validate new score against maxScore
        if (newScore > assessment.getMaxScore()) {
            throw new IllegalArgumentException(
                    "New score (" + newScore + ") exceeds max score (" + assessment.getMaxScore() + ")"
            );
        }

        // Step 4: Load Student
        StudentDetails student = studentDetailsRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        // Step 5: Find existing StudentAssessment
        StudentAssessmentKey key = new StudentAssessmentKey(studentId, assessmentId);
        StudentAssessment sa = studentAssessmentRepository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException("Score record not found – create it first"));

        // Step 6: Update score and timestamp
        sa.setScore(newScore);
        sa.setGradedAt(LocalDateTime.now());

        return studentAssessmentRepository.save(sa);
    }

    // Updates Multiple existing scores
    @Transactional
    public BulkScoreRecordResponse updateBulkScores(User authenticatedUser, List<BulkStudentScoreRequest.SingleScore> updates) {

        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        BulkScoreRecordResponse response = new BulkScoreRecordResponse();
        int successCount = 0;

        for (BulkStudentScoreRequest.SingleScore update : updates) {
            try {
                // Basic per-item validation
                if (update.getAssessmentId() == null || update.getStudentId() == null) {
                    throw new IllegalArgumentException("Assessment ID and Student ID are required");
                }
                if (update.getScore() == null || update.getScore() < 0) {
                    throw new IllegalArgumentException("Score must be non-negative");
                }

                // Load assessment and check ownership
                Assessment assessment = assessmentRepository.findById(update.getAssessmentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Assessment not found: " + update.getAssessmentId()));

                if (!assessment.getTeacherCourseAssignment().getTeacher().getId().equals(teacher.getId())) {
                    throw new IllegalArgumentException("Not authorized for assessment: " + update.getAssessmentId());
                }

                // Validate score <= maxScore
                if (update.getScore() > assessment.getMaxScore()) {
                    throw new IllegalArgumentException(
                            "Score (" + update.getScore() + ") exceeds max score (" + assessment.getMaxScore() + ")");
                }

                // Load student
                StudentDetails student = studentDetailsRepository.findById(update.getStudentId())
                        .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + update.getStudentId()));

                // Find or create StudentAssessment
                StudentAssessmentKey key = new StudentAssessmentKey(update.getStudentId(), update.getAssessmentId());
                StudentAssessment sa = studentAssessmentRepository.findById(key)
                        .orElseGet(() -> {
                            StudentAssessment newSa = new StudentAssessment();
                            newSa.setId(key);
                            newSa.setStudent(student);
                            newSa.setAssessment(assessment);
                            return newSa;
                        });

                // Update score and timestamp
                sa.setScore(update.getScore());
                sa.setGradedAt(LocalDateTime.now());

                studentAssessmentRepository.save(sa);
                successCount++;

            } catch (Exception e) {
                // Collect failure but continue processing others
                BulkScoreRecordResponse.FailedRecord failure = new BulkScoreRecordResponse.FailedRecord();
                failure.setAssessmentId(update.getAssessmentId());
                failure.setStudentId(update.getStudentId());
                failure.setReason(e.getMessage());
                response.getFailedRecords().add(failure);
            }
        }

        response.setRecordedCount(successCount);
        return response;
    }

    @Transactional(readOnly = true)
    public AssessmentScoresResponse getScores(User authenticatedUser, Long teacherCourseAssignmentId) {

        // Step 1: Get authenticated teacher
        TeacherDetail teacher = teacherDetailRepository.findByUser(authenticatedUser)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));

        // Step 2: Load TeacherCourseAssignment and validate ownership
        TeacherCourseAssignment tca = tcaRepository.findById(teacherCourseAssignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Course assignment not found"));

        if (!tca.getTeacher().getId().equals(teacher.getId())) {
            throw new IllegalArgumentException("You are not assigned to this course or batch");
        }

        // Step 3: Build response header info
        AssessmentScoresResponse response = new AssessmentScoresResponse();
        response.setTeacherCourseAssignmentId(tca.getId());
        response.setTeacherName(tca.getTeacher().getFirstNameEnglish() + " " + tca.getTeacher().getLastNameEnglish());
        response.setCourseCode(tca.getCourse().getCCode());
        response.setCourseTitle(tca.getCourse().getCTitle());
        response.setBatchClassYearSemester(tca.getBcys().getDisplayName()); // assuming you have a display method

        // Step 4: Get all assessments for this assignment, ordered by creation
        List<Assessment> assessments = assessmentRepository.findByTeacherCourseAssignmentOrderByCreatedAtAsc(tca);

        // Map to AssessmentInfo DTOs
        List<AssessmentScoresResponse.AssessmentInfo> assessmentInfos = assessments.stream()
                .map(a -> {
                    AssessmentScoresResponse.AssessmentInfo info = new AssessmentScoresResponse.AssessmentInfo();
                    info.setAssessmentId(a.getAssID());
                    info.setTitle(a.getAssTitle());
                    info.setMaxScore(a.getMaxScore());
                    info.setDueDate(a.getDueDate());
                    info.setStatus(a.getAssStatus());
                    info.setHeadApproval(a.getHeadApproval());
                    return info;
                })
                .toList();
        response.setAssessments(assessmentInfos);

        // Step 5: Get all students enrolled in this course and BCYS via StudentCourseScore
        // Find all StudentCourseScore records matching the course and BCYS from the teacherCourseAssignment
        List<StudentCourseScore> courseScores = studentCourseScoreRepo.findByCourseAndBatchClassYearSemester(
                tca.getCourse(),
                tca.getBcys()
        );
        
        // Extract unique User objects from the StudentCourseScore records
        Set<User> uniqueUsers = courseScores.stream()
                .map(StudentCourseScore::getStudent)
                .collect(Collectors.toSet());
        
        // Load StudentDetails for all unique users
        List<StudentDetails> students = uniqueUsers.stream()
                .map(studentDetailsRepository::findByUser)
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .collect(Collectors.toList());

        // Step 6: For each student, build their scores list (matching assessment order)
        List<AssessmentScoresResponse.StudentScoreView> studentViews = students.stream().map(student -> {
            AssessmentScoresResponse.StudentScoreView view = new AssessmentScoresResponse.StudentScoreView();
            view.setStudentId(student.getId());
            // Adjust these fields according to your actual StudentDetails columns
            view.setStudentIdNumber(student.getUser().getUsername());
            view.setFullNameENG(student.getFirstNameENG() + " " + student.getFatherNameENG() + " " + student.getGrandfatherNameENG());
            view.setFullNameAMH(student.getFirstNameAMH() + " " + student.getFatherNameAMH() + " " + student.getGrandfatherNameAMH());

            List<AssessmentScoresResponse.SingleScore> scores = assessments.stream().map(ass -> {
                StudentAssessmentKey key = new StudentAssessmentKey(student.getId(), ass.getAssID());
                Double score = studentAssessmentRepository.findById(key)
                        .map(StudentAssessment::getScore)
                        .orElse(null);
                return new AssessmentScoresResponse.SingleScore(ass.getAssID(), score);
            }).toList();

            view.setScores(scores);
            return view;
        }).toList();

        response.setStudents(studentViews);

        return response;
    }
}
