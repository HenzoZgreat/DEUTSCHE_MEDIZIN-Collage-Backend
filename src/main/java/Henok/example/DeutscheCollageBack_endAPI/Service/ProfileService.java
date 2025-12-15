package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.ProfileResponse;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.GeneralManagerDetailRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.RegistrarDetailRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.StudentDetailsRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final StudentDetailsRepository studentRepo;
    private final TeacherRepository teacherRepo;
    private final RegistrarDetailRepository registrarRepo;
    private final GeneralManagerDetailRepository gmRepo;
    // add more repositories as new roles come

    public ProfileResponse buildProfileResponse(User user) {
        String fullName = "";
        String fullNameAmharic = "";
        byte[] photo = null;
        String email = null;
        String phone = null;

        switch (user.getRole()) {
            case STUDENT -> {
                StudentDetails s = studentRepo.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
                fullName = s.getFirstNameENG() + " " + s.getFatherNameENG() + " " + s.getGrandfatherNameENG();
                fullNameAmharic = s.getFirstNameAMH() + " " + s.getFatherNameAMH() + " " + s.getGrandfatherNameAMH();
                photo = s.getStudentPhoto();
                email = s.getEmail();
                phone = s.getPhoneNumber();
            }
            case TEACHER -> {
                TeacherDetail t = teacherRepo.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));
                fullName = t.getFirstNameEnglish() + " " + t.getLastNameEnglish();
                fullNameAmharic = t.getFirstNameAmharic() + " " + t.getLastNameAmharic();
                photo = t.getPhotograph();
                email = t.getEmail();
                phone = t.getPhoneNumber();
            }
            case REGISTRAR -> {
                RegistrarDetail r = registrarRepo.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException("Registrar profile not found"));
                fullName = r.getFirstNameEnglish() + " " + r.getLastNameEnglish();
                fullNameAmharic = r.getFirstNameAmharic() + " " + r.getLastNameAmharic();
                photo = r.getPhotograph();
                email = r.getEmail();
                phone = r.getPhoneNumber();
            }
            case GENERAL_MANAGER -> {
                GeneralManagerDetail gm = gmRepo.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException("General Manager profile not found"));
                fullName = gm.getFirstNameEnglish() + " " + gm.getLastNameEnglish();
                fullNameAmharic = gm.getFirstNameAmharic() + " " + gm.getLastNameAmharic();
                photo = gm.getPhotograph();
                email = gm.getEmail();
                phone = gm.getPhoneNumber();
            }
            // future roles → just add new case here
            default -> throw new IllegalStateException("Unsupported role: " + user.getRole());
        }

        return ProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .fullName(fullName.trim())
                .fullNameAmharic(fullNameAmharic.trim())
                .photoBase64(photo != null ? Base64.getEncoder().encodeToString(photo) : null)
                .email(email)
                .phoneNumber(phone)
                .build();
    }
}