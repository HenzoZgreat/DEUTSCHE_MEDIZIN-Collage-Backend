package Henok.example.DeutscheCollageBack_endAPI.Error;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalValidationHandler {

    // Handles validation errors from @Valid on DTO fields (@NotBlank, @Size, @NotNull, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        // Collect all field errors into a readable message
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> {
                    String field = error.getField();
                    String message = error.getDefaultMessage();
                    return field + ": " + message;
                })
                .collect(Collectors.joining(", "));

        Map<String, String> response = Map.of("error", "Validation failed: " + errorMessage);

        return ResponseEntity.badRequest().body(response);
    }

    // Handles @Valid on @RequestPart (multipart/form-data) when the JSON part fails parsing/validation
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleJsonParseErrors(HttpMessageNotReadableException ex) {

        String message = "Invalid JSON in 'data' part";
        if (ex.getCause() instanceof InvalidFormatException) {
            message = "Invalid value format in request";
        } else if (ex.getMessage().contains("Required request part 'data' is not present")) {
            message = "Missing required 'data' JSON part";
        }

        Map<String, String> response = Map.of("error", message);
        return ResponseEntity.badRequest().body(response);
    }

    // Optional: Also catch ConstraintViolationException (for @PathVariable or @RequestParam validations)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolations(ConstraintViolationException ex) {

        String errorMessage = ex.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining(", "));

        Map<String, String> response = Map.of("error", "Validation failed: " + errorMessage);
        return ResponseEntity.badRequest().body(response);
    }
}