package server.jacksonclasses;

import java.util.List;

public class Structure implements java.io.Serializable {
    private List<Attribute> Attributes;

    public Structure() {
    }

    public Structure(List<Attribute> attributes) {
        Attributes = attributes;
    }

    public List<Attribute> getAttributes() {
        return Attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        Attributes = attributes;
    }
}
