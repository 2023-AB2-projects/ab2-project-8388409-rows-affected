package client;

import server.jacksonclasses.Database;
import server.jacksonclasses.Table;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class VisualQueryDesigner extends JPanel {

    private final JTable table;

    private final ArrayList<Database> databases;
    private final ArrayList<Table> tables;

    public VisualQueryDesigner(KliensNew kliens) {

        databases = kliens.getDatabases();
        tables = kliens.getTables();

        setBackground(new Color(233, 255, 255));
        String[][] data = {
                {"Kundan Kumar Jha", "4031", "CSE"},
                {"Anand Jha", "6014", "IT"}
        };

        // Column Names
        String[] columnNames = {"Name", "Roll Number", "Department"};

        table = new JTable(data, columnNames);
        table.setBounds(0, 0, getWidth(), getHeight());
        this.add(table);
        this.setVisible(true);
    }


}
