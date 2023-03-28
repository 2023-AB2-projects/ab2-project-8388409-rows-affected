package server.jacksonclasses;

public class Structure {
    private Attribute[] Attributes;

    public Structure(Attribute[] attributes) {
        Attributes = attributes;
    }

    public Attribute[] getAttributes() {
        return Attributes;
    }

    public void setAttributes(Attribute[] attributes) {
        Attributes = attributes;
    }
}
