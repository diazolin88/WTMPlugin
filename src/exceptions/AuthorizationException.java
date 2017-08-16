package exceptions;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String userName) {
        super("Can't authorize user : '" + userName + "'");
    }
}
