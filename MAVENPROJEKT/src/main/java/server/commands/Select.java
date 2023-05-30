package server.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import server.Parser;
import server.jacksonclasses.*;
import server.mongobongo.DataTable;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.mongodb.client.MongoClients.create;
import static com.mongodb.client.model.Filters.*;

public class Select {

    private DataTable resultTable;

    private final HashMap<String, ArrayList<String>> tableProjectionMap;
    private final HashMap<Object, ArrayList<String>> whereClauseMap;
    private final HashMap<String, ArrayList<String>> selectedColumsMap;
    private final ArrayList<DataTable> resultTables;
    private final ArrayList<String> selectedColums;
    private final String fromTable;
    private final String joinClause;

    private final ArrayList<String> joinKeys;

    private final String[] joinTables;
    private final String[] whereClause;
    private String[] groupBy;

    private final String currentDatabase;
    private final Parser parser;

    private ArrayList<Document> ArrayListIntersection(ArrayList<Document> list1, ArrayList<Document> list2) {
        ArrayList<Document> result = new ArrayList<>();
        Set<Object> idSet = new HashSet<>();
        for (Document document : list1) {
            idSet.add(document.get("_id"));
        }
        for (Document document : list2) {
            if (idSet.contains(document.get("_id"))) {
                result.add(document);
            }
        }
        return result;
    }

    public void where(String currentTable, String[] whereClause) {


        System.out.println("WHERE");
        System.out.println(" ==== Table:" + currentTable + " Where clause: " + Arrays.toString(whereClause));

        String connectionString = "mongodb://localhost:27017";
        // SELECT * FROM table
        ArrayList<String> selectedColums = new ArrayList<>();
        selectedColums.add("*");

        System.out.println("SELECT * FROM table eset");

        ArrayList<String> columnTypes = new ArrayList<>();
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
                        if (table.get_tableName().equals(currentTable)) {
                            tableExists = true;
                            myTable = table;
                            break;
                        }
                    }
                }
            }
            if (!tableExists) {
                parser.setOtherError("Table " + currentTable + " does not exist");
                return;
            }
            if (selectedColums.get(0).equals("*") && selectedColums.size() == 1) {
                selectedColums.clear();
                List<Attribute> attributes = myTable.getStructure().getAttributes();
                for (Attribute attribute : attributes) {
                    System.out.println("Attribute: " + attribute.get_attributeName());
                    selectedColums.add(attribute.get_attributeName());
                    columnTypes.add(attribute.get_type());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        // SELECT * FROM table
        if ((whereClause.length == 1 && whereClause[0].equals("")) || whereClause.length == 0) {
//            System.out.println("Where clause length: " + whereClause.length);
//            System.out.println("Where clause: " + Arrays.toString(whereClause));
//            System.out.println("Where clause 0: " + whereClause[0]);
            System.out.println("Nincs where");
            try (MongoClient mongoClient = create(connectionString)) {
                Table tableStructure = findTableInCatalog(currentTable);
                MongoDatabase db = mongoClient.getDatabase(currentDatabase);
                MongoCollection<Document> collection = db.getCollection(currentTable);
                System.out.println("Current table: " + currentTable);
                ArrayList<Document> documents = collection.find().into(new ArrayList<>());
                resultTable = new DataTable(documents, tableStructure, selectedColums, parser);
                resultTable.setTableName(currentTable);
                resultTable.setDatabaseName(currentDatabase);
                resultTables.add(resultTable);
                System.out.println("OKE WHERE MEGVOLT A " + currentTable + " TABLAHOZ");

//                JFrame frame = new JFrame("Result table");
//                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                frame.setSize(800, 600);
//                frame.setLayout(new FlowLayout());
//                frame.add(new DataTableGUI(resultTable));
//                frame.setVisible(true);


                System.out.println("Result table created");
            }
        }
        // SELECT * FROM table WHERE condition(s)
        else {
            System.out.println("Van where");
            ArrayList<ArrayList<Document>> arrayLists = new ArrayList<>();
            FindIterable<Document> filteredCollection = null;
            for (String condition : whereClause) {
                String[] conditionParts = condition.split(" ");
                String attributeName = conditionParts[0];
                String operator = conditionParts[1]; //:TODO: ide bejon akkor is ha nincs where
                String value = conditionParts[2];

                String attributeType = "";
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
                                if (table.get_tableName().equals(currentTable)) {
                                    tableExists = true;
                                    myTable = table;
                                    break;
                                }
                            }
                        }
                    }
                    if (!tableExists) {
                        parser.setOtherError("Table " + currentTable + " does not exist");
                        return;
                    }

                    boolean attributeExists = false;
                    List<Attribute> attributes = myTable.getStructure().getAttributes();
                    for (Attribute attribute : attributes) {
                        if (attribute.get_attributeName().equals(attributeName)) {
                            attributeExists = true;
                            attributeType = attribute.get_type().toLowerCase();
                            break;
                        }
                    }
                    if (!attributeExists) {
                        parser.setOtherError("Attribute " + attributeName + " does not exist");
                        return;
                    }
                    if (attributeType.equals("")) {
                        parser.setOtherError("Attribute " + attributeName + " does not exist");
                        return;
                    }

                    if (attributeType.equalsIgnoreCase("int")) {
                        try {
                            Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            parser.setOtherError("Value " + value + " is not an integer");
                            return;
                        }
                    } else if (attributeType.equalsIgnoreCase("float")) {
                        try {
                            Float.parseFloat(value);
                        } catch (NumberFormatException e) {
                            parser.setOtherError("Value " + value + " is not a float");
                            return;
                        }
                    } else {
                        if (attributeType.equalsIgnoreCase("varchar")) {
                            System.out.println("Varchar");
                        } else if (attributeType.equalsIgnoreCase("date")) {
                            System.out.println("Date");
                        } else {
                            parser.setOtherError("Attribute type " + attributeType + " is not supported");
                            return;
                        }
                    }

                    boolean isPk = false;
                    List<PrimaryKey> primaryKeys = myTable.getPrimaryKeys();
                    List<String> primaryKeyNames = primaryKeys.stream().map(PrimaryKey::getPkAttribute).toList();
                    if (primaryKeyNames.contains(attributeName)) {
                        isPk = true;
                    }

                    int attributeIndexDB = -1;
                    if (!isPk) {
                        for (Attribute attribute : attributes) {
                            if (!primaryKeyNames.contains(attribute.get_attributeName())) {
                                attributeIndexDB++;
                            }
                        }
                    }
                    boolean indexExists = false;
                    String indexAttributeName = "";
                    String indexType = "";
                    String indexName = "";
                    IndexFiles indexFiles = myTable.getIndexFiles();
                    List<IndexFile> indexFileList = indexFiles.getIndexFiles();
                    if (indexFileList == null) {
                        indexFileList = new ArrayList<>();
                        indexFiles.setIndexFiles(indexFileList);
                    }

                    for (IndexFile indexFile : indexFileList) {
                        indexAttributeName = indexFile.getIndexAttributes().get(0).getIAttribute();
                        indexName = indexFile.get_indexName();
                        if (indexAttributeName.equals(attributeName)) {
                            indexType = indexFile.get_indexType();
                            indexExists = true;
                            break;
                        }
                    }

                    try (MongoClient mongoClient = create(connectionString)) {
                        MongoDatabase db = mongoClient.getDatabase(currentDatabase);

                        // primary key esetén
                        if (isPk && indexType.equals("primary")) {
//                            System.out.println("primary key eset " + attributeName);
//                            MongoCollection<Document> tableCollection = db.getCollection(currentTable);
//
//                            Bson filter = empty();
//                            switch (operator) {
//                                case "=" -> filter = eq("_id", value);
//                                case "<" -> filter = lt("_id", value);
//                                case ">" -> filter = gt("_id", value);
//                                case "<=" -> filter = lte("_id", value);
//                                case ">=" -> filter = gte("_id", value);
//                                case "!=" -> filter = ne("_id", value);
//                                default -> {
//                                    parser.setOtherError("Operator " + operator + " is not supported");
//                                    return;
//                                }
//                            }
//                            System.out.println("Filter: " + filter);
//                            if (filter == null) {
//                                System.out.println("FILTER NULL");
//                            }
////                            use mongo cursor and sort
//
//
//                            ArrayList<Document> filteredDocuments = tableCollection.find(filter).into(new ArrayList<>());
//                            System.out.println("Filtered documents: " + filteredDocuments.toString());
//                            arrayLists.add(filteredDocuments);
                            System.out.println("primary key eset " + attributeName);
                            MongoCollection<Document> tableCollection = db.getCollection(currentTable);

                            Bson filter;
                            switch (operator) {
                                case "=" -> filter = eq("_id", value);
                                case "<" -> filter = lt("_id", value);
                                case ">" -> filter = gt("_id", value);
                                case "<=" -> filter = lte("_id", value);
                                case ">=" -> filter = gte("_id", value);
                                case "!=" -> filter = ne("_id", value);
                                default -> {
                                    parser.setOtherError("Operator " + operator + " is not supported");
                                    return;
                                }
                            }
                            System.out.println("Filter: " + filter);

                            MongoCursor<Document> cursor = tableCollection.find(filter).iterator();
                            try {
                                ArrayList<Document> filteredDocuments = new ArrayList<>();
                                while (cursor.hasNext()) {
                                    filteredDocuments.add(cursor.next());
                                }
                                System.out.println("Filtered documents: " + filteredDocuments);
                                arrayLists.add(filteredDocuments);
                            } finally {
                                cursor.close();
                            }
                        } else if (indexExists && indexType.equals("unique")) {
                            System.out.println("Van index a " + attributeName + " attribútumra");
                            MongoCollection<Document> indexCollection = db.getCollection(indexName);

                            System.out.println("Attribute type: " + attributeType);
                            Bson filter = null;
//                            switch (attributeType) {
//                                case "int" -> {
//                                    int intValue = Integer.parseInt(value);
//                                    System.out.println("int value: " + intValue);
//                                    switch (operator) {
//                                        case "=" -> filter = eq("_id", intValue);
//                                        case "<" -> filter = lt("_id", intValue);
//                                        case ">" -> filter = gt("_id", intValue);
//                                        case "<=" -> filter = lte("_id", intValue);
//                                        case ">=" -> filter = gte("_id", intValue);
//                                        case "!=" -> filter = ne("_id", intValue);
//                                        default -> {
//                                            parser.setOtherError("Operator " + operator + " is not supported");
//                                            return;
//                                        }
//                                    }
//                                }
//                                case "float" -> {
//                                    float floatValue = Float.parseFloat(value);
//                                    System.out.println("float value: " + floatValue);
//                                    switch (operator) {
//                                        case "=" -> filter = eq("_id", floatValue);
//                                        case "<" -> filter = lt("_id", floatValue);
//                                        case ">" -> filter = gt("_id", floatValue);
//                                        case "<=" -> filter = lte("_id", floatValue);
//                                        case ">=" -> filter = gte("_id", floatValue);
//                                        case "!=" -> filter = ne("_id", floatValue);
//                                        default -> {
//                                            parser.setOtherError("Operator " + operator + " is not supported");
//                                            return;
//                                        }
//                                    }
//                                }
//                                case "varchar", "date" -> {
//                                    switch (operator) {
//                                        case "=" -> filter = eq("_id", value);
//                                        case "<" -> filter = lt("_id", value);
//                                        case ">" -> filter = gt("_id", value);
//                                        case "<=" -> filter = lte("_id", value);
//                                        case ">=" -> filter = gte("_id", value);
//                                        case "!=" -> filter = ne("_id", value);
//                                        default -> {
//                                            parser.setOtherError("Operator " + operator + " is not supported");
//                                            return;
//                                        }
//                                    }
//                                }
//                            }
                            switch (operator) {
                                case "=" -> filter = eq("_id", value);
                                case "<" -> filter = lt("_id", value);
                                case ">" -> filter = gt("_id", value);
                                case "<=" -> filter = lte("_id", value);
                                case ">=" -> filter = gte("_id", value);
                                case "!=" -> filter = ne("_id", value);
                                default -> {
                                    parser.setOtherError("Operator " + operator + " is not supported");
                                    return;
                                }
                            }

                            if (filter == null) {
                                System.out.println("FILTER NULL");
                            }

                            System.out.println(indexCollection.find(filter).into(new ArrayList<>()));

                            ArrayList<Document> filteredDocuments = indexCollection.find(filter).into(new ArrayList<>());

                            ArrayList<Document> result = new ArrayList<>();
                            // using the index to get the documents from the table
                            for (Document document : filteredDocuments) {
                                String indexvalue = document.getString("indexvalue");
                                MongoCollection<Document> tableCollection = db.getCollection(currentTable);
                                Bson pkFilter = eq("_id", indexvalue);
                                Document resultDocument = tableCollection.find(pkFilter).first();
                                result.add(resultDocument);
                            }
                            arrayLists.add(result);

                            // nem indexelt esetén
                        } else {
                            System.out.println("Nincs index");
                            MongoCollection<Document> collection = db.getCollection(currentTable);
                            ArrayList<Document> filteredDocuments = new ArrayList<>();
                            for (Document document : collection.find()) {
                                String pk = document.getString("_id");
                                System.out.println("_id: " + pk);
                                String row = document.getString("row");
                                System.out.println("row: " + row);
                                String[] split = row.split("#");
                                System.out.println("split: " + Arrays.toString(split));
                                System.out.println("attributeIndexDB: " + attributeIndexDB);


                                ArrayList<Attribute> attrNames = findTableInCatalog(currentTable).zAttributumok();
                                for (int i = 0; i < attrNames.size(); i++) {
                                    System.out.println("attrNames.get(i).getNev(): " + attrNames.get(i).get_attributeName());
                                    if (attrNames.get(i).get_attributeName().equals(attributeName)) {
                                        attributeIndexDB = i;
                                        System.out.println("attributeIndexDB: " + attributeIndexDB);
                                    }
                                }


                                String attributeValue = "";
                                if (isPk) {
                                    System.out.println("PK");
                                    attributeValue = pk;
                                } else {
                                    attributeValue = split[attributeIndexDB - 1];
                                }
                                System.out.println("attributeValue: " + attributeValue);
                                switch (attributeType.toLowerCase()) {
                                    case "int" -> {
                                        int attributeValueInt = Integer.parseInt(attributeValue);
                                        int valueInt = Integer.parseInt(value);

                                        System.out.println("attributeValueInt: " + attributeValueInt * 10);
                                        System.out.println("valueInt: " + valueInt * 10);

                                        switch (operator) {
                                            case "=" -> {
                                                if (attributeValueInt == valueInt) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case "<" -> {
                                                if (attributeValueInt < valueInt) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case ">" -> {
                                                if (attributeValueInt > valueInt) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case "<=" -> {
                                                if (attributeValueInt <= valueInt) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case ">=" -> {
                                                if (attributeValueInt >= valueInt) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case "!=" -> {
                                                if (attributeValueInt != valueInt) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            default -> {
                                                parser.setOtherError("Operator " + operator + " is not supported");
                                                return;
                                            }
                                        }
                                    }
                                    case "float" -> {
                                        float attributeValueFloat = Float.parseFloat(attributeValue);
                                        float valueFloat = Float.parseFloat(value);
                                        switch (operator) {
                                            case "=" -> {
                                                if (attributeValueFloat == valueFloat) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case "<" -> {
                                                if (attributeValueFloat < valueFloat) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case ">" -> {
                                                if (attributeValueFloat > valueFloat) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case "<=" -> {
                                                if (attributeValueFloat <= valueFloat) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case ">=" -> {
                                                if (attributeValueFloat >= valueFloat) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case "!=" -> {
                                                if (attributeValueFloat != valueFloat) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            default -> {
                                                parser.setOtherError("Operator " + operator + " is not supported");
                                                return;
                                            }
                                        }
                                    }
                                    case "varchar", "date" -> {
                                        if ((value.charAt(0) == '\'' && value.charAt(value.length() - 1) == '\'') || (value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"')) {
                                            value = value.substring(1, value.length() - 1);
                                        }
                                        switch (operator) {
                                            case "=" -> {
                                                if (attributeValue.equals(value)) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case "<" -> {
                                                if (attributeValue.compareTo(value) < 0) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case ">" -> {
                                                if (attributeValue.compareTo(value) > 0) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case "<=" -> {
                                                if (attributeValue.compareTo(value) <= 0) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case ">=" -> {
                                                if (attributeValue.compareTo(value) >= 0) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            case "!=" -> {
                                                if (!attributeValue.equals(value)) {
                                                    filteredDocuments.add(document);
                                                }
                                            }
                                            default -> {
                                                parser.setOtherError("Operator " + operator + " is not supported");
                                                return;
                                            }
                                        }
                                    }
                                    default -> {
                                        parser.setOtherError("Type " + attributeType + " is not supported");
                                        return;
                                    }
                                }
                            }
                            arrayLists.add(filteredDocuments);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            ArrayList<Document> result = arrayLists.get(0);
            System.out.println("arrayLists.size() : " + arrayLists.size());
            for (int i = 1; i < arrayLists.size(); i++) {
                result = ArrayListIntersection(result, arrayLists.get(i));
            }

            System.out.print("Selected columns: ");
            for (String selectedColumn : selectedColums) {
                System.out.print(selectedColumn + " ");
            }
            System.out.println();
            System.out.println("Result: " + result.size() + " rows");
            for (Document document : result) {
                String pk = document.getString("_id");
                String row = document.getString("row").replace("#", " ");
                System.out.println(pk + " " + row);
            }
            Table tableStructure = findTableInCatalog(currentTable);
            resultTable = new DataTable(result, tableStructure, selectedColums, parser);
            resultTable.setTableName(currentTable);
            resultTable.setDatabaseName(currentDatabase);
            resultTables.add(resultTable);
            System.out.println("OKE WHERE MEGVOLT A " + currentTable + " TABLAHOZ");
        }
    }


    public void processProjection(ArrayList<String> selectedColums) {

        if (selectedColums.contains("*")) {
            ArrayList<String> columns = tableProjectionMap.get(fromTable);
            Table myTable = findTableInCatalog(fromTable);
            List<Attribute> attributes = myTable.getStructure().getAttributes();

            for (Attribute attribute : attributes) {
                columns.add(attribute.get_attributeName());
            }

            if (!(joinTables.length == 1 && joinTables[0].equals(""))) {
                for (String joinTable : joinTables) {
                    ArrayList<String> joinColumns = tableProjectionMap.get(joinTable);
                    Table table = findTableInCatalog(joinTable);
                    List<Attribute> attributes1 = table.getStructure().getAttributes();
                    for (Attribute attribute : attributes1) {
                        joinColumns.add(attribute.get_attributeName());
                    }
                }
            }

            return;
        }

        for (String column : selectedColums) {

            try {

                if (column.contains(".")) {
                    String[] split = column.split("\\.");
                    String tableName = split[0];
                    String columnName = split[1];
                    ArrayList<String> columns = tableProjectionMap.get(tableName);
                    columns.add(columnName);
                } else {
                    ArrayList<String> columns = tableProjectionMap.get(fromTable);
                    columns.add(column);
                }


            } catch (Exception e) {
                e.printStackTrace();
                parser.setOtherError("Column " + column + " is not found");
                return;
            }

        }
    }

    public void fiterSelectedTables() {

        if (selectedColums.contains("*")) {
            return;
        }

        ArrayList<String> tmpS = new ArrayList<>();

        for (String c : selectedColums) {
            if (c.contains(".")) {
                String[] split = c.split("\\.");
                c = split[1];
                System.out.println("S table: " + c);
            }

            tmpS.add(c);
        }

        DataTable tmp = resultTables.get(0);
        for (String dcm : tmp.getColumnsName()) {
//            if dcm contains . then split and check if table is in selected tables

            if (!tmpS.contains(dcm)) {
                System.out.println("Removing column: " + dcm);
                tmp.removeColumn(dcm);
            }

        }
    }

    public Select(String currentDatabase, String text, Parser parser) {
        String connectionString = "mongodb://localhost:27017";
        this.parser = parser;
        whereClauseMap = new HashMap<Object, ArrayList<String>>();
        tableProjectionMap = new HashMap<>();
        selectedColumsMap = new HashMap<>();
        joinKeys = new ArrayList<>();
        this.currentDatabase = currentDatabase;
        resultTables = new ArrayList<>();
        selectedColums = selectedColums(text);
        System.out.println(" --- --  Selected columns: ");
        for (String an : selectedColums) {
            System.out.print(an + " ");
        }
        System.out.println();
        fromTable = fromTables(text);
        System.out.println("Table: " + fromTable);
        ArrayList<String> columns = new ArrayList<>();
        tableProjectionMap.put(fromTable.trim(), columns);

        ArrayList<String> whereConds = new ArrayList<>();
        whereClauseMap.put(fromTable, whereConds);


        System.out.println();
        joinClause = joinClause(text);
        System.out.println("Join clause: ");
        System.out.println(joinClause);
        System.out.println();

        joinTables = joinTables(joinClause);
        System.out.println("Join tables: ");
        for (String an : joinTables) {
            System.out.print(an + " ");
            ArrayList<String> columns2 = new ArrayList<>();
            ArrayList<String> columns3 = new ArrayList<>();

            tableProjectionMap.put(an.trim(), columns2);
            whereClauseMap.put(an.trim(), columns3);
        }

        whereClause = whereClause(text);
        System.out.println("Where clause: ");
        for (String an : whereClause) {
            System.out.print("|" + an + "| ");
        }
        System.out.println();


//        addKeysToProjection(joinClause);
        System.out.println("Table projection map: ");
        for (ArrayList<String> columns1 : tableProjectionMap.values()) {
            for (String column : columns1) {
                System.out.print(column + " ");
            }
            System.out.println();
        }


//              tableProjectionMap.get(fromTable)
        String[] tmp = new String[whereClauseMap.get(fromTable).size()];
        for (int i = 0; i < whereClauseMap.get(fromTable).size(); i++) {
            tmp[i] = whereClauseMap.get(fromTable).get(i);
            System.out.println(" --- -- - -- -- --  WHERE CLAUSE: " + tmp[i] + " Table: " + fromTable);
        }
        where(fromTable, tmp);

//        where(fromTable, whereClause);


        if (joinTables.length == 1 && joinTables[0].equals("")) {
            System.out.println("Join tables is empty");
            return;
        }
        for (int i = 0; i < joinTables.length; i++) {
            String[] empty = new String[whereClauseMap.get(joinTables[i]).size()];

            for (int j = 0; j < whereClauseMap.get(joinTables[i]).size(); j++) {
                empty[j] = whereClauseMap.get(joinTables[i]).get(j);
                System.out.println(" --- -- - -- -- --  WHERE CLAUSE: " + empty[j] + " Table: " + joinTables[i]);
            }

//            System.out.println("WHERE JOIN TABLE: " + joinTables[i]);
            where(joinTables[i], empty);
        }


        System.out.println("Join clause is not empty");
        Join joinRes = new Join(resultTables, joinClause, joinKeys, parser);
        resultTables.set(0, joinRes.getResultTable());
    }

    private String[] joinTables(String joinClause) {
        ArrayList<String> ans = new ArrayList<>();
        String[] joinClauseSplit = joinClause.split(" ");
        ans.add(joinClauseSplit[0]);
        for (int i = 1; i < joinClauseSplit.length; i++) {
            if (joinClauseSplit[i].equals("JOIN")) {
                ans.add(joinClauseSplit[i + 1]);
            }
        }
        String[] ansArray = new String[ans.size()];
        for (int i = 0; i < ans.size(); i++) {
            ansArray[i] = ans.get(i);
        }
        return ansArray;
    }

    public String betweenString(String text, String start, String end) {
        String ans = "";
        String textUpper = text.toUpperCase();
        int startindex = textUpper.indexOf(start.toUpperCase());
        int endindex = textUpper.indexOf(end.toUpperCase());

        if (startindex == -1 && endindex == -1) {
            return ans;
        }

        if (startindex == -1) {
            startindex = 0;
        }
        if (endindex == -1) {
            endindex = text.length();
        }
        System.out.println("- start: " + start + " startindex: " + startindex);
        System.out.println("- end: " + end + " endindex: " + endindex);

        ans = text.substring(startindex + start.length(), endindex);
        System.out.println("- ans:" + ans);
        return ans;
    }

    public ArrayList<String> selectedColums(String text) {

//        SELECT ans FROM
        ArrayList<String> ans = new ArrayList<>();
        String data = betweenString(text, "SELECT", "FROM");
        String[] split = data.split(",");
        for (String s : split) {
            ans.add(s.trim());
        }
        return ans;
    }

    public Table findTableInCatalog(String tableName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Databases databases = objectMapper.readValue(new File("Catalog.json"), Databases.class);
            List<Database> databaseList = databases.getDatabases();
            for (Database db : databaseList) {
                if (db.get_dataBaseName().equals(this.currentDatabase)) {
                    List<Table> tableList = db.getTables();
                    for (Table table : tableList) {
                        if (table.get_tableName().equals(tableName)) {
                            return table;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String fromTables(String text) {
//        FROM ans JOIN vagy FROM WHERE
        if (text.contains("INNER JOIN")) {
            String data = betweenString(text, "FROM", "INNER JOIN");
            return data.trim();
        } else if (text.contains("WHERE")) {
            String data = betweenString(text, "FROM", "WHERE");
            return data.trim();
        } else {
            String data = betweenString(text, "FROM", "ORDER BY");
            return data.trim();
        }
    }

    public String joinClause(String text) {
//        INNER JOIN ans WHERE
        if (!text.contains("INNER JOIN")) {
            return "";
        }
        String data = betweenString(text, "INNER JOIN", "WHERE");
//
        return data.trim();
    }

    public String[] whereClause(String text) {
        String[] ans = new String[0];
        String data = betweenString(text, "WHERE", "ORDER BY");
        if (text.contains("AND")) {
            ans = data.split("AND");

            for (int i = 0; i < ans.length; i++) {
                ans[i] = ans[i].trim();
            }
        } else {
            ans = new String[1];
            ans[0] = data.trim();

        }

        ArrayList<String> chars = new ArrayList<>();
        chars.add("=");
        chars.add(">");
        chars.add("<");
        chars.add(">=");
        chars.add("<=");


        for (String an : ans) {
//           table.column = table.column

            for (String elv : chars) {
                if (an.contains(elv)) {
                    System.out.println("AANNN= "+ an);
                    String cond1 = an.split(elv)[0].trim();
                    String cond2 = an.split(elv)[1].trim();
                    String sTable = "";
                    String sColumn = "";
                    String other = "";
                    if (cond1.contains(".")) {
                        sTable = cond1.split("\\.")[0].trim();
                        sColumn = cond1.split("\\.")[1].trim();
                        other = cond2;
                    }
                    if (cond2.contains(".")) {
                        sTable = cond2.split("\\.")[0].trim();
                        sColumn = cond2.split("\\.")[1].trim();
                        other = cond1;
                    }


                    if (sTable.equals("")) {
                        sTable = this.fromTable;
                        ArrayList<String> wheres1 = whereClauseMap.get(sTable);
                        wheres1.add(an);
                        System.out.println("()()()()()()()()()()()()()()WHERE CLAUSE: " + an + " in table: " + sTable);
                        continue;
                    }

                    ArrayList<String> wheres = whereClauseMap.get(sTable);
                    wheres.add(sColumn + " " + elv + " " + other);
                    System.out.println("()()()()()()()()()()()()()()WHERE CLAUSE: " + sColumn + " = " + other + " in table: " + sTable);
                }
            }

            for (ArrayList<String> ss : whereClauseMap.values()) {

                for (String s : ss) {
                    System.out.println("WHERE CLAUSE: " + s);
                }
            }
        }


        return ans;
    }

    public DataTable getResultTable() {
        try {
            fiterSelectedTables();
            System.out.println("Result table: " + resultTables.get(0).getTableName());


            return new DataTable(resultTables.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            parser.setOtherError("Error in where clause");
            parser.setParserError(true);
            System.out.println(e.getMessage());
            return new DataTable();
        }

    }
}
