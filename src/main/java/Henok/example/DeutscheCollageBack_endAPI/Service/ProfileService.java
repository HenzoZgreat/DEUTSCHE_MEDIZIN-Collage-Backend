package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.ProfileResponse;
import Henok.example.DeutscheCollageBack_endAPI.Entity.*;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DeanViceDeanDetailsRepository;
import Henok.example.DeutscheCollageBack_endAPI.Repository.DepartmentHeadRepository;
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
    private final DepartmentHeadRepository deptHeadRepo;
    private final DeanViceDeanDetailsRepository deanRepo;

    public ProfileResponse buildProfileResponse(User user) {
        String fullName = "";
        String fullNameAmharic = "";
        byte[] photo = null;
        String email = null;
        String phone = null;
        String departmentName = null;
        long departmentId = 0;

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
            case DEPARTMENT_HEAD -> {
                DepartmentHeadDetails dh = deptHeadRepo.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException("Department Head profile not found"));
                fullName = dh.getFirstNameENG() + " " + dh.getFatherNameENG() + " " + dh.getGrandfatherNameENG();
                fullNameAmharic = dh.getFirstNameAMH() + " " + dh.getFatherNameAMH() + " " + dh.getGrandfatherNameAMH();
                photo = dh.getPhoto();
                email = dh.getEmail();
                phone = dh.getPhoneNumber();
                departmentId = dh.getDepartment().getDptID();
                departmentName = dh.getDepartment().getDeptName();
            }
            case DEAN, VICE_DEAN -> {
                DeanViceDeanDetails d = deanRepo.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException("Dean/Vice Dean profile not found"));
                fullName = d.getFirstNameENG() + " " + d.getFatherNameENG() + " " + d.getGrandfatherNameENG();
                fullNameAmharic = d.getFirstNameAMH() + " " + d.getFatherNameAMH() + " " + d.getGrandfatherNameAMH();
                photo = d.getPhoto();
                email = d.getEmail();
                phone = d.getPhoneNumber();
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
                .departmentName(departmentName)
                .departmentId(departmentId)
                .build();
    }


    public byte[] getMyDocument(User user) {
        switch (user.getRole()) {
            case STUDENT -> {
                StudentDetails s = studentRepo.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
                return s.getDocument();
            }
            case TEACHER -> {
                TeacherDetail t = teacherRepo.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException("Teacher profile not found"));
                return t.getDocuments();
            }
            case REGISTRAR -> {
                // Registrar doesn't have a document field in given schema, or assuming no doc stored
                // If it does, follow similar pattern. Returning null or throwing error if not supported.
                throw new IllegalArgumentException("Registrar documents are not available for download");
            }
            case GENERAL_MANAGER -> {
                // Similarly for General Manager if no document column exists
               throw new IllegalArgumentException("General Manager documents are not available for download");
            }
            case DEPARTMENT_HEAD -> {
                DepartmentHeadDetails dh = deptHeadRepo.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException("Department Head profile not found"));
                return dh.getDocuments();
            }
            case DEAN, VICE_DEAN -> {
                DeanViceDeanDetails d = deanRepo.findByUser(user)
                        .orElseThrow(() -> new ResourceNotFoundException("Dean/Vice Dean profile not found"));
                return d.getDocuments();
            }
            default -> throw new IllegalStateException("Unsupported role for document retrieval: " + user.getRole());
        }
    }
}