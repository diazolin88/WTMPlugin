package view;

import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.Section;
import com.codepine.api.testrail.model.Suite;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.containers.Convertor;
import model.OurSection;
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
import javax.swing.tree.TreePath;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
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
    private ToolWindowData data;

    public TestRailWindow(Project project) {
        super(project);
        this.project = project;
        client = new RailClient(RailConnection.getInstance(project).getClient());
        setContent(panel1);
        sectionsTree.setCellRenderer(new TreeRenderer());
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
        sectionsTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) sectionsTree.getLastSelectedPathComponent();
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
                    // TODO: view layer.
                    disableComponent(this.suitesCB);
                    disableComponent(this.projectCB);

                    //TODO start here
                    OurSection rootSection = new OurSection();
                    rootSection.setId(null);
                    rootSection.setName((String) suitesCB.getSelectedItem()); // TODO: bad code -> need refactor

                    List<Section> sectionList = client.getSections(data.getProjectId(), data.getSuiteId());
                    inflateOurSection(sectionList, null, rootSection);


                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(new RootCustom(rootSection.getName()));
                    showTree(rootSection, root);

//
//
//                    for(Section section :sectionList){
//                        if(null == section.getParentId()) {
//                            DefaultMutableTreeNode rootChild = new DefaultMutableTreeNode(new PackageCustom(section));
//                            root.add(rootChild);
//                        }
//                    }
//

                    sectionsTree.setModel(new DefaultTreeModel(root));
                    //TODO end here

                    enableComponent(projectCB);
                    enableComponent(suitesCB);
                    makeVisible(this.sectionsTree);
                });
            } else {
                makeInvisible(this.sectionsTree);
            }
        });
    }

    private void showTree(OurSection rootSection, DefaultMutableTreeNode root) {
        if (rootSection.getSectionList().isEmpty())
            return;

        for (OurSection ourSection : rootSection.getSectionList()) {
            DefaultMutableTreeNode subSection = new DefaultMutableTreeNode(new RootCustom(ourSection.getName()));
            root.add(subSection);

            showTree(ourSection, subSection);
        }
    }

    private void inflateOurSection(List<Section> sectionList, Integer parentId, OurSection rootSection) {
        for (Section section : sectionList) {

            if (parentId == null) {
                if (section.getParentId() == parentId) {
                    OurSection ourSection = new OurSection();
                    ourSection.setId(section.getId());
                    ourSection.setName(section.getName());

                    rootSection.addSubSection(ourSection);
                    inflateOurSection(sectionList, ourSection.getId(), ourSection);
                }
            }
            if (parentId != null) {
                if (section.getParentId() != null) {
                    if (section.getParentId().equals(parentId)) {
                        OurSection ourSection = new OurSection();
                        ourSection.setId(section.getId());
                        ourSection.setName(section.getName());

                        rootSection.addSubSection(ourSection);
                        inflateOurSection(sectionList, ourSection.getId(), ourSection);
                    }
                }
            }
        }
    }
}
