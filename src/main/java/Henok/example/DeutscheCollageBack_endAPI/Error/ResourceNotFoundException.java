package Henok.example.DeutscheCollageBack_endAPI.Error;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}