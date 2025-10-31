package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherCourseResponse;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.TeacherResponseDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.TeacherDetail;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public ResponseEntity<List<TeacherResponseDTO>> getAll() {
        return ResponseEntity.ok(teacherService.getAllTeachers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getTeacherById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(
            @RequestPart("data") TeacherRegisterRequest request,
            @RequestPart(name = "photograph", required = false) MultipartFile photograph,
            @RequestPart(name = "document", required = false) MultipartFile document) {
        try {
            TeacherDetail saved = teacherService.registerTeacher(request, photograph, document);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved.getId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestPart("data") TeacherRegisterRequest request,
            @RequestPart(name = "photograph", required = false) MultipartFile photograph,
            @RequestPart(name = "document", required = false) MultipartFile document) {
        try {
            TeacherDetail updated = teacherService.updateTeacher(id, request, photograph, document);
            return ResponseEntity.ok(toResponseDto(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.deleteTeacher(id);
        return ResponseEntity.noContent().build();
    }

    // Helper to avoid duplication
    private TeacherResponseDTO toResponseDto(TeacherDetail t) {
        return teacherService.toResponseDto(t);
    }

    // Returns all courses a Teacher teaches
    @GetMapping("/{teacherId}/courses")
    public ResponseEntity<List<TeacherCourseResponse>> getTeacherCourses(@PathVariable Long teacherId) {
        return ResponseEntity.ok(teacherService.getTeacherCourses(teacherId));
    }
}