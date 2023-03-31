package server.commands;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import server.Parser;
import server.jacksonclasses.Database;
import server.jacksonclasses.Databases;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CreateDatabase {
    public CreateDatabase(String databaseName, Parser parser) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Databases databases = objectMapper.readValue(new File("Catalog.json"), Databases.class);
            List<Database> databaseList = databases.getDatabases();

            if (databaseList == null) {
                databaseList = new ArrayList<>();
            }

            boolean databaseExists = false;
            for (Database database : databaseList) {
                if (database.get_dataBaseName().equals(databaseName)) {
                    databaseExists = true;
                    break;
                }
            }
            if (!databaseExists) {
                Database database = new Database(databaseName, new ArrayList<>());
                databaseList.add(database);
                databases.setDatabases(databaseList);
                objectMapper.writeValue(new File("Catalog.json"), databases);
            } else {
                parser.setParserError(true);
            }
        } catch (StreamReadException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }
}
