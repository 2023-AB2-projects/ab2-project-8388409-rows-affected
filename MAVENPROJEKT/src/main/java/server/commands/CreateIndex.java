package server.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.simple.JSONArray;
import server.Parser;
import server.jacksonclasses.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.mongodb.client.MongoClients.create;

public class CreateIndex {
    public CreateIndex(String indexName, String tableName, String contents, String currentDatabase, Parser parser) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Databases databases = objectMapper.readValue(new File("Catalog.json"), Databases.class);
            Database myDatabase = null;
            for (int i = 0; i < databases.getDatabases().size(); i++) {
                if (databases.getDatabases().get(i).get_dataBaseName().equals(currentDatabase)) {
                    myDatabase = databases.getDatabases().get(i);
                    break;
                }
            }
            if (myDatabase == null) {
                parser.setOtherError("Database does not exist");
                return;
            }
            Table myTable = null;
            for (int i = 0; i < myDatabase.getTables().size(); i++) {
                if (myDatabase.getTables().get(i).get_tableName().equals(tableName)) {
                    myTable = myDatabase.getTables().get(i);
                    break;
                }
            }
            if (myTable == null) {
                parser.setOtherError("Table does not exist");
                return;
            }

            int index = -1;
            for (int i = 0; i < myTable.getStructure().getAttributes().size(); i++) {
                if (myTable.getStructure().getAttributes().get(i).get_attributeName().equals(contents)) {
                    index = i;
                    break;
                }
            }
            if (index == -1) {
                parser.setOtherError("Attribute does not exist");
                return;
            }

            // check if contents is primary key
            boolean isPrimaryKey = false;
            List<PrimaryKey> primaryKeys = myTable.getPrimaryKeys();
            for (int i = 0; i < primaryKeys.size(); i++) {
                if (primaryKeys.get(i).getPkAttribute().equals(contents)) {
                    isPrimaryKey = true;
                    break;
                }
            }

            // check if contents is unique
            boolean isUnique = false;
            List<UniqueKey> uniqueKeys = myTable.getUniqueKeys();
            for (int i = 0; i < uniqueKeys.size(); i++) {
                if (uniqueKeys.get(i).getUniqueAttribute().equals(contents)) {
                    isUnique = true;
                    break;
                }
            }

            IndexFiles indexFiles = myTable.getIndexFiles();
            if (indexFiles == null) {
                indexFiles = new IndexFiles();
                myTable.setIndexFiles(indexFiles);
            }

            if (indexFiles.getIndexFiles() == null) {
                indexFiles.setIndexFiles(new JSONArray());
            }
            List<IndexFile> lif = indexFiles.getIndexFiles();
            for (int i = 0; i < lif.size(); i++) {
                if (lif.get(i).get_indexName().equals(indexName)) {
                    parser.setOtherError("Index already exists");
                    return;
                }
            }

            IndexFile newIndexFile = new IndexFile();
            newIndexFile.set_indexName(indexName);
            if (newIndexFile.getIndexAttributes() == null) {
                newIndexFile.setIndexAttributes(new JSONArray());
            }
            newIndexFile.getIndexAttributes().add(new IndexAttribute(indexName));
            lif.add(newIndexFile);

            objectMapper.writeValue(new File("Catalog.json"), databases);

            System.out.println("DEBUG");
            String connectionString = "mongodb://localhost:27017";
            try (MongoClient mongoClient = create(connectionString)) {
                MongoDatabase database = mongoClient.getDatabase(currentDatabase);
                MongoCollection<Document> collection = database.getCollection(tableName);
                MongoCollection<Document> indexCollection = database.getCollection(indexName);
                if (isPrimaryKey) {
                    System.out.println("Index created on primary key");
                } else if (isUnique) {
                    System.out.println("Index created on unique key");
                    // get keys from collection
                    int finalIndex = index;
                    collection.find().forEach((Document document) -> {
                        // get value of key
                        System.out.println("_id = " + document.get("_id"));
                        System.out.println("row = " + document.get("row"));
                        String[] row = document.get("row").toString().split("#");
                        String uniqueKey = row[finalIndex];
                        // create new document with key and value
                        Document newDocument = new Document();
                        newDocument.append(uniqueKey, document.get("_id").toString());
                        // insert into index collection
                        indexCollection.insertOne(newDocument);
                    });
                } else {
                    System.out.println("Index created on non-key");
                }
            } catch (MongoException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}