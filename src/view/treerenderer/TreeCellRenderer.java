package view.treerenderer;

import com.codepine.api.testrail.model.Case;
import com.intellij.ui.ColoredTreeCellRenderer;
import model.section.OurSection;
import utils.GuiUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

import static com.intellij.util.PlatformIcons.PACKAGE_ICON;

/**
 * Renderer for tree cell.
 */
public class TreeCellRenderer extends ColoredTreeCellRenderer {

    private static final Icon CASE_ICON = GuiUtil.loadIcon("test_case_icon.png");

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
            setIcon(CASE_ICON);
        }
    }
}
