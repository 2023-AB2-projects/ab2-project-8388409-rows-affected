package client;

import server.Message;
import server.jacksonclasses.Database;
import server.jacksonclasses.Table;

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
    private JComponent VisualQueryDesigner;
    private final JScrollPane scrollTextResp = new JScrollPane();
    private JTextArea textArea;
    private final JTextArea textAreas = new JTextArea(); // TODO: REMOVE
    private JTextArea outText = new JTextArea();
    private boolean connected = false;
    private boolean send = false;
    private JButton connectionButton; // TODO: REMOVE
    private int currentTabId = -1;
    private QueryPanel currentQueryPanel;
    private final Syntax syntax;
    private final boolean responseToUser = true; // TODO: REMOVE
    private int tabsCounter;

    private final ArrayList<String> databases;
    private final ArrayList<Table> tableObjects;
    private final ArrayList<Database> databaseObjects;


    KliensNew() {
//        InitQueryPanel();
//        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setTitle("AB: Client");
        this.setSize(1000, 700);
        this.setLocationRelativeTo(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        this.setIconImage(new ImageIcon("src/main/resources/icons/ablogo512.jpg").getImage());

        tableObjects = new ArrayList<>();
        databaseObjects = new ArrayList<>();
        databases = new ArrayList<>();
        syntax = new Syntax(this);
        tabsCounter = 0;
        leftPanel = new ObjectExplorer(this, databases);
        rightPanel = new SidePanel(this);
        topPanel = new SidePanel(this);
        tabbedPane = new JTabbedPane();
        rightPanelTabs = new JTabbedPane();
        QueryPanel = new JPanel();
        VisualQueryDesigner = new JPanel();
        queryPanelOptions = new JPanel();
        visualQueryDesignerOptions = new JPanel();
        configQueryPanelOptions();
//        configVisualQueryDesignerOptions();

//        rightPanelTabs.addTab("Query Opt", queryPanelOptions);
//        rightPanelTabs.addTab("VQD Opt", visualQueryDesignerOptions);
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

        VisualQueryDesigner = new JPanel();

        VisualQueryDesigner.setBackground(new Color(115, 34, 34));
        int width = visualQueryDesignerOptions.getWidth();
        int height = visualQueryDesignerOptions.getHeight();

        VisualQueryDesigner.setLayout(new BoxLayout(VisualQueryDesigner, BoxLayout.Y_AXIS));
        JPanel panel = new JPanel();
        panel.setBounds(50, 50, width - 50, height - 50);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton button = new JButton("Create");
        JButton buttonDelete = new JButton("Delete");
        JButton addRow = new JButton("Add Row");
        JButton execute = new JButton("Insert");

        JComboBox<String> comboBox = new JComboBox<>();
        JComboBox<String> comboBox2 = new JComboBox<>();

        panel.add(button, BorderLayout.NORTH);
        panel.add(addRow, BorderLayout.NORTH);
        panel.add(execute, BorderLayout.NORTH);
        panel.add(buttonDelete, BorderLayout.NORTH);

        panel.add(comboBox, BorderLayout.WEST);
        panel.add(comboBox2, BorderLayout.EAST);

        for (Database database : databaseObjects) {
            System.out.println(database.get_dataBaseName());
            comboBox.addItem(database.get_dataBaseName());
        }

        if (comboBox.getItemCount() > 0) {
            comboBox.setSelectedIndex(0);
        }

        comboBox.addActionListener(e -> {
            comboBox2.removeAllItems();
            Database database = databaseObjects.get(comboBox.getSelectedIndex());
            for (Table table : database.getTables()) {
                comboBox2.addItem(table.get_tableName());
            }
            comboBox2.setVisible(comboBox2.getItemCount() > 0);
        });

        addRow.addActionListener(e -> {
            VisualQueryDesigner visualQueryDesigner = tabbedPane.getSelectedComponent() instanceof VisualQueryDesigner ? (VisualQueryDesigner) tabbedPane.getSelectedComponent() : null;
            if (visualQueryDesigner != null) {
                visualQueryDesigner.addRow();
            }
        });

        if (comboBox2.getItemCount() > 0) {
            comboBox2.setSelectedIndex(0);
        }

        button.addActionListener(e -> {

            VisualQueryDesigner visualQueryDesigner = tabbedPane.getSelectedComponent() instanceof VisualQueryDesigner ? (VisualQueryDesigner) tabbedPane.getSelectedComponent() : null;
            if (visualQueryDesigner != null) {
                for (Table table : databaseObjects.get(comboBox.getSelectedIndex()).getTables()) {
                    if (table.get_tableName().equals(comboBox2.getSelectedItem())) {
                        visualQueryDesigner.createTable(table);
                        break;
                    }
                }
            }
        });

        execute.addActionListener(e -> {
            VisualQueryDesigner visualQueryDesigner = tabbedPane.getSelectedComponent() instanceof VisualQueryDesigner ? (VisualQueryDesigner) tabbedPane.getSelectedComponent() : null;
            if (visualQueryDesigner != null) {
                textArea = visualQueryDesigner.generateQuery((String) comboBox.getSelectedItem());
                send = true;
            }
        });
        buttonDelete.addActionListener(e -> {
            VisualQueryDesigner visualQueryDesigner = tabbedPane.getSelectedComponent() instanceof VisualQueryDesigner ? (VisualQueryDesigner) tabbedPane.getSelectedComponent() : null;
            if (visualQueryDesigner != null) {
                textArea = visualQueryDesigner.generateQueryDelete((String) comboBox.getSelectedItem());
                send = true;
            }
        });

//        visualQueryDesignerOptions.add(comboBox);
//        visualQueryDesignerOptions.add(comboBox2);
//        visualQueryDesignerOptions.add(button);
        visualQueryDesignerOptions.add(panel);
        visualQueryDesignerOptions.setVisible(true);
        validate();

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
            databaseObjects.clear();
            databaseObjects.addAll(mess.getDatabaseObjects());
            leftPanel.updateDatabase(databases);
            leftPanel.repaint();
            resizeWindowLayout();
            visualQueryDesignerOptions.removeAll();
            visualQueryDesignerOptions.validate();
            configVisualQueryDesignerOptions();
            validate();

        }
        for (Table s : mess.getTables()) {
            System.out.println("mess.getTables(): " + s.get_tableName());

        }
        if (!mess.isTablesEmpty()) {
            System.out.println("mess.getTables(): " + mess.getTables());
            tableObjects.clear();
            tableObjects.addAll(mess.getTables());
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
                rightPanelTabs.removeAll();
                rightPanelTabs.addTab("Query Opt", queryPanelOptions);
                rightPanelTabs.validate();
                rightPanelTabs.setVisible(true);
                validate();

                outText = ((QueryPanel) tabbedPane.getSelectedComponent()).getOutText();
            } else if (tabbedPane.getSelectedComponent() instanceof VisualQueryDesigner) {
                rightPanelTabs.removeAll();
                rightPanelTabs.addTab("VQD Opt", visualQueryDesignerOptions);
                rightPanelTabs.setVisible(true);
                rightPanelTabs.validate();
                validate();

            } else {
                rightPanelTabs.setVisible(false);
            }
        });

        newQuery.addActionListener(e -> {
            String tabName = "Query " + tabsCounter;
            JComponent queryPanel = new QueryPanel(this, tabbedPane);
            tabbedPane.addTab(tabName, queryPanel);
            tabsCounter++;
//            rightPanelTabs.setSelectedIndex(0);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        });

        newVisualQueryDesigner.addActionListener(e -> {
            String tabName = "VQD " + tabsCounter;
            JComponent VisualQueryDesigner = new VisualQueryDesigner(this);
            tabbedPane.addTab(tabName, VisualQueryDesigner);
            tabsCounter++;
//            rightPanelTabs.setSelectedIndex(1);
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

        });

        clear.addActionListener(e -> {
            textArea.setText("");
        });


        connectionButton.addActionListener(e -> {
            System.out.println("Connect");
            if (connectionButton.getText().equals("Connect")) {
                connectionButton.setText("Disconnect");
                rightPanelTabs.setVisible(true);
                if (tabbedPane.getTabCount() != 0) {
                    if (tabbedPane.getSelectedComponent() instanceof QueryPanel) {
//                        rightPanelTabs.setVisible(false);
//                        rightPanelTabs.setSelectedIndex(0);
//                        rightPanelTabs.setVisible(true);

                        outText = ((QueryPanel) tabbedPane.getSelectedComponent()).getOutText();
                    } else if (tabbedPane.getSelectedComponent() instanceof VisualQueryDesigner) {
//                        rightPanelTabs.setVisible(false);
//                        rightPanelTabs.setSelectedIndex(1);
//                        rightPanelTabs.setVisible(true);

                    }
                } else {
                    rightPanelTabs.setVisible(false);
                }
                connected = true;
                new Thread(this).start();

            } else {
                System.out.println("Disconected");
                rightPanelTabs.setVisible(false);
                databases.clear();
                databaseObjects.clear();
                tableObjects.clear();
                leftPanel.updateDatabase(databases);
                leftPanel.emptyDatabase();
                leftPanel.repaint();
                connected = false;
                connectionButton.setText("Connect");
                visualQueryDesignerOptions.removeAll();
                configVisualQueryDesignerOptions();

            }
//            resizeWindowLayout();
        });

        exit.addActionListener(e -> {
            System.out.println("Exit");

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
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                ObjectInputStream in = new ObjectInputStream(inputStream);
                ObjectOutputStream oot = new ObjectOutputStream(outputStream)
        ) {
            System.out.println("Connected to server");
            Message message = new Message();

            while (connected) {

                if (clientSocket.isClosed()) {
                    System.out.println("Socket closed");
                    break;
                }

                if (send) {
                    message = new Message();
                    message.setMessageUser(textArea.getText());
                    message.setKlientID(tabbedPane.getSelectedIndex());
                    oot.writeObject(message); // TODO: MEGHALT ITT java.net.SocketException: Connection reset by peer / java.net.SocketException: An established connection was aborted by the software in your host machine
                    oot.flush();
                    send = false;
                }
                if (inputStream.available() > 0) {
                    System.out.println("Waiting for message");
                    if (clientSocket.isClosed()) {
                        System.out.println("Socket closed");
                        break;
                    }
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

    }


    public void setCurrentTabId(int id) {
        currentTabId = id;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setTextArea(String text) {
        this.textArea.setText(text);
    }

    public void setOptionTabbedPane(int index) {
        tabbedPane.setSelectedIndex(index);
    }

    public static void main(String[] args) {
        new KliensNew();
    }

    public ArrayList<Database> getDatabases() {
        return this.databaseObjects;
    }

    public ArrayList<Table> getTables() {
        return this.tableObjects;
    }
}
