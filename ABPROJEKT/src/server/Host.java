package server;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.jacksonclasses.Database;
import server.jacksonclasses.Databases;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Host {
    private JSONObject catalog = new JSONObject();
    private String currentDatabase = "";

    private String error;
    private List<String> elvSzavak;
    private  String acc;

    private String answer = "";

    public Host() {

        elvSzavak = new ArrayList<>();
        elvSzavak.add("USE");
        elvSzavak.add("CREATE");
        elvSzavak.add("DROP");
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
        Databases Databases = new Databases();
        try {
            mapper.writeValue(new File("Catalog.json"), Databases);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Starting server...");
//
//        JSONObject databases = new JSONObject();
//        JSONArray databasesArray = new JSONArray();
//        databases.put("Databases", databasesArray);
//
//
//        try {
//            Reader reader = new FileReader("Catalog.json");
//            catalog = (JSONObject) new JSONParser().parse(reader);
//        } catch (FileNotFoundException e) {
//            try {
//                FileWriter fileWriter = new FileWriter("Catalog.json");
//                fileWriter.write(databases.toJSONString());
//                fileWriter.close();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        } catch (ParseException e) {
//            System.out.println("Catalog is empty, initializing...");
//            FileWriter fileWriter = null;
//            try {
//                fileWriter = new FileWriter("Catalog.json");
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//            try {
//                fileWriter.write(databases.toJSONString());
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//            try {
//                fileWriter.close();
//            } catch (IOException ex) {
//                throw new RuntimeException(ex);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    private void Create_socket_communication() {
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

            while (true) {
                if ((inputLine = in.readLine()) != null && inputLine.length() > 0) {

                    if (inputLine.equals("EXIT")) {
                        break;
                    }

                    darabol(inputLine);

                    if (answer.equals("ok")){

                        if (error.length() > 0) {
                            out.println("ERROR: " + error);
                            error = "";
                        } else {
                            out.println("ok");
                        }
                        answer = "";
                    }
                }
            }

            System.out.println("Client disconnected.");
            Write_lastCurrentDatabase();
        } catch (IOException e) {
            System.err.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection: " + e.getMessage());
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
        String[] words = input.split(" ");
        int command_length = 0;
        if (!Objects.equals(acc, "")) {
            command_length = acc.length();
        }


        for (int i = 0; i < words.length; i++) {

//            System.out.println("command_length: " + command_length);

            if (elvSzavak.contains(words[i].trim().toUpperCase())) {
                if (command_length > 0) {

                    String fullInput = (acc + command);
                    fullInput = reformatParserInput(fullInput);

                    System.out.println("|=> parsed command: " + fullInput);
                    new Parser(fullInput, this);

                    acc = "";
                    command = new StringBuilder();
                } else {
                    System.out.println("adding STRONG word: " + words[i]);
                    command = new StringBuilder();
                }

                command_length = 0;

            }
            if(words[i].equals("__end_of_file__"))
            {
                String fullInput = (acc + command);
                fullInput = reformatParserInput(fullInput);

                System.out.println("|=> parsed command: " + fullInput);
                new Parser(fullInput, this);
                acc = "";
                answer = "ok";
                break;
            }
            command.append(" ").append(words[i]);
            command_length += words[i].length() + 1;

        }

        acc += command.toString();

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
