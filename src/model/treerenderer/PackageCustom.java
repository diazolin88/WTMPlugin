package model.treerenderer;

import com.codepine.api.testrail.model.Section;

import java.util.List;

public class PackageCustom {
    private int id;
    private Section section;
    private String name;
    private PackageCustom parent;
    private List<PackageCustom> children;

    public PackageCustom(Section section) {
        this.section = section;
        this.id = section.getId();
        this.name = section.getName();
    }

    public void setParent(PackageCustom parent){
        this.parent = parent;
    }

    public int getId() {
        return this.id;
    }

    public Section getSection() {
        return this.section;
    }

    public String getName() {
        return this.name;
    }

    public PackageCustom getParent() {
        return this.parent;
    }

    public List<PackageCustom> getChildren() {
        return this.children;
    }

    @SuppressWarnings("CollectionAddedToSelf")
    public void setChildren(List<PackageCustom> children){
        children.addAll(children);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PackageCustom that = (PackageCustom) o;

        if (id != that.id) return false;
        if (section != null ? !section.equals(that.section) : that.section != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        return children != null ? children.equals(that.children) : that.children == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (section != null ? section.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }
}
