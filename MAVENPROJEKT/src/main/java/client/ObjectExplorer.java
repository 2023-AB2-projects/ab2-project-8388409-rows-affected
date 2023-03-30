package client;

import org.bson.codecs.pojo.annotations.BsonId;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Set;

public class ObjectExplorer extends SidePanel {

     private ArrayList<DatabaseIllustration> databaseIllustrations = new ArrayList<>();

    public ObjectExplorer(KliensNew kliensNew, ArrayList<String> databaseNames) {
        super(kliensNew);
        setLayout( null);

    }

    public void updateDatabase(ArrayList<String> databaseNames) {
        int i = 0;
        for (String databaseName : databaseNames) {
            DatabaseIllustration databaseIllustration = new DatabaseIllustration(databaseName);
            databaseIllustration.setBounds(20, 80*i+50, 120, 60);
            databaseIllustrations.add(databaseIllustration);
            add(databaseIllustration);
            i++;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(80, 80, 80));
        g.drawString("Object Explorer", 20, 20);
        for (DatabaseIllustration databaseIllustration : databaseIllustrations) {
            databaseIllustration.repaint();
        }

    }


}
