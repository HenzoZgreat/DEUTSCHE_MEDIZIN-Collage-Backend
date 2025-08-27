package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.GeneralManagerRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrationAndLogin.UserRegisterRequest;
import Henok.example.DeutscheCollageBack_endAPI.Entity.GeneralManagerDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Enums.Role;
import Henok.example.DeutscheCollageBack_endAPI.Repository.GeneralManagerDetailRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GeneralManagerService {

    @Autowired
    private UserService userService;

    @Autowired
    private GeneralManagerDetailRepository generalManagerDetailRepository;

    @Transactional
    public GeneralManagerDetail registerGeneralManager(GeneralManagerRegisterRequest request) {
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
        if (generalManagerDetailRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number already exists");
        }

        // Create User with GENERAL_MANAGER role
        UserRegisterRequest userRequest = new UserRegisterRequest();
        userRequest.setUsername(request.getUsername());
        userRequest.setPassword(request.getPassword());
        userRequest.setRole(Role.GENERAL_MANAGER);
        User user = userService.registerUser(userRequest);

        // Create GeneralManagerDetail
        GeneralManagerDetail generalManagerDetail = new GeneralManagerDetail();
        generalManagerDetail.setUser(user);
        generalManagerDetail.setUserId(user.getId());
        generalManagerDetail.setFirstNameAmharic(request.getFirstNameAmharic());
        generalManagerDetail.setLastNameAmharic(request.getLastNameAmharic());
        generalManagerDetail.setFirstNameEnglish(request.getFirstNameEnglish());
        generalManagerDetail.setLastNameEnglish(request.getLastNameEnglish());
        generalManagerDetail.setEmail(request.getEmail());
        generalManagerDetail.setPhoneNumber(request.getPhoneNumber());
        generalManagerDetail.setNationalIdImage(request.getNationalIdImage());
        generalManagerDetail.setPhotograph(request.getPhotograph());

        return generalManagerDetailRepository.save(generalManagerDetail);
    }
}