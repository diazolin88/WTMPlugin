package model.treerenderer;

import com.codepine.api.testrail.model.Case;
import com.intellij.ui.ColoredTreeCellRenderer;
import model.section.OurSection;
import utils.GuiUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import static com.intellij.util.PlatformIcons.PACKAGE_ICON;

// TODO: not a model.
public class TreeRenderer extends ColoredTreeCellRenderer {

    @Override
    public void customizeCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        Object userObject = node.getUserObject();

        if (userObject instanceof OurSection) {
            OurSection pack = (OurSection) userObject;
            append(pack.getName());
            setIcon(PACKAGE_ICON);
        }else if( userObject instanceof Case){
            Case pack = (Case) userObject;
            append(pack.getTitle());
            setIcon(PACKAGE_ICON);
            setIcon(GuiUtil.loadIcon("test_case_icon.png"));
        }
    }
}
