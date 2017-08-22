package exceptions;

public class AuthorizationException extends Exception {

    public AuthorizationException(String userName) {
        super("Can't authorize user : '" + userName + "'");
    }
}
