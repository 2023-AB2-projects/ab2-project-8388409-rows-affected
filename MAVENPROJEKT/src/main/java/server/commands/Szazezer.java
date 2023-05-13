package server.commands;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import server.Parser;

public class Szazezer {
    public Szazezer(Parser parser) {
        String connectionString = "mongodb://localhost:27017";
        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            String currentDatabas = "db";
            MongoDatabase mongoDatabase = mongoClient.getDatabase(currentDatabas);
            String tablename = "tbl";
            MongoCollection<Document> mongoCollection = mongoDatabase.getCollection(tablename);
            for (int i = 0; i < 100000; i++) {
                String key = String.valueOf(i);
                String value = i + i + "#" + "string" + i;
                Document document = new Document("_id", key).append("row", value);
                mongoCollection.insertOne(document);
            }
        }
    }
}
