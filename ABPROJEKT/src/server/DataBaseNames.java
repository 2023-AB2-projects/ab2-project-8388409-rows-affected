package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import server.jacksonclasses.Database;
import server.jacksonclasses.Databases;

import java.io.File;
import java.util.ArrayList;

public class DataBaseNames {

    private ArrayList<String> databaseNames;

    public DataBaseNames() {
        ObjectMapper objectMapper = new ObjectMapper();
        databaseNames = new ArrayList<>();
        databaseNames.add("__databases__");
        try{
            Databases databases = objectMapper.readValue(new File("Catalog.json"), Databases.class);
            Database myDatabase = null;
            for (Database database : databases.getDatabases()) {
                databaseNames.add(database.get_dataBaseName());
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        databaseNames.add("__databases_end__");
    }
    public ArrayList<String> getDatabaseNames() {
        return databaseNames;
    }

}
