package utils;

import com.codepine.api.testrail.TestRail;
import com.codepine.api.testrail.model.Case;
import com.codepine.api.testrail.model.CaseField;
import com.codepine.api.testrail.model.Section;
import com.codepine.api.testrail.model.User;
import model.testrail.RailTestCase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static model.testrail.RailConstants.*;

/**
 * Created by dnizkopoklonniy on 01.09.2017.
 */
public class RailClientWorkFlow {
    private static List<Section> sectionList = new ArrayList<>();
    private static List<User> userList = new ArrayList<>();

//    @Autowired
    // TODO: rail client lol
    private TestRail railClient;

    public List<RailTestCase> getTestCasesBySectionId(int id) {
        List<CaseField> caseFieldList = railClient.caseFields().list().execute();
        List<Case> cases = railClient.cases().list(RAIL_PROJECT_ID, SUITE_ID, caseFieldList).execute();
        return cases.stream()
                .filter(aCase -> aCase.getSectionId() == id)
                .map(aCase -> new RailTestCase(aCase.getId(), getUserName(aCase.getCreatedBy()) , aCase.getTitle(), aCase.getCustomField(STEPS_SEPARATED_FIELD),aCase.getCustomField(PRECONDITION_FIELD) , aCase.getCustomField(KEYWORDS), getStoryNameBySectionId(aCase.getSectionId())))
                .collect(Collectors.toList());
    }

    private List<Section> getSections() {
        if (sectionList.isEmpty()) {
            sectionList = railClient.sections().list(RAIL_PROJECT_ID, SUITE_ID).execute();
            return sectionList;
        } else {
            return sectionList;
        }
    }

    private List<User> getUsers(){
        if(userList.isEmpty()){
            userList = railClient.users().list().execute();
            return userList;
        }else{
            return userList;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private String getStoryNameBySectionId(int sectionId) {
        return getSections()
                .stream()
                .filter(section -> section.getId() == sectionId)
                .map(Section::getName)
                .collect(Collectors.toList())
                .get(0);
    }

    private String getUserName(int userId){
        return getUsers()
                .stream()
                .filter(user -> user.getId() == userId)
                .map(User::getName)
                .collect(Collectors.toList())
                .get(0);
    }
}
