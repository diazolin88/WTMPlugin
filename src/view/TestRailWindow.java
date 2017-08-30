package view;

import com.codepine.api.testrail.model.Section;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import model.testrail.RailClient;
import model.testrail.RailConnection;
import model.treerenderer.PackageCustom;
import model.treerenderer.RootCustom;
import model.treerenderer.TreeRenderer;
import utils.GuiUtil;
import utils.ToolWindowData;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.stream.Collectors;

import static utils.ComponentUtil.*;

public class TestRailWindow extends WindowPanelAbstract implements Disposable {
    private Project project;
    private JPanel panel1;
    private JComboBox projectCB;
    private JComboBox suitesCB;
    private Tree sectionTree;
    private JScrollPane scroll;
    private JTabbedPane tabbedPane1;
    private JPanel treePanel;
    private RailClient client;
    private ToolWindowData data;

    public TestRailWindow(Project project) {
        super(project);
        this.project = project;
        client = new RailClient(RailConnection.getInstance(project).getClient());
        sectionTree.setSize(new Dimension(-1,600));
        sectionTree.setCellRenderer(new TreeRenderer());
        setProjectSelectedItemAction();
        setSuiteSelectedItemAction();
        setContent(panel1);
    }

    public static TestRailWindow getInstance(Project project) {
        return ServiceManager.getService(project, TestRailWindow.class);
    }

    public JPanel getTreePanel() {
        return treePanel;
    }

    public void setTreePanel(JPanel treePanel) {
        this.treePanel = treePanel;
    }

    public JPanel getPanel1() {
        return panel1;
    }

    public void setPanel1(JPanel panel1) {
        this.panel1 = panel1;
    }

    public JComboBox getProjectCB() {
        return projectCB;
    }

    public void setProjectCB(JComboBox projectCB) {
        this.projectCB = projectCB;
    }

    public JComboBox getSuitesCB() {
        return suitesCB;
    }

    public void setSuitesCB(JComboBox suitesCB) {
        this.suitesCB = suitesCB;
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

    public void setSectionsTreeAction() {
        sectionTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) sectionTree.getLastSelectedPathComponent();
            node.getUserObject();
        });
    }

    public void setSuiteSelectedItemAction() {
        suitesCB.addActionListener(e -> {
            //Set data to use in every other cases
            data = new ToolWindowData((String) suitesCB.getSelectedItem(), (String) projectCB.getSelectedItem(), client);
            String selectedSuite = (String) suitesCB.getSelectedItem();
            if (selectedSuite != null && !selectedSuite.equals("Select your suite...")) {

                GuiUtil.runInSeparateThread(() -> {
                    disableComponent(this.suitesCB);
                    disableComponent(this.projectCB);
                    //TODO start here
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new RootCustom((String) suitesCB.getSelectedItem()));

                    for (Section section : client.getSections(data.getProjectId(), data.getSuiteId())) {
                        if (null == section.getParentId()) {
                            DefaultMutableTreeNode rootChild = new DefaultMutableTreeNode(new PackageCustom(section));
                            root.add(rootChild);
                        }
                    }
                    sectionTree.setModel(new DefaultTreeModel(root));
                    //TODO end here

                    enableComponent(projectCB);
                    enableComponent(suitesCB);
                    makeVisible(this.sectionTree);
                });
            } else {
                makeInvisible(this.sectionTree);
            }
        });
    }

    private Integer getParentSection(int sectionID, int projectID, int suiteId) {
        return client.getSections(projectID, suiteId).stream()
                .map(Section::getParentId)
                .findFirst().orElse(null);
    }

    private List<Section> getChildren(Section parent) {
        return client.getSections(data.getProjectId(), data.getSuiteId())
                .stream()
                .filter(section -> null != section.getParentId())
                .filter(section1 -> parent.getId() == section1.getParentId())
                .collect(Collectors.toList());
    }

}
