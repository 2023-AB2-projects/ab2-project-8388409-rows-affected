package server.mongobongo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONArray;
import server.jacksonclasses.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataTable extends JPanel implements java.io.Serializable {
    protected String databaseName;
    protected String tableName;
    protected ArrayList<DataColumn> columns;

    public DataTable(String databaseName, String tableName) {
        setBackground(Color.BLACK);
        setLayout(new FlowLayout());
        this.databaseName = databaseName;
        this.tableName = tableName;
        columns = new ArrayList<>();
        setCatalogData(databaseName, tableName);
        setMongoData(databaseName, tableName);
        for (DataColumn column : columns) {
            add(column);
        }
    }

    public DataTable(DataTable table) {
        setLayout(new FlowLayout());
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

    public void setCatalogData(String databaseName, String tableName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            Databases databases = objectMapper.readValue(new File("Catalog.json"), Databases.class);
            List<Database> databaseList = databases.getDatabases();
            for (Database db : databaseList) {
                System.out.println(db.get_dataBaseName());

                if (db.get_dataBaseName().equals(databaseName)) {
                    List<Table> tables = db.getTables();
                    for (Table table : tables) {
                        if (table.get_tableName().equals(tableName)) {

                            columns = new ArrayList<>();
                            System.out.println(table.get_tableName());
                            ArrayList<Attribute> attributes = table.zAttributumok();
                            List<PrimaryKey> pks = table.getPrimaryKeys();
                            List<ForeignKey> fks = table.getForeignKeys();
                            List<UniqueKey> uks = table.getUniqueKeys();

                            for (Attribute attribute : attributes) {
//                                System.out.print(attribute.get_attributeName() + " ");
//                                System.out.println(attribute.get_type());
//                                System.out.println(attribute.get_isnull());

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
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Cszice:" + columns.size());
    }

    public ArrayList<String> getRow(int index) {
        ArrayList<String> ret = new ArrayList<>();
        for (DataColumn column : columns) {
            ret.add(column.getRow(index));
        }
        return ret;
    }

    public void setMongoData(String db, String table) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase(db);
        MongoCollection<Document> collection = database.getCollection(table);
        for (Document document : collection.find()) {
            int index = 0;
            JSONArray jsonArray = new JSONArray();
            System.out.println(document.toJson());
            for (String key : document.keySet()) {
                System.out.println(key);
                System.out.println(document.get(key));

                if (index == 0) {
                    columns.get(index).addValue(document.get(key).toString());
                } else {
                    String[] values = document.get(key).toString().split("#");
                    for (int i = 0; i < values.length; i++) {
                        columns.get(index).addValue(values[i]);
                        System.out.println("+>" + index + " " + values[i]);
                        System.out.println("index: " + index);
                        index++;
                    }

                }
                System.out.println("index: " + index);
                index++;
            }

        }
        mongoClient.close();

    }

    public ArrayList<DataColumn> getDataColums() {
        return columns;
    }

}
