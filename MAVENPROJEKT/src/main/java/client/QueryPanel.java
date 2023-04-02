package client;

import javax.accessibility.Accessible;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class QueryPanel extends JComponent implements Accessible, MenuElement {

    private final JTextArea textArea = new JTextArea();
    private JTextArea outText = new JTextArea();

    private final JTabbedPane tabbedPane;
    private final KliensNew kliensNew;
    public QueryPanel(KliensNew kliensNew, JTabbedPane tabbedPane){
        super();

        this.kliensNew = kliensNew;
        this.tabbedPane = tabbedPane;


        textArea.setFont(new Font("Monospaced", Font.PLAIN, 15));
        textArea.setBorder(BorderFactory.createLineBorder(Color.black));


        JScrollPane scrollText = new JScrollPane(textArea);

        outText = new JTextArea();
        outText.setEditable(false);
        outText.setText("welcome friend!");
        outText.setBorder(BorderFactory.createLineBorder(Color.black));

        JScrollPane scrollTextResp = new JScrollPane(outText);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // set fix size for the text area
        scrollText.setPreferredSize(new Dimension(1000, 300));
        scrollTextResp.setPreferredSize(new Dimension(1000, 300));


        add(scrollText);
        add(scrollTextResp);
    }

    @Override
    public void processMouseEvent(MouseEvent event, MenuElement[] path, MenuSelectionManager manager) {
//        if right clicked on this tab, then show the menu with the options to close this tab

        if (event.getButton() == MouseEvent.BUTTON3) {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem menuItem = new JMenuItem("Close");
            menuItem.addActionListener(e -> {
                tabbedPane.remove(this);
            });
            popup.add(menuItem);
            popup.show(this, event.getX(), event.getY());
        }

    }

    @Override
    public void processKeyEvent(KeyEvent event, MenuElement[] path, MenuSelectionManager manager) {

    }

    @Override
    public void menuSelectionChanged(boolean isIncluded) {

    }

    @Override
    public MenuElement[] getSubElements() {
        return new MenuElement[0];
    }

    @Override
    public Component getComponent() {
        return null;
    }

    public JTextArea getTextArea() {
        return textArea;
    }

    public void setOutText(String text) {
        outText.setText(text);
    }


    public JTextArea getOutText() {
        return outText;
    }
}
