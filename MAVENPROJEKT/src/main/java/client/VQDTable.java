package client;

import server.jacksonclasses.Attribute;
import server.jacksonclasses.Table;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class VQDTable extends JPanel {

    private final Table table;
    private final int attributeCount;
    private final ArrayList<Attribute> attributes;
    private int rows = 2;
    private JTable jTable;
    private final String[] attr;

    private String[][] attrTypes;

    public VQDTable(Table table) {

        setLayout(new BorderLayout());


        this.table = table;
        this.attributeCount = table.getStructure().getAttributes().size();
        this.attributes = table.zAttributumok();

        attr = new String[attributeCount];
        attrTypes = new String[rows][attributeCount];
        for (Attribute attribute : attributes) {
            attr[attributes.indexOf(attribute)] = attribute.get_attributeName();
            attrTypes[0][attributes.indexOf(attribute)] = attribute.get_attributeName() + " ( " + attribute.get_type() + " )";
            attrTypes[1][attributes.indexOf(attribute)] = "";
        }

        jTable = new JTable(attrTypes, attrTypes);
        jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        jTable.setPreferredScrollableViewportSize(new Dimension(700, this.getHeight()));


//        for (int i = 1; i < attributeCount; i++) {
//            jTable.getColumnModel().getColumn(i).setResizable(true);
//            jTable.getColumnModel().getColumn(i).setPreferredWidth(120);
//        }

        this.add(jTable, BorderLayout.CENTER);
        setVisible(true);
    }

    public JTable getjTable() {
        return jTable;
    }


    public void addRow() {

        this.remove(jTable);
        rows++;
        String[][] tmp = new String[rows][attributeCount];
        for (int i = 0; i < rows - 1; i++) {
            System.arraycopy(attrTypes[i], 0, tmp[i], 0, attributeCount);
        }
        for (int i = 0; i < attributeCount; i++) {
            tmp[rows - 1][i] = "";
        }
        attrTypes = tmp;
        jTable = new JTable(tmp, attr);


        for (int i = 1; i < attributeCount; i++) {
            jTable.getColumnModel().getColumn(i).setPreferredWidth(120);
        }
        this.add(jTable, BorderLayout.CENTER);
        validate();
        repaint();
        System.out.println("row added");

    }

    public JTextArea generateQuery(String db) {
        JTextArea query = new JTextArea();
        query.setText("USE " + db + "\n");
        for (int i = 1; i < rows; i++) {
            query.append("INSERT INTO " + table.get_tableName() + " VALUES (");
            for (int j = 0; j < attributeCount; j++) {
                if (j == attributeCount - 1) {
                    query.append(attrTypes[i][j]);
                } else {
                    query.append(attrTypes[i][j] + ",");
                }
            }
            query.append(" ) \n");

        }
        System.out.println(query.getText());

        return query;
    }

    public JTextArea generateQueryDelete(String db) {

        boolean first = true;
        JTextArea query = new JTextArea();
        query.setText("USE " + db + "\n");
        for (int i = 1; i < rows; i++) {
            query.append("DELETE FROM " + table.get_tableName() + " WHERE ");
            for (int j = 0; j < attributeCount; j++) {
                if (j == attributeCount - 1) {
                    if (!attrTypes[i][j].equals(""))
                        query.append(attributes.get(j).get_attributeName() + " = " + attrTypes[i][j]);
                } else {
                    if (!attrTypes[i][j].equals("")) {
                        if (!first) {
                            query.append(" AND ");
                        } else {
                            first = false;
                        }
                        query.append(attributes.get(j).get_attributeName() + " = " + attrTypes[i][j]);
                    }
                }
            }
            query.append(" \n");

        }
        System.out.println(query.getText());

        return query;
    }
}
