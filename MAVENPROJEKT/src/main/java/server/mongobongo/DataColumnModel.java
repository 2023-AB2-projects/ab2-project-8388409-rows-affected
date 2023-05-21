package server.mongobongo;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class DataColumnModel extends JPanel implements Serializable {
    protected final String name;
    protected final String type;
    protected Boolean primaryKey;
    protected Boolean isNull;


    protected Boolean isForeignKey;
    protected ArrayList<String> values;

    protected String special;


    public DataColumnModel(DataColumnModel column) {
        setLayout(new GridLayout(100, 1));
        this.name = column.getName();
        this.type = column.getType();
        this.primaryKey = column.getPrimaryKey();
        this.special = column.getSpecial();
        this.isForeignKey = column.getForeignKey();
        this.isNull = column.getIsNull();
        this.values = new ArrayList();
//        values.addAll(column.getValues());

        setVisible(true);
    }

    public DataColumnModel(String name, String type) {
        setLayout(new GridLayout(100, 1));
        this.name = name;
        this.type = type;
        this.primaryKey = false;
        this.values = new ArrayList();
        this.special = "";

    }

    private Boolean getIsNull() {
        return isNull;
    }


    public int getColumnSize() {
        return this.values.size();
    }

    public void addValue(String value) {
        this.values.add(value);

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DataColumnModel dbcl = new DataColumnModel("id", "int");
        dbcl.addValue("1");
        dbcl.addValue("2");
        dbcl.addValue("3");
        frame.add(dbcl);
        frame.add(new DataColumnModel("kor", "int"));
        frame.add(new DataColumnModel("magassag", "float"));
        frame.add(new DataColumnModel("suly", "double"));


        frame.setVisible(true);

    }

    public ArrayList<String> getValues() {
        return values;
    }


    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    String getType() {
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

    public int getLength() {
        return this.values.size();
    }

    public String getColumnName() {
        return this.name;
    }


    public String getDataType() {
        return this.type;
    }

    public String getRow(int index) {
        return this.values.get(index).toString();
    }
}
