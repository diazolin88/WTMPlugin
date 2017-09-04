package view;

import actions.CreateDraftClassAction;
import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.CaseType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.treeStructure.Tree;
import model.section.OurSection;
import model.section.OurSectionInflator;
import model.testrail.RailClient;
import model.testrail.RailConnection;
import model.testrail.RailDataStorage;
import model.treerenderer.TestCase;
import model.treerenderer.TreeRenderer;
import utils.GuiUtil;
import utils.ToolWindowData;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static utils.ComponentUtil.*;

public class TestRailWindow extends WindowPanelAbstract implements Disposable {
    private JPanel mainPanel;
    private JComboBox projectComboBox;
    private JComboBox suitesComboBox;
    private Tree sectionTree;
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
        addToolBar();
    }

    public static TestRailWindow getInstance(Project project) {
        return ServiceManager.getService(project, TestRailWindow.class);
    }

    public JComboBox getProjectComboBox() {
        return projectComboBox;
    }

    public JComboBox getSuitesComboBox() {
        return suitesComboBox;
    }

    @Override
    public void dispose() {
    }

    private void addToolBar(){
        DefaultActionGroup group = new DefaultActionGroup();
        group.addAction(new CreateDraftClassAction());
        GuiUtil.installActionGroupInToolBar(group, this, ActionManager.getInstance(), "TestRailWindowToolBar");
    }

    @SuppressWarnings("unchecked")
    private void setProjectSelectedItemAction() {
        projectComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                disableComponent(this.suitesComboBox);
                GuiUtil.runInSeparateThread(() -> {
                    makeInvisible(sectionTree);
                    String selectedProject = (String) projectComboBox.getSelectedItem();
                    if (!selectedProject.equals("Select project...")) {
                        getSuitesComboBox().removeAllItems();
                        getSuitesComboBox().addItem("Select your suite...");
                        client.getSuitesList(selectedProject)
                                .forEach(suite -> getSuitesComboBox().addItem(suite.getName()));
                        enableComponent(this.suitesComboBox);
                    } else {
                        getSuitesComboBox().removeAllItems();
                    }
                });
            }
        });
    }

    public void print(){
//        DraftClassesCreator.getInstance()
    }

    private List<Case> casesFromSelectedPacks = new ArrayList<>();
    private void setSectionsTreeAction() {
        sectionTree.addTreeSelectionListener(e -> {

            GuiUtil.runInSeparateThread(() -> {
                clearAndRepaint(detailsPanel);
                //draw stats
                detailsPanel.setLayout(new GridLayout());
                TreePath[] paths;

                casesFromSelectedPacks.clear();
                if (null != (paths = sectionTree.getSelectionPaths())) {

                    for (TreePath path : paths) {
                        Object userObject = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

                        if (userObject instanceof OurSection) {
                            OurSection section = (OurSection) userObject;
                            //TODO here need to add all cases from current folder or(and) children folders
                            casesFromSelectedPacks.addAll(section.getCases());
                        }
                    }
                }
                List<CaseType> caseTypes = client.getCaseTypes();
                StringBuilder builder = new StringBuilder();
                for (CaseType type : caseTypes) {
                    List<Case> casesWithOneType = casesFromSelectedPacks.stream()
                            .filter(aCase -> aCase.getTypeId() == type.getId())
                            .collect(Collectors.toList());
                    builder.append(type.getName()).append(" : ").append(casesWithOneType.size()).append("<br>");
                    //TODO here need to create JLabel with details {TypeName and casesWithOneType.size()}

                }
                JLabel label = new JBLabel("<html>" + builder.toString() + "</html>");
                detailsPanel.add(label);
                repaintComponent(detailsPanel);
            });
        });
    }


    private void setSuiteSelectedItemAction() {
        suitesComboBox.addActionListener(e -> {
            //Set data to use in every other cases
            data = new ToolWindowData((String) this.suitesComboBox.getSelectedItem(), (String) projectComboBox.getSelectedItem(), client);
            String selectedSuite = (String) this.suitesComboBox.getSelectedItem();
            if (selectedSuite != null && !selectedSuite.equals("Select your suite...")) {

                GuiUtil.runInSeparateThread(() -> {
                    // TODO: view layer.
                    disableComponent(this.suitesComboBox);
                    disableComponent(this.projectComboBox);
                    makeVisible(this.loadingLabel);
                    makeInvisible(this.sectionTree);
                    // Create root node.
                    OurSection rootSection = new OurSection();
                    rootSection.setId(null);
                    rootSection.setName(selectedSuite);

                    // Inflates root section.
                    // TODO: i don't understand what is the line doing
                    RailDataStorage railData = new RailDataStorage()
                            .setCases(client.getCases(data.getProjectId(), data.getSuiteId()))
                            .setSections(client.getSections(data.getProjectId(), data.getSuiteId()));
                    OurSectionInflator.inflateOurSection(railData, null, rootSection);

                    // Draw one node.
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootSection);
                    // Draw tree.
                    showTree(rootSection, root);

                    sectionTree.setModel(new DefaultTreeModel(root));

                    enableComponent(this.projectComboBox);
                    enableComponent(this.suitesComboBox);
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
                    .forEach(testCase -> {
                        TestCase testCaseData = new TestCase(testCase.getTitle());
                        testCaseData.setId(testCase.getId());
                        subSection.add(new DefaultMutableTreeNode(testCaseData));
                    });
            showTree(ourSection, subSection);
        }
    }
}
