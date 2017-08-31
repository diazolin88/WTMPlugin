package view;

import com.codepine.api.testrail.model.Section;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import model.OurSectionInflator;
import model.section.OurSection;
import model.testrail.RailClient;
import model.testrail.RailConnection;
import model.treerenderer.TreeRenderer;
import utils.GuiUtil;
import utils.ToolWindowData;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ItemEvent;
import java.util.List;

import static utils.ComponentUtil.*;

public class TestRailWindow extends WindowPanelAbstract implements Disposable {
    private Project project;
    private JPanel panel1;
    private JComboBox projectCB;
    private JComboBox suitesCB;
    private Tree sectionTree;
    private JScrollPane scroll;
    private JPanel treePanel;
    private RailClient client;
    private ToolWindowData data;

    public TestRailWindow(Project project) {
        super(project);
        this.project = project;
        client = new RailClient(RailConnection.getInstance(project).getClient());

        setContent(panel1);
        sectionTree.setCellRenderer(new TreeRenderer());

        setProjectSelectedItemAction();
        setSuiteSelectedItemAction();
    }

    public static TestRailWindow getInstance(Project project) {
        return ServiceManager.getService(project, TestRailWindow.class);
    }

    public JComboBox getProjectCB() {
        return projectCB;
    }

    public JComboBox getSuitesCB() {
        return suitesCB;
    }

    @Override
    public void dispose() {
    }

    @SuppressWarnings("unchecked")
    public void setProjectSelectedItemAction() {
        projectCB.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                disableComponent(this.suitesCB);
                GuiUtil.runInSeparateThread(() -> {
                    makeInvisible(sectionTree);
                    String selectedProject = (String) projectCB.getSelectedItem();
                    if (!selectedProject.equals("Select project...")) {
                        getSuitesCB().removeAllItems();
                        getSuitesCB().addItem("Select your suite...");
                        client.getSuitesList(selectedProject)
                                .forEach(suite -> getSuitesCB().addItem(suite.getName()));
                        enableComponent(this.suitesCB);
                    } else {
                        getSuitesCB().removeAllItems();
                    }
                });
            }
        });
    }

//    public void setSectionsTreeAction() {
//        sectionTree.addTreeSelectionListener(e -> {
//            DefaultMutableTreeNode node = (DefaultMutableTreeNode) sectionTree.getLastSelectedPathComponent();
//            node.getUserObject();
//        });
//    }

    public void setSuiteSelectedItemAction() {
        suitesCB.addActionListener(e -> {
            //Set data to use in every other cases
            data = new ToolWindowData((String) suitesCB.getSelectedItem(), (String) projectCB.getSelectedItem(), client);
            String selectedSuite = (String) suitesCB.getSelectedItem();
            if (selectedSuite != null && !selectedSuite.equals("Select your suite...")) {

                GuiUtil.runInSeparateThread(() -> {
                    // TODO: view layer.
                    disableComponent(this.suitesCB);
                    disableComponent(this.projectCB);

                    // Create root node.
                    OurSection rootSection = new OurSection();
                    rootSection.setId(null);
                    rootSection.setName((String) suitesCB.getSelectedItem()); // TODO: bad code -> need refactor

                    // Inflates root section.
                    List<Section> sectionList = client.getSections(data.getProjectId(), data.getSuiteId());
                    OurSectionInflator.inflateOurSection(sectionList, null, rootSection);

                    // Draw one node.
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootSection);
                    // Draw tree.
                    showTree(rootSection, root);

                    sectionTree.setModel(new DefaultTreeModel(root));

                    enableComponent(projectCB);
                    enableComponent(suitesCB);
                    makeVisible(this.sectionTree);
                });
            } else {
                makeInvisible(this.sectionTree);
            }
        });
    }

    private void showTree(OurSection rootSection, DefaultMutableTreeNode root) {
        if (rootSection.getSectionList().isEmpty())
            return;

        for (OurSection ourSection : rootSection.getSectionList()) {
            DefaultMutableTreeNode subSection = new DefaultMutableTreeNode(ourSection);
            root.add(subSection);

            showTree(ourSection, subSection);
        }
    }
}
