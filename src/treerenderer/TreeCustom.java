package treerenderer;

import com.codepine.api.testrail.model.Case;

import java.util.ArrayList;
import java.util.List;

public class TreeCustom {
    private List<TreeCustom> children = new ArrayList<>();
    private final TreeCustom parent;
    private int id;
    private String name;
    private List<Case> cases;

    public TreeCustom(TreeCustom parent) {
        this.parent = parent;
    }

    public void addChildren(TreeCustom node){
        children.add(node);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<TreeCustom> getChildren() {
        return children;
    }

    public TreeCustom getChildrenById(int id){
       return children.stream().filter(treeCustom -> treeCustom.getId() == id).findFirst().orElse(null);
    }

    public TreeCustom getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Case> getCases() {
        return cases;
    }

    public void setCases(List<Case> cases) {
        this.cases = cases;
    }

}
