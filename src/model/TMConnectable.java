package model;

import exceptions.AuthorizationException;
import settings.WTMSettings;

import javax.swing.*;

/**
 * TM is Test Management.
 *
 * @param <T> Type of something.
 */
@FunctionalInterface
public interface TMConnectable<T> {
    T login(WTMSettings state, JComponent component) throws AuthorizationException;
}
