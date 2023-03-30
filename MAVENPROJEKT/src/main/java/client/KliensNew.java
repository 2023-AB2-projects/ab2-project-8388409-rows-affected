package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.System.exit;

public class KliensNew extends JFrame implements Runnable {

    private ObjectExplorer leftPanel;
    private SidePanel rightPanel;
    private SidePanel topPanel;
    private JTabbedPane tabbedPane;

    private JComponent QueryPanel;
    private JComponent VisualQueryDesigner;
    private JScrollPane scrollTextResp = new JScrollPane();
    private JTextArea textArea;
    private JTextArea textAreas = new JTextArea();
    private JTextArea outText = new JTextArea();
    private boolean connected = false;
    private boolean send = false;
    private JButton connectionButton;
    private int currentTabId = -1;
    private QueryPanel currentQueryPanel;
    private Syntax syntax;
    private boolean responseToUser = true;
    private int tabsCounter;

    private ArrayList<String> databases;

    KliensNew() {
//        InitQueryPanel();
        this.setTitle("client.Kliens");
        this.setSize(1000, 700);
        this.setLocationRelativeTo(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);

        databases = new ArrayList<>();
        syntax = new Syntax(this);
        tabsCounter = 0;
        leftPanel = new ObjectExplorer(this,databases);
        rightPanel = new SidePanel(this);
        topPanel = new SidePanel(this);
        tabbedPane = new JTabbedPane();
        QueryPanel = new JPanel();
        VisualQueryDesigner = new JPanel();

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        textArea.setBorder(BorderFactory.createLineBorder(Color.black));

        JScrollPane scrollText = new JScrollPane(textArea);

        outText = new JTextArea();
        outText.setEditable(false);
        outText.setText("welcome friend!");
        outText.setBorder(BorderFactory.createLineBorder(Color.black));

        JScrollPane scrollTextResp = new JScrollPane(outText);
        QueryPanel.setLayout(new BoxLayout(QueryPanel, BoxLayout.Y_AXIS));


        QueryPanel.add(scrollText);

        JButton connectionButton = new JButton("Connect");
        JButton execButton = new JButton("Execute");
        JButton clear = new JButton("Clear");
        JButton exit = new JButton("Exit");
        JButton newQuery = new JButton("New Query");

        JButton newVisualQueryDesigner = new JButton("Visual Query Designer");
        topPanel.add(connectionButton);
        topPanel.add(execButton);
        topPanel.add(clear);
        topPanel.add(newVisualQueryDesigner);
        topPanel.add(exit);
        topPanel.add(newQuery);

//        InitQueryPanel();
        EventsAndActions();
        ButtonEventsAndActions(connectionButton, execButton, clear, exit, newVisualQueryDesigner, newQuery);
        resizeWindowLayout();


        this.add(leftPanel);
        this.add(rightPanel);
        this.add(topPanel);
        this.add(tabbedPane);

        this.setVisible(true);
    }

    public void processInformation(String valasz) {

//        convert to arrylist valasz

        String[] valaszArray2 = valasz.split(" ");

        for (String s : valaszArray2) {
            s=s.trim();

            s=s.replace(",", "");
            System.out.println(s);

            if (s.contains("[__databases__")) {
                databases.clear();
                System.out.println("databases cleared");
                continue;
            }
            if (s.contains("__databases_end__]")) {
                responseToUser = true;
                System.out.println("databases end");
                for (String s1 : databases) {
                    System.out.println(s1);
                }
                leftPanel.updateDatabase(databases);
                leftPanel.repaint();
                resizeWindowLayout();
                continue;
            }
            System.out.println("databases added");
            databases.add(s);
            leftPanel.repaint();
        }


    }

    public void print(String s) {

        if (s.contains("__databases__"))
            responseToUser = false;

        if (responseToUser) {

            outText.setText(outText.getText() + "\n" + s);
            System.out.println(s);

            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if (tabbedPane.getComponentAt(i) instanceof QueryPanel) {
                    QueryPanel queryPanel = (QueryPanel) tabbedPane.getComponentAt(i);
                    queryPanel.setOutText(outText.getText());
                }
            }
        }
        else {
            processInformation(s);
        }

    }

    private void resizeWindowLayout(){


        leftPanel.resizePanel(0, getHeight() / 8, getWidth() / 4, getHeight());
        rightPanel.resizePanel(getWidth() - getWidth() / 4, getHeight() / 8, getWidth() / 4, getHeight());
        topPanel.resizePanel(0, 0, getWidth(), getHeight() / 8);
        tabbedPane.setBounds(getWidth() / 4, getHeight() / 8, getWidth() - getWidth() / 2, getHeight() - getHeight() / 4);
        QueryPanel.setBounds(getWidth() / 4, getHeight() / 8, getWidth() - getWidth() / 2, getHeight() - getHeight() / 4);
        VisualQueryDesigner.setBounds(getWidth() / 4, getHeight() / 8, getWidth() - getWidth() / 2, getHeight());
        scrollTextResp.setBounds(getWidth() / 4, getHeight() / 8, getWidth() - getWidth() / 2, getHeight() - getHeight() / 4);
    }
    private void ButtonEventsAndActions(JButton connectionButton, JButton execButton, JButton clear, JButton exit, JButton newVisualQueryDesigner, JButton newQuery) {

        newQuery.addActionListener(e -> {
            String tabName = "Query " + tabsCounter;
            JComponent queryPanel = new QueryPanel(this, tabbedPane);
            tabbedPane.addTab(tabName, queryPanel);
            tabsCounter++;
        });

        newVisualQueryDesigner.addActionListener(e -> {
            String tabName = "VQD " + tabsCounter;
            tabbedPane.addTab(tabName, VisualQueryDesigner);
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
                databases.clear();
                leftPanel.repaint();
                connectionButton.setText("Connect");
                textAreas = new JTextArea(textArea.getText());
                textArea.setText("EXIT");
                send = true;

            }
//            resizeWindowLayout();
        });

        exit.addActionListener(e -> {
            System.out.println("Exit");
            textArea.setText("EXIT");
            send = true;
            try {
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

                                    }
                                }
        );

        execButton.addActionListener(e -> {
            print("Execute");

            int id = tabbedPane.getSelectedIndex();
            QueryPanel q = tabbedPane.getComponentAt(id) instanceof QueryPanel ? (QueryPanel) tabbedPane.getComponentAt(id) : null;
            if (q != null) {
                q.getTextArea().setText(textArea.getText());

            }
            send = true;
        });
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == this) {
                print("no query panel");
            }
            if (tabbedPane.getSelectedComponent() instanceof QueryPanel) {
                currentQueryPanel = (QueryPanel) tabbedPane.getSelectedComponent();
                textArea = currentQueryPanel.getTextArea();
            }
        });


    }

    private void EventsAndActions() {
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                resizeWindowLayout();
            }
        });


    }

    public void setCurrentQueryPanel(QueryPanel queryPanel){
        this.currentQueryPanel = queryPanel;
    }


    @Override
    public void run() {
        if (connectToServer() != 0) {
            connectionButton.setText("Connect");
        }
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


            while (connected) {

                while (!send) {
                    try {
                        Thread.sleep(100);
                        if (in.ready()) {
                            serverResponse = in.readLine();
                            print("Server: " + serverResponse);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        print(e.getMessage());
                    }
                }

                userInput = textArea.getText();
                print("Client: " + userInput);
                out.println(userInput + "\n__end_of_file__");
                print("Client: " + userInput);
                send = false;
                if (userInput.equals("EXIT")) {
                    connected = false;

                    textArea.setText(textAreas.getText());
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Exception caught when trying to connect to server: " + e.getMessage());
            print("Disconnected");
            connectionButton.setText("Connect");
            print(e.getMessage());
        }

        if (connectionButton != null)
            connectionButton.setText("Connect");
        return -1;

    }

    public void setCurrentTabId(int id){
        currentTabId = id;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(String text) {
        this.textArea.setText(text);
    }


    public static void main(String[] args) {
        new KliensNew();
    }

}
