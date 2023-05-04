package client;

import server.mongobongo.DataColumn;
import server.mongobongo.DataTable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.max;

public class VisualQueryDesignerTableEdit extends DataTable {

    private ArrayList<JButton> buttons;

    public VisualQueryDesignerTableEdit(DataTable dataTable) {
        super(dataTable);
        int size = 0;
        setLayout(new FlowLayout());
        for (DataColumn column : getDataColums()) {
            size = max(column.getLength(), size);
        }

        DataColumn delete = new DataColumn("", "");
        delete.addButtons("delete", size);
        this.removeAll();
//        delete.setPreferredSize(new Dimension(100, 100));
        delete.addButtons("insert", 1);
        add(delete);

        for (JButton button : delete.getButtons()) {
            button.addActionListener(e -> {
                int index = 0;
                for (int i = 0; i < delete.getButtons().size(); i++) {
                    if (delete.getButtons().get(i).equals(button)) {
                        index = i;
                    }
                }
                if (button.getText().equals("delete")) {
                    System.out.println("delete");
                    System.out.println(index);
                    System.out.println();

//                    delete();
                } else if (button.getText().equals("insert")) {
                    System.out.println("insert");
//                    insert();
                }
            });
        }

        for (DataColumn column : getDataColums()) {
            add(column);
            column.addInputField(1);

        }

        setVisible(true);

    }


}
