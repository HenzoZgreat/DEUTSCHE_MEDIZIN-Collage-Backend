package Henok.example.DeutscheCollageBack_endAPI.Error;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
