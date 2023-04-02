package server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import server.jacksonclasses.Database;
import server.jacksonclasses.Databases;
import server.jacksonclasses.Table;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Host {
    private final JSONObject catalog = new JSONObject();
    private String currentDatabase = "";

    private String error;
    private final List<String> elvSzavak;
    private final String acc;

    private String answer = "";

    public Host() {

        elvSzavak = new ArrayList<>();
        elvSzavak.add("USE");
        elvSzavak.add("CREATE");
        elvSzavak.add("DROP");
        elvSzavak.add("use");
        elvSzavak.add("create");
        elvSzavak.add("drop");
        elvSzavak.add("INSERT");
        elvSzavak.add("insert");

        error = "";
        acc = "";

        Create_load_catalog();
        Create_load_lastCurrentDatabase();
        Create_socket_communication();
    }
    private void Write_lastCurrentDatabase(){
        try {
            Writer writer = new FileWriter("currentdatabase.txt");
            writer.write(currentDatabase);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void Create_load_lastCurrentDatabase(){
        // currentdatabase.txt contains the last used database name
        try {
            BufferedReader br = new BufferedReader(new FileReader("currentdatabase.txt"));
            String line = br.readLine();
            if (line != null) {
                currentDatabase = line;
            }
        } catch (FileNotFoundException e) {
            Writer writer = null;
            try {
                writer = new FileWriter("currentdatabase.txt");
                writer.write("");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void Create_load_catalog() {
        ObjectMapper mapper = new ObjectMapper();

        File file = new File("Catalog.json");
        if (file.length() == 0){
            System.out.println("Catalog is empty, initializing...");
            Databases dbs = new Databases();
            try {
                mapper.writeValue(file, dbs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Starting server...");

    }

    private void Create_socket_communication() {
        int portNumber = 1234; // replace with your port number

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                OutputStream outputStream = clientSocket.getOutputStream();
                ObjectOutputStream outS = new ObjectOutputStream(outputStream);
                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream inS = new ObjectInputStream(inputStream)
        ) {

            Message message = new Message();
            message.setMessageUser("Welcome to the server!");
            message.setDatabases(new DataBaseNames().getDatabaseNames());
            DataBaseNames dbn = new DataBaseNames();
            ArrayList<Database> databaseArrayList = new ArrayList<>();
            ArrayList<Table> tableArrayList = new ArrayList<>();

            for (String databaseName : dbn.getDatabaseNames()) {
                System.out.println(databaseName);
                databaseArrayList.add(dbn.getDatabase(databaseName));
            }
            for (Database db : databaseArrayList) {
                System.out.println(db.get_dataBaseName());
                tableArrayList.addAll(db.getTables());
            }
            message.setTables(tableArrayList);
            message.setDatabases(dbn.getDatabaseNames());
            outS.writeObject(message);
            outS.flush();
            System.out.println("message sent to client: " + message.getMessageUser());
            while (true) {
                try {

                    message = null;
                    message = (Message) inS.readObject(); // TODO : itt meghal EXIT után
                    System.out.println("message received from client: " + message.getMessageUser());
                    darabol(message.getMessageUser());
                    message.setMessageUser(answer);
                    outS.writeObject(message);
                    outS.flush();

                    Thread.sleep(100);
                    Write_lastCurrentDatabase();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // TODO: EZT ITT FENT MIÉRT KOMMENTELTED KI??
            // ANSWER: mert ijrairtam a kommunikaciot es kb semmi se volt ugy jo

        } catch (Exception e) {
            System.out.println("exeption message= " + e.getMessage());
            e.printStackTrace();
        }
    }

    String reformatParserInput(String fullInput)
    {
        fullInput = fullInput.trim();
//                    remove extra spaces
        fullInput = fullInput.replaceAll("\\s+", " ");
//                    replace tabs with spaces
        fullInput = fullInput.replaceAll("\\t", " ");
//                    if ( has no space  after it, add one
        fullInput = fullInput.replaceAll("([^\\s])\\(", "$1 (");
//                     if ( has no space before it, add one
        fullInput = fullInput.replaceAll("\\)([^\\s])", ") $1");
//                    if ) has no space after it, add one
        fullInput = fullInput.replaceAll("\\)\\(", ") (");
//                    if ) has no space before it, add one
        fullInput = fullInput.replaceAll("\\)\\(", ") (");
        return fullInput;
    }
    public void darabol(String input) {


        StringBuilder command = new StringBuilder();
//        newline character to space
        input = reformatParserInput(input);

        String[] words = input.split(" ");


        ArrayList<String> commandList = new ArrayList<>();
        String fullInput = "";
        for (int i = 0; i < words.length; i++) {
            if (elvSzavak.contains(words[i].toUpperCase())) {
                if (i == 0) {
                    fullInput = words[i] + " ";
                } else {
                    fullInput = fullInput.trim();
                    System.out.println("|=> parsed command: " + fullInput + "|");
                    new Parser(fullInput, this);
                    fullInput = words[i] + " ";
                }
            } else {
                fullInput += words[i] + " ";
            }
        }
        fullInput = fullInput.trim();
        System.out.println("|=> parsed command: " + fullInput + "| ");
        new Parser(fullInput, this);
        if (error.length() > 0) {
            answer = "ERROR: " + error;
            error = "";
        } else {
            answer = "ok";
        }
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setCurrentDatabase(String currentDatabase) {
        this.currentDatabase = currentDatabase;
    }

    public String getCurrentDatabase() {
        return currentDatabase;
    }

    public static void main(String[] args) {
        Host host = new Host();
    }
}
