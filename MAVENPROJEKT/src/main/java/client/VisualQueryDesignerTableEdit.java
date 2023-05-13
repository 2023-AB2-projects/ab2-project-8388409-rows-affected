package client;

import server.mongobongo.DataColumn;
import server.mongobongo.DataTable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.max;

public class VisualQueryDesignerTableEdit extends DataTable {

    private ArrayList<JButton> buttons;
    private final KliensNew kliens;

    public VisualQueryDesignerTableEdit(DataTable dataTable, KliensNew kliens) {

        super(dataTable);
        this.kliens = kliens;
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

                    System.out.println(getRow(index));
                    ArrayList<String> args = getRow(index);
                    String[] args2 = new String[args.size()];
                    for (int i = 0; i < args.size(); i++) {
                        args2[i] = args.get(i);
                    }
                    delete(args2);

//                    delete();
                } else if (button.getText().equals("insert")) {
                    System.out.println("insert");
                    System.out.println(index);
                    ArrayList<String> args = getRow(index);
                    String[] args2 = new String[args.size()];
                    for (int i = 0; i < args.size(); i++) {
                        args2[i] = args.get(i);
                    }
                    insers(args2);

                }
            });
        }

        for (DataColumn column : getDataColums()) {
            add(column);
            column.addInputField(1);

        }

        setVisible(true);

    }

    private void delete(String[] args) {
        String sql = "USE " + getDatabaseName() + " \n ";
        sql += "DELETE FROM " + getTableName() + " WHERE ";

        ArrayList<DataColumn> columns = getDataColums();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getIsPrimaryKey()) {
                sql += getColumnName(i) + " = " + args[i];
                break;
            }
        }
        System.out.println(sql);
        kliens.setTextArea(sql);
        kliens.send();
    }

    private void insers(String[] args) {
        String sql = "USE " + getDatabaseName() + " \n ";
        sql += "INSERT INTO " + getTableName() + " VALUES (";
        for (int i = 0; i < args.length; i++) {
            sql += args[i];
            if (i != args.length - 1) {
                sql += ", ";
            }
        }
        sql += ")";
        System.out.println(sql);
        System.out.println(sql);
        kliens.setTextArea(sql);
        kliens.send();

    }

    private String getColumnName(int i) {
        return getDataColums().get(i).getColumnName();
    }


}