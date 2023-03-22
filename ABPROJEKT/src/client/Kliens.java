package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import static java.lang.System.exit;


public class Kliens extends JFrame implements Runnable {

    private JTextArea textArea;
    private JTextArea outText;
    private JTable table;

    private boolean connected = false;
    private boolean send = false;

    private JButton connectionButton;
    private Syntax syntax;

    public Kliens() {

        setTitle("client.Kliens");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        syntax = new Syntax(this);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        textArea.setBorder(BorderFactory.createLineBorder(Color.black));

        JScrollPane scrollText = new JScrollPane(textArea);

        outText = new JTextArea();
        outText.setEditable(false);
        outText.setText("welcome friend!");
        outText.setBorder(BorderFactory.createLineBorder(Color.black));

        JScrollPane scrollTextResp = new JScrollPane(outText);

        table = new JTable();
        JScrollPane srollTable = new JScrollPane(table);
//        table.setBounds(0,getWidth()*(1/4),getWidth(),getWidth()*(1/4));

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

                                        if (e.getKeyCode() == KeyEvent.VK_SPACE)
                                            syntax.syntaxHighlighting();
                                        if (e.getKeyCode() == KeyEvent.VK_ENTER)
                                            syntax.syntaxHighlighting();

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
        add(srollTable, BorderLayout.WEST);
        add(panel, BorderLayout.CENTER);

        setVisible(true);

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

    public void print(String s) {
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

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(String text) {
        this.textArea.setText(text);
    }
}

