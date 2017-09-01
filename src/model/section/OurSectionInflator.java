package model.section;

import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.Section;
import model.section.OurSection;
import model.section.OurSectionAdapter;
import utils.RailDataStorage;

import java.util.List;
import java.util.stream.Collectors;

public class OurSectionInflator {

    private OurSectionInflator() {
    }

    /**
     * Inflates our section as tree structure.
     *
     * @param dataStorage Class stores section list and case list.
     * @param parentId    Parent id section.
     * @param rootSection Our section. Root.
     */
    public static void inflateOurSection(RailDataStorage dataStorage, Integer parentId, OurSection rootSection) {
        List<Section> sectionList = dataStorage.getSections();
        List<Case> caseList = dataStorage.getCases();

        for (Section section : sectionList) {

            if ((parentId == null && section.getParentId() == null) ||
                    (parentId != null && section.getParentId() != null && section.getParentId().equals(parentId))) {
                OurSection ourSection = OurSectionAdapter.get(section);
                ourSection.setCases(caseList.stream().filter(aCase -> aCase.getSectionId() == section.getId()).collect(Collectors.toList()));

                rootSection.addSubSection(ourSection);
                inflateOurSection(dataStorage, ourSection.getId(), ourSection);
            }
        }
    }
}
