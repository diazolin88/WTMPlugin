package model;

/**
 * TM is Test Management.
 *
 * @param <T> Type of something.
 */
@FunctionalInterface
public interface TMConnectable<T> {
    T login(String user, String password, String url);
}
