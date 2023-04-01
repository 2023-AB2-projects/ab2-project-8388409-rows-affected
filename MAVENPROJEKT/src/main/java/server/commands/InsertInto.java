package server.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
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
        String[] values = contents.split(",");

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
            String[] splitValues = values;

            for (int i = 0; i < splitValues.length; i++) {
                splitValues[i] = splitValues[i].trim();
            }

            // get the primary key of the table
            String primaryKeyName = "";
            List<PrimaryKey> primaryKeyList = myTable.getPrimaryKeys();
            if (primaryKeyList != null) {
                primaryKeyName = primaryKeyList.get(0).getPkAttribute();
            }
            if (primaryKeyName.equals("")) {
                parser.setOtherError("The table does not have a primary key");
                return;
            }

            // get the primary key value from values
            int primaryKeyIndex = -1;
            String primaryKeyType = "";
            Structure structure = myTable.getStructure();
            List<Attribute> attributeList = structure.getAttributes();
            for (int i = 0; i < attributeList.size(); i++) {
                if (attributeList.get(i).get_attributeName().equals(primaryKeyName)) {
                    primaryKeyIndex = i;
                    primaryKeyType = attributeList.get(i).get_type();
                    break;
                }
            }
            if (primaryKeyIndex == -1) {
                parser.setOtherError("The primary key does not exist in the table");
                return;
            }
            String primaryKeyValue = splitValues[primaryKeyIndex];

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

            if (primaryKeyType.equals("varchar")) {
                primaryKeyValue = primaryKeyValue.substring(1, primaryKeyValue.length() - 1);
            }


            String connectionString = "mongodb://localhost:27017";
            try (MongoClient mongoClient = create(connectionString)) {
                Document document = new Document();
                document.append(primaryKeyValue, contents);
                mongoClient.getDatabase(databaseName).getCollection(tableName).insertOne(document);
            } catch (Exception e) {
                System.out.println(e);
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }
}
