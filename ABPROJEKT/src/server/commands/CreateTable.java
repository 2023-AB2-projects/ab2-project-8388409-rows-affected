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
        contents = contents.trim();

        if (!contents.contains("(") || !contents.contains(")")
                || contents.indexOf('(') > contents.indexOf(')')) {
            parser.setOtherError("Invalid syntax");
            return;
        }

        // Check if the syntax is correct (...)
        if (contents.charAt(0) != '(' || contents.charAt(contents.length() - 1) != ')') {
            parser.setOtherError("Invalid syntax");
            return;
        }

        contents = contents.substring(1, contents.length() - 1);
        contents = contents.trim();

        System.out.println("Contents: " + contents);

        String[] attr = contents.split(",");

        for (int i = 0; i < attr.length; i++) {
            attr[i] = attr[i].trim();
            String[] splattr = attr[i].split(" ");
            String name = splattr[0];
            String type = splattr[1];
            String other = ""; // foregin key, primary key, etc.
            for (int j = 2; j < splattr.length; j++) {
                other += splattr[j] + " ";
            }
            other = other.trim();

            // check if other is valid
            if (other.toUpperCase().contains("UNIQUE") || other.toUpperCase().contains("PRIMARY KEY") || other.toUpperCase().contains("FOREIGN KEY")) {
                if (other.toUpperCase().contains("UNIQUE") && other.toUpperCase().contains("PRIMARY KEY")) {
                    parser.setOtherError("Invalid other");
                    return;
                }
                if (other.toUpperCase().contains("FOREIGN KEY")) {
                    if (!other.toUpperCase().contains("REFERENCES")) {
                        parser.setOtherError("Invalid other");
                        return;
                    }
                }
            } else if (!other.equals("")) {
                parser.setOtherError("Invalid other");
                return;

            }

            // check if type is valid
            if (!isAcceptedType(type.toLowerCase())) {
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
        JSONArray primaryKeys = new JSONArray();
        JSONArray foreignKeys = new JSONArray();
        JSONArray uniqueKeys = new JSONArray();

        for (int i = 0; i < attr.length; i++) {
            attr[i] = attr[i].trim();
            System.out.println("attr["+i+"]: " + attr[i]);
            String[] splattr = attr[i].split(" ");
            String name = splattr[0];
            String type = splattr[1];
            String other = ""; // foregin key, primary key, etc.
            for (int j = 2; j < splattr.length; j++) {
                other += splattr[j] + " ";
            }
            other = other.trim();

            System.out.println("other: " + other);

            if (other.toUpperCase().contains("PRIMARY KEY")) {
                JSONObject primaryKey = new JSONObject();
                primaryKey.put("pkAttribute", name);
                primaryKeys.add(primaryKey);
            } else if (other.toUpperCase().contains("FOREIGN KEY")) {
                //other foreign key stucture: FOREIGN KEY REFERENCES Persons(PersonID)

                // if other doesn't contain () then it's invalid
                if (!other.contains("(") || !other.contains(")")) {
                    parser.setOtherError("Invalid syntax: ...tablname --> ( or ) is missing");
                    return;
                }

                String[] spl = other.split(" ");
                for (int s=0; s<spl.length; s++) {
                    System.out.println("spl["+s+"]: " + spl[s]);
                }

                // check if the structure is correct
                if (spl.length != 4) {
                    parser.setOtherError("Invalid other: incorrect foreign key syntax");
                    return;
                }

                // check if () is present
                if (!spl[3].contains("(") || !spl[3].contains(")")) {
                    parser.setOtherError("Invalid other: () is missing");
                    return;
                }

                String refTableandattr = spl[3];

                String refTable = refTableandattr.substring(0, refTableandattr.indexOf("("));
                String refAttr = refTableandattr.substring(refTableandattr.indexOf("(") + 1, refTableandattr.indexOf(")"));

                // check if refTable exists
                boolean refTableExists = false;
                for (int j = 0; j < tables.size(); j++) {
                    JSONObject table2 = (JSONObject) tables.get(j);
                    JSONObject tableContents2 = (JSONObject) table2.get("Table");
                    String tableNameInCatalog = (String) tableContents2.get("_tableName");
                    if (tableNameInCatalog.equals(refTable)) {
                        refTableExists = true;
                        break;
                    }
                }
                if (!refTableExists) {
                    parser.setOtherError("Referenced table does not exist");
                    return;
                }

                // check if refAttr exists in refTable
                boolean refAttrExists = false;
                for (int j = 0; j < tables.size(); j++) {
                    JSONObject table2 = (JSONObject) tables.get(j);
                    JSONObject tableContents2 = (JSONObject) table2.get("Table");
                    String tableNameInCatalog = (String) tableContents2.get("_tableName");
                    if (tableNameInCatalog.equals(refTable)) {
                        JSONObject structure2 = (JSONObject) tableContents2.get("Structure");
                        JSONArray attributes2 = (JSONArray) structure2.get("Attributes");
                        for (int k = 0; k < attributes2.size(); k++) {
                            JSONObject attribute2 = (JSONObject) attributes2.get(k);
                            String attributeName = (String) attribute2.get("_attributeName");
                            if (attributeName.equals(refAttr)) {
                                refAttrExists = true;
                                break;
                            }
                        }
                        break;
                    }
                }

                JSONObject foreignKey = new JSONObject();
                foreignKey.put("fkAttribute", name);
                foreignKey.put("fkRefTable", refTable);
                foreignKey.put("fkRefAttribute", refAttr);
                foreignKeys.add(foreignKey);
            } else if (other.toUpperCase().contains("UNIQUE")) {
                JSONObject uniqueKey = new JSONObject();
                uniqueKey.put("ukAttribute", name);
                uniqueKeys.add(uniqueKey);
            }

            JSONObject attribute = new JSONObject();
            attribute.put("_attributeName", name);
            attribute.put("_type", type);
            attribute.put("_isnull", "0");
            attributes.add(attribute);
        }

        tableContents.put("Structure", structure);
        tableContents.put("PrimaryKeys", primaryKeys);
        tableContents.put("ForeignKeys", foreignKeys);
        tableContents.put("uniqueKeys", uniqueKeys);

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
