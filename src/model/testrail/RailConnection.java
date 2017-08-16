package model.testrail;

import com.codepine.api.testrail.TestRail;
import com.intellij.openapi.components.ServiceManager;
import model.TMConnectable;


public class RailConnection {
    private String user = "***";
    private String upas = "***";
    private String url = "***";

    public TestRail login(){
        return TestRail.builder(url,user,upas).build();
    }


}
