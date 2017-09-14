package model.testrail;

import exceptions.AuthorizationException;
import settings.LoginData;

public interface Loginable<T extends Loginable> {
    T login(LoginData loginData) throws AuthorizationException;
}
