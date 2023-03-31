package client;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DatabaseIllustration extends JPanel {
    private BufferedImage image;
    private JButton button = new JButton("+");
    private String databaseName;
    public DatabaseIllustration(String databaseName) {
        super();
        this.databaseName = databaseName;
        try {
            image = javax.imageio.ImageIO.read(new java.io.File("logo.png"));
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        setLayout(new FlowLayout());
        add(button);

    }
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        button.setBounds(0,0,100,100);
        g.drawImage(image, 0, 0, 100, 100, null);
        g.drawString(databaseName, 0, 100);
    }


}
