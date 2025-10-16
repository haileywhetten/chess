package exception;

//Based off what was done in PetShop

public class ResponseException extends RuntimeException {
    public enum Code {
        ServerError,
        BadRequestError,
        UnauthorizedError;
    }
    private final Code code;

    public ResponseException(String message, Code code) {
        super(message);
        this.code = code;
    }

}
