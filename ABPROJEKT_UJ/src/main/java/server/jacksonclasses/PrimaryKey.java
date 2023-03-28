package server.jacksonclasses;

public class PrimaryKey {
    private String pkAttribute;

    public PrimaryKey(String pkAttribute) {
        this.pkAttribute = pkAttribute;
    }

    public String getPkAttribute() {
        return pkAttribute;
    }

    public void setPkAttribute(String pkAttribute) {
        this.pkAttribute = pkAttribute;
    }
}
