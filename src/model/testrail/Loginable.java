package model.testrail;

import exceptions.AuthorizationException;
import settings.LoginData;

public interface Loginable {
    void login(LoginData loginData) throws AuthorizationException;
}
