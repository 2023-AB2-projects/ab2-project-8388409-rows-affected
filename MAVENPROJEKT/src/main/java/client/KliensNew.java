package client;

import server.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.System.exit;

public class KliensNew extends JFrame implements Runnable {

    private final ObjectExplorer leftPanel;
    private final SidePanel rightPanel;
    private final SidePanel topPanel;
    private final JTabbedPane tabbedPane;
    private final JTabbedPane rightPanelTabs;
    private final JPanel queryPanelOptions;
    private final JPanel visualQueryDesignerOptions;
    private final JComponent QueryPanel;
    private final JComponent VisualQueryDesigner;
    private final JScrollPane scrollTextResp = new JScrollPane();
    private JTextArea textArea;
    private JTextArea textAreas = new JTextArea();
    private JTextArea outText = new JTextArea();
    private boolean connected = false;
    private boolean send = false;
    private JButton connectionButton;
    private int currentTabId = -1;
    private QueryPanel currentQueryPanel;
    private final Syntax syntax;
    private final boolean responseToUser = true;
    private int tabsCounter;

    private final ArrayList<String> databases;

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
        rightPanelTabs = new JTabbedPane();
        QueryPanel = new JPanel();
        VisualQueryDesigner = new JPanel();
        queryPanelOptions = new JPanel();
        visualQueryDesignerOptions = new JPanel();
        configQueryPanelOptions();
        configVisualQueryDesignerOptions();

        rightPanelTabs.addTab("Query Opt", queryPanelOptions);
        rightPanelTabs.addTab("VQD Opt", visualQueryDesignerOptions);
        rightPanelTabs.setPreferredSize(new Dimension(300, 700));
        rightPanelTabs.setEnabled(false);
        rightPanel.add(rightPanelTabs);
        rightPanelTabs.setVisible(false);


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

        JButton connectionButton = new JButton("Connect") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (connected) {
                    g.setColor(Color.GREEN);
                } else {
                    g.setColor(Color.RED);
                }
                g.fillOval(getWidth() / 10, getHeight() / 2 - 1, 3, 3);
            }
        };
        JButton execButton = new JButton("Execute");
        JButton clear = new JButton("Clear");
        JButton exit = new JButton("Exit");
        JButton newQuery = new JButton("New Query");

        JButton newVisualQueryDesigner = new JButton("Visual Query Designer");
        topPanel.add(connectionButton);
        topPanel.add(newQuery);
        topPanel.add(newVisualQueryDesigner);
        topPanel.add(exit);

        queryPanelOptions.add(execButton);
        queryPanelOptions.add(clear);
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

    private void configQueryPanelOptions() {
//        queryPanelOptions.setBackground(new Color(134, 134, 134));

    }
    private void configVisualQueryDesignerOptions() {
        VisualQueryDesigner.setBackground(new Color(98, 98, 98));
        VisualQueryDesigner.setLayout(new BoxLayout(VisualQueryDesigner, BoxLayout.Y_AXIS));
        JButton button = new JButton("New row");
        visualQueryDesignerOptions.add(button);

    }


    private void processMessage(Message mess) {

        System.out.println("processMessage");
        System.out.println("mess.isMessageUserEmpy(): " + mess.isMessageUserEmpy());
        System.out.println("mess.isMessageServerEmpy(): " + mess.isMessageServerEmpy());
        System.out.println("mess.isDatabasesEmpty(): " + mess.isDatabasesEmpty());

        System.out.println("mess Message: " + mess.getMessageUser() + " \n" + mess.getMessageServer() + " \n" + mess.getDatabases() + " ");
        if (!mess.isMessageUserEmpy()) {
            System.out.println("mess.getMessageUser(): " + mess.getMessageUser());
            outText.setText(outText.getText() + "\n" + mess.getMessageUser());
        }
        if (!mess.isMessageServerEmpy()) {

        }
        if (!mess.isDatabasesEmpty()) {
            System.out.println("mess.getDatabases(): " + mess.getDatabases());
            databases.clear();
            databases.addAll(mess.getDatabases());
            leftPanel.updateDatabase(databases);
            leftPanel.repaint();
            resizeWindowLayout();

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

        tabbedPane.addChangeListener(e1 -> {
            if (tabbedPane.getSelectedComponent() instanceof QueryPanel) {
                rightPanelTabs.setVisible(true);
                rightPanelTabs.setSelectedIndex(0);
                outText = ((QueryPanel) tabbedPane.getSelectedComponent()).getOutText();
            } else if (tabbedPane.getSelectedComponent() instanceof VisualQueryDesigner) {
                rightPanelTabs.setVisible(true);
                rightPanelTabs.setSelectedIndex(1);
            } else {
                rightPanelTabs.setVisible(false);
            }
        });


        newQuery.addActionListener(e -> {
            String tabName = "Query " + tabsCounter;
            JComponent queryPanel = new QueryPanel(this, tabbedPane);
            tabbedPane.addTab(tabName, queryPanel);
            tabsCounter++;
            rightPanelTabs.setSelectedIndex(0);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        });

        newVisualQueryDesigner.addActionListener(e -> {
            String tabName = "VQD " + tabsCounter;
            JComponent VisualQueryDesigner = new VisualQueryDesigner(this);
            tabbedPane.addTab(tabName, VisualQueryDesigner);
            tabsCounter++;
            rightPanelTabs.setSelectedIndex(1);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        });

        clear.addActionListener(e -> {
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

            int id = tabbedPane.getSelectedIndex();
            QueryPanel q = tabbedPane.getComponentAt(id) instanceof QueryPanel ? (QueryPanel) tabbedPane.getComponentAt(id) : null;
            if (q != null) {
                q.getTextArea().setText(textArea.getText());

            }
            send = true;
        });
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == this) {
            }
            if (tabbedPane.getSelectedComponent() instanceof QueryPanel) {
                currentQueryPanel = (QueryPanel) tabbedPane.getSelectedComponent();
                textArea = currentQueryPanel.getTextArea();
            }
        });


    }

    private void print(String execute, int id) {

    }

    void print(String execute) {
        outText.setText(outText.getText() + "\n" + execute);
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
//        if (connectToServer() != 0) {
//            connectionButton.setText("Connect");
//        } else {
//            connectionButton.setText("Disconnect");
//
//        }
        try {
            connectSendReceive();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void connectSendReceive() throws IOException {
        String hostName = "localhost";
        int portNumber = 1234;
        try (
                Socket clientSocket = new Socket(hostName, portNumber);
//                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                ObjectInputStream in = new ObjectInputStream(inputStream);
                ObjectOutputStream oot = new ObjectOutputStream(outputStream)
        ) {
            System.out.println("Connected to server");
            Message message = new Message();

            while (connected) {
                if (send) {
                    message = new Message();
                    message.setMessageUser(textArea.getText());
                    message.setKlientID(tabbedPane.getSelectedIndex());
                    oot.writeObject(message);
                    oot.flush();
                    send = false;

                }
                if (inputStream.available() > 0) {
                    System.out.println("Waiting for message");
                    message = null;
                    message = (Message) in.readObject();
                    if (message != null) {
                        System.out.println("New message");
                        System.out.println(message.getMessageUser());
                        processMessage(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
//        catch (ClassNotFoundException e) {
////            throw new RuntimeException(e);
//            System.out.println("Class not found");
//        }
    }
//
//    private int connectToServer() {
//        String hostName = "localhost";
//        int portNumber = 1234;
//        try (
//                Socket clientSocket = new Socket(hostName, portNumber);
//                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
//        ) {
//            String userInput;
//
//            // Read server response and print to console
//            String serverResponse = in.readLine();
//            print("Server: " + serverResponse);
//
//            while (connected) {
//
//                while (!send) {
//                    try {
//                        Thread.sleep(100);
//                        if (in.ready()) {
//                            serverResponse = in.readLine();
//                            print("Server: " + serverResponse);
//                        }
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        print(e.getMessage());
//                    }
//                }
//
//                userInput = textArea.getText();
//                print("Client: " + userInput);
//                out.println(userInput + "\n__end_of_file__");
//                print("Client: " + userInput);
//                send = false;
//                if (userInput.equals("EXIT")) {
//                    connected = false;
//
//                    textArea.setText(textAreas.getText());
//                    break;
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("Exception caught when trying to connect to server: " + e.getMessage());
//            print("Disconnected");
//            connected = false;
//            print(e.getMessage());
//        }
//
//        if (connectionButton != null) {
//            connectionButton.setText("Connect");
//            connected = false;
//        }
//        return -1;
//
//    }

    public void setCurrentTabId(int id) {
        currentTabId = id;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(String text) {
        this.textArea.setText(text);
    }
    public void setOptionTabbedPane(int index){
        tabbedPane.setSelectedIndex(index);
    }

    public static void main(String[] args) {
        new KliensNew();
    }

}
