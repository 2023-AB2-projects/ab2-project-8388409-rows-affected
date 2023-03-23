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
    private boolean isAcceptedType(String type) {
        String[] acceptedTypes = {"int", "float", "bit", "date", "datetime", "varchar"};
        for (String acceptedType : acceptedTypes) {
            if (type.equals(acceptedType)) {
                return true;
            }
        }
        return false;
    }
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

        // Get the database
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

        // Check if table already exists
        for (int i = 0; i < tables.size(); i++) {
            JSONObject table = (JSONObject) tables.get(i);
            JSONObject tableContents = (JSONObject) table.get("Table");
            String tableNameInCatalog = (String) tableContents.get("_tableName");
            if (tableNameInCatalog.equals(tableName)) {
                parser.setOtherError("Table already exists");
                return;
            }
        }

        contents = contents.replace("\n", "");

        System.out.println("contents: "+ contents);

        // Check if the syntax is correct (...)
        if (contents.charAt(0) != '(' || contents.charAt(contents.length() - 1) != ')') {
            parser.setOtherError("Invalid syntax");
            return;
        }

        contents = contents.substring(1, contents.length() - 1);
        String[] attr = contents.split(",");

        String[] acceptedTypes = {"int", "float", "bit", "date", "datetime", "varchar"};

        for (int i = 0; i < attr.length; i++) {
            attr[i] = attr[i].trim();
            String[] splattr = attr[i].split(" ");
            String name = splattr[0];
            String type = splattr[1];

            // check if type is valid
            if (!isAcceptedType(type)) {
                parser.setOtherError("Invalid type");
                return;
            }

            // check for duplicates
            for (int j = i + 1; j < attr.length; j++) {
                String[] splattr2 = attr[j].split(" ");
                String name2 = splattr2[0];
                if (name.equals(name2)) {
                    parser.setOtherError("Duplicate attribute name");
                    return;
                }
            }
        }

        JSONObject table = new JSONObject();
        JSONObject tableContents = new JSONObject();
        table.put("Table", tableContents);


        JSONObject structure = new JSONObject();
        JSONArray attributes = new JSONArray();
        structure.put("Attributes", attributes);

        for (int i = 0; i < attr.length; i++) {
            attr[i] = attr[i].trim();
            String[] splattr = attr[i].split(" ");
            String name = splattr[0];
            String type = splattr[1];

            JSONObject attribute = new JSONObject();
            attribute.put("_attributeName", name);
            attribute.put("_type", type);
            attribute.put("_length", name.length());
            attribute.put("_isnull", "0");
            attributes.add(attribute);
        }





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
