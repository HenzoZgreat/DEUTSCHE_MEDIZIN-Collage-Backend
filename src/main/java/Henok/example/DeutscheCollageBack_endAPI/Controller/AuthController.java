package Henok.example.DeutscheCollageBack_endAPI.Controller;

import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.*;
import Henok.example.DeutscheCollageBack_endAPI.Entity.GeneralManagerDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.RegistrarDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.StudentDetails;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Error.ErrorResponse;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Security.JwtUtil;
import Henok.example.DeutscheCollageBack_endAPI.Service.GeneralManagerService;
import Henok.example.DeutscheCollageBack_endAPI.Service.RegistrarService;
import Henok.example.DeutscheCollageBack_endAPI.Service.StudentDetailService;
import Henok.example.DeutscheCollageBack_endAPI.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentDetailService studentDetailsService;

    @Autowired
    private GeneralManagerService generalManagerService;

    @Autowired
    private RegistrarService registrarService;

    // Authenticates a user and generates a JWT token
    // Why: Provides secure login with username/password, returns JWT for subsequent requests
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("jwt", jwt);
            response.put("userId", ((User) userDetails).getId().toString());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Authentication failed: " + e.getMessage()));
        }
    }

    // Registers a general user with specified role
    // Why: Allows creation of non-student users (e.g., staff)
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {
        try {
            User user = userService.registerUser(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully with username: " + user.getUsername());
            response.put("userId", user.getId().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("User registration failed due to duplicate entry: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while registering the user: " + e.getMessage()));
        }
    }

    // Registers a student with full details and user account
    // Why: Creates StudentDetails and associated User with STUDENT role, returns username/password
    @PostMapping(value = "/register/student", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerStudent(
            @RequestPart(name = "data") StudentRegisterRequest request,
            @RequestPart(name = "studentPhoto", required = false) MultipartFile studentPhoto,
            @RequestPart(name = "document", required = false) MultipartFile document) {
        try {
            StudentDetails studentDetails = studentDetailsService.registerStudent(request, studentPhoto, document);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Student registered successfully");
            response.put("studentId", studentDetails.getId().toString());
            response.put("userId", studentDetails.getUser().getId().toString());
            response.put("username", studentDetails.getUser().getUsername());
            response.put("password", request.getPassword()); // Note: In production, avoid returning plain password
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Student registration failed due to duplicate entry: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while registering the student: " + e.getMessage()));
        }
    }

    // Registers a general manager with optional file uploads
    // Why: Handles multipart form data for general manager registration
    @PostMapping(value = "/register/general-manager", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerGeneralManager(
            @RequestPart(name = "data") GeneralManagerRegisterRequest request,
            @RequestPart(name = "nationalIdImage", required = false) MultipartFile nationalIdImage,
            @RequestPart(name = "photograph", required = false) MultipartFile photograph) {
        try {
            GeneralManagerDetail generalManagerDetail = generalManagerService.registerGeneralManager(request, nationalIdImage, photograph);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "General Manager registered successfully with username: " + generalManagerDetail.getUser().getUsername());
            response.put("userId", generalManagerDetail.getUser().getId().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("General Manager registration failed due to duplicate entry: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while registering the general manager: " + e.getMessage()));
        }
    }

    // Registers a registrar with optional file uploads
    // Why: Handles multipart form data for registrar registration
    @PostMapping(value = "/register/registrar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerRegistrar(
            @RequestPart(name = "data") RegistrarRegisterRequest request,
            @RequestPart(name = "nationalIdImage", required = false) MultipartFile nationalIdImage,
            @RequestPart(name = "photograph", required = false) MultipartFile photograph) {
        try {
            RegistrarDetail registrarDetail = registrarService.registerRegistrar(request, nationalIdImage, photograph);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registrar registered successfully with username: " + registrarDetail.getUser().getUsername());
            response.put("userId", registrarDetail.getUser().getId().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Registrar registration failed due to duplicate entry: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while registering the registrar: " + e.getMessage()));
        }
    }
}