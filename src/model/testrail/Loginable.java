package model.testrail;

import exceptions.AuthorizationException;
import settings.User;

public interface Loginable {
    void login(User loginData) throws AuthorizationException;
}
