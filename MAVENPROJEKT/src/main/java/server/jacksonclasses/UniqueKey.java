package server.jacksonclasses;

public class UniqueKey {
    private String UniqueAttribute;

    public UniqueKey() {
    }

    public UniqueKey(String uniqueAttribute) {
        UniqueAttribute = uniqueAttribute;
    }

    public String getUniqueAttribute() {
        return UniqueAttribute;
    }

    public void setUniqueAttribute(String uniqueAttribute) {
        UniqueAttribute = uniqueAttribute;
    }
}