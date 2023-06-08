package server.commands;

import server.mongobongo.DataColumnModel;
import server.mongobongo.DataTable;
//impoert bason document
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GroupBY {

    private HashMap<String, ArrayList<String>> groupByMap;
    //    private HashMap<String, Integer> groupByMapResult;
    private String tableName;

    ArrayList<String> selectColumns;

    private HashMap<String, HashMap<String, Integer>> groupByMapResultFull;
    private ArrayList<Document> operations;
    private String text;
    private String[] groupBy;

    public GroupBY(String text, String tableName,ArrayList<String> selectColumns) {
        this.selectColumns = selectColumns;
        this.text = text;
        this.tableName = tableName;
        groupByMap = new HashMap<>();
        groupByMapResultFull = new HashMap<>();
        operations = new ArrayList<>();
        groupBy = null;
        processSelected(selectColumns);
    }

    public void addToMap(String text, String type) {
        System.out.println("-------------------------------- add to matp gropu by");
        String name = text.substring(text.indexOf("(") + 1, text.indexOf(")"));
        String table = tableName;

        if (name.contains(".")) {
            table = name.substring(0, name.indexOf("."));
            name = name.substring(name.indexOf(".") + 1);
        }

        System.out.println("Aggr Name: " + name);
        System.out.println("Aggr type: " + type);
        System.out.println("Aggr table: " + table);

        HashMap<String, Integer> tmp = new HashMap<>();


        groupByMapResultFull.put(name + "_" + type, tmp);
//        make a bson document
        Document document = new Document();
        document.append("type", type);
        document.append("name", name);
        operations.add(document);

    }

    public DataTable processTable(DataTable table) {

        if (groupByMapResultFull.isEmpty() || groupBy==null) {
            return table;
        }

        System.out.println("*(********* * * ** *  GROUP BY ");
        System.out.println("GroupByMap: " + groupByMap);
        if (table == null) {
            return null;
        }



//        egyre irjukk meg
        ArrayList<String> tableColumns = table.getColumnsName();

        for (String c : tableColumns) {
            System.out.println("table column: " + c);
        }

        for (Document d : operations) {
            System.out.println("document: " + d);
        }


        ArrayList<DataColumnModel> oks = new ArrayList<>();
        int size = 0;
        for (String s : groupBy) {
            System.out.println("group by: " + s);
            oks.add(table.getColumn(s));
            size = table.getColumn(s).getColumnSize();
        }

        ArrayList<String> byThis = new ArrayList<>(Arrays.asList(groupBy));


        for (int i = 0; i < size; i++) {

            String key = "";
            for (DataColumnModel o : oks) {
                if (byThis.contains(o.getName())) {
                    key += o.getValues().get(i);
                }
                System.out.println("value: " + o.getValues().get(i) + " column: " + o.getName());
            }
            System.out.println("By this key:" + key);

            for (Document doc : operations) {
//
                String type = doc.getString("type");
                String name = doc.getString("name");
                System.out.println("type: " + type);
                System.out.println("name: " + name);

//                full bol lekerjuk
                HashMap<String, Integer> partial = groupByMapResultFull.get(name + "_" + type);

//                type es namefeltoli a sajat Hasehe
                String v = table.getColumn(name).getValues().get(i);
                System.out.println("value: " + v);
                int vInt = Integer.parseInt(v);
                if (type.equals("MAX")) {

                    if (partial.containsKey(key)) {
                        partial.put(key, Math.max(partial.get(key), vInt));
                    } else {
                        System.out.println("not contains key");
                        partial.put(key, vInt);
                    }
                }
                if (type.equals("MIN")) {

                    if (partial.containsKey(key)) {
                        partial.put(key, Math.min(partial.get(key), vInt));
                    } else {
                        System.out.println("not contains key");
                        partial.put(key, vInt);
                    }
                }
                if (type.equals("SUM")) {
                    if (partial.containsKey(key)) {
                        partial.put(key, partial.get(key) + vInt);
                    } else {
                        System.out.println("not contains key");
                        partial.put(key, vInt);
                    }
                }
                if (type.equals("COUNT")) {
                    if (partial.containsKey(key)) {
                        partial.put(key, partial.get(key) + 1);
                    } else {
                        System.out.println("not contains key");
                        partial.put(key, 1);
                    }
                }
                if (type.equals("AVG")) {
                    if (partial.containsKey(key)) {
                        partial.put(key, partial.get(key) + vInt);
                    } else {
                        System.out.println("not contains key");
                        partial.put(key, vInt);
                    }
                }

            }
        }
//        ArrayList<DataColumnModel> resultColumns = new ArrayList<>();


        ArrayList<DataColumnModel> resultColumns = new ArrayList<>();


        System.out.println("================ results ================");

        for (Document doc : operations) {
            String type = doc.getString("type");
            String name = doc.getString("name");
            System.out.println("type: " + type);
            System.out.println("name: " + name);
            HashMap<String, Integer> partial = groupByMapResultFull.get(name + "_" + type);
            DataColumnModel resultColumn = new DataColumnModel(type + "("+name+")", "int");
            ArrayList<String> values = new ArrayList<>();
            for (String k : partial.keySet()) {
                System.out.println("key: " + k);
                System.out.println("value: " + partial.get(k));
                values.add(partial.get(k) + "");
            }
            resultColumn.setValues(values);
            resultColumns.add(resultColumn);
        }

        for (HashMap<String, Integer> partial : groupByMapResultFull.values()) {

//            DataColumnModel resultColumn = new DataColumnModel()

            int indexC = 0;
            System.out.println("partial: " + partial);
            for(String k : partial.keySet()){
                System.out.println("key: " + k);
                System.out.println("value: " + partial.get(k));


            }
            indexC++;
        }

        DataTable result = new DataTable();
        result.setColumns(resultColumns);

        return new DataTable(result);


//        for (int i = 0; i < size; i++) {
//            String key = "";
//            for (DataColumnModel o : oks) {
//                key += o.getValues().get(i);
//            }
//
//            System.out.println("key: " + key);
//            if (groupByMapResult.containsKey(key)) {
//                groupByMapResult.put(key, groupByMapResult.get(key) + 1);
//            } else {
//                groupByMapResult.put(key, 1);
//            }
//        }
//
//        for (String key : groupByMapResult.keySet()){
//            System.out.println("key: " + key);
//            System.out.println("value: " + groupByMapResult.get(key));
//        }
//


    }

    public void processSelected(ArrayList<String> selectedColumns) {

        try {
            if (text.contains("GROUP BY")) {
                System.out.println("text: " + text);
                String data = text.substring(text.indexOf("GROUP BY") + 9);
                if (data.contains(",")) {
                    groupBy = data.split(",");

                } else {
                    groupBy = new String[]{data.strip()};
                    System.out.println("GROUP BY: " + groupBy);
                }

            }


            for (String attr : selectedColumns) {

                if (attr.contains("MAX")) {
                    addToMap(attr, "MAX");
                } else if (attr.contains("MIN")) {
                    addToMap(attr, "MIN");
                } else if (attr.contains("AVG")) {
                    addToMap(attr, "AVG");
                } else if (attr.contains("SUM")) {
                    addToMap(attr, "SUM");
                } else if (attr.contains("COUNT")) {
                    addToMap(attr, "COUNT");
                }

            }

        } catch (Exception e) {
            System.out.println("Error in GroupBy: " + e.getMessage());
        }


    }


}
