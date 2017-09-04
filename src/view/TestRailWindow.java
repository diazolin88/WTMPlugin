package view;

import actions.CreateDraftClassAction;
import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.CaseType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
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
    private JLabel detailsLabel;
    private JPanel detailsPanel;
    private RailClient client;
    private ToolWindowData data;
    private List<Case> casesFromSelectedPacks = new ArrayList<>();

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

    public JPanel getDetailsPanel() {
        return detailsPanel;
    }

    @Override
    public void dispose() {
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

    private void setSectionsTreeAction() {
        sectionTree.addTreeSelectionListener(e -> {

            GuiUtil.runInSeparateThread(() -> {

                casesFromSelectedPacks = getCasesForSelectedTreeRows();

                displayCaseTypesInfo();
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
                    makeInvisible(this.detailsPanel);

                    // Shows section tree.
                    showSectionTree(selectedSuite);

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

    public void createDraftClasses() {
        System.out.println("Create draft classes");
        casesFromSelectedPacks.forEach(testCase -> {
            System.out.println(testCase.getTitle());
        });
    }


    private void addToolBar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.addAction(new CreateDraftClassAction());
        GuiUtil.installActionGroupInToolBar(group, this, ActionManager.getInstance(), "TestRailWindowToolBar");
    }

    // endregion

    // region Section tree

    private void showSectionTree(String selectedSuite) {
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
        createTreeNode(rootSection, root);

        sectionTree.setModel(new DefaultTreeModel(root));
    }

    /**
     * Creates tree node for our section model of data.
     *
     * @param rootSection Our section model of data.
     * @param root        Tree node view.
     */
    private void createTreeNode(OurSection rootSection, DefaultMutableTreeNode root) {
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
            createTreeNode(ourSection, subSection);
        }
    }

    private List<Case> getCasesForSelectedTreeRows() {
        TreePath[] paths = sectionTree.getSelectionPaths();
        List<Case> casesFromSelectedPacks = new ArrayList<>();

        if (null != paths) {

            for (TreePath path : paths) {
                Object userObject = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

                if (userObject instanceof OurSection) {
                    OurSection section = (OurSection) userObject;
                    //TODO here need to add all cases from current folder or(and) children folders
                    casesFromSelectedPacks.addAll(getCases(section));
                }
            }
            makeVisible(this.detailsPanel);
        } else if( null == paths) {
            makeInvisible(detailsPanel);
        }
        return casesFromSelectedPacks;
    }

    private List<Case> getCases(OurSection section) {
        List<Case> cases = new ArrayList<>();
        for (OurSection section1 : section.getSectionList()) {
            cases.addAll(getCases(section1));
        }
        cases.addAll(section.getCases());
        return cases;
    }

    private void displayCaseTypesInfo() {
        List<CaseType> caseTypes = client.getCaseTypes();
        StringBuilder builder = new StringBuilder();
        for (CaseType type : caseTypes) {
            List<Case> casesWithOneType = casesFromSelectedPacks.stream()
                    .filter(aCase -> aCase.getTypeId() == type.getId())
                    .collect(Collectors.toList());
            builder.append(type.getName()).append(" : ").append(casesWithOneType.size()).append("<br>");

        }

        detailsLabel.setText("<html>" + builder.toString() + "</html>");
        repaintComponent(detailsLabel);
    }
    // endregion
}
