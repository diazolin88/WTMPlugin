package view;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.treeStructure.Tree;
import model.OurSectionInflator;
import model.section.OurSection;
import model.testrail.RailClient;
import model.testrail.RailConnection;
import model.treerenderer.CaseCustom;
import model.treerenderer.TreeRenderer;
import utils.GuiUtil;
import utils.RailDataStorage;
import utils.ToolWindowData;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.ItemEvent;

import static utils.ComponentUtil.*;

public class TestRailWindow extends WindowPanelAbstract implements Disposable {
    private JPanel mainPanel;
    private JComboBox projectCB;
    private JComboBox suitesCB;
    private Tree sectionTree;
    private JScrollPane scroll;
    private JLabel loadingLabel;
    private JPanel detailsPanel;
    private RailClient client;
    private ToolWindowData data;

    public TestRailWindow(Project project) {
        super(project);
        client = new RailClient(RailConnection.getInstance(project).getClient());
        makeInvisible(loadingLabel);
        setContent(mainPanel);
        sectionTree.setCellRenderer(new TreeRenderer());
        setProjectSelectedItemAction();
        setSuiteSelectedItemAction();
        setSectionsTreeAction();
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
    private void setProjectSelectedItemAction() {
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

    private void setSectionsTreeAction() {
        sectionTree.addTreeSelectionListener(e -> {
            GuiUtil.runInSeparateThread(() -> {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) sectionTree.getLastSelectedPathComponent();
                Object selectedRow = node.getUserObject();
                if(selectedRow instanceof OurSection){
                    getMainPanel().add(new JLabel("STRING"));
                }
            });

        });
    }

    private void setSuiteSelectedItemAction() {
        suitesCB.addActionListener(e -> {
            //Set data to use in every other cases
            data = new ToolWindowData((String) this.suitesCB.getSelectedItem(), (String) projectCB.getSelectedItem(), client);
            String selectedSuite = (String) this.suitesCB.getSelectedItem();
            if (selectedSuite != null && !selectedSuite.equals("Select your suite...")) {

                GuiUtil.runInSeparateThread(() -> {
                    // TODO: view layer.
                    disableComponent(this.suitesCB);
                    disableComponent(this.projectCB);
                    makeVisible(this.loadingLabel);
                    makeInvisible(this.sectionTree);
                    // Create root node.
                    OurSection rootSection = new OurSection();
                    rootSection.setId(null);
                    rootSection.setName(selectedSuite);

                    // Inflates root section.
                    RailDataStorage railData = new RailDataStorage()
                            .setCases(client.getCases(data.getProjectId(), data.getSuiteId()))
                            .setSections(client.getSections(data.getProjectId(), data.getSuiteId()));
                    OurSectionInflator.inflateOurSection(railData, null, rootSection);

                    // Draw one node.
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootSection);
                    // Draw tree.
                    showTree(rootSection, root);

                    sectionTree.setModel(new DefaultTreeModel(root));

                    enableComponent(this.projectCB);
                    enableComponent(this.suitesCB);
                    makeVisible(this.sectionTree);
                    makeInvisible(this.loadingLabel);
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
            ourSection.getCases()
                    .forEach(testCase ->  {
                        CaseCustom testCaseCustom = new CaseCustom(testCase.getTitle());
                        testCaseCustom.setId(testCase.getId());
                                subSection.add(new DefaultMutableTreeNode(testCaseCustom));
                    });
            showTree(ourSection, subSection);
        }
    }

    private JPanel getMainPanel(){
        return this.mainPanel;
    }
}
