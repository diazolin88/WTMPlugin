package model.testrail;

import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.Section;
import java.util.List;

/**
 * Storage for rail data (sections and test cases).
 */
public final class RailDataStorage {
    private List<Section> sections;
    private List<Case> cases;

    public RailDataStorage setSections(List<Section> sections) {
        this.sections = sections;
        return this;
    }

    public RailDataStorage setCases(List<Case> cases) {
        this.cases = cases;
        return this;
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Case> getCases() {
        return cases;
    }
}
