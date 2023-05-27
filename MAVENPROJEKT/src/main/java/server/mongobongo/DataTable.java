package server.mongobongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import server.Parser;
import server.jacksonclasses.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataTable implements Serializable {
    protected String databaseName;
    protected String tableName;
    protected ArrayList<DataColumnModel> columns;

    protected Parser parser;

    protected ArrayList<Integer> selectedColumnIndexes = new ArrayList<>();

    public DataTable(String databaseName, String tableName, Parser parser) {
        this.parser = parser;

        this.databaseName = databaseName;
        this.tableName = tableName;
        columns = new ArrayList<>();
        String ok = setCatalogData(databaseName, tableName);
        System.out.println("ok: " + ok);
        if (!ok.equalsIgnoreCase("ok")) {
            parser.setOtherError("Table does not exist");
            return;
        }
        ok = setMongoData(databaseName, tableName);
        if (!ok.equalsIgnoreCase("ok")) {
            parser.setOtherError(ok);
            return;
        }
    }

    public DataTable(String databaseName, String tableName) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        columns = new ArrayList<>();
        String ok = setCatalogData(databaseName, tableName);
        if (!ok.equalsIgnoreCase("ok")) {
            parser.setOtherError("Table does not exist");
            return;
        }
        ok = setMongoData(databaseName, tableName);
        if (!ok.equalsIgnoreCase("ok")) {
            parser.setOtherError(ok);
        }

    }

    public DataTable(ArrayList<Document> documentList, Table tableStructure, ArrayList<String> columnNames, Parser parser) {

        this.databaseName = "tempDB";
        this.tableName = "tempTable";
        columns = new ArrayList<>();
//        System.out.println("Projecting columns"+columnNames.toString());

        buildColumns(columnNames, tableStructure);

        setData(documentList);
    }


    public void buildColumns(ArrayList<String> columnNames, Table table) {
        columns = new ArrayList<>();

        ArrayList<Attribute> attributes = table.zAttributumok();
//        for (String cn : columnNames) {
//            System.out.println(cn);
//        }
//        System.out.println("end");
        int index = 0;
        for (Attribute attribute : attributes) {
//            System.out.println(attribute.get_attributeName());
                if (columnNames.contains(attribute.get_attributeName())) {
                    DataColumnModel dataColumn = new DataColumnModel(attribute.get_attributeName(), attribute.get_type());
                    columns.add(dataColumn);
                    selectedColumnIndexes.add(index);
//                    System.out.println("!Adding column: " + attribute.get_attributeName());
                }

            index++;
        }


    }

    public String setData(ArrayList<Document> documents) {


        for (Document document : documents) {
            int index = 0;

            ArrayList<String> keys = new ArrayList<>(document.keySet());
//            System.out.println("Projecting colum indexes:"+selectedColumnIndexes.toString());

//            System.out.println("keys: " + keys.size());
//            System.out.println("keys: " + keys.toString());

            for (Integer i : selectedColumnIndexes) {
//                System.out.println(i);

                if (i==0){
                    columns.get(index).addValue(document.get("_id").toString());
                    columns.get(index).setPrimaryKey(true);
                } else {
                    String value = (String) document.get(keys.get(1));
//                    System.out.println("-key: " + i + " : " + keys.get(1));
                    String[] values = value.split("#");
//                    System.out.println(Arrays.toString(values));
//                    System.out.println("key: " + i + " value: " + values[i-1]);
                    columns.get(index).addValue(values[i-1]);
                }
                index++;


            }


        }

        return "OK";
    }

    public DataTable(String databaseName, String tableName, String skelton) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        columns = new ArrayList<>();
        String ok = setCatalogData(databaseName, tableName);
        if (!ok.equalsIgnoreCase("ok")) {
            parser.setOtherError("Table does not exist");
            return;
        }

    }

    public DataTable(DataTable table) {
        this.columns = new ArrayList<>();
        this.databaseName = table.getDatabaseName();
        this.tableName = table.getTableName();
        for (DataColumnModel column : table.getColumns()) {

            if (column != null)
                columns.add(new DataColumnModel(column));
        }
    }

    public DataTable() {
        columns = new ArrayList<>();
    }

    public ArrayList<DataColumnModel> getColumns() {
        ArrayList<DataColumnModel> ret = new ArrayList<>();
        for (DataColumnModel column : columns) {
            ret.add(new DataColumnModel(column));
        }
        return columns;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }


    public int[] quicSort(int[] arr){
        if(arr.length <= 1) {
            return arr;
        }
        int pivot = arr[0];
        int[] left = new int[arr.length];
        int[] right = new int[arr.length];
        int leftCount = 0;
        int rightCount = 0;
        for(int i = 1; i < arr.length; i++) {
            if(arr[i] < pivot) {
                left[leftCount++] = arr[i];
            } else {
                right[rightCount++] = arr[i];
            }
        }
        int[] leftSorted = quicSort(Arrays.copyOfRange(left, 0, leftCount));
        int[] rightSorted = quicSort(Arrays.copyOfRange(right, 0, rightCount));
        int[] sorted = new int[arr.length];
        System.arraycopy(leftSorted, 0, sorted, 0, leftCount);
        sorted[leftCount] = pivot;
        System.arraycopy(rightSorted, 0, sorted, leftCount + 1, rightCount);
        return sorted;
    }
    public void sort(int index) {
        int[] indexLocal = new int[columns.size()];
        for (int i = 0; i < indexLocal.length; i++) {
            indexLocal[i] = i;
        }
        int temp = indexLocal[columns.size()];
        ArrayList<String> tmp = getColumns().get(index).getValues();
        int[] tmp2 = new int[tmp.size()];
        for (int i = 0; i < tmp.size(); i++) {
           tmp2[i] = Integer.parseInt(tmp.get(i));
        }
        int [] tmp3 = quicSort(tmp2);



    }

    public String setCatalogData(String databaseName, String tableName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            Databases databases = objectMapper.readValue(new File("Catalog.json"), Databases.class);
            List<Database> databaseList = databases.getDatabases();
            for (Database db : databaseList) {
//                System.out.println(db.get_dataBaseName());

                if (db.get_dataBaseName().equals(databaseName)) {
                    List<Table> tables = db.getTables();
                    for (Table table : tables) {
                        if (table.get_tableName().equals(tableName)) {

                            columns = new ArrayList<>();
//                            System.out.println(table.get_tableName());
                            ArrayList<Attribute> attributes = table.zAttributumok();
                            List<PrimaryKey> pks = table.getPrimaryKeys();
                            List<ForeignKey> fks = table.getForeignKeys();
                            List<UniqueKey> uks = table.getUniqueKeys();

                            for (Attribute attribute : attributes) {

                                DataColumnModel dataColumn = new DataColumnModel(attribute.get_attributeName(), attribute.get_type());
                                if (pks != null) {
                                    for (PrimaryKey pk : pks) {
                                        if (pk.getPkAttribute().equals(attribute.get_attributeName())) {
                                            dataColumn.isPrimaryKey();
                                        }
                                    }
                                }
                                if (fks != null) {
                                    for (ForeignKey fk : fks) {
                                        if (fk.getFkAttribute().equals(attribute.get_attributeName())) {
                                            dataColumn.isForeignKey();
                                        }
                                    }
                                }
                                columns.add(dataColumn);
                            }
                            return "ok";
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Table not found";
    }

    public ArrayList<String> getRow(int index) {
        ArrayList<String> ret = new ArrayList<>();
        for (DataColumnModel column : columns) {
            ret.add(column.getRow(index));
        }
        return ret;
    }

    public String setMongoData(String db, String table) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");

        MongoDatabase database = mongoClient.getDatabase(db);
        if (database == null) {
            return "Database not found";
        }

        ArrayList<Document> documents = new ArrayList<>();

        MongoCollection<Document> collection = database.getCollection(table);
        if (collection == null) {
            return "Collection not found";
        }
        for (Document document : collection.find()) {
            int index = 0;

            for (String key : document.keySet()) {

                if (index == 0) {
                    columns.get(index).addValue(document.get(key).toString());
                } else {
                    String[] values = document.get(key).toString().split("#");
                    for (String value : values) {
                        columns.get(index).addValue(value);
                        index++;
                    }

                }
                index++;
            }

        }
        mongoClient.close();
        return "OK";
    }

    public ArrayList<DataColumnModel> getDataColums() {
        return columns;
    }

    public void addColomn(DataColumnModel column) {
        columns.add(column);
    }

    public void setColumns(ArrayList<DataColumnModel> columns) {
        this.columns = columns;
    }

    public ArrayList<Integer> findRowIndexByColumNameAndValue(String columnName, String value) {
        int index = 0;
        ArrayList<Integer> fineIndexes = new ArrayList();
        for (DataColumnModel dc : getColumns()) {
            if (dc.getColumnName().equals(columnName)) {
//                TODO: lehet az object nem jo
                for (Object rl : dc.getValues()) {
                    if (index > 1) {
                        if (rl.equals(value))
                            fineIndexes.add(index - 2);
                    }
                    index++;
                }
            }
        }

        return fineIndexes;
    }

//    public ArrayList<String> getRow(int index) {
//        index += 2;
//        ArrayList<String> ret = new ArrayList<>();
//        for (String value : this.columns.get(0).getValues()) {
//            ret.add(value);
//        }
//        return ret;
//    }

//    public static void main(String[] args) {
//        DataTable dt = new DataTable("ab", "GPU");
//
//        JFrame jf = new JFrame();
//        jf.setSize(400, 300);
//        jf.setLayout(new FlowLayout());
//        jf.setBackground(new Color(203, 141, 141));
//        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        JScrollPane sp = new JScrollPane(new DataTable(dt), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//
////        System.out.println("width: " + dt.getPanelWidth() + " height: " + dt.getPanelHeight());
////        System.out.println("width: " + sp.getWidth() + " height: " + sp.getHeight());
//
////        sp.setPreferredSize(new Dimension());
////      sp be resizable
//        sp.setAutoscrolls(true);
//
//        sp.getVerticalScrollBar().getMaximumSize();
//
//
//        jf.add(sp);
//        jf.setVisible(true);
//
//    }


    public void setTableName(String fromTable) {
        this.tableName = fromTable;
    }

    public void setDatabaseName(String fromDatabase) {
        this.databaseName = fromDatabase;
    }
}
