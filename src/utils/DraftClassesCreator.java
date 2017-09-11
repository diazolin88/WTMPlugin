package utils;

import com.codepine.api.testrail.model.Field;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import model.testrail.RailTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static utils.TemplateEngine.*;

public class DraftClassesCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DraftClassesCreator.class);
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String PROJECT_PREFIX = "NG";
    private static final String CLASS_NAME_STUB = "TBD";
    private Project project;

    public DraftClassesCreator(Project project) {
        this.project = project;
    }

    public static DraftClassesCreator getInstance(Project project) {
        return ServiceManager.getService(project, DraftClassesCreator.class);
    }

    // region Create draft class or classes
    public void create(List<RailTestCase> testCaseList, String template) {
        for (RailTestCase testCase : testCaseList) {
            create(testCase, template);
        }
    }

    public void create(RailTestCase testCase, String template) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        HashMap<String, String> draftDataMap = new HashMap<>();

        String preconditions = format(testCase.getPreconditions() == null ? "" : testCase.getPreconditions());
        String className = getClassNameForTestCase(testCase);
        String description = getDescriptionForTestCase(testCase);

        draftDataMap.put(SUMMARY_KEY, testCase.getName());
        draftDataMap.put(USER_NAME_KEY, testCase.getUserName());
        draftDataMap.put(TEST_DATE_KEY, dateFormat.format(new Date()));
        draftDataMap.put(TEST_DESCRIPTION_KEY, description);
        draftDataMap.put(PROJECT_PREFIX_KEY, PROJECT_PREFIX);
        draftDataMap.put(TEST_RAIL_ID_KEY, String.valueOf(testCase.getId()));
        draftDataMap.put(TEST_RAIL_TITLE_KEY, testCase.getName());
        draftDataMap.put(PRECONDITIONS_KEY, preconditions);
        draftDataMap.put(CLASS_NAME_KEY, className);
        draftDataMap.put(TEST_METHOD_NAME_KEY, getUserCreds(testCase));
        draftDataMap.put(CASE_PREFIX_KEY, "_C");

        LOGGER.info("------------------------------------------------");
        TemplateEngine engine = new TemplateEngine(project);
        engine.generateDraftClass(draftDataMap, template);
        LOGGER.info("------------------------------------------------");
    }

    // endregion

    // region Formatting strings for test case

    public String getClassNameForTestCase(RailTestCase testCase) {
        return new StringBuilder()
                .append(PROJECT_PREFIX)
                .append("_C")
                .append(testCase.getId())
                .append("_")
                .append(testCase.getName())
                .toString();
    }

    private String getDescriptionForTestCase(RailTestCase testCase) {
        int numberStep = 0;

        if (testCase.getDescription() == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for (Field.Step step : testCase.getDescription()) {
            numberStep++;
            builder
                    .append(numberStep)
                    .append(". ");

            String content = step.getContent();
            String[] contentArray = content.split(".");
            if (contentArray.length != 0) {
                content = "";
                for (int i = 0; i < contentArray.length; i++) {
                    content += contentArray[i] + " \n * ";
                }
            }

            builder
                    .append(content)
                    .append("\n * Expected result:\n * ")
                    .append(format(step.getExpected()))
                    .append("\n * ");
        }

        String content = builder.toString();
        return content.substring(0, content.length() - 4);
    }

    private String format(String formatString) {
        if (formatString.endsWith("\n")) {
            formatString = formatString.substring(0, formatString.length() - 1);
        }
        formatString = formatString.replaceAll("\n", "\n * ");
        return formatString;
    }

    private String getUserCreds(RailTestCase testCase) {
        return testCase.getUserName().split(" ")[0].subSequence(0, 1).toString() + testCase.getUserName().toString().split(" ")[1].subSequence(0, 1).toString().trim().toUpperCase();
    }

    // endregion
}
