package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCourseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentCourseService {

    @Autowired
    private StudentCourseRepo studentCourseRepo;

    @Autowired
    private StudentDetailsRepository studentDetailsRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private BatchClassYearSemesterRepo batchClassYearSemesterRepo;

    @Autowired
    private CourseSourceRepo courseSourceRepo;

    public void addCourse(StudentCourseDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Student course DTO cannot be null");
        }

        StudentDetails student = studentDetailsRepo.findById(dto.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + dto.getStudentId()));
        Course course = courseRepo.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + dto.getCourseId()));
        BatchClassYearSemester bcys = batchClassYearSemesterRepo.findById(dto.getBatchClassYearSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("BatchClassYearSemester not found with id: " + dto.getBatchClassYearSemesterId()));
        CourseSource courseSource = courseSourceRepo.findById(dto.getSourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Course source not found with id: " + dto.getSourceId()));

        if (studentCourseRepo.existsByStudentIdAndCourseIdAndBatchClassYearSemesterId(
                dto.getStudentId(), dto.getCourseId(), dto.getBatchClassYearSemesterId())) {
            throw new IllegalArgumentException("Student is already enrolled in this course for the given semester");
        }

        for (Course prereq : course.getPrerequisites()) {
            if (!studentCourseRepo.findByStudentIdAndCourseIdAndBatchClassYearSemesterId(dto.getStudentId(), prereq.getCID(), null)
                    .map(sc -> sc.getScore() != null && sc.getScore() >= 50).orElse(false)) {
                throw new IllegalArgumentException("Prerequisite course " + prereq.getCCode() + " not completed with passing score");
            }
        }

        StudentCourse studentCourse = new StudentCourse(null, student, course, bcys, courseSource, null, false);
        studentCourseRepo.save(studentCourse);
    }

    public void updateScore(Long studentId, Long courseId, Long batchClassYearSemesterId, Double score) {
        if (score != null && (score < 0 || score > 100)) {
            throw new IllegalArgumentException("Score must be between 0 and 100");
        }

        StudentCourse studentCourse = studentCourseRepo.findByStudentIdAndCourseIdAndBatchClassYearSemesterId(studentId, courseId, batchClassYearSemesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student " + studentId + " and course " + courseId));

        studentCourse.setScore(score);
        studentCourseRepo.save(studentCourse);
    }

    public void releaseScore(Long studentId, Long courseId, Long batchClassYearSemesterId, boolean isReleased) {
        StudentCourse studentCourse = studentCourseRepo.findByStudentIdAndCourseIdAndBatchClassYearSemesterId(studentId, courseId, batchClassYearSemesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student " + studentId + " and course " + courseId));

        studentCourse.setIsReleased(isReleased);
        studentCourseRepo.save(studentCourse);
    }

    public List<StudentCourse> getStudentScores(Long studentId) {
        studentDetailsRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
        List<StudentCourse> scores = studentCourseRepo.findByStudentIdAndIsReleasedTrue(studentId);
        if (scores.isEmpty()) {
            throw new ResourceNotFoundException("No released scores found for student " + studentId);
        }
        return scores;
    }
}