package client;

import java.awt.*;
import java.util.ArrayList;

public class ObjectExplorer extends SidePanel {

    private ArrayList<String> databaseNames = new ArrayList<>();
    private ArrayList<DatabaseIllustration> databaseIllustrations = new ArrayList<>();
    public ObjectExplorer(KliensNew kliensNew, ArrayList<String> databaseNames) {
        super(kliensNew);
        for (String databaseName : databaseNames) {
            databaseIllustrations.add(new DatabaseIllustration(databaseName));
            System.out.println("adding");
            System.out.println(databaseName);
        }
        this.databaseNames = databaseNames;

    }
    public void updateDatabase(){
        databaseIllustrations.clear();
        System.out.println("updating database");
        for (String databaseName : databaseNames) {
            databaseIllustrations.add(new DatabaseIllustration(databaseName));
            System.out.println("adding");
            System.out.println(databaseName);
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawString("Databases", 10, 20);
        g.drawLine(10, 25, 100, 25);

        for ( DatabaseIllustration db : databaseIllustrations ){
            db.paintComponent(g);
            System.out.println("painting");
        }

    }


}
