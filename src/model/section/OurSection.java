package model.section;

import com.codepine.api.testrail.model.Case;

import java.util.ArrayList;
import java.util.List;

/**
 * Our section model for the tree view.
 */
public class OurSection {

    private Integer id;
    private String name;
    private List<OurSection> sectionList = new ArrayList<>();
    private List<Case> cases = new ArrayList<>();

    public OurSection() {
    }

    public OurSection(Integer id, String name, List<OurSection> sectionList, List<Case> cases) {
        this.id = id;
        this.name = name;
        this.sectionList = sectionList;
        this.cases = cases;
    }
// region Getters and setters.

    public List<OurSection> getSectionList() {
        return sectionList;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSectionList(List<OurSection> sectionList) {
        this.sectionList = sectionList;
    }

    public List<Case> getCases() {
        return cases;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

    // endregion

    public void addSubSection(OurSection section) {
        sectionList.add(section);
    }

    @Override
    public String toString() {
        return name;
    }

}
