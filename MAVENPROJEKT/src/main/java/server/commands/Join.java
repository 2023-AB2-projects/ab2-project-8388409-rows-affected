package server.commands;

import server.Parser;
import server.mongobongo.DataTable;

import javax.swing.text.Document;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Join implements Serializable {

    private HashMap<String, ArrayList<String>> joinConditionMap;
    private HashMap<String, DataTable> connectionMap;


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
//        for (String s : joinConditionArray) {
//            System.out.println(s);
//            getJoinCondition(s);
//        }
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
            sortByColumn(connectionMap.get(firstTable), firstColumn);
            sortByColumn(connectionMap.get(secondTable), secondColumn);
        } catch (Exception e) {
            System.out.println("Error in Join.getJoinCondition");
            e.printStackTrace();
        }
    }

    public void sortByColumn(DataTable table, String column) {
        System.out.println("sortByColumn");
        System.out.println("--> "+table.getTableName());
        System.out.println("--> "+column);



    }

    public void SortMergeJoin(ArrayList<DataTable> tables, String joinCondition) {



    }

    public static void main(String[] args) {
    }

    public DataTable getResultTable() {
        return null;
    }
}
