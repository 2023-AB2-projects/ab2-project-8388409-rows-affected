package client;

import javax.swing.*;
import java.awt.*;

public class VisualQueryDesigner extends JPanel {

    private final JTable table;

    public VisualQueryDesigner(KliensNew kliens) {

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
