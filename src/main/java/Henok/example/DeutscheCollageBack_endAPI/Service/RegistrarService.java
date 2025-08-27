package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.RegistrarRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.UserRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.RegistrarDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Repository.RegistrarDetailRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrarService {

    @Autowired
    private UserService userService;

    @Autowired
    private RegistrarDetailRepository registrarDetailRepository;

    @Transactional
    public RegistrarDetail registerRegistrar(RegistrarRegisterRequest request) {
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

        // Create User with REGISTRAR role
        UserRegisterRequest userRequest = new UserRegisterRequest();
        userRequest.setUsername(request.getUsername());
        userRequest.setPassword(request.getPassword());
        userRequest.setRole(Role.REGISTRAR);
        User user = userService.registerUser(userRequest);

        // Create RegistrarDetail
        RegistrarDetail registrarDetail = new RegistrarDetail();
        registrarDetail.setUser(user);
        registrarDetail.setId(user.getId());
        registrarDetail.setFirstNameAmharic(request.getFirstNameAmharic());
        registrarDetail.setLastNameAmharic(request.getLastNameAmharic());
        registrarDetail.setFirstNameEnglish(request.getFirstNameEnglish());
        registrarDetail.setLastNameEnglish(request.getLastNameEnglish());
        registrarDetail.setEmail(request.getEmail());
        registrarDetail.setPhoneNumber(request.getPhoneNumber());
        registrarDetail.setNationalIdImage(request.getNationalIdImage());
        registrarDetail.setPhotograph(request.getPhotograph());

        return registrarDetailRepository.save(registrarDetail);
    }
}