package model;

import com.codepine.api.testrail.model.Section;
import model.treerenderer.OurSection;

import java.util.List;

public class OurSectionInflator {

    private OurSectionInflator() {
    }

    /**
     * Inflates our section as tree structure.
     *
     * @param sectionList List of sections. It is data from rational api.
     * @param parentId    Parent id section.
     * @param rootSection Our section. Root.
     */
    public static void inflateOurSection(List<Section> sectionList, Integer parentId, OurSection rootSection) {
        for (Section section : sectionList) {

            if ((parentId == null && section.getParentId() == parentId) ||
                    (parentId != null && section.getParentId() != null && section.getParentId().equals(parentId))) {
                OurSection ourSection = OurSectionAdapter.get(section);

                //TODO ourSection.addCases(client.getCasesById())
                rootSection.addSubSection(ourSection);
                inflateOurSection(sectionList, ourSection.getId(), ourSection);
            }
        }
    }
}
