package server.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.Parser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLOutput;

public class CreateIndex {
    public CreateIndex(String indexName,String tableName,String contents,String currentDatabase, Parser parser){

        JSONObject catalog;
        try {
            Reader reader = new FileReader("Catalog.json");
            JSONParser jsonParser = new JSONParser();
            catalog = (JSONObject) jsonParser.parse(reader);
            reader.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject myDatabase = null;
        JSONArray databases = (JSONArray) catalog.get("Databases");


        // Get the database
        for (int i = 0; i < databases.size(); i++) {
            JSONObject database = (JSONObject) databases.get(i);
            JSONObject databaseContents = (JSONObject) database.get("Database");
            String databaseNameInCatalog = (String) databaseContents.get("_dataBaseName");
            if (databaseNameInCatalog.equals(currentDatabase)) {
                myDatabase = database;
                break;
            }
        }
        if (myDatabase == null) {
            parser.setOtherError("Database does not exist");
            return;
        }

        JSONObject databaseContents = (JSONObject) myDatabase.get("Database");
        JSONArray tables = (JSONArray) databaseContents.get("Tables");
        JSONObject myTable = null;

        // Check if table exists
        for (int i = 0; i < tables.size(); i++) {
            JSONObject table = (JSONObject) tables.get(i);
            JSONObject tableContents = (JSONObject) table.get("Table");
            String tableNameInCatalog = (String) tableContents.get("_tableName");
            if (tableNameInCatalog.equals(tableName)) {
                myTable = table;
                break;
            }
        }
        if (myTable == null) {
            parser.setOtherError("Table does not exist");
            return;
        }

        JSONObject tableContents = (JSONObject) myTable.get("Table");
        JSONArray indexes = (JSONArray) tableContents.get("IndexFiles");
        JSONObject indexfile = new JSONObject();
        JSONObject IndexAttributes = new JSONObject();

        IndexAttributes.put("IAttributes", indexName);
        indexfile.put("IndexFile", IndexAttributes);
        indexfile.put("_indexName", indexName + ".ind");

        indexes.add(indexfile);
        JSONObject indname = new JSONObject();

        try {
            FileWriter fileWriter = new FileWriter("Catalog.json");
            fileWriter.write(catalog.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}