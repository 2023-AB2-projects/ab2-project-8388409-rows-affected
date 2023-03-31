package server.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import server.Parser;
import server.jacksonclasses.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CreateIndex {
    public CreateIndex(String indexName,String tableName,String contents,String currentDatabase, Parser parser){

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

            IndexFiles indexFiles = myTable.getIndexFiles();

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
            newIndexFile.getIndexAttributes().add(new IndexAttribute(indexName));
            lif.add(newIndexFile);

            objectMapper.writeValue(new File("Catalog.json"), databases);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}