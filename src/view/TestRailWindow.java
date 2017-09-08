package view;

import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.CaseType;
import com.codepine.api.testrail.model.Field;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.treeStructure.Tree;
import model.section.OurSection;
import model.section.OurSectionInflator;
import model.testrail.RailClient;
import model.testrail.RailConnection;
import model.testrail.RailDataStorage;
import model.testrail.RailTestCase;
import model.treerenderer.TreeRenderer;
import utils.DraftClassesCreator;
import utils.GuiUtil;
import utils.ToolWindowData;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static model.testrail.RailConstants.*;
import static utils.ComponentUtil.*;

public class TestRailWindow extends WindowPanelAbstract implements Disposable {
    private static final String HTML_CLOSE_TAG = "</html>";
    private static final String HTML_OPEN_TAG = "<html>";
    private Project project;
    private JPanel mainPanel;
    private JComboBox projectComboBox;
    private JComboBox suitesComboBox;
    private Tree sectionTree;
    private JLabel loadingLabel;
    private JLabel detailsLabel;
    private JPanel detailsPanel;
    private JComboBox customFieldsComboBox;
    private JLabel customFieldsLabel;
    private RailConnection connection;
    private RailClient client;
    private ToolWindowData data;
    private List<Case> casesFromSelectedPacks = new ArrayList<>();
    private List<RailClient.CaseFieldCustom> customProjectFieldsMap = new ArrayList<>();
    private JPopupMenu testCasePopupMenu;
    private DefaultMutableTreeNode currentSelectedTreeNode = null;
    private boolean isCtrlPressed = false;
    private List<Object> selectedTreeNodeList = new ArrayList<>();

    public TestRailWindow(Project project) {
        super(project);
        this.project = project;

        disableComponent(this.suitesComboBox);
        makeInvisible(this.detailsPanel);
        makeInvisible(sectionTree);
        connection = RailConnection.getInstance(project);
        setContent(mainPanel);
        sectionTree.setCellRenderer(new TreeRenderer());

        //Listeners
        setProjectSelectedItemAction();
        setSuiteSelectedItemAction();
        setSectionsTreeAction();
        setCustomFieldsComboBoxAction();
        initTestCasePopupMenu();
    }

    public static TestRailWindow getInstance(Project project) {
        return ServiceManager.getService(project, TestRailWindow.class);
    }

    public void setDefaultFields() {
        client = new RailClient(connection.getClient());
        this.projectComboBox.addItem("Select project...");
        client.getProjectList().forEach(var -> this.projectComboBox.addItem(var.getName()));
        makeInvisible(loadingLabel);
    }

    public Tree getSectionTree() {
        return sectionTree;
    }

    public void refreshSelectedFolder() {
        TreePath[] paths = sectionTree.getSelectionPaths();
        if (null != paths && paths.length == 1) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) sectionTree.getLastSelectedPathComponent();
            if (node != null && node.getUserObject() instanceof OurSection) {
                OurSection section = (OurSection) node.getUserObject();

                sectionTree.getSelectionModel();
                List<Case> casesFromServer = client.getCases(data.getProjectId(), data.getSuiteId())
                        .stream()
                        .filter(caze -> caze.getSectionId() == section.getId())
                        .collect(Collectors.toList());

                if (section.getCases().size() != casesFromServer.size()) {
                    //sectionTree.set
                    DefaultTreeModel model = (DefaultTreeModel) sectionTree.getModel();
                    //wrap
                    DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) sectionTree.getLastSelectedPathComponent();

                    //Find cases and add those that not exists
                    casesFromServer.removeAll(section.getCases());
                    casesFromServer.forEach(caze -> node1.add(new DefaultMutableTreeNode(caze)));

                    model.nodeStructureChanged(node1);
                    model.reload();
                    sectionTree.setModel(model);

                    //clearAndRepaint(sectionTree);
                }

                //sectionTree.getModel().getChildCount(section);
            }
        }
    }

    //region Listeners

    @Override
    public void dispose() {
        ToolWindowManager.getInstance(project).unregisterToolWindow("WTM plugin");
    }

    @SuppressWarnings("unchecked")
    private void setProjectSelectedItemAction() {
        projectComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                disableComponent(this.suitesComboBox);
                GuiUtil.runInSeparateThread(() -> {
                    makeInvisible(sectionTree);
                    String selectedProject = (String) projectComboBox.getSelectedItem();
                    if (null != selectedProject && !selectedProject.equals("Select project...")) {
                        this.suitesComboBox.removeAllItems();
                        this.suitesComboBox.addItem("Select your suite...");
                        client.getSuitesList(selectedProject)
                                .forEach(suite -> this.suitesComboBox.addItem(suite.getName()));
                        enableComponent(this.suitesComboBox);
                    } else {
                        this.suitesComboBox.removeAllItems();
                    }
                });
            }
        });
    }

    private void setSectionsTreeAction() {
        addRightClickListenerToTree();

        sectionTree.addTreeSelectionListener(e -> {

            GuiUtil.runInSeparateThread(() -> {
                DefaultMutableTreeNode lastSelectedTreeNode = (DefaultMutableTreeNode) sectionTree.getLastSelectedPathComponent();

                selectedTreeNodeList.clear();
                if (null != sectionTree.getSelectionPaths()) {
                    for (TreePath path : sectionTree.getSelectionPaths()) {
                        Object userObject = ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                        selectedTreeNodeList.add(userObject);
                    }
                }
                if (null != lastSelectedTreeNode && null != lastSelectedTreeNode.getUserObject() && !(lastSelectedTreeNode.getUserObject() instanceof OurSection)) {
                    //DO nothing here as selection is Test case
                    //TODO add logic here if selected test case
                } else {
                    customProjectFieldsMap = client.getCustomFieldNamesMap(data.getProjectId());

                    casesFromSelectedPacks = getCasesForSelectedTreeRows();

                    displayCaseTypesInfo();

                    GuiUtil.runInSeparateThread(() -> {
                        disableComponent(customFieldsComboBox);
                        makeVisible(loadingLabel);

                        customFieldsComboBox.removeAllItems();

                        if (!customProjectFieldsMap.isEmpty()) {
                            makeVisible(customFieldsComboBox);
                            customProjectFieldsMap.forEach(value -> customFieldsComboBox.addItem(value.getDisplayedName()));
                            repaintComponent(customFieldsLabel);
                        } else {
                            makeInvisible(customFieldsComboBox);
                            customFieldsLabel.setText("No defined custom fields found!");
                            repaintComponent(customFieldsLabel);
                        }
                        repaintComponent(detailsPanel);
                        makeInvisible(loadingLabel);
                        enableComponent(customFieldsComboBox);
                    });
                }
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

    private void setCustomFieldsComboBoxAction() {
        customFieldsComboBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                GuiUtil.runInSeparateThread(() -> {
                    String selectedValue = (String) this.customFieldsComboBox.getSelectedItem();
                    StringBuilder builder = new StringBuilder();
                    builder.append(HTML_OPEN_TAG);
                    customProjectFieldsMap
                            .stream()
                            .filter(caseField -> caseField.getDisplayedName().equals(selectedValue))
                            .collect(Collectors.toList())
                            .forEach(caseField -> caseField.getConfigs()
                                    .forEach(
                                            config -> {
                                                renderStats(builder, config);
                                            }));

                });

            }
        });
    }

    /**
     * Render stats in @customFieldsLabel depends on TYPE which is defined in getCustomFieldNamesMap method
     */
    private void renderStats(StringBuilder builder, Field.Config config) {
        try {
            ((Field.Config.DropdownOptions) config.getOptions()).getItems().forEach((key, value) -> {
                //filter cases by option
                List<Case> cases = casesFromSelectedPacks.stream()
                        .filter(caseField1 -> caseField1.getCustomFields().entrySet().stream()
                                .anyMatch(o -> o.getValue() != null && o.getValue().equals(key)))
                        .collect(Collectors.toList());

                builder.append(value + " : " + cases.size()).append("<br>");
            });
            builder.append(HTML_CLOSE_TAG);
            customFieldsLabel.setText(builder.toString());
            repaintComponent(customFieldsLabel);
        } catch (ClassCastException ex1) {
            try {
                ((Field.Config.MultiSelectOptions) config.getOptions()).getItems().forEach((key, value) -> {
                    List<Case> cases = casesFromSelectedPacks.stream()
                            .filter(caseField1 -> caseField1.getCustomFields().entrySet().stream()
                                    .anyMatch(o -> o.getValue() != null && o.getValue().equals(key)))
                            .collect(Collectors.toList());
                    builder.append(value).append(" : ").append(cases.size()).append("<br>");
                });
            } catch (ClassCastException ex2) {
                customFieldsLabel.setText("No Options!");
                repaintComponent(customFieldsLabel);
            }
            builder.append(HTML_CLOSE_TAG);
            customFieldsLabel.setText(builder.toString());
            repaintComponent(customFieldsLabel);
        }
    }

    // endregion

    // region Section tree

    public synchronized void createDraftClasses() {
        GuiUtil.runInSeparateThread(() -> {
            makeVisible(loadingLabel);

            List<RailTestCase> railTestCases = casesFromSelectedPacks.stream()
                    .map(aCase -> new RailTestCase(aCase.getId(), client.getUserName(aCase.getCreatedBy()), aCase.getTitle(), aCase.getCustomField(STEPS_SEPARATED_FIELD), aCase.getCustomField(PRECONDITION_FIELD), aCase.getCustomField(KEYWORDS), client.getStoryNameBySectionId(data.getProjectId(), data.getSuiteId(), aCase.getSectionId())))
                    .collect(Collectors.toList());

            railTestCases.forEach(railTestCase -> {
                DraftClassesCreator.getInstance(project).create(railTestCase, settings.getTemplate());
            });

            StatusBar statusBar = WindowManager.getInstance()
                    .getStatusBar(project);

            JBPopupFactory.getInstance()
                    .createHtmlTextBalloonBuilder("<html>Draft classes created! <br>Please sync if not appeared</html>", MessageType.INFO, null)
                    .setFadeoutTime(7500)
                    .createBalloon()
                    .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                            Balloon.Position.atLeft);

            makeInvisible(loadingLabel);
        });
    }

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
                    .forEach(testCase -> subSection.add(new DefaultMutableTreeNode(testCase)));
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
        } else {
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
    // endregion

    // region Test case Tree popup

    private void displayCaseTypesInfo() {
        List<CaseType> caseTypes = client.getCaseTypes();
        StringBuilder builder = new StringBuilder();
        for (CaseType type : caseTypes) {
            List<Case> casesWithOneType = casesFromSelectedPacks.stream()
                    .filter(aCase -> aCase.getTypeId() == type.getId())
                    .collect(Collectors.toList());
            builder.append(type.getName()).append(" : ").append(casesWithOneType.size()).append("<br>");

        }

        detailsLabel.setText(HTML_OPEN_TAG + builder.toString() + HTML_CLOSE_TAG);
        repaintComponent(detailsLabel);
    }

    private void initTestCasePopupMenu() {
        testCasePopupMenu = new JPopupMenu();
        ActionListener menuListener = event -> {
            GuiUtil.runInSeparateThread(() -> {
                makeVisible(loadingLabel);

                System.out.println("Create draft classes button pressed " + selectedTreeNodeList.size());
                List<Case> caseList = selectedTreeNodeList
                        .stream()
                        .map(treeNode -> (Case)treeNode)
                        .collect(Collectors.toList());
                System.out.println("Case list " + caseList.size());

                List<RailTestCase> railTestCases = caseList.stream()
                        .map(aCase -> new RailTestCase(aCase.getId(), client.getUserName(aCase.getCreatedBy()), aCase.getTitle(), aCase.getCustomField(STEPS_SEPARATED_FIELD), aCase.getCustomField(PRECONDITION_FIELD), aCase.getCustomField(KEYWORDS), client.getStoryNameBySectionId(data.getProjectId(), data.getSuiteId(), aCase.getSectionId())))
                        .collect(Collectors.toList());
                System.out.println("Rail Test case list " + caseList.size());

                railTestCases.forEach(railTestCase -> {
                    System.out.println("Rail Test case with name " + railTestCase.getName() + "was created");
                    DraftClassesCreator.getInstance(project).create(railTestCase, settings.getTemplate());
                });

                StatusBar statusBar = WindowManager.getInstance()
                        .getStatusBar(project);

                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder("<html>Draft classes created! <br>Please sync if not appeared</html>", MessageType.INFO, null)
                        .setFadeoutTime(7500)
                        .createBalloon()
                        .show(RelativePoint.getCenterOf(statusBar.getComponent()),
                                Balloon.Position.atLeft);

                makeInvisible(loadingLabel);
            });

            System.out.println("Popup menu item ["
                    + event.getActionCommand() + "] was pressed.");
        };
        JMenuItem item = new JMenuItem("Create draft class");
        item.setIcon(GuiUtil.loadIcon("draft.png"));
        item.addActionListener(menuListener);
        testCasePopupMenu.add(item);
    }

    private void addRightClickListenerToTree() {
        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                handleContextMenu(mouseEvent);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                handleContextMenu(mouseEvent);
            }
        };

        sectionTree.addMouseListener(mouseListener);

        sectionTree.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent keyEvent) {

            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isCtrlPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
                    isCtrlPressed = false;
                }
            }
        });
    }

    private void handleContextMenu(MouseEvent mouseEvent) {
        if (mouseEvent.isPopupTrigger()) {
            TreePath pathForLocation = sectionTree.getPathForLocation(mouseEvent.getPoint().x, mouseEvent.getPoint().y);
            if (pathForLocation != null) {
                boolean isSection = false;
                for (Object item: selectedTreeNodeList) {
                    if (item instanceof OurSection) {
                        isSection = true;
                        break;
                    }
                }

                if (!isSection) {
                    currentSelectedTreeNode = (DefaultMutableTreeNode) pathForLocation.getLastPathComponent();
                    if (currentSelectedTreeNode.getUserObject() instanceof Case) {
                        testCasePopupMenu.show(mouseEvent.getComponent(),
                                mouseEvent.getX(),
                                mouseEvent.getY());
                    }
                }
            }
        }
    }

    // endregion
}
