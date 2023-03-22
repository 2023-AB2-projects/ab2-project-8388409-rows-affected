package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

public class CreateTable {
    public CreateTable(String tableName, String databaseName, String contents, Parser parser) {
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

        for (int i = 0; i < databases.size(); i++) {
            JSONObject database = (JSONObject) databases.get(i);
            JSONObject databaseContents = (JSONObject) database.get("Database");
            String databaseNameInCatalog = (String) databaseContents.get("_dataBaseName");
            if (databaseNameInCatalog.equals(databaseName)) {
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

        for (int i = 0; i < tables.size(); i++) {
            JSONObject table = (JSONObject) tables.get(i);
            JSONObject tableContents = (JSONObject) table.get("Table");
            String tableNameInCatalog = (String) tableContents.get("_tableName");
            if (tableNameInCatalog.equals(tableName)) {
                parser.setOtherError("Table already exists");
                return;
            }
        }

        JSONObject table = new JSONObject();
        JSONObject tableContents = new JSONObject();
        table.put("Table", tableContents);

        JSONObject structure = new JSONObject();
        JSONArray attributes = new JSONArray();
        structure.put("Attributes", attributes);
        tableContents.put("Structure", structure);

        JSONObject primaryKey = new JSONObject();
        tableContents.put("PrimaryKey", primaryKey);

        JSONObject foreignKeys = new JSONObject();
        tableContents.put("ForeignKeys", foreignKeys);

        JSONObject IndexFiles = new JSONObject();
        tableContents.put("IndexFiles", IndexFiles);

        tableContents.put("_tableName", tableName);
        tableContents.put("_fileName", tableName + ".bin");
        tableContents.put("_rowLength", "0");

        tables.add(table);



        try {
            FileWriter fileWriter = new FileWriter("Catalog.json");
            fileWriter.write(catalog.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
