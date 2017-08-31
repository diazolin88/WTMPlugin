package model.section;

import java.util.ArrayList;
import java.util.List;

/**
 * Our section model for the tree view.
 */
public class OurSection {

    private Integer id;
    private String name;
    private List<OurSection> sectionList = new ArrayList<>();

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

    // endregion

    public void addSubSection(OurSection section) {
        sectionList.add(section);
    }

    @Override
    public String toString() {
        return name;
    }
}
