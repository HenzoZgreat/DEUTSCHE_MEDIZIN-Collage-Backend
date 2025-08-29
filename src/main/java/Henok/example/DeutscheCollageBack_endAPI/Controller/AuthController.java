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
    private StudentDetailService studentDetailService;

    @Autowired
    private GeneralManagerService generalManagerService;

    @Autowired
    private RegistrarService registrarService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new AuthResponse(jwt));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Invalid username or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request) {
        try {
            User user = userService.registerUser(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully with username: " + user.getUsername());
            response.put("userID", user.getId().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody StudentRegisterRequest request) {
        try {
            StudentDetails studentDetails = studentDetailService.registerStudent(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Student registered successfully with username: " + studentDetails.getUser().getUsername());
            response.put("userID", studentDetails.getUser().getId().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred while registering the student"));
        }
    }

    @PostMapping(value = "/register/general-manager", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerGeneralManager(
            @RequestPart(name = "data") GeneralManagerRegisterRequest request,
            @RequestPart(name = "nationalIdImage", required = false) MultipartFile nationalIdImage,
            @RequestPart(name = "photograph", required = false) MultipartFile photograph) {
        try {
            GeneralManagerDetail generalManagerDetail = generalManagerService.registerGeneralManager(request, nationalIdImage, photograph);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "General Manager registered successfully with username: " + generalManagerDetail.getUser().getUsername());
            response.put("userID", generalManagerDetail.getUser().getId().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred while registering the general manager"));
        }
    }

    @PostMapping(value = "/register/registrar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerRegistrar(
            @RequestPart(name = "data") RegistrarRegisterRequest request,
            @RequestPart(name = "nationalIdImage", required = false) MultipartFile nationalIdImage,
            @RequestPart(name = "photograph", required = false) MultipartFile photograph) {
        try {
            RegistrarDetail registrarDetail = registrarService.registerRegistrar(request, nationalIdImage, photograph);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registrar registered successfully with username: " + registrarDetail.getUser().getUsername());
            response.put("userID", registrarDetail.getUser().getId().toString());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("An error occurred while registering the registrar"));
        }
    }
}