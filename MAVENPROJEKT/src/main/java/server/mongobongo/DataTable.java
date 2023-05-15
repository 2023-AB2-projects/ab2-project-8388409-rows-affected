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
import java.util.List;

public class DataTable extends JPanel implements Serializable {
    protected String databaseName;
    protected String tableName;
    protected ArrayList<DataColumn> columns;

    protected Parser parser;

    protected ArrayList<Integer> selectedColumnIndexes = new ArrayList<>();

    public DataTable(String databaseName, String tableName, Parser parser) {
        this.parser = parser;
        setLayout(new FlowLayout());
//        setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(400, 300));
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
        for (DataColumn column : columns) {
            add(column);
        }

        setVisible(true);
    }

    public DataTable(String databaseName, String tableName) {
        setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(400, 300));
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
            return;
        }
        for (DataColumn column : columns) {
            add(column);
        }

        setVisible(true);
    }

    public DataTable(ArrayList<Document> documentList, Table tableStructure, ArrayList<String> columnNames, Parser parser) {
        setLayout(new FlowLayout());

        this.setPreferredSize(new Dimension(400, 300));
        this.databaseName = "tempDB";
        this.tableName = "tempTable";
        columns = new ArrayList<>();
        buildColumns(columnNames, tableStructure);

        for (DataColumn column : columns) {
            System.out.println("column: " + column.getName());
            add(column);
        }
        revalidate();

        setData(documentList);
        setVisible(true);
    }


    public void buildColumns(ArrayList<String> columnNames, Table table) {
        columns = new ArrayList<>();

        ArrayList<Attribute> attributes = table.zAttributumok();
        System.out.println("Projecting columns");
        for (String cn : columnNames) {
            System.out.println(cn);
        }
        System.out.println("end");
        int index = 0;
        for (Attribute attribute : attributes) {
            System.out.println(attribute.get_attributeName());

            if (columnNames.get(0).equals("*") && columnNames.size() == 1) {
                DataColumn dataColumn = new DataColumn(attribute.get_attributeName(), attribute.get_type());
                columns.add(dataColumn);
                selectedColumnIndexes.add(index);
            } else {
                if (columnNames.contains(attribute.get_attributeName())) {
                    DataColumn dataColumn = new DataColumn(attribute.get_attributeName(), attribute.get_type());
                    columns.add(dataColumn);
                    selectedColumnIndexes.add(index);
                }
            }

            index++;
        }


    }

    public String setData(ArrayList<Document> documents) {


        for (Document document : documents) {
            int index = 0;
            for (String key : document.keySet()) {
                if (selectedColumnIndexes.contains(index)) {
                    if (index == 0) {
                        columns.get(index).addValue(document.get(key).toString());
                    } else {
                        String[] values = document.get(key).toString().split("#");
                        for (String value : values) {
                            columns.get(index).addValue(value);
                            index++;
                        }
                    }
                }
                index++;
            }


        }

        return "OK";
    }

    public DataTable(String databaseName, String tableName, String skelton) {
        setLayout(new FlowLayout());
        setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(400, 300));
        this.databaseName = databaseName;
        this.tableName = tableName;
        columns = new ArrayList<>();
        String ok = setCatalogData(databaseName, tableName);
        if (!ok.equalsIgnoreCase("ok")) {
            parser.setOtherError("Table does not exist");
            return;
        }
        for (DataColumn column : columns) {
            add(column);
        }

        setVisible(true);
    }

    public DataTable(DataTable table) {
        setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(400, 300));
        this.columns = new ArrayList<>();
        this.databaseName = table.getDatabaseName();
        this.tableName = table.getTableName();
        for (DataColumn column : table.getColumns()) {
            if (column != null)
                columns.add(new DataColumn(column));
        }
        for (DataColumn column : columns) {
            add(column);
        }
        setVisible(true);
    }

    public DataTable() {
        setBackground(Color.BLACK);
        setLayout(new FlowLayout());
        this.setPreferredSize(new Dimension(400, 300));
        columns = new ArrayList<>();
        setVisible(true);
    }

    public ArrayList<DataColumn> getColumns() {
        ArrayList<DataColumn> ret = new ArrayList<>();
        for (DataColumn column : columns) {
            ret.add(new DataColumn(column));
        }
        return columns;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
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

                                DataColumn dataColumn = new DataColumn(attribute.get_attributeName(), attribute.get_type());
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
        for (DataColumn column : columns) {
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

//            System.out.println(document.toJson());
            for (String key : document.keySet()) {
//                System.out.println(key);
//                System.out.println(document.get(key));

                if (index == 0) {
                    columns.get(index).addValue(document.get(key).toString());
                } else {
                    String[] values = document.get(key).toString().split("#");
                    for (String value : values) {
                        columns.get(index).addValue(value);
//                        System.out.println("+>" + index + " " + value);
//                        System.out.println("index: " + index);
                        index++;
                    }

                }
//                System.out.println("index: " + index);
                index++;
            }

        }
        mongoClient.close();
        return "OK";
    }

    public ArrayList<DataColumn> getDataColums() {
        return columns;
    }

    public void addColomn(DataColumn column) {
        columns.add(column);
    }

    public void setColumns(ArrayList<DataColumn> columns) {
        this.columns = columns;
    }

    public ArrayList<Integer> findRowIndexByColumNameAndValue(String columnName, String value) {
        int index = 0;
        ArrayList<Integer> fineIndexes = new ArrayList();
        for (DataColumn dc : getColumns()) {
            if (dc.getColumnName().equals(columnName)) {
                for (ResizeLabel rl : dc.getValueLabels()) {
                    if (index > 1) {
                        if (rl.getText().equals(value))
                            fineIndexes.add(index - 2);
                    }
                    index++;
                }
            }
        }

        return fineIndexes;
    }

    public static void main(String[] args) {
        DataTable dt = new DataTable("ab", "GPU");
        ArrayList<Integer> rowIndex = dt.findRowIndexByColumNameAndValue("price", "220");
        for (Integer i : rowIndex) {
            ArrayList<String> row = dt.getRow(i);
            for (String lab : row) {
//                System.out.print(lab + ", ");
            }
//            System.out.println();
        }
        JFrame jf = new JFrame();
        jf.setSize(400, 300);
        jf.setLayout(new FlowLayout());
        jf.setBackground(new Color(203, 141, 141));
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JScrollPane sp = new JScrollPane(new DataTable(dt), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

//        System.out.println("width: " + dt.getPanelWidth() + " height: " + dt.getPanelHeight());
//        System.out.println("width: " + sp.getWidth() + " height: " + sp.getHeight());

//        sp.setPreferredSize(new Dimension());
//      sp be resizable
        sp.setAutoscrolls(true);

        sp.getVerticalScrollBar().getMaximumSize();


        jf.add(sp);
        jf.setVisible(true);

    }

    public int getPanelWidth() {
        return getColumns().get(0).getValueLabels().size() * 10;
    }

    public int getPanelHeight() {
        return getColumns().size() * 20;
    }

    public DataColumn getColumnByName(String key1) {
//        System.out.println();
        for (DataColumn dc : columns) {
//            System.out.println(dc.getColumnName()+" "+key1);
            if (dc.getColumnName().equals(key1)) {
                return dc;
            }
        }
        return null;
    }

    public ArrayList<String> getRowByIndex(Integer integer) {
        ArrayList<String> ret = new ArrayList<>();
        for (DataColumn column : columns) {
            ret.add(column.getRow(integer));
        }
        return ret;
    }

    public ArrayList<String>[] getRows() {
        ArrayList[] ret = new ArrayList[columns.get(0).getValueLabels().size()];
        for (int i = 0; i < columns.get(0).getValueLabels().size(); i++) {
            ret[i] = new ArrayList<>();
        }
        for (DataColumn column : columns) {
            for (int i = 0; i < column.getValueLabels().size(); i++) {
                ret[i].add(column.getRow(i));
            }
        }
        return ret;
    }

    public int addRow(ArrayList<String> row) {
        if (row.size() != columns.size()) {
//            System.out.println(row.size() + " " + columns.size());
//            System.out.println("Nem egyezik a sor hossza a tábla oszlopainak számával!");
            return -1;
        }
        for (int i = 0; i < row.size(); i++) {
            columns.get(i).addValue(row.get(i));
        }
        return 0;
    }

    public void addColumn(DataColumn column) {
        columns.add(column);
    }

}
