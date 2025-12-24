package Henok.example.DeutscheCollageBack_endAPI.Controller;

// GeneralManagerController – REST endpoints for general manager profile management.
// Protected by role GENERAL_MANAGER.
// Uses @AuthenticationPrincipal to get the authenticated user from JWT.
// Delegates all business logic to GeneralManagerService.
// Errors are handled globally via @ControllerAdvice (assumed to exist).

import Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager.GeneralManagerDetailDTO;
import Henok.example.DeutscheCollageBack_endAPI.DTO.GeneralManager.GeneralManagerUpdateDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Service.GeneralManagerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/general-managers")
@PreAuthorize("hasRole('GENERAL_MANAGER')")
public class GeneralManagerController {

    private final GeneralManagerService generalManagerService;

    public GeneralManagerController(GeneralManagerService generalManagerService) {
        this.generalManagerService = generalManagerService;
    }

    // GET /api/general-managers/profile
    // Returns the profile of the authenticated general manager.
    @GetMapping("/profile")
    public ResponseEntity<GeneralManagerDetailDTO> getProfile(@AuthenticationPrincipal User user) {
        GeneralManagerDetailDTO profile = generalManagerService.getProfileByUser(user);
        return ResponseEntity.ok(profile);
    }

    // PATCH /api/general-managers/update
    // Partial update – only provided fields are modified.
    @PatchMapping("/update")
    public ResponseEntity<GeneralManagerDetailDTO> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid GeneralManagerUpdateDTO updateDTO) {

        GeneralManagerDetailDTO updated = generalManagerService.updateProfile(user, updateDTO);
        return ResponseEntity.ok(updated);
    }
}