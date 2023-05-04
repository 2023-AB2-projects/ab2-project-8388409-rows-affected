package client;

import server.jacksonclasses.Table;
import server.mongobongo.DataTable;

import javax.swing.*;
import java.awt.*;

public class VisualQueryDesigner extends JPanel {

//    private final JTable table;

    private Table dbTable;
    private final JTable table;

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

    public void createTable(DataTable table) {

//        add horizontal and vertical scroll bar
        VisualQueryDesignerTableEdit vqdt = new VisualQueryDesignerTableEdit(table);
        JScrollPane jps = new JScrollPane(vqdt, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setLayout(new BorderLayout());
        add(jps, BorderLayout.CENTER);
        add(jps);
        table.setBounds(0, 0, getWidth(), this.getHeight());
        this.revalidate();
//
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
