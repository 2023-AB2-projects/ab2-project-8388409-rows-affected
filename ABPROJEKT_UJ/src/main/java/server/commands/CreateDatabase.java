package server.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.Parser;

import java.io.*;

public class CreateDatabase {
    public CreateDatabase(String databaseName, Parser parser) {
        JSONObject catalog;
        try {
            Reader reader = new FileReader("Catalog.json");
            JSONParser jsonParser = new JSONParser();
            catalog = (JSONObject) jsonParser.parse(reader);
            reader.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        JSONArray databases = (JSONArray) catalog.get("Databases");

        for (int i = 0; i < databases.size(); i++) {
            JSONObject database = (JSONObject) databases.get(i);
            JSONObject databaseContents = (JSONObject) database.get("Database");
            String databaseNameInCatalog = (String) databaseContents.get("_dataBaseName");
            if (databaseNameInCatalog.equals(databaseName)) {
                parser.setParserError(true);
                try {
                    FileWriter fileWriter = new FileWriter("Catalog.json");
                    fileWriter.write(catalog.toJSONString());
                    fileWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }

//        databases.add(catalog.get("Databases"));

        databases = (JSONArray) catalog.get("Databases");

        JSONObject database = new JSONObject();
        JSONArray tables = new JSONArray();
        JSONObject databasecontents = new JSONObject();
        database.put("Database", databasecontents);
        databasecontents.put("Tables", tables);
        databasecontents.put("_dataBaseName", databaseName);


        databases.add(database);
        catalog.put("Databases", databases);
        try {
            FileWriter fileWriter = new FileWriter("Catalog.json");
            fileWriter.write(catalog.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
