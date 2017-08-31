package model;

import com.codepine.api.testrail.model.Section;
import model.treerenderer.OurSection;

public class OurSectionAdapter {

    private OurSectionAdapter() {
    }

    public static OurSection get(Section section) {
        OurSection ourSection = new OurSection();
        ourSection.setId(section.getId());
        ourSection.setName(section.getName());
        return ourSection;
    }
}
