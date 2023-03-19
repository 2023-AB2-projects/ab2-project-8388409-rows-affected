import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


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
            return Boolean.TRUE;

        if (w.equals("WHERE"))
            return Boolean.TRUE;

        if (w.equals("INSERT"))
            return Boolean.TRUE;

        if (w.equals("UPDATE"))
            return Boolean.TRUE;

        if (w.equals("DELETE"))
            return Boolean.TRUE;

        if (w.equals("CREATE"))
            return Boolean.TRUE;

        if (w.equals("DROP"))
            return Boolean.TRUE;

        if (w.equals("ALTER"))
            return Boolean.TRUE;

        if (w.equals("TABLE"))
            return Boolean.TRUE;

        if (w.equals("DATABASE"))
            return Boolean.TRUE;

        if (w.equals("ON"))
            return Boolean.TRUE;

        if (w.equals("FROM"))
            return Boolean.TRUE;

        if (w.equals("AND"))
            return Boolean.TRUE;

        if (w.equals("OR"))
            return Boolean.TRUE;

        return Boolean.FALSE;

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

    }

    public static void main(String[] args) {

        new Kliens();

    }

}