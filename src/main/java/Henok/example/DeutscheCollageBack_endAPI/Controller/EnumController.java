package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.Enums.DocumentStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Gender;
import Henok.example.DeutscheCollageBack_endAPI.Enums.MaritalStatus;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enums")
public class EnumController {

    @GetMapping("/genders")
    public ResponseEntity<?> getAllGenders() {
        try {
            List<String> genders = Arrays.stream(Gender.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(genders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve genders: " + e.getMessage()));
        }
    }

    @GetMapping("/marital-statuses")
    public ResponseEntity<?> getAllMaritalStatuses() {
        try {
            List<String> maritalStatuses = Arrays.stream(MaritalStatus.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(maritalStatuses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve marital statuses: " + e.getMessage()));
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        try {
            List<String> roles = Arrays.stream(Role.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve roles: " + e.getMessage()));
        }
    }

    @GetMapping("/document-statuses")
    public ResponseEntity<?> getAllDocumentStatuses() {
        try {
            List<String> documentStatuses = Arrays.stream(DocumentStatus.values())
                    .map(Enum::name)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(documentStatuses);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to retrieve document statuses: " + e.getMessage()));
        }
    }
}