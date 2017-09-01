package utils;

import com.codepine.api.testrail.model.Field;
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

    // region Create draft class or classes
    public void create(List<RailTestCase> testCaseList) {
        for (RailTestCase testCase : testCaseList) {
            create(testCase);
        }
    }

    public void create(RailTestCase testCase) {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        HashMap<String, String> draftDataMap = new HashMap<>();

        String preconditions = format(testCase.getPreconditions());
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

        LOGGER.info("------------------------------------------------");
        TemplateEngine.getInstance().generateDraftClass(draftDataMap);
        LOGGER.info("------------------------------------------------");
    }

    // endregion

    // region Formatting strings for test case

    private String getClassNameForTestCase(RailTestCase testCase) {
        return new StringBuilder()
                .append(PROJECT_PREFIX)
                .append("_C")
                .append(testCase.getId())
                .append("_")
                .append(CLASS_NAME_STUB)
                .toString();
    }

    private String getDescriptionForTestCase(RailTestCase testCase) {
        int numberStep = 0;
        StringBuilder builder = new StringBuilder();
        for (Field.Step step : testCase.getDescription()) {
            numberStep++;
            builder
                    .append(numberStep)
                    .append(". ")
                    .append(format(step.getContent()))
                    .append("\n * Expected result:\n * ")
                    .append(format(step.getExpected()));
        }
        return builder.toString();
    }

    private String format(String formatString) {
        if (formatString.endsWith("\n")) {
            formatString = formatString.substring(0, formatString.length() - 1);
        }
        formatString = formatString.replaceAll("\n", "\n * ");
        return formatString;
    }

    // endregion
}
