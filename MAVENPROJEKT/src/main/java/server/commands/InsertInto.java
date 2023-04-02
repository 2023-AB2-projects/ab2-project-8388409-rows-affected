package server.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import org.bson.Document;
import server.Parser;
import server.jacksonclasses.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.MongoClients.create;

public class InsertInto {
    public InsertInto(String databaseName, String tableName, String contents, Parser parser) {
        tableName = tableName.trim();

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
            Table myTable = null;
            boolean tableExists = false;
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
            String[] splitValues = contents.split(",");

            for (int i = 0; i < splitValues.length; i++) {
                splitValues[i] = splitValues[i].trim();
            }

            // get the primary key of the table
            List<String> primaryKeyNames = new ArrayList<>();
            List<PrimaryKey> primaryKeyList = myTable.getPrimaryKeys();
            for (PrimaryKey primaryKey : primaryKeyList) {
                primaryKeyNames.add(primaryKey.getPkAttribute());
            }
            if (primaryKeyNames.size() == 0) {
                parser.setOtherError("The table does not have a primary key");
                return;
            }

            // get the primary key value from values
            List<Integer> primaryKeyIndexes = new ArrayList<>();
            List<String> primaryKeyTypes = new ArrayList<>();
            Structure structure = myTable.getStructure();
            List<Attribute> attributeList = structure.getAttributes();
            for (String primaryKeyName : primaryKeyNames) {
                for (int i = 0; i < attributeList.size(); i++) {
                    if (attributeList.get(i).get_attributeName().equals(primaryKeyName)) {
                        primaryKeyIndexes.add(i);
                        primaryKeyTypes.add(attributeList.get(i).get_type());
                    }
                }
            }
            if (primaryKeyIndexes.size() != primaryKeyNames.size()) {
                parser.setOtherError("The primary key does not exist in the table");
                return;
            }
            List<String> primaryKeyValue = new ArrayList<>();
            for (Integer primaryKeyIndex : primaryKeyIndexes) {
                primaryKeyValue.add(splitValues[primaryKeyIndex]);
            }

            for (Database database : databaseList) {
                if (database.get_dataBaseName().equals(databaseName)) {
                    List<Table> tableList = database.getTables();
                    for (Table table : tableList) {
                        if (table.get_tableName().equals(tableName)) {
                            structure = table.getStructure();
                            attributeList = structure.getAttributes();
                            List<String> attributeNames = new ArrayList<>();

                            for (Attribute attribute : attributeList) {
                                attributeNames.add(attribute.get_attributeName());
                            }
                            if (attributeNames.size() != splitValues.length) {
                                parser.setOtherError("The number of values does not match the number of columns");
                                return;
                            }
                            for (int i = 0; i < attributeNames.size(); i++) {
                                if (splitValues[i].equals("null")) {
                                    continue;
                                }
                                if (attributeNames.get(i).toLowerCase().contains("int")) {
                                    try {
                                        Integer.parseInt(splitValues[i]);
                                    } catch (NumberFormatException e) {
                                        parser.setOtherError("The value " + splitValues[i] + " is not an integer");
                                        return;
                                    }
                                } else if (attributeNames.get(i).toLowerCase().contains("float")) {
                                    try {
                                        Float.parseFloat(splitValues[i]);
                                    } catch (NumberFormatException e) {
                                        parser.setOtherError("The value " + splitValues[i] + " is not a float");
                                        return;
                                    }
                                } else if (attributeNames.get(i).toLowerCase().contains("varchar")) {
                                    if ((splitValues[i].charAt(0) != '\'' || splitValues[i].charAt(splitValues[i].length() - 1) != '\'') || (splitValues[i].charAt(0) != '\"' || splitValues[i].charAt(splitValues[i].length() - 1) != '\"')) {
                                        parser.setOtherError("The value " + splitValues[i] + " is not a string");
                                        return;
                                    } else {
                                        splitValues[i] = splitValues[i].substring(1, splitValues[i].length() - 1);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            contents = contents.replace(",", "#");
            contents = contents.substring(contents.indexOf("#") + 1);


            // if varchar, remove the quotes
            for (int i = 0; i < splitValues.length; i++) {
                if ((splitValues[i].charAt(0) == '\'' && splitValues[i].charAt(splitValues[i].length() - 1) == '\'') || (splitValues[i].charAt(0) == '\"' && splitValues[i].charAt(splitValues[i].length() - 1) == '\"')) {
                    splitValues[i] = splitValues[i].substring(1, splitValues[i].length() - 1);
                }
            }

            String key = "";
            String value = "";
            for (int i = 0; i < splitValues.length; i++) {
                splitValues[i] = splitValues[i].trim();
                if (primaryKeyIndexes.contains(i)) {
                    key = key + splitValues[i] + "#";
                    continue;
                } else {
                    value = value + splitValues[i] + "#";
                }
            }

            key = key.substring(0, key.length() - 1);
            value = value.substring(0, value.length() - 1);

            System.out.println("Trying to connect to MongoDB");
            String connectionString = "mongodb://localhost:27017";
            try (MongoClient mongoClient = create(connectionString)) {
                Document document = new Document("_id", key).append(key, value);
                mongoClient.getDatabase(databaseName).getCollection(tableName).insertOne(document);
            } catch (MongoWriteException e) {
                if (e.getError().getCode() == 11000) {
                    parser.setOtherError("The primary key already exists");
                    return;
                } else {
                    System.out.println(e);
                    throw new RuntimeException(e);
                }
            }
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
        System.out.println("Inserted into " + tableName + " successfully");
    }
}
