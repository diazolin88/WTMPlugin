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
import org.jetbrains.annotations.NotNull;
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
    private JComboBox projectCB;
    private JComboBox suitesCB;
    private Tree sectionTree;
    private JLabel loadingLabel;
    private JLabel detailsLabel;
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

    public JComboBox getProjectCB() {
        return projectCB;
    }

    public JLabel getDetailsLabel() {
        return detailsLabel;
    }

    public JComboBox getSuitesCB() {
        return suitesCB;
    }

    @Override
    public void dispose() {
    }

    private void addToolBar() {
        DefaultActionGroup group = new DefaultActionGroup();
        group.addAction(new CreateDraftClassAction());
        GuiUtil.installActionGroupInToolBar(group, this, ActionManager.getInstance(), "TestRailWindowToolBar");
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

    public void print() {
        System.out.print("test");
    }

    private void setSectionsTreeAction() {
        sectionTree.addTreeSelectionListener(e -> {

            GuiUtil.runInSeparateThread(() -> {
                //draw stats

                List<Case> casesFromSelectedPacks = getCasesForSelectedTreeRows();
                List<CaseType> caseTypes = client.getCaseTypes();
                         StringBuilder builder = new StringBuilder();

                for (CaseType type : caseTypes) {
                    List<Case> casesWithOneType = casesFromSelectedPacks.stream()
                            .filter(aCase -> aCase.getTypeId() == type.getId())
                            .collect(Collectors.toList());
                    builder.append(type.getName()).append(" : ").append(casesWithOneType.size()).append("<br>");

                }

                makeVisible(this.detailsLabel);
                detailsLabel.setText("<html>" + builder.toString() + "</html>");
                repaintComponent(detailsLabel);
                //detailsPanel.add(detailsLabel);
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
                    //TODO add children to rootsection!!!

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
                    .forEach(testCase -> {
                        TestCase testCaseData = new TestCase(testCase.getTitle());
                        testCaseData.setId(testCase.getId());
                        subSection.add(new DefaultMutableTreeNode(testCaseData));
                    });
            showTree(ourSection, subSection);
        }
    }

    @NotNull
    private List<Case> getCasesForSelectedTreeRows() {
        TreePath[] paths;
        List<Case> casesFromSelectedPacks = new ArrayList<>();

        if (null != (paths = sectionTree.getSelectionPaths())) {

            for (TreePath path : paths) {
                Object userObject = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();

                if (userObject instanceof OurSection) {
                    OurSection section = (OurSection) userObject;
                    //TODO here need to add all cases from current folder or(and) children folders
                    casesFromSelectedPacks.addAll(getCases(section));
                }
            }
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
}
