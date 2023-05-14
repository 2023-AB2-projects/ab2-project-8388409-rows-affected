package server.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;
import server.Parser;
import server.jacksonclasses.*;
import server.mongobongo.DataTable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.MongoClients.create;
import static com.mongodb.client.model.Filters.*;

public class Select {

    private DataTable resultTable;
    private final String[] selectedColums;
    private final String fromTable;
    private final String[] joinClause;
    private final String[] whereClause;
    private String[] groupBy;

    private final String database;
    private final Parser parser;

    public Select(String currentDatabase, String text, Parser parser) {
        String connectionString = "mongodb://localhost:27017";
        this.parser = parser;
        database = currentDatabase;

        selectedColums = selectedColums(text);
        System.out.println("Selected columns: ");
        for (String an : selectedColums) {
            System.out.print(an + " ");
        }
        System.out.println();
        fromTable = fromTables(text);
        System.out.println("Table: " + fromTable);

        System.out.println();
        joinClause = joinClause(text);
        System.out.println("Join clause: ");
        for (String an : joinClause) {
            System.out.print(an + " ");
        }
        System.out.println();

        whereClause = whereClause(text);
        System.out.println("Where clause: ");
        for (String an : whereClause) {
            System.out.print(an + " ");
        }
        System.out.println();


        if (selectedColums[0].equals("*") && selectedColums.length == 1) {
            if (whereClause.length == 0) {
                resultTable = new DataTable(database, fromTable);
            } else {
                FindIterable<Document> filteredCollection = null;
                for (String condition : whereClause) {
                    String[] conditionParts = condition.split(" ");
                    String attributeName = conditionParts[0];
                    String operator = conditionParts[1];
                    String value = conditionParts[2];

                    String attributeType = "";
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
                            if (database.get_dataBaseName().equals(currentDatabase)) {
                                databaseExists = true;
                                break;
                            }
                        }
                        if (!databaseExists) {
                            parser.setOtherError("Database " + currentDatabase + " does not exist");
                            return;
                        }
                        Table myTable = null;
                        boolean tableExists = false;
                        for (Database database : databaseList) {
                            if (database.get_dataBaseName().equals(currentDatabase)) {
                                List<Table> tableList = database.getTables();
                                if (tableList == null) {
                                    parser.setOtherError("There are no tables in the database " + database);
                                    return;
                                }
                                for (Table table : tableList) {
                                    if (table.get_tableName().equals(fromTable)) {
                                        tableExists = true;
                                        myTable = table;
                                        break;
                                    }
                                }
                            }
                        }
                        if (!tableExists) {
                            parser.setOtherError("Table " + fromTable + " does not exist");
                            return;
                        }

                        List<Attribute> attributes = myTable.getStructure().getAttributes();
                        for (Attribute attribute : attributes) {
                            if (attribute.get_attributeName().equals(attributeName)) {
                                attributeType = attribute.get_type();
                                break;
                            }
                        }
                        if (attributeType.equals("")) {
                            parser.setOtherError("Attribute " + attributeName + " does not exist");
                            return;
                        }

                        if (attributeType.equals("int")) {
                            try {
                                Integer.parseInt(value);
                            } catch (NumberFormatException e) {
                                parser.setOtherError("Value " + value + " is not an integer");
                                return;
                            }
                        } else if (attributeType.equals("float")) {
                            try {
                                Float.parseFloat(value);
                            } catch (NumberFormatException e) {
                                parser.setOtherError("Value " + value + " is not a float");
                                return;
                            }
                        } else {
                            boolean b = value.charAt(0) != '"' || value.charAt(value.length() - 1) != '"';
                            if (attributeType.equals("string")) {
                                if (b) {
                                    parser.setOtherError("Value " + value + " is not a string");
                                    return;
                                }
                            } else if (attributeType.equals("date")) {
                                if (b) {
                                    parser.setOtherError("Value " + value + " is not a date");
                                    return;
                                }
                            } else {
                                parser.setOtherError("Attribute type " + attributeType + " is not supported");
                                return;
                            }
                        }

                        boolean indexExists = false;
                        String indexName = "";
                        IndexFiles indexFiles = myTable.getIndexFiles();
                        List<IndexFile> indexFileList = indexFiles.getIndexFiles();
                        if (indexFileList == null) {
                            indexFileList = new ArrayList<>();
                            indexFiles.setIndexFiles(indexFileList);
                        }

                        for (IndexFile indexFile : indexFileList) {
                            indexName = indexFile.get_indexName();
                            if (indexName.equals(attributeName)) {
                                indexExists = true;
                                break;
                            }
                            String indexType = indexFile.get_indexType();
                            List<IndexAttribute> indexAttributes = indexFile.getIndexAttributes();
                            String indexAttribute = indexAttributes.get(0).getIAttribute();
                        }

                        try (MongoClient mongoClient = create(connectionString)) {
                            MongoDatabase db = mongoClient.getDatabase(currentDatabase);
                            MongoCollection<Document> indexCollection = db.getCollection(indexName);

                            Bson filter = null;


                            if (attributeType == "int") {
                                int filterValue = Integer.parseInt(value);
                                switch (operator) {
                                    case "=" -> filter = eq(attributeName, filterValue);
                                    case "<" -> filter = lt(attributeName, filterValue);
                                    case ">" -> filter = gt(attributeName, filterValue);
                                    case "<=" -> filter = lte(attributeName, filterValue);
                                    case ">=" -> filter = gte(attributeName, filterValue);
                                    case "!=" -> filter = ne(attributeName, filterValue);
                                    default -> {
                                        parser.setOtherError("Operator " + operator + " is not supported");
                                        return;
                                    }
                                }
                            } else if (attributeType == "float") {
                                float filterValue = Float.parseFloat(value);
                                switch (operator) {
                                    case "=" -> filter = eq(attributeName, filterValue);
                                    case "<" -> filter = lt(attributeName, filterValue);
                                    case ">" -> filter = gt(attributeName, filterValue);
                                    case "<=" -> filter = lte(attributeName, filterValue);
                                    case ">=" -> filter = gte(attributeName, filterValue);
                                    case "!=" -> filter = ne(attributeName, filterValue);
                                    default -> {
                                        parser.setOtherError("Operator " + operator + " is not supported");
                                        return;
                                    }
                                }
                            } else {
                                switch (operator) {
                                    case "=" -> filter = eq(attributeName, value);
                                    case "<" -> filter = lt(attributeName, value);
                                    case ">" -> filter = gt(attributeName, value);
                                    case "<=" -> filter = lte(attributeName, value);
                                    case ">=" -> filter = gte(attributeName, value);
                                    case "!=" -> filter = ne(attributeName, value);
                                    default -> {
                                        parser.setOtherError("Operator " + operator + " is not supported");
                                        return;
                                    }
                                }
                            }

                            filteredCollection = indexCollection.find(filter);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


//                try (MongoClient mongoClient = MongoClients.create(connectionString)) {
//                    MongoDatabase db = mongoClient.getDatabase(database);
//
//                    // check if there is an
//                }
                }
            }
        }

    }

    public String betweenString(String text, String start, String end) {
        String ans = "";
        String textUpper = text.toUpperCase();
        int startindex = textUpper.indexOf(start);
        int endindex = textUpper.indexOf(end);

        if (startindex == -1 && endindex == -1) {
            return ans;
        }

        if (startindex == -1) {
            startindex = 0;
        }
        if (endindex == -1) {
            endindex = text.length();
        }


        ans = text.substring(startindex + start.length(), endindex);
//        System.out.println("ans =" + ans);
        return ans;
    }

    public String[] selectedColums(String text) {

//        SELECT ans FROM

        String data = betweenString(text, "SELECT", "FROM");
        String[] ans = data.split(",");
        return ans;
    }

    public String fromTables(String text) {
//        FROM ans JOIN vagy FROM WHERE
        String data = betweenString(text, "FROM", "INNER JOIN");
        return data.trim();
    }

    public String[] joinClause(String text) {
//        INNER JOIN ans WHERE
        String data = betweenString(text, "INNER JOIN", "WHERE");
        String[] ans = data.split(",");
        return ans;
    }

    public String[] whereClause(String text) {

        String data = betweenString(text, "WHERE", "ORDER BY");
        String[] ans = data.split("AND");
        return ans;
    }

    public ArrayList<DataTable> getBaseTables() {

        ArrayList<DataTable> tables = new ArrayList<>();
        JFrame frame = new JFrame("Select");

        System.out.println("\nDatabase: |" + database + "| Table: |" + fromTable + "|\n");
        tables.add(new DataTable(database, fromTable, parser));
        resultTable = tables.get(0);
        JFrame jf = new JFrame();
        jf.setSize(400, 300);
        jf.setLayout(new FlowLayout());
        jf.setBackground(new Color(203, 141, 141));
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        System.out.println("Tables: ");

        jf.setVisible(true);
        return tables;

    }

    public DataTable getResultTable() {
        return resultTable;
    }
}
