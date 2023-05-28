package server.mongobongo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DataTableGUI extends JPanel {

    private DataTable tableModel;
    private ArrayList<DataColumnGUI> dataColumnGUI;

    public DataTableGUI(DataTable tableModel) {
        this.tableModel = tableModel;
        this.dataColumnGUI = new ArrayList<>();
        setLayout(new FlowLayout());
        for (DataColumnModel column : tableModel.getColumns()) {
            DataColumnGUI columnGUI = new DataColumnGUI(column);
            dataColumnGUI.add(columnGUI);
            add(columnGUI);
        }

        System.out.println("Width: " + getRowSize() + " Height: " + getColumn());
        setVisible(true);
    }
    public int getRowSize() {
        return tableModel.getColumns().size();
    }

    public int getColumn() {
        try {
            return tableModel.getColumns().get(0).getValues().size();
        } catch (Exception e) {
            return 0;
        }
    }

    public JScrollPane getAsScrollPane() {
        JScrollPane scrollPane = new JScrollPane(this, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }

    public String getDatabaseName() {
        return tableModel.getDatabaseName();
    }

    public String getTableName() {
        return tableModel.getTableName();
    }

    public ArrayList<String> getRow(int index) {
        ArrayList<String> row = new ArrayList<>();
        for (DataColumnGUI column : getDataColums()) {

            row.add(column.getRow(index+2));
        }
        return row;
    }


    public ArrayList<DataColumnModel> getColumnsModel() {
        ArrayList<DataColumnModel> columns = new ArrayList<>();
        for (DataColumnGUI column : dataColumnGUI) {
            columns.add(column.getDataColumnModel());
        }
        return columns;
    }

    public ArrayList<DataColumnGUI> getDataColums() {
        return dataColumnGUI;
    }



    public static void main(String[] args) {
        DataTable dt = new DataTable("ab", "GPU");
        DataTableGUI dtg = new DataTableGUI(dt);
        JFrame frame = new JFrame();
        frame.add(dtg.getAsScrollPane());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

    }



}