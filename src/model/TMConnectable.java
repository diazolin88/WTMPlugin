package model;

import com.intellij.openapi.components.PersistentStateComponent;
import exceptions.AuthorizationException;
import settings.WTMSettings;
import view.WindowPanelAbstract;

import javax.swing.*;

/**
 * TM is Test Management.
 *
 * @param <T> Type of something.
 */
public abstract class TMConnectable<T> {
    public abstract T login(PersistentStateComponent state) throws AuthorizationException;
    public abstract T login(WindowPanelAbstract component) throws AuthorizationException;
}
