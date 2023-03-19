import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Kliens extends JFrame {

    private JTextArea textArea;

    public Kliens() {

        setTitle("Kliens");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 20));

        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel gombPanel = new JPanel();

        JButton execButton = new JButton("Execute");
        JButton connectionButton = new JButton("Connect");
        JButton clear = new JButton("Clear");
        JButton exit = new JButton("Exit");

        execButton.addActionListener(e -> {
            System.out.println("Execute");

        });

        clear.addActionListener(e -> {
            System.out.println("Clear");
            textArea.setText("");
        });

        connectionButton.addActionListener(e -> {
            System.out.println("Connect");
            if (connectionButton.getText().equals("Connect")) {
                connectionButton.setText("Disconnect");
            } else {
                connectionButton.setText("Connect");
            }
        });

        exit.addActionListener(e -> {
            System.out.println("Exit");
            System.exit(0);
        });

        textArea.addKeyListener(new KeyAdapter() {
                                    @Override
                                    public void keyReleased(KeyEvent e) {

                                        super.keyReleased(e);

                                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                            textArea.append("\n ");
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

        panel.add(textArea, BorderLayout.CENTER);

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

        String newTextArea = "";


        for (String line : textArea.getText().split("\n")) {
            System.out.println(line);

            for (String word : line.split(" ")) {
                if (isSyntax(word)) {

//                    set word color to blue
                    newTextArea += word.toUpperCase() + " ";

                } else {
                    newTextArea += word + " ";
                }
            }
            newTextArea += "\n";

        }

        textArea.setText(newTextArea);

        JTextArea cTextArea = new JTextArea();
        cTextArea.setText(newTextArea);
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
        }

        textArea.setCaretPosition(textArea.getText().length() - 1);
    }

    private void connectToServer() {
        String hostName = "localhost"; // replace with your host name or IP address
        int portNumber = 1234; // replace with your port number

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
        ) {
            String userInput;

            // Read server response and print to console
            String serverResponse = in.readLine();
            System.out.println("Server: " + serverResponse);

            // Read user input from console and send to server
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                serverResponse = in.readLine();
                System.out.println("Server: " + serverResponse);
            }
        } catch (IOException e) {
            System.err.println("Exception caught when trying to connect to server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

        new Kliens();

    }

}