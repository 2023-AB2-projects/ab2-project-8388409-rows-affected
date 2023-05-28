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
    private final HashMap<String, DataTable> connectionMap;
    private final HashMap<String, DataTable> connectionMap2;


    private final ArrayList<String> joinKeys;
    private DataTable resultTable;
    private final String elvalasztoKarakter = "-";

    private Parser parser;

    public Join(ArrayList<DataTable> tables, String joinCondition, ArrayList<String> joinKeys, Parser parser) {
        this.joinKeys = joinKeys;
        this.parser = parser;
        connectionMap2 = new HashMap<>();
        for (DataTable table : tables) {
            System.out.println(table.getTableName());
            System.out.println("join to");
        }
        connectionMap = new HashMap<>();
        for (DataTable table : tables) {
            connectionMap.put(table.getTableName(), table);
        }

        ArrayList<String> tableNames = new ArrayList<>();
        for (DataTable table : tables) {
            tableNames.add(table.getTableName());
        }
        String joinConditionString = felosztJoin(tableNames);
        System.out.println("joinConditionString: "+joinConditionString);
        String[] joinConditionArray = joinCondition.split("INNER JOIN");

        getJoinCondition(joinConditionArray[0]);
    }

    public String felosztJoin(ArrayList<String> tables){
        if (tables.size() == 0)
            return null;

        if (tables.size() == 1 )
            return tables.get(0);

        if (tables.size() == 2 )
            return tables.get(0)+elvalasztoKarakter+tables.get(1);
        int pivot = tables.size() / 2;
        ArrayList<String> left = new ArrayList<>();
        ArrayList<String> right = new ArrayList<>();
        for (int i = 0; i < pivot; i++) {
            left.add(tables.get(i));
        }
        for (int i = pivot; i < tables.size(); i++) {
            right.add(tables.get(i));
        }


        if (felosztJoin(left) == null) {
            System.out.println(right);
            return felosztJoin(right);
        }
        if (felosztJoin(right) == null) {
            System.out.println(left);
            return felosztJoin(left);
        }

        String result = felosztJoin(left)+elvalasztoKarakter+felosztJoin(right);
        System.out.println(result);
        return result;
    }

    public void getJoinCondition(String joinCondition) {

//        try {
            joinCondition = joinCondition.trim();
            System.out.println("joinCondition" + joinCondition);
            String[] joinConditionArray = joinCondition.split("ON");
            String[] keys = joinConditionArray[1].split("=");
            String[] first = keys[0].split("\\.");
            String[] second = keys[1].split("\\.");
            String firstTable = first[0].trim();
            String secondTable = second[0].trim();
            String firstColumn = first[1].trim();
            String secondColumn = second[1].trim();
            resultTable = indexNextedLoop(connectionMap.get(firstTable), connectionMap.get(secondTable), firstColumn, secondColumn);
//        } catch (Exception e) {
//
//            System.out.println("Error in Join Condition");
//            System.out.println(e.getMessage());
//            resultTable = new DataTable();
//            e.printStackTrace();
//        }

//        join remaning tables to resultTable


//        JFrame frame = new JFrame("Join");
//        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(400, 400);
//        assert resultTable != null;
//        frame.add(new DataTableGUI(resultTable));
//        frame.add(new DataTableGUI(connectionMap.get(firstTable)));
//        frame.add(new DataTableGUI(connectionMap.get(secondTable)));
//        frame.setVisible(true);

    }

    private DataTable indexNextedLoop(DataTable dataTable, DataTable dataTable1, String firstColumn, String secondColumn) {

        DataTable result = new DataTable();

//        try {
            ArrayList<String> columnNames1 = dataTable.getColumnsName();
            ArrayList<String> columnTypes1 = dataTable.getColumnsType();
            ArrayList<String> columnNames = dataTable1.getColumnsName();
            ArrayList<String> columnTypes = dataTable1.getColumnsType();

            columnNames.addAll(columnNames1);
            columnTypes.addAll(columnTypes1);
            for (int i = 0; i < columnNames.size(); i++) {
                result.addColumn(columnNames.get(i), columnTypes.get(i));
            }

            result.setTableName(dataTable.getTableName() + elvalasztoKarakter + dataTable1.getTableName());

            ArrayList<ArrayList<String>> rows = new ArrayList<>();
            ArrayList<String> row = new ArrayList<>();

            if (dataTable.hasColumn(firstColumn) && dataTable1.hasColumn(secondColumn)) {
                for (int j = 0; j < dataTable1.getColumnSize(); j++) {
                    for (int i = 0; i < dataTable.getColumnSize(); i++) {


                        if (dataTable.getColumn(firstColumn).getValues().get(i).equals(dataTable1.getColumn(secondColumn).getValues().get(j))) {
                            System.out.println("i: " + i + " j: " + j);
                            row.addAll(dataTable.getRow(i));
                            row.addAll(dataTable1.getRow(j));
                            rows.add(row);
                            row = new ArrayList<>();
                        }
                    }
                }
            }


            for (ArrayList<String> row1 : rows) {
                result.addRow(row1);
            }
            for (String key : joinKeys) {
                result.removeColumn(key);
            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            parser.setOtherError("Error in Join");
//            parser.setParserError(true);
//        }
        return result;
    }


    public static void main(String[] args) {
        DataTable table1 = new DataTable("ab", "Termekek");
        DataTable table2 = new DataTable("ab", "Gyartok");

    }

    public DataTable getResultTable() {
        return new DataTable(resultTable);
    }
}
