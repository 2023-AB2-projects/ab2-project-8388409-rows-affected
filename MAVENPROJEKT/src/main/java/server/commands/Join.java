package server.commands;

import server.Parser;
import server.mongobongo.DataTable;
import server.mongobongo.DataTableGUI;

import javax.swing.*;
import javax.swing.text.Document;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Join implements Serializable {

    private HashMap<String, ArrayList<String>> joinConditionMap;
    private HashMap<String, DataTable> connectionMap;

    private DataTable resultTable;

    public Join(ArrayList<DataTable> tables, String joinCondition, Parser parser) {

        for (DataTable table : tables) {
           System.out.println(table.getTableName());
            System.out.println("join to");
       }
        connectionMap = new HashMap<>();
        for (DataTable table : tables) {
            connectionMap.put(table.getTableName(), table);
        }

        String[] joinConditionArray = joinCondition.split("INNER JOIN");

        getJoinCondition(joinConditionArray[0]);
    }

    public void getJoinCondition(String joinCondition) {
        joinCondition = joinCondition.trim();
        System.out.println("joinCondition"+joinCondition);
        String[] joinConditionArray = joinCondition.split("ON");
        String[] keys = joinConditionArray[1].split("=");
        String[] first = keys[0].split("\\.");
        String[] second = keys[1].split("\\.");
        String firstTable = first[0].trim();
        String secondTable = second[0].trim();
        String firstColumn = first[1].trim();
        String secondColumn = second[1].trim();

        try {
          resultTable = indexNextedLoop(connectionMap.get(firstTable), connectionMap.get(secondTable), firstColumn, secondColumn);
        } catch (Exception e) {
            System.out.println("Error in Join.getJoinCondition");
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Join");
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        assert resultTable != null;
        frame.add(new DataTableGUI(resultTable));
        frame.add(new DataTableGUI(connectionMap.get(firstTable)));
        frame.add(new DataTableGUI(connectionMap.get(secondTable)));
        frame.setVisible(true);

    }

    private DataTable indexNextedLoop(DataTable dataTable, DataTable dataTable1, String firstColumn, String secondColumn) {

        DataTable result = new DataTable();
        ArrayList<String> columnNames1 =  dataTable.getColumnsName();
        ArrayList<String> columnTypes1 = dataTable.getColumnsType();
        ArrayList<String> columnNames =  dataTable1.getColumnsName();
        ArrayList<String> columnTypes = dataTable1.getColumnsType();

        columnNames.addAll(columnNames1);
        columnTypes.addAll(columnTypes1);
        for (int i = 0; i < columnNames.size(); i++) {
            result.addColumn(columnNames.get(i), columnTypes.get(i));
        }

        result.setTableName(dataTable.getTableName()+"_"+dataTable1.getTableName());

        ArrayList<ArrayList<String>> rows = new ArrayList<>();
        ArrayList<String> row = new ArrayList<>();
        for (int i = 0; i < dataTable.getColumnSize(); i++) {
            for (int j = 0; j < dataTable1.getColumnSize(); j++) {
                if (dataTable.getColumn(firstColumn).getValues().get(i).equals(dataTable1.getColumn(secondColumn).getValues().get(j))) {
                    System.out.println("i: "+i+" j: "+j);
                    row.addAll(dataTable.getRow(i));
                    row.addAll(dataTable1.getRow(j));
                    rows.add(row);
                    row = new ArrayList<>();
                }
            }
        }

        for (ArrayList<String> row1 : rows) {
            result.addRow(row1);
        }

        return result;
    }


    public static void main(String[] args) {
        DataTable table1 = new DataTable("ab","Termekek");
        DataTable table2 = new DataTable("ab","Gyartok");

    }

    public DataTable getResultTable() {
        return new DataTable(resultTable);
    }
}
