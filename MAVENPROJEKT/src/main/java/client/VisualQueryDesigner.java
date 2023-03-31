package client;

import javax.swing.*;
import java.awt.*;

public class VisualQueryDesigner extends JPanel {

    private JTable table;
    public VisualQueryDesigner(KliensNew kliens){

        setBackground(new Color(233, 255, 255));
        table = new JTable();
        table.setBounds(0, 0, 100, 100);
        this.add(table);
        this.setVisible(true);
    }


}
