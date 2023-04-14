package server.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import server.Parser;
import server.jacksonclasses.Database;
import server.jacksonclasses.Databases;
import server.jacksonclasses.PrimaryKey;
import server.jacksonclasses.Table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.MongoClients.create;

public class DeleteFrom {
    public DeleteFrom(String databaseName, String tableName, String condition, Parser parser) {
        tableName = tableName.trim();
        System.out.println("tableName = " + tableName);
        System.out.println("condition = " + condition);
        condition = condition.trim();
        condition = condition.replace("AND", "and");
        String[] conditions = condition.split("and");
        List<String> conditionsList = new ArrayList<>();
        for (String s : conditions) {
            conditionsList.add(s.trim());
        }


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Databases databases = objectMapper.readValue(new File("Catalog.json"), Databases.class);
            List<Database> databaseList = databases.getDatabases();
            if (databaseList == null) {
                parser.setOtherError("There are no databases in the catalog");
                return;
            }
            boolean databaseExists = false;
            for (Database database : databaseList) {
                if (database.get_dataBaseName().equals(databaseName)) {
                    databaseExists = true;
                    break;
                }
            }
            if (!databaseExists) {
                parser.setOtherError("Database " + databaseName + " does not exist");
                return;
            }
            boolean tableExists = false;
            Table myTable = null;
            for (Database database : databaseList) {
                if (database.get_dataBaseName().equals(databaseName)) {
                    List<Table> tableList = database.getTables();
                    if (tableList == null) {
                        parser.setOtherError("There are no tables in the database " + databaseName);
                        return;
                    }
                    for (Table table : tableList) {
                        if (table.get_tableName().equals(tableName)) {
                            tableExists = true;
                            myTable = table;
                            break;
                        }
                    }
                }
            }
            if (!tableExists) {
                parser.setOtherError("Table " + tableName + " does not exist");
                return;
            }

            List<String> primaryKeyNames = new ArrayList<>();
            List<PrimaryKey> primaryKeyList = myTable.getPrimaryKeys();
            if (primaryKeyList != null) {
                for (PrimaryKey primaryKey : primaryKeyList) {
                    primaryKeyNames.add(primaryKey.getPkAttribute());
                }
            } else {
                parser.setOtherError("Table " + tableName + " does not have a primary key");
                return;
            }
            Boolean[] primaryKeyChecked = new Boolean[primaryKeyNames.size()];
            Arrays.fill(primaryKeyChecked, false);

            for (String s : conditionsList) {
                String[] conditionParts = s.split("=");
                String key = conditionParts[0].trim();
                boolean primaryKeyExists = false;
                for (int i = 0; i < primaryKeyNames.size(); i++) {
                    if (key.equals(primaryKeyNames.get(i))) {
                        primaryKeyExists = true;
                        if (primaryKeyChecked[i]) {
                            parser.setOtherError("Primary key " + key + " is used more than once");
                            return;
                        } else {
                            primaryKeyChecked[i] = true;
                        }
                        break;
                    }
                }
                if (!primaryKeyExists) {
                    parser.setOtherError("Primary key " + key + " does not exist");
                    return;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String pkValue = "";
        for (String s : conditionsList) {
            String[] conditionParts = s.split("=");
            String value = conditionParts[1].trim();
            pkValue += value + "#";
        }
        pkValue = pkValue.substring(0, pkValue.length() - 1);

        String connectionString = "mongodb://localhost:27017";
        try (MongoClient mongoClient = create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(tableName);
            collection.deleteOne(new Document("_id", pkValue));
        }
    }
}
