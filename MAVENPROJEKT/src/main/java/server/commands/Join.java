package server.commands;

import server.Parser;
import server.mongobongo.DataTable;

import javax.swing.text.Document;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public static DataTable hashJoin(DataTable table1, DataTable table2, String joinKey1, String joinKey2) {
        // Step 1: Partition the rows of both tables into buckets based on the join keys
        Map<Object, ArrayList<Map<String, Object>>> buckets1 = partition(table1.getRows(), joinKey1);
        Map<Object, ArrayList<Map<String, Object>>> buckets2 = partition(table2.getRows(), joinKey2);

        // Step 2: Build a hash table for one of the tables
        Map<Object, ArrayList<Map<String, Object>>> hashTable;
        ArrayList<Map<String, Object>> probeList;
        if (buckets1.size() < buckets2.size()) {
            hashTable = buildHashTable(buckets1, joinKey1);
            probeList = table2.getRows();
        } else {
            hashTable = buildHashTable(buckets2, joinKey2);
            probeList = table1.getRows();
        }

        // Step 3: Probe the hash table for each row of the other table to find matching rows
        ArrayList<Map<String, Object>> resultRows = new ArrayList<>();
        for (Map<String, Object> row : probeList) {
            Object key = row.containsKey(joinKey2) ? row.get(joinKey2) : row.get(joinKey1);
            if (hashTable.containsKey(key)) {
                for (Map<String, Object> matchingRow : hashTable.get(key)) {
                    Map<String, Object> combinedRow = new HashMap<>(row);
                    combinedRow.putAll(matchingRow);
                    resultRows.add(combinedRow);
                }
            }
        }

        // Step 4: Create a new DataTable with the combined rows
        ArrayList<String> columnNames = new ArrayList<>(table1.getColumnNames());
        columnNames.addAll(table2.getColumnNames());
        DataTable resultTable = new DataTable(columnNames);
        for (Map<String, Object> row : resultRows) {
            resultTable.addRow(row);
        }

        return resultTable;
    }

    private static Map<Object, ArrayList<Map<String, Object>>> partition(ArrayList<Map<String, Object>> rows, String joinKey) {
        Map<Object, ArrayList<Map<String, Object>>> buckets = new HashMap<>();
        for (Map<String, Object> row : rows) {
            Object key = row.get(joinKey);
            if (!buckets.containsKey(key)) {
                buckets.put(key, new ArrayList<>());
            }
            buckets.get(key).add(row);
        }
        return buckets;
    }

    private static Map<Object, ArrayList<Map<String, Object>>> buildHashTable(Map<Object, ArrayList<Map<String, Object>>> buckets, String joinKey) {
        Map<Object, ArrayList<Map<String, Object>>> hashTable = new HashMap<>();
        for (ArrayList<Map<String, Object>> bucket : buckets.values()) {
            for (Map<String, Object> row : bucket) {
                Object key = row.get(joinKey);
                if (!hashTable.containsKey(key)) {
                    hashTable.put(key, new ArrayList<>());
                }
                hashTable.get(key).add(row);
            }
        }
        return hashTable;
    }
    public static void main(String[] args) {
        DataTable table1 = new DataTable("ab","Termekek");
        DataTable table2 = new DataTable("ab","Gyartok");

    }

    public DataTable getResultTable() {
        return null;
    }
}
