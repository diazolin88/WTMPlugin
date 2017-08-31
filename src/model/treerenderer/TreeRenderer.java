package model.treerenderer;

import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import utils.GuiUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import static com.intellij.util.PlatformIcons.PACKAGE_ICON;

public class TreeRenderer extends ColoredTreeCellRenderer {

    @Override
    public void customizeCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        Object userObject = node.getUserObject();

        if (userObject instanceof PackageCustom) {
            PackageCustom pack = (PackageCustom) userObject;
            append(pack.getName());
            setIcon(PACKAGE_ICON);
        }else if( userObject instanceof CaseCustom){
            CaseCustom pack = (CaseCustom) userObject;
            append(pack.getName());
            setIcon(PACKAGE_ICON);
        } else if (userObject instanceof RootCustom){
            RootCustom pack = (RootCustom) userObject;
            append(pack.getName());
            setIcon(GuiUtil.loadIcon("icon_TC.png"));
        }
    }
}
