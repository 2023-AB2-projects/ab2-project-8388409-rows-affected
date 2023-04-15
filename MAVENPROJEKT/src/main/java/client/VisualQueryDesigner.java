package client;

import server.jacksonclasses.Table;

import javax.swing.*;
import java.awt.*;

public class VisualQueryDesigner extends JPanel {

//    private final JTable table;

    private Table dbTable;
    private JTable table;

    private VQDTable vqdTable;

    public VisualQueryDesigner(KliensNew kliens) {

        setBackground(new Color(233, 255, 255));

        table = new JTable();
        table.setBounds(0, 0, 700, this.getHeight());
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setPreferredScrollableViewportSize(new Dimension(700, this.getHeight()));
        this.add(table);
        this.setVisible(true);
    }

    public void createTable(Table table) {
        this.dbTable = table;
        vqdTable = new VQDTable(table);
        vqdTable.setBounds(0, 0, getWidth(), getHeight());
        this.add(vqdTable);
        this.table = vqdTable.getjTable();
        validate();
        this.repaint();
    }

    public JTextArea generateQuery(String db) {
        return vqdTable.generateQuery(db);
    }

    public JTextArea generateQueryDelete(String db) {
        return vqdTable.generateQueryDelete(db);
    }

    public void addRow() {
        vqdTable.addRow();
    }

}
