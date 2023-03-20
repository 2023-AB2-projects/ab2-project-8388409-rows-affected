import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static java.lang.System.exit;


public class Kliens extends JFrame implements Runnable {

    private JTextArea textArea;
    private JTextArea outText;

    private boolean connected = false;
    private boolean send = false;

    private JButton connectionButton;


    public Kliens() {

        setTitle("Kliens");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        textArea.setBorder(BorderFactory.createLineBorder(Color.black));

        JScrollPane scrollText = new JScrollPane(textArea);

        outText = new JTextArea();
        outText.setEditable(false);
        outText.setText("welcome friend!");
        outText.setBorder(BorderFactory.createLineBorder(Color.black));

        JScrollPane scrollTextResp = new JScrollPane(outText);

        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        connectionButton = new JButton("Connect");
        JPanel gombPanel = new JPanel();
        JButton execButton = new JButton("Execute");
        JButton clear = new JButton("Clear");
        JButton exit = new JButton("Exit");

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setEditable(true);
        comboBox.addItem("localhost");

        gombPanel.add(comboBox);
        gombPanel.add(connectionButton);
        gombPanel.setBounds(0,0,getWidth(),10);

        execButton.addActionListener(e -> {
            print("Execute");
            send = true;
        });

        clear.addActionListener(e -> {
            print("Clear");
            textArea.setText("");
        });

        connectionButton.addActionListener(e -> {
            System.out.println("Connect");
            if (connectionButton.getText().equals("Connect")) {
                connectionButton.setText("Disconnect");
                connected = true;
                new Thread(this).start();
            } else {
                connectionButton.setText("Connect");
                textArea.setText("EXIT");
                send = true;

            }
        });

        exit.addActionListener(e -> {
            System.out.println("Exit");
            textArea.setText("EXIT");
            send = true;
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            exit(0);

        });

        textArea.addKeyListener(new KeyAdapter() {
                                    @Override
                                    public void keyReleased(KeyEvent e) {

                                        super.keyReleased(e);

                                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_ALT) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_CAPS_LOCK) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_TAB) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_UP) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                                            return;
                                        }
                                        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                                            return;
                                        }

                                        if (e.getKeyCode() == KeyEvent.VK_SPACE)
                                            syntaxHighlighting();
                                    }
                                }
        );

        gombPanel.add(execButton, BorderLayout.WEST);
        gombPanel.add(clear, BorderLayout.EAST);
        gombPanel.add(connectionButton, BorderLayout.WEST);
        gombPanel.add(exit, BorderLayout.EAST);


        panel.add(gombPanel, BorderLayout.NORTH);
        panel.add(scrollText, BorderLayout.CENTER);
        panel.add(scrollTextResp, BorderLayout.SOUTH);
        add(panel, BorderLayout.CENTER);

        setVisible(true);

    }

    private Boolean isSyntax(String word) {
        String w = word.toUpperCase();
        if (w.equals("SELECT"))
            return true;

        if (w.equals("WHERE"))
            return true;

        if (w.equals("INSERT"))
            return true;

        if (w.equals("UPDATE"))
            return true;

        if (w.equals("DELETE"))
            return true;

        if (w.equals("CREATE"))
            return true;

        if (w.equals("DROP"))
            return true;

        if (w.equals("ALTER"))
            return true;

        if (w.equals("TABLE"))
            return true;

        if (w.equals("DATABASE"))
            return true;

        if (w.equals("ON"))
            return true;

        if (w.equals("FROM"))
            return true;

        if (w.equals("AND"))
            return true;

        if (w.equals("OR"))
            return true;

        return false;

    }

    private void syntaxHighlighting() {
        // TODO

        SimpleAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setForeground(attrs, Color.blue);

        StringBuilder newTextArea = new StringBuilder();


        for (String line : textArea.getText().split("\n")) {
            System.out.println(line);

            for (String word : line.split(" ")) {
                if (isSyntax(word)) {

//                    set word color to blue
                    newTextArea.append(word.toUpperCase()).append(" ");

                } else {
                    newTextArea.append(word).append(" ");
                }
            }
            newTextArea.append("\n");

        }

        textArea.setText(newTextArea.toString());

        JTextArea cTextArea = new JTextArea();
        cTextArea.setText(newTextArea.toString());
        cTextArea.setForeground(Color.blue);

        try {
            for (String line : cTextArea.getText().split("\n")) {
                System.out.println(line);

                for (String word : line.split(" ")) {

                    if (isSyntax(word)) {
                        textArea.getHighlighter().addHighlight(textArea.getText().indexOf(word), textArea.getText().indexOf(word) + word.length(), new DefaultHighlighter.DefaultHighlightPainter(new Color(173, 255, 177)));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            print(e.getMessage());
        }

        textArea.setCaretPosition(textArea.getText().length() - 1);
    }

    private int connectToServer() {
        String hostName = "localhost";
        int portNumber = 1234;
        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String userInput;

            // Read server response and print to console
            String serverResponse = in.readLine();
            print("Server: " + serverResponse);

            // Read user input from console and send to server
//            while ((userInput = stdIn.readLine()) != null) {
//                out.println(userInput);
//                serverResponse = in.readLine();
//                System.out.println("Server: " + serverResponse);
//            }

//            System.out.println("most kuldok");
            while (connected) {

                while (!send){
                    try {
                        Thread.sleep(100);
                        if (in.ready()){
                            serverResponse = in.readLine();
                            print("server: " + serverResponse);
                        }
//                        if ((serverResponse = in.readLine())!= null){
//                            print(serverResponse);
//                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        print(e.getMessage());
                    }
                }

                userInput = textArea.getText();
//                System.out.println("userInput: " + userInput);
                out.println(userInput);
                print("Client: " + userInput);
                send = false;
                if(userInput.equals("EXIT")){
                    connected = false;
                    return 0;
                }
            }
        } catch (IOException e) {
            System.err.println("Exception caught when trying to connect to server: " + e.getMessage());
            print(e.getMessage());
        }
        return -1;

    }

    private void print(String s){
        outText.setText(outText.getText() + "\n" + s);
        System.out.println(s);

    }

    public static void main(String[] args) {

        new Kliens();

    }

    @Override
    public void run() {
        if (connectToServer() != 0) {
            connectionButton.setText("Connect");
        }
    }
}