package client;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ObjectExplorer extends SidePanel {

    private ArrayList<String> databaseNames;
    private BufferedImage image;

    public ObjectExplorer(KliensNew kliensNew, ArrayList<String> databaseNames) {
        super(kliensNew);
        this.databaseNames = databaseNames;
//        read the image from the file
        try {
            image = ImageIO.read(new File("logo.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.setColor(Color.black);
        g.setFont(new Font("Monospaced", Font.PLAIN, 15));
        g.drawString("Databases", 10, 20);
        g.drawLine(10, 25, 100, 25);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        if (databaseNames == null) {
            return;
        }
        for (int i = 0; i < databaseNames.size(); i++) {
            g.setColor(Color.black);
            g.drawImage(image, 10, 40 + i * 15, 15, 15, null);
            g.drawString(databaseNames.get(i), 30, 50 + i * 15);
            System.out.println(databaseNames.get(i));
        }
    }

}
