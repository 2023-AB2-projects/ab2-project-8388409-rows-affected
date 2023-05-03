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

public class DataTable extends JPanel {
    private final String database;
    private final String tableName;

    private ArrayList<DataColumn> columns;

    public DataTable(String databaseName, String tableName) {
        setLayout(new FlowLayout());
        this.database = databaseName;
        this.tableName = tableName;
        columns = new ArrayList<>();
        setCatalogData(databaseName, tableName);
        setMongoData(databaseName, tableName);
        for (DataColumn column : columns) {
            add(column);
        }

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

    public static void main(String[] args) {
        JFrame frame = new JFrame("DataTable");
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.add(new DataTable("ab", "GPU"));
        frame.setVisible(true);

    }
}
