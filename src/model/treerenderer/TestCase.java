package model.treerenderer;

public class TestCase {
    private int id;
    private String name;

    public TestCase(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
