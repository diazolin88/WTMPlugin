package settings;

public interface LoginData {
    default String getUserName() {
        return null;
    }

    default String getPassword() {
        return null;
    }

    default String getURL() {
        return null;
    }
}
