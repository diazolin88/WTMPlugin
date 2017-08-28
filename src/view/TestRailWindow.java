package view;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.Suite;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import model.testrail.RailClient;
import model.testrail.RailConnection;
import utils.GuiUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class TestRailWindow extends WindowPanelAbstract implements Disposable {
    private Project project;
    private JPanel panel1;
    private JComboBox projectCB;
    private JComboBox suitesCB;
    private JTree tree1;
    private JPanel treePanel;
    private TestRail client;

    public TestRailWindow(Project project) {
        super(project);
        this.project = project;
        client = RailConnection.getInstance(project).getClient();
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
    public void setProjectSelectedItemAction(RailClient client) {

        projectCB.addItemListener(e -> {

            GuiUtil.runInSeparateThread(() -> {
                String selectedProject = (String) projectCB.getSelectedItem();
                getSuitesCB().removeAllItems();
                client.getSuitesList(selectedProject)
                        .stream().forEach(suite -> getSuitesCB().addItem(suite.getName()));
            });

        });
    }

    public void setSuiteSelectedItemAction(RailClient client) {
        this.tree1.setVisible(false);
        suitesCB.addItemListener(e ->
                GuiUtil.runInSeparateThread(() -> {
                    this.tree1.setVisible(false);
                    int projectID = getProjectId((String) projectCB.getSelectedItem());
                    int suiteId = getSuiteId((String) suitesCB.getSelectedItem(), projectID);

                    DefaultMutableTreeNode root = new DefaultMutableTreeNode(suitesCB.getSelectedItem());
                    DefaultTreeModel model = new DefaultTreeModel(root);

                    client.getCases(projectID, suiteId).forEach(aCase -> root.add(new DefaultMutableTreeNode(aCase.getTitle())));
                    this.tree1.setModel(model);
                    this.tree1.setVisible(true);
                    this.tree1.setVisibleRowCount(50);
                }));
    }

    //TODO to util class
    private int getProjectId(String projectName) {
        return client.projects().list().execute().stream()
                .filter(project1 -> project1.getName().equals(projectName))
                .map(com.codepine.api.testrail.model.Project::getId)
                .findFirst().get();
    }

    private int getSuiteId(String suiteName, int projectId) {
        return client.suites().list(projectId).execute()
                .stream()
                .filter(suite -> suite.getName().equals(suiteName))
                .map(Suite::getId)
                .findFirst().get();
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
