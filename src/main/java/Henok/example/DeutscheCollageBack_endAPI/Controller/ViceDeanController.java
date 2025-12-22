package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean.DeanViceDeanListDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean.DeanViceDeanProfileDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.DeanAndVice_Dean.DeanViceDeanUpdateRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Service.DeanViceDeanService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/vice-deans")
@RequiredArgsConstructor
public class ViceDeanController {

    private final DeanViceDeanService deanViceDeanService;


    // -----------[Get All Active Vice-Deans]------------------
    @GetMapping("/active")
    public ResponseEntity<?> getAllActiveViceDeans() {
        try {
            List<DeanViceDeanListDTO> viceDeans = deanViceDeanService.getAllActiveByRole(Role.VICE_DEAN);
            return ResponseEntity.ok(viceDeans);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred while fetching active Vice-Deans");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // -----------[Update Vice-Dean]------------------
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateViceDean(
            @PathVariable Long id,
            @RequestPart("data") DeanViceDeanUpdateRequest request,
            @RequestPart(name = "photograph", required = false) MultipartFile photograph,
            @RequestPart(name = "document", required = false) MultipartFile document) {

        try {
            deanViceDeanService.updateDeanViceDean(id, request, photograph, document, Role.VICE_DEAN);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Vice-Dean updated successfully");
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
    @GetMapping("/profile")
    public ResponseEntity<?> getViceDeanProfile(@AuthenticationPrincipal User user) {
        try {
            if (user.getRole() != Role.VICE_DEAN) {
                throw new IllegalArgumentException("Access denied: Not a Vice-Dean");
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
}
