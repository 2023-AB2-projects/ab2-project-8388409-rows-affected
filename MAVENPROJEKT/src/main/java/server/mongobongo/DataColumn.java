package server.mongobongo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DataColumn extends JPanel {
    protected final String name;
    protected final String type;
    protected Boolean primaryKey;
    protected Boolean isNull;


    protected Boolean isForeignKey;
    protected ArrayList values;
    protected ArrayList<ResizeLabel> valueLabels;
    protected String special;


    public DataColumn(DataColumn column) {
        setLayout(new GridLayout(100, 1));
        this.name = column.getName();
        this.type = column.getType();
        this.primaryKey = column.getPrimaryKey();
        this.valueLabels = new ArrayList<>();
        this.special = column.getSpecial();
        this.isForeignKey = column.getForeignKey();
        this.isNull = column.getIsNull();
        this.values = new ArrayList();
        values.addAll(column.getValues());
        this.valueLabels.addAll(Arrays.asList(column.valueLabels()));
        for (ResizeLabel label : this.valueLabels) {
            add(label);
        }
        setVisible(true);
    }

    public DataColumn(String name, String type) {
        setLayout(new GridLayout(100, 1));
        this.name = name;
        this.type = type;
        this.primaryKey = false;
        this.values = new ArrayList();
        this.valueLabels = new ArrayList();
        this.special = "";
        ResizeLabel nameL = getLabelTop(name);
        ResizeLabel typeL = getLabelTop(type);
        this.valueLabels.add(nameL);
        this.valueLabels.add(typeL);
        add(nameL);
        add(typeL);
        setVisible(true);
    }


    private ResizeLabel[] valueLabels() {
        ResizeLabel[] ret = new ResizeLabel[this.valueLabels.size()];
        for (int i = 0; i < this.valueLabels.size(); i++) {
            ret[i] = this.valueLabels.get(i);
        }
        return ret;
    }

    private Boolean getIsNull() {
        return isNull;
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

    public void addButtons(String buttonName, int size) {
        for (int i = 0; i < size; i++) {
            ResizeLabel label = getLabel(buttonName);
            label.addButton(buttonName);
            this.valueLabels.add(label);
            add(label);
        }
    }

    public void addValue(String value) {
        System.out.println("value: " + value);
        System.out.println("type: " + this.type);
        ResizeLabel resLabel = getLabel(value);
        this.valueLabels.add(resLabel);
        add(resLabel);
//        add event listener to label
        findRightSize();
        if (getSpecial().equalsIgnoreCase("delete")) {
            resLabel.addButton("delete");
            return;
        }

        if (this.type.equalsIgnoreCase("int")) {

            if (value.equalsIgnoreCase("null")) {
                this.values.add(0);
                return;
            }

            System.out.println(value);
            this.values.add(Integer.parseInt(value));
            return;
        }
        if (this.type.equalsIgnoreCase("float")) {
            if (value.equalsIgnoreCase("null")) {
                this.values.add(0);
                return;
            }

            this.values.add(Float.parseFloat(value));
            return;
        }

        if (this.type.equalsIgnoreCase("varchar")) {
            this.values.add(value.charAt(0));
            return;
        }

        if (this.type.equalsIgnoreCase("double")) {
            if (value.equalsIgnoreCase("null")) {
                this.values.add(0);
            }

        }


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

    public ArrayList getValues() {
        return values;
    }

    public ArrayList<ResizeLabel> getLabels() {
        return valueLabels;
    }

    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    private String getType() {
        return type;
    }


    public void setSpecial(String special) {
        this.special = special;
    }

    public String getSpecial() {
        return special;
    }


    public void isPrimaryKey() {
        this.primaryKey = true;
    }

    public boolean getIsPrimaryKey() {
        return this.primaryKey;
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


    @Override
    public String getName() {
        return name;
    }

    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Boolean getNull() {
        return isNull;
    }

    public void setNull(Boolean aNull) {
        isNull = aNull;
    }

    public Boolean getForeignKey() {
        return isForeignKey;
    }

    public void setForeignKey(Boolean foreignKey) {
        isForeignKey = foreignKey;
    }

    public ArrayList<ResizeLabel> getValueLabels() {
        return valueLabels;
    }

    public int getLength() {
        return this.values.size();
    }

    public void addInputField(int size) {
        for (int i = 0; i < size; i++) {
            ResizeLabel label = getLabel("");
            label.addInput();
            this.valueLabels.add(label);
            add(label);
        }
    }

    public ArrayList<JButton> getButtons() {
        ArrayList<JButton> ret = new ArrayList<>();
        for (ResizeLabel label : this.valueLabels) {
            JButton button = label.getButton();
            if (button != null) {
                ret.add(button);
            }
        }
        return ret;
    }

    public String getRow(int index) {
        index += 2;
        ArrayList<String> ret = new ArrayList<>();
        for (ResizeLabel label : this.valueLabels) {

            ret.add(label.getTextFromLabel());
        }
        return ret.get(index);
    }

    public String getColumnName() {
        return this.name;
    }


    public String getDataType() {
        return this.type;
    }
}
