package client;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class VQDTable extends JPanel {

    String[] attributeNames;
    ArrayList<String[]> data;

    public VQDTable(String[] attributeNames, String[][] data) {
        this.attributeNames = attributeNames;
        this.data = new ArrayList<>();
        this.data.addAll(Arrays.asList(data));
        setLayout(null);
        setBounds(0, 0, getWidth(), getHeight());
        setVisible(true);
    }

    public VQDTable() {
        attributeNames = new String[]{"Name", "Roll Number", "Department"};
        data = new ArrayList<>();
        data.add(new String[]{"Kundan Kumar Jha", "4031", "CSE"});
        data.add(new String[]{"Anand Jha", "6014", "IT"});
        setLayout(null);
        setBounds(0, 0, getWidth(), getHeight());
        setVisible(true);
    }

    public void clear() {
        attributeNames = new String[]{};
        data = new ArrayList<>();
    }

    public void setAttributeNames(String[] attributeNames) {
        this.attributeNames = attributeNames;
    }

    public void addData(String[] data) {
        this.data.add(data);
    }

    public void setData(String[][] data) {
        this.data = new ArrayList<>();
        this.data.addAll(Arrays.asList(data));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(80, 80, 80));
        g.drawString("Object Explorer", 20, 20);
        int x = 20;
        int y = 50;
        int width = 120;
        int height = 60;
        for (String attributeName : attributeNames) {
            g.drawString(attributeName, x, y);
            x += width;
        }

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        VQDTable vqdTable = new VQDTable();
        frame.add(vqdTable);
    }
}
