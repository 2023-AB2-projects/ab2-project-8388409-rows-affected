package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class CreateTable {
    public CreateTable(String tableName, String databaseName) {
        JSONObject catalog;
        try {
            Reader reader = new FileReader("Catalog.json");
            JSONParser jsonParser = new JSONParser();
            catalog = (JSONObject) jsonParser.parse(reader);
            reader.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

    }
}
