package model.testrail;

import com.codepine.api.testrail.TestRail;

public class RailProject {
    private TestRail name = new RailConnection().login("","","");

    public TestRail.Projects.Get getProjectById(int id) {
        return this.name.projects().get(id);
    }
}
