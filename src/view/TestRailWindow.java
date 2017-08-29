package view;

import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.Section;
import com.codepine.api.testrail.model.Suite;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import model.testrail.RailClient;
import model.testrail.RailConnection;
import treerenderer.TreeCustom;
import utils.GuiUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ItemEvent;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static utils.ComponentUtil.*;

public class TestRailWindow extends WindowPanelAbstract implements Disposable {
    private Project project;
    private JPanel panel1;
    private JComboBox projectCB;
    private JComboBox suitesCB;
    private Tree sectionsTree;
    private JScrollPane scroll;
    private JPanel treePanel;
    private RailClient client;
    DefaultMutableTreeNode parent;
    public TestRailWindow(Project project) {
        super(project);
        this.project = project;
        client = new RailClient(RailConnection.getInstance(project).getClient());
        setContent(panel1);
        setProjectSelectedItemAction();
        setSuiteSelectedItemAction();
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
                    makeInvisible(sectionsTree);
                    String selectedProject = (String) projectCB.getSelectedItem();
                    if (!selectedProject.equals("Select project...")) {
                        getSuitesCB().removeAllItems();
                        client.getSuitesList(selectedProject)
                                .forEach(suite -> getSuitesCB().addItem(suite.getName()));
                        enableComponent(this.suitesCB);
                    }
                });
            }
        });
    }

    public void setSuiteSelectedItemAction() {

        suitesCB.addActionListener(e -> {
            GuiUtil.runInSeparateThread(() -> {
                makeInvisible(this.sectionsTree);
                disableComponent(this.suitesCB);
                TreeCustom tree = buildTree();

                DefaultMutableTreeNode root = new DefaultMutableTreeNode(this.suitesCB.getSelectedItem());
                DefaultTreeModel model = new DefaultTreeModel(root);

                for (TreeCustom section : tree.getChildren()) {
                    if (section.getParent() == null) {
                        parent = new DefaultMutableTreeNode(section.getName());
                    }
                    DefaultMutableTreeNode finalSection = parent;
                    section.getCases()
                            .stream()
                            .filter(aCase -> aCase.getSectionId() == section.getId())
                            .forEach(aCase -> finalSection.add(new DefaultMutableTreeNode(aCase.getTitle())));
                    root.add(parent);
                }
                this.sectionsTree.setModel(model);
                scroll.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);

                enableComponent(suitesCB);
                makeVisible(this.sectionsTree);
            });
        });

    }

    private Integer getProjectId(String projectName) {
        try {
            return client.getProjectList().stream()
                    .filter(project1 -> project1.getName().equals(projectName))
                    .map(com.codepine.api.testrail.model.Project::getId)
                    .findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private Integer getSuiteId(String projectName, String suiteName) {
        try {
            return client.getSuitesList(projectName)
                    .stream()
                    .filter(suite -> suite.getName().equals(suiteName))
                    .map(Suite::getId)
                    .findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private Integer getParentSection(int sectionID, int projectID, int suiteId) {
        try {
            return client.getSections(projectID, suiteId).stream()
                    .map(Section::getParentId)
                    .findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private TreeCustom buildTree() {
        int projectID = getProjectId((String) projectCB.getSelectedItem());
        int suiteID = getSuiteId((String) projectCB.getSelectedItem(),(String) suitesCB.getSelectedItem());

        java.util.List<Section> sections = client.getSections(projectID, suiteID);
        TreeCustom root = new TreeCustom(null);
        java.util.List<Case> cases = client.getCases(projectID, suiteID);
        for (Section section : sections) {
            if (null == section.getParentId()) {
                TreeCustom parent = new TreeCustom(root);
                parent.setCases(cases.stream().filter(aCase -> aCase.getSectionId() == section.getId()).collect(Collectors.toList()));
                parent.setName(section.getName());
                parent.setId(section.getId());
                root.addChildren(parent);
            }
            if(null != section.getParentId()){
                TreeCustom parentOfChild = root.getChildrenById(section.getParentId());
                TreeCustom child = new TreeCustom(parentOfChild);
                child.setCases(cases.stream().filter(aCase -> aCase.getSectionId() == section.getId()).collect(Collectors.toList()));
                child.setName(section.getName());
                child.setId(section.getId());
                parentOfChild.addChildren(child);
            }
        }
        return root;
    }
}
