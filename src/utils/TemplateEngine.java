package utils;

import com.intellij.openapi.project.Project;
import org.slf4j.*;
import settings.WTMSettings;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

/**
 * Template engine.
 * He parses tpl file (*.tpl) and replaces markers on some data(a string).
 * Markers must have a view {{MARKER}} .
 * If you want to add markers and to do a work engine please do this:
 * 1. Add to *.tpl marker.
 * 2. Add to draft hash map key (he is equals with marker name in *tpl without '{' and '}' ).
 */
public class TemplateEngine {
    private final Project project;
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateEngine.class);

    public static final String USER_NAME_KEY = "USER_NAME";
    public static final String TEST_DATE_KEY = "TEST_DATE";
    public static final String TEST_DESCRIPTION_KEY = "TEST_DESCRIPTION";
    public static final String PROJECT_PREFIX_KEY = "PROJECT_PREFIX";
    public static final String TEST_RAIL_ID = "TEST_RAIL_ID";
    public static final String TEST_RAIL_TITLE_KEY = "TEST_RAIL_TITLE";
    public static final String CLASS_NAME = "CLASS_NAME";
    public static final String PRECONDITIONS_KEY = "PRECONDITIONS";
    public static final String SUMMARY_KEY = "SUMMARY";
    public static final String TEST_METHOD_NAME_KEY = "TEST_METHOD_NAME_KEY";
    public static final String CASE_PREFIX_KEY = "CASE_PREFIX_KEY";
    public static final String AUTHOR_SHORT_NAME = "AUTHOR_SHORT_NAME";
    public static final String STORY_KEY = "STORY";
    public static final String GHERKIN = "GHERKIN";
    public static final String TITLE = "TITLE";
    public static final String KEYWORDS = "KEYWORDS";

    private String draftDirectoryPath = "";
    private static final String KEY_WORD_TEMPLATE = "\\{\\{%s\\}\\}";
    private WTMSettings settings;

    public TemplateEngine(Project project) {
        this.project = project;
        draftDirectoryPath = project.getBasePath() + "/drafts";
        settings = WTMSettings.getInstance(project);
    }

    /**
     * Generates draft class by map data.
     *
     * @param draftDataMap Map of draft data.
     */
    public void generateDraftClass(HashMap<String, String> draftDataMap, String template) {
        List<String> rowList = new ArrayList<>(Arrays.asList(template.split("\n")));

        // Creates directory if directory doesn't exist.
        File draftDirectory = new File(draftDirectoryPath);
        if (!draftDirectory.exists()) {
            LOGGER.info("Created draft directory");
            draftDirectory.mkdir();
        }

        LOGGER.info("Beginning of replacement of markers");
        Set<String> keySet = draftDataMap.keySet();
        rowList = rowList
                .stream()
                .map(rowItem -> {
                    for (String key : keySet) {
                        //TODO IllegalArgumentException when replace test with { }
                        rowItem = rowItem.replaceAll(String.format(KEY_WORD_TEMPLATE, key), draftDataMap.get(key));
                    }
                    rowItem += "\n";
                    return rowItem;
                })
                .collect(Collectors.toList());
        LOGGER.info("Ending of replacement of markers");

        LOGGER.info(String.format("Creating %s class", draftDataMap.get(CLASS_NAME)));
        Pattern pattern = Pattern.compile("public class.*");
        Matcher matcher = pattern.matcher(settings.getTemplate());
        //get public class row
        String str = null;
        if (matcher.find()) {
            str = matcher.group();
        }

        Pattern pattern2 = Pattern.compile("\\{\\{[A-Z_]*\\}\\}");
        Matcher matcher2 = pattern2.matcher(str);
        //get field list
        List<String> list = new ArrayList<>();
        while (matcher2.find()) {
            list.add(matcher2.group().replaceAll("[\\{\\}]", ""));
        }
        String className = str.split(" ")[2];

        for (String var : list) {
            className = className.replaceAll("\\{\\{[" + var + "]*\\}\\}", draftDataMap.get(var));
        }

        String filepath = draftDirectoryPath + "/" + className;
        writeRowListToFile(rowList, filepath + ".java");
    }

    /**
     * Writes row list to file.
     *
     * @param rowList  List of rows.
     * @param filePath Path to file.
     */
    private void writeRowListToFile(List<String> rowList, String filePath) {
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(filePath))) {
            for (String row : rowList) {
                fileWriter.write(row);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
