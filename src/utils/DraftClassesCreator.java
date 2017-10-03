package utils;

import com.codepine.api.testrail.model.Field;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import model.testrail.RailTestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static utils.TemplateEngine.*;

public class DraftClassesCreator {
    private static final Logger LOGGER = LoggerFactory.getLogger(DraftClassesCreator.class);
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String PROJECT_PREFIX = "NG";
    private static final String CLASS_NAME_STUB = "TBD";
    private final Project project;

    private DraftClassesCreator(Project project) {
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
        draftDataMap.put(USER_NAME_KEY, getOnlyString(testCase.getUserName()));
        draftDataMap.put(TEST_DATE_KEY, dateFormat.format(new Date()));
        draftDataMap.put(TEST_DESCRIPTION_KEY, description);
        draftDataMap.put(PROJECT_PREFIX_KEY, PROJECT_PREFIX);
        draftDataMap.put(TEST_RAIL_ID_KEY, String.valueOf(testCase.getId()));
        draftDataMap.put(TEST_RAIL_TITLE_KEY, testCase.getName());
        draftDataMap.put(PRECONDITIONS_KEY, preconditions);
        draftDataMap.put(CLASS_NAME_KEY, className);
        draftDataMap.put(TEST_METHOD_NAME_KEY, getUserCreds(testCase));
        draftDataMap.put(CASE_PREFIX_KEY, "_C");
        draftDataMap.put(STORY_KEY, "\"" + getFormattedFolderName(testCase.getFolderName()) + "\"");

        String[] userNamePart = testCase.getUserName().trim().split(" ");
        draftDataMap.put(AUTHOR_SHORT_NAME, getOnlyString(userNamePart[0]).substring(0, 1) + getOnlyString(userNamePart[1]).substring(0, 1));

        LOGGER.info("------------------------------------------------");
        TemplateEngine engine = new TemplateEngine(project);
        engine.generateDraftClass(draftDataMap, template);
        LOGGER.info("------------------------------------------------");
    }

    // endregion

    // region Formatting strings for test case

    /**
     * Remove all not literals symbols. For ex. - need for getting string from html.
     *
     * @param string Any string.
     * @return String with literals.
     */
    private String getOnlyString(String string) {
        Pattern pattern = Pattern.compile("[^a-z A-Z]");
        Matcher matcher = pattern.matcher(string);
        return matcher.replaceAll("");
    }

    private String getFormattedFolderName(String folderName) {
        Pattern pattern = Pattern.compile("NG-[0-9]+");
        Matcher matcher = pattern.matcher(folderName);
        matcher.find();
        return matcher.group();
    }

    public String getClassNameForTestCase(RailTestCase testCase) {
        return new StringBuilder()
                .append("C")
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
                    .append("<p>\n")
                    .append(" * Step ")
                    .append(numberStep)
                    .append(": \n");

            String content = step.getContent();

            String[] c = content.split("\n");
            List<String> out = Arrays.stream(c).map(s -> " *   " + s.replaceAll("_", "")).collect(Collectors.toList());
            StringBuilder b2 = new StringBuilder();
            out.forEach(s -> b2.append(s).append("\n"));

            builder
                    .append(b2.toString())
                    .append(" * Expected result:\n *   ")
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
        formatString = formatString.replaceAll("\n", "\n *   ").replaceAll("_", "");
        return formatString;
    }

    private String getUserCreds(RailTestCase testCase) {
        return testCase.getUserName().split(" ")[0].subSequence(0, 1).toString() + testCase.getUserName().toString().split(" ")[1].subSequence(0, 1).toString().trim().toUpperCase();
    }

    // endregion
}
