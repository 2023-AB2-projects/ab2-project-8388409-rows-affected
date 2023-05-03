package server.mongobongo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DataColumn extends JPanel {
    private final String name;
    private final String type;
    private Boolean primaryKey;
    private Boolean isNull;

    private Boolean isForeignKey;
    private final ArrayList values;
    private final ArrayList<ResizeLabel> valueLabels;
    private String special;


    public void setSpecial(String special) {
        this.special = special;
    }

    public String getSpecial() {
        return special;
    }

    public DataColumn(String name, String type) {
        setLayout(new GridLayout(100, 1));
        this.name = name;
        this.type = type;
        this.primaryKey = false;
        this.values = new ArrayList();
        this.valueLabels = new ArrayList();

        ResizeLabel nameL = getLabelTop(name);
        ResizeLabel typeL = getLabelTop(type);
        this.valueLabels.add(nameL);
        this.valueLabels.add(typeL);
        add(nameL);
        add(typeL);

    }

    private void findRightSize() {
        for (ResizeLabel label : this.valueLabels) {
//            label.setLabelWidth(100);
        }
    }

    private ResizeLabel getLabel(String text) {
        ResizeLabel label = new ResizeLabel(text, "", this.valueLabels);
        return label;
    }

    private ResizeLabel getLabelTop(String text) {
        ResizeLabel label = new ResizeLabel(text, "top", this.valueLabels);
        return label;
    }


    public void isPrimaryKey() {
        this.primaryKey = true;
    }

    public void notPrimaryKey() {
        this.primaryKey = false;
    }

    public void isNull() {
        this.isNull = true;
    }

    public void notNull() {
        this.isNull = false;
    }

    public void isForeignKey() {
        this.isForeignKey = true;
    }

    public void notForeignKey() {
        this.isForeignKey = false;
    }

    public void addValue(String value) {
        System.out.println("value: " + value);
        System.out.println("type: " + this.type);
        ResizeLabel resLabel = getLabel(value);
        this.valueLabels.add(resLabel);
//        add event listener to label
        findRightSize();


        if (this.type.equalsIgnoreCase("int")) {
            System.out.println(value);
            this.values.add(Integer.parseInt(value));
            add(resLabel);
            return;
        }
        if (this.type.equalsIgnoreCase("float")) {
            this.values.add(Float.parseFloat(value));
            add(resLabel);
            return;
        }

        if (this.type.equalsIgnoreCase("varchar")) {
            this.values.add(value.charAt(0));
            add(resLabel);
            return;
        }

        if (this.type.equalsIgnoreCase("double")) {
            this.values.add(Double.parseDouble(value));
            add(resLabel);
            return;
        }

        add(getLabel(value));

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DataColumn dbcl = new DataColumn("id", "int");
        dbcl.addValue("1");
        dbcl.addValue("2");
        dbcl.addValue("3");
        frame.add(dbcl);
        frame.add(new DataColumn("kor", "int"));
        frame.add(new DataColumn("magassag", "float"));
        frame.add(new DataColumn("suly", "double"));


        frame.setVisible(true);

    }
}
