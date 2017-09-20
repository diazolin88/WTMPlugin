package settings;

/**
 * User data. The class uses for  plugin settings as storage.
 */
public interface User {
    default String getUserName() {
        return null;
    }

    default String getUserPassword() {
        return null;
    }

    default String getURL() {
        return null;
    }
}
