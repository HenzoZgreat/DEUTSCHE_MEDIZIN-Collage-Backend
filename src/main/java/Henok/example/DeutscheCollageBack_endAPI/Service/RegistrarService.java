package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.RegistrarRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.UserRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.RegistrarDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Repository.RegistrarDetailRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class RegistrarService {

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrarDetailRepository registrarDetailRepository;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public RegistrarDetail registerRegistrar(RegistrarRegisterRequest request, MultipartFile nationalIdImage, MultipartFile photograph) {
        // Validate required fields
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (request.getFirstNameAmharic() == null || request.getFirstNameAmharic().isEmpty()) {
            throw new IllegalArgumentException("First name in Amharic cannot be empty");
        }
        if (request.getLastNameAmharic() == null || request.getLastNameAmharic().isEmpty()) {
            throw new IllegalArgumentException("Last name in Amharic cannot be empty");
        }
        if (request.getFirstNameEnglish() == null || request.getFirstNameEnglish().isEmpty()) {
            throw new IllegalArgumentException("First name in English cannot be empty");
        }
        if (request.getLastNameEnglish() == null || request.getLastNameEnglish().isEmpty()) {
            throw new IllegalArgumentException("Last name in English cannot be empty");
        }
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (request.getPhoneNumber() == null || request.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        // Check for unique phone number
        if (registrarDetailRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Create and save User with REGISTRAR role
        UserRegisterRequest userRequest = new UserRegisterRequest();
        userRequest.setUsername(request.getUsername());
        userRequest.setPassword(request.getPassword());
        userRequest.setRole(Role.REGISTRAR);
        User user = userService.registerUser(userRequest);

        // Flush to ensure User is persisted
        entityManager.flush();

        // Check for existing RegistrarDetail with the same user
        if (registrarDetailRepository.findByUser(user).isPresent()) {
            throw new IllegalArgumentException("Registrar detail already exists for user: " + user.getUsername());
        }

        // Create RegistrarDetail
        RegistrarDetail registrarDetail = new RegistrarDetail();
        registrarDetail.setUser(user);
        registrarDetail.setFirstNameAmharic(request.getFirstNameAmharic());
        registrarDetail.setLastNameAmharic(request.getLastNameAmharic());
        registrarDetail.setFirstNameEnglish(request.getFirstNameEnglish());
        registrarDetail.setLastNameEnglish(request.getLastNameEnglish());
        registrarDetail.setEmail(request.getEmail());
        registrarDetail.setPhoneNumber(request.getPhoneNumber());

        // Convert MultipartFile to byte[]
        byte[] nationalIdImageBytes = null;
        byte[] photographBytes = null;
        try {
            if (nationalIdImage != null && !nationalIdImage.isEmpty()) {
                nationalIdImageBytes = nationalIdImage.getBytes();
            }
            if (photograph != null && !photograph.isEmpty()) {
                photographBytes = photograph.getBytes();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to process image files: " + e.getMessage());
        }
        registrarDetail.setNationalIdImage(nationalIdImageBytes);
        registrarDetail.setPhotograph(photographBytes);

        // Clear persistence context to avoid stale entity issues
        entityManager.clear();

        return registrarDetailRepository.save(registrarDetail);
    }
}