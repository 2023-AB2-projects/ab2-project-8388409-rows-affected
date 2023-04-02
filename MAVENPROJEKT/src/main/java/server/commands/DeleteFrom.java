package server.commands;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import server.Parser;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.MongoClients.create;

public class DeleteFrom {
    public DeleteFrom(String databaseName, String tableName, String condition, Parser parser) {
        tableName = tableName.trim();
        System.out.println("tableName = " + tableName);
        System.out.println("condition = " + condition);
        condition = condition.trim();
        String[] conditions = condition.split("and");
        List<String> conditionsList = new ArrayList<>();
        for (String s : conditions) {
            conditionsList.add(s.trim());
        }
        String connectionString = "mongodb://localhost:27017";
        try (MongoClient mongoClient = create(connectionString)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(tableName);
            List<Document> documents = collection.find().into(new ArrayList<>());
            for (Document document : documents) {
                boolean delete = true;
                for (String s : conditionsList) {
                    String[] conditionParts = s.split("=");
                    String key = conditionParts[0].trim();
                    String value = conditionParts[1].trim();
                    if (!document.get(key).equals(value)) {
                        delete = false;
                        break;
                    }
                }
                if (delete) {
                    collection.deleteOne(document);
                }
            }
        }
    }
}
