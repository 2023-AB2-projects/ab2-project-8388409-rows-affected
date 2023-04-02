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
        String[] split = condition.split("=");
        String columnName = split[0].trim();
        String value = split[1].trim();

        if (value.startsWith("'") && value.endsWith("'") || value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }

        String connectionString = "mongodb://localhost:27017";
        try (MongoClient mongoClient = create(connectionString)) {
            List<String> databaseNames = mongoClient.listDatabaseNames().into(new ArrayList<>());
            if (!databaseNames.contains(databaseName)) {
                parser.setOtherError("Database " + databaseName + " does not exist");
                return;
            }
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            // find the value where key = value parameter
            MongoCollection<Document> collection = database.getCollection(tableName);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }
}
