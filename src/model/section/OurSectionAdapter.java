package model.section;

import com.codepine.api.testrail.model.Section;

/**
 * Section adapter is class which transforms Section of TestRail to  OurSection.
 */
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
