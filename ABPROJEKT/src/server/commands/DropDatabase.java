package server.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.jacksonclasses.Databases;

import java.io.*;

public class DropDatabase {
    public DropDatabase(String databaseName) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Databases databases = objectMapper.readValue(new File("Catalog.json"), Databases.class);
            for (int i = 0; i < databases.getDatabases().size(); i++) {
                if (databases.getDatabases().get(i).get_dataBaseName().equals(databaseName)) {
                    databases.getDatabases().remove(i);
                    break;
                }
            }

            objectMapper.writeValue(new File("Catalog.json"), databases);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
