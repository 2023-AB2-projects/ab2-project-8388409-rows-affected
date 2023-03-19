import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Host {
    JSONObject catalog = new JSONObject();
    public void Create() {
        System.out.println("Starting server...");

        JSONObject jsonObject = new JSONObject();

        // Creating the innermost JSONObject
        JSONObject indexAttr = new JSONObject();
        indexAttr.put("IAttribute", "SpecName");

        // Creating the innermost JSONArray
        JSONArray indexFileArray = new JSONArray();
        JSONObject indexFile1 = new JSONObject();
        indexFile1.put("IndexAttributes", indexAttr);
        indexFileArray.add(indexFile1);

        // Creating the innermost JSONObject
        JSONObject uniqueAttr = new JSONObject();
        uniqueAttr.put("UniqueAttribute", "SpecName");

        // Creating the innermost JSONArray
        JSONArray tableArray = new JSONArray();
        JSONObject table1 = new JSONObject();
        table1.put("Structure", new JSONObject().put("Attribute", new JSONArray()));
        table1.put("primaryKey", new JSONObject().put("pkAttribute", "Specid"));
        table1.put("uniqueKeys", uniqueAttr);
        table1.put("IndexFiles", new JSONObject().put("IndexFile", indexFileArray.get(0)));
        tableArray.add(table1);

        // Creating the innermost JSONObject
        JSONObject ref = new JSONObject();
        ref.put("refTable", "Specialization");
        ref.put("refAttribute", "Specid");
        JSONObject foreignKey = new JSONObject();
        foreignKey.put("fkAttribute", "Specid");
        foreignKey.put("references", ref);

        // Creating the innermost JSONArray
        JSONObject table2 = new JSONObject();
        table2.put("Structure", new JSONObject().put("Attribute", new JSONArray()));
        table2.put("primaryKey", new JSONObject().put("pkAttribute", "Groupid"));
        table2.put("foreignKeys", foreignKey);
        table2.put("IndexFiles", new JSONObject().put("IndexFile", indexFileArray.get(0)));
        tableArray.add(table2);

        // Creating the innermost JSONObject
        JSONObject tables = new JSONObject();
        tables.put("Table", tableArray);

        // Creating the innermost JSONObject
        JSONObject database = new JSONObject();
        database.put("Tables", tables);

        // Creating the outermost JSONObject
        JSONObject databases = new JSONObject();
        databases.put("DataBase", database);
        jsonObject.put("Databases", databases);


        try {
            Reader reader = new FileReader("Catalog.json");
            catalog = (JSONObject) new JSONParser().parse(reader);
            // if empty, initialize
            if (catalog.isEmpty()) {
                reader.close();
                FileWriter fileWriter = new FileWriter("Catalog.json");
                fileWriter.write(jsonObject.toJSONString());
                fileWriter.close();
            }
        } catch (FileNotFoundException e) {
            try {
                FileWriter fileWriter = new FileWriter("Catalog.json");
                fileWriter.write(jsonObject.toJSONString());
                fileWriter.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        int portNumber = 1234; // replace with your port number

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))

        ) {
            System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

            // Send a welcome message to the client
            out.println("Welcome to the server!");

            String inputLine;
            while (true){
//                System.out.println("waiting.");
                if ((inputLine = in.readLine()) != null && inputLine.length() > 0) {

                    System.out.println("Received from client: " + inputLine);
//                    out.println("You said: " + inputLine);
                    if (inputLine.equals("EXIT")) {
                        break;
                    }
                }

            }


            System.out.println("Client disconnected.");
        } catch (IOException e) {
            System.err.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Host host = new Host();
        host.Create();
    }
}
