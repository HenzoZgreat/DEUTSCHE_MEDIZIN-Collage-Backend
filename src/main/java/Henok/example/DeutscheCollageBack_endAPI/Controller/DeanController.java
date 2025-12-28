package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean.*;
import Henok.example.DeutscheCollageBack_endAPI.DTO.StudentCGPADTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Service.DeanViceDeanService;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deans")
@RequiredArgsConstructor
public class DeanController {

    private final DeanViceDeanService deanViceDeanService;
    private final StudentDetailService studentDetailService;


    // -----------[Get All Active Deans]------------------
    // description - Retrieves list of all active Deans.
    // endpoint - GET /api/deans/active
    // success response - 200 OK with List<DeanViceDeanListDTO>
    // ErrorResponse - { "error": "message" } (500 if unexpected)
    @GetMapping("/active")
    public ResponseEntity<?> getAllActiveDeans() {
        try {
            List<DeanViceDeanListDTO> deans = deanViceDeanService.getAllActiveByRole(Role.DEAN);
            return ResponseEntity.ok(deans);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred while fetching active Deans");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //Retrieves full details of a specific Dean by their details ID.
    //Includes residential address with names and codes.
    //Returns hasPhoto and hasDocument flags instead of binary data.
    @GetMapping("/{id}")
    public ResponseEntity<?> getDeanById(@PathVariable Long id) {
        try {
            DeanViceDeanDetailDTO detail = deanViceDeanService.getDetailById(id, Role.DEAN);
            return ResponseEntity.ok(detail);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal User user,
            @RequestPart("data") DeanViceDeanUpdateRequest request,
            @RequestPart(name = "photograph", required = false) MultipartFile photograph) {
        
        try {
            if (user.getRole() != Role.DEAN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
            }
            
            deanViceDeanService.updateSelf(user, request, photograph);
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred during update");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ==================== GET FILES ====================
    @GetMapping(value = "/get-photo/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<?> getPhoto(@PathVariable Long id) {
        try {
            byte[] photo = deanViceDeanService.getDeanViceDeanPhoto(id, Role.DEAN);
            if (photo == null || photo.length == 0) {
                Map<String, String> error = new HashMap<>(); 
                error.put("error", "Photo not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>(); 
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>(); 
            error.put("error", "Failed to retrieve photo");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping(value = "/get-document/{id}")
    public ResponseEntity<?> getDocumentById(@PathVariable Long id) {
        try {
            byte[] doc = deanViceDeanService.getDeanViceDeanDocument(id, Role.DEAN);
            if (doc == null || doc.length == 0) {
                Map<String, String> error = new HashMap<>(); 
                error.put("error", "Document not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"dean_doc_" + id + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(doc);
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
    
    // -----------[Update Dean]------------------
    // description - Partially updates a Dean's details (admin/existing function).
    // ...
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDean(
            @PathVariable Long id,
            @RequestPart("data") DeanViceDeanUpdateRequest request, // No @Valid since partial
            @RequestPart(name = "photograph", required = false) MultipartFile photograph,
            @RequestPart(name = "document", required = false) MultipartFile document){  // ← added{

        try {
            deanViceDeanService.updateDeanViceDean(id, request, photograph, document, Role.DEAN);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Dean updated successfully");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred during update");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // -----------[Get Profile]------------------
    // description - Retrieves the profile for the authenticated Dean.
    //               Excludes remarks.
    //               Requires JWT token in Authorization header.
    // endpoint - GET /api/deans/profile
    // success response - 200 OK with DeanViceDeanProfileDTO
    // ErrorResponse - { "error": "message" } (400 if not found, 500 unexpected)
    @GetMapping("/profile")
    public ResponseEntity<?> getDeanProfile(@AuthenticationPrincipal User user) {
        try {
            if (user.getRole() != Role.DEAN) {
                throw new IllegalArgumentException("Access denied: Not a Dean");
            }
            DeanViceDeanProfileDTO profile = deanViceDeanService.getProfile(user);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred while fetching profile");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}/document")
    public ResponseEntity<?> getDocument(@PathVariable Long id) {
        return deanViceDeanService.getDocumentByIdAndRole(id, Role.DEAN);
    }

    // -----------[Get Dean Dashboard]------------------
    // description - Retrieves aggregated statistics and chart data for the Dean's dashboard.
    //               Includes totals, distributions, and trends.
    //               Accessible only to authenticated users with ROLE_DEAN.
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal User user) {
        try {
            if (user.getRole() != Role.DEAN) {
                throw new IllegalArgumentException("Access denied: Not a Dean");
            }
            DeanDashboardDTO dashboard = deanViceDeanService.getDashboardData();
            return ResponseEntity.ok(dashboard);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred while fetching dashboard data");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // -----------[Get All Students with CGPA]------------------
    // description - Retrieves report of all students with their Cumulative GPA.
    // endpoint - GET /api/deans/get-all-students-cgpa
    @GetMapping("/get-all-students-cgpa")
    public ResponseEntity<?> getAllStudentsWithCGPA(@AuthenticationPrincipal User user) {
        try {
            if (user.getRole() != Role.DEAN) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Access denied"));
            }
            List<StudentCGPADTO> students = studentDetailService.getAllStudentsWithCGPA();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred while fetching student CGPAs");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
