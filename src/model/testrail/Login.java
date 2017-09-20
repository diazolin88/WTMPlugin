package model.testrail;

import exceptions.AuthorizationException;
import settings.User;

/**
 * Interface for authorization.
 */
public interface Login {
    void login(User loginData) throws AuthorizationException;
}
