package utils;

import com.codepine.api.testrail.model.Field;

import java.util.List;

/**
 * Model for TestRail test case.
 */
public class RailTestCase {
    private int id;
    private String userName;
    private String name;
    private List<Field.Step> description;
    private String preconditions;
    private List<String> keywords;
    private String folderName;

    public RailTestCase(int id, String userName , String name, List<Field.Step> description, String preconditions, List<String> keywords, String folderName) {
        this.id = id;
        this.userName = userName;

        this.name = name;
        this.description = description;
        this.preconditions = preconditions;
        this.keywords = keywords;
        this.folderName = folderName;
    }

    public List<Field.Step> getDescription() {
        return description;
    }

    public String getPreconditions() {
        return preconditions;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }
}
