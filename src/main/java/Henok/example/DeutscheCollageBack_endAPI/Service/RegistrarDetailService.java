package Henok.example.DeutscheCollageBack_endAPI.Service;

import Henok.example.DeutscheCollageBack_endAPI.DTO.RegistrarDetailDTO;
import Henok.example.DeutscheCollageBack_endAPI.Entity.RegistrarDetail;
import Henok.example.DeutscheCollageBack_endAPI.Entity.User;
import Henok.example.DeutscheCollageBack_endAPI.Error.ResourceNotFoundException;
import Henok.example.DeutscheCollageBack_endAPI.Repository.RegistrarDetailRepo;
import Henok.example.DeutscheCollageBack_endAPI.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RegistrarDetailService {

    @Autowired
    private RegistrarDetailRepo registrarDetailRepository;

    @Autowired
    private UserRepository userRepository;

    public void addRegistrarDetails(List<RegistrarDetailDTO> registrarDetailDTOs) {
        if (registrarDetailDTOs == null || registrarDetailDTOs.isEmpty()) {
            throw new IllegalArgumentException("Registrar detail list cannot be null or empty");
        }

        List<RegistrarDetail> registrarDetails = registrarDetailDTOs.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());

        for (RegistrarDetail detail : registrarDetails) {
            validateRegistrarDetail(detail);
            if (registrarDetailRepository.existsByUser(detail.getUser())) {
                throw new IllegalArgumentException("Registrar detail already exists for user with id: " + detail.getUser().getId());
            }
            if (registrarDetailRepository.existsByEmail(detail.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + detail.getEmail());
            }
        }

        registrarDetailRepository.saveAll(registrarDetails);
    }

    public void addRegistrarDetail(RegistrarDetailDTO registrarDetailDTO) {
        if (registrarDetailDTO == null) {
            throw new IllegalArgumentException("Registrar detail DTO cannot be null");
        }

        RegistrarDetail registrarDetail = mapToEntity(registrarDetailDTO);
        validateRegistrarDetail(registrarDetail);

        if (registrarDetailRepository.existsByUser(registrarDetail.getUser())) {
            throw new IllegalArgumentException("Registrar detail already exists for user with id: " + registrarDetail.getUser().getId());
        }
        if (registrarDetailRepository.existsByEmail(registrarDetail.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + registrarDetail.getEmail());
        }

        registrarDetailRepository.save(registrarDetail);
    }

    public List<RegistrarDetail> getAllRegistrarDetails() {
        List<RegistrarDetail> registrarDetails = registrarDetailRepository.findAll();
        if (registrarDetails.isEmpty()) {
            throw new ResourceNotFoundException("No registrar details found");
        }
        return registrarDetails;
    }

    public RegistrarDetail getRegistrarDetailById(Long id) {
        return registrarDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registrar detail not found with id: " + id));
    }

    public void updateRegistrarDetail(Long id, RegistrarDetailDTO registrarDetailDTO) {
        if (registrarDetailDTO == null) {
            throw new IllegalArgumentException("Registrar detail DTO cannot be null");
        }

        RegistrarDetail existingDetail = getRegistrarDetailById(id);
        String newEmail = registrarDetailDTO.getEmail();

        if (!existingDetail.getEmail().equals(newEmail) &&
                registrarDetailRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email already exists: " + newEmail);
        }

        User user = userRepository.findById(registrarDetailDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + registrarDetailDTO.getUserId()));

        if (!existingDetail.getUser().getId().equals(user.getId()) &&
                registrarDetailRepository.existsByUser(user)) {
            throw new IllegalArgumentException("Registrar detail already exists for user with id: " + user.getId());
        }

        existingDetail.setUser(user);
        existingDetail.setFirstNameAmharic(registrarDetailDTO.getFirstNameAmharic());
        existingDetail.setLastNameAmharic(registrarDetailDTO.getLastNameAmharic());
        existingDetail.setFirstNameEnglish(registrarDetailDTO.getFirstNameEnglish());
        existingDetail.setLastNameEnglish(registrarDetailDTO.getLastNameEnglish());
        existingDetail.setEmail(newEmail);
        existingDetail.setPhoneNumber(registrarDetailDTO.getPhoneNumber());
        existingDetail.setNationalIdImage(registrarDetailDTO.getNationalIdImage() != null ?
                Base64.getDecoder().decode(registrarDetailDTO.getNationalIdImage()) : null);

        validateRegistrarDetail(existingDetail);
        registrarDetailRepository.save(existingDetail);
    }

    public void deleteRegistrarDetail(Long id) {
        if (!registrarDetailRepository.existsById(id)) {
            throw new ResourceNotFoundException("Registrar detail not found with id: " + id);
        }
        registrarDetailRepository.deleteById(id);
    }

    private RegistrarDetail mapToEntity(RegistrarDetailDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId()));

        byte[] nationalIdImage = dto.getNationalIdImage() != null ?
                Base64.getDecoder().decode(dto.getNationalIdImage()) : null;

        return new RegistrarDetail(null, user, dto.getFirstNameAmharic(), dto.getLastNameAmharic(),
                dto.getFirstNameEnglish(), dto.getLastNameEnglish(), dto.getEmail(), dto.getPhoneNumber(),
                nationalIdImage);
    }

    private void validateRegistrarDetail(RegistrarDetail registrarDetail) {
        if (registrarDetail.getUser() == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (registrarDetail.getFirstNameAmharic() == null || registrarDetail.getFirstNameAmharic().trim().isEmpty()) {
            throw new IllegalArgumentException("First name (Amharic) cannot be null or empty");
        }
        if (registrarDetail.getLastNameAmharic() == null || registrarDetail.getLastNameAmharic().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name (Amharic) cannot be null or empty");
        }
        if (registrarDetail.getFirstNameEnglish() == null || registrarDetail.getFirstNameEnglish().trim().isEmpty()) {
            throw new IllegalArgumentException("First name (English) cannot be null or empty");
        }
        if (registrarDetail.getLastNameEnglish() == null || registrarDetail.getLastNameEnglish().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name (English) cannot be null or empty");
        }
        if (registrarDetail.getEmail() == null || registrarDetail.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (!isValidEmail(registrarDetail.getEmail())) {
            throw new IllegalArgumentException("Invalid email format: " + registrarDetail.getEmail());
        }
        if (registrarDetail.getPhoneNumber() != null && !registrarDetail.getPhoneNumber().trim().isEmpty() &&
                !isValidPhoneNumber(registrarDetail.getPhoneNumber())) {
            throw new IllegalArgumentException("Invalid phone number format: " + registrarDetail.getPhoneNumber());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneRegex = "^\\+?\\d{10,13}$";
        return phoneNumber.matches(phoneRegex);
    }
}