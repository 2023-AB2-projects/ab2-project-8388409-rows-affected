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
        System.out.println("painting");
        for (int i = 0; i < attributeNames.length; i++) {
            for (int j = 0; j < data.size(); j++) {
                if (j % 2 == 0)
                    g.setColor(new Color(131, 131, 131));
                else
                    g.setColor(new Color(176, 176, 176));
                g.fillRect(10 + i * 100, 20 + j * 20, 100, 20);
                g.setColor(Color.black);
                g.drawString(data.get(j)[i], 20 + i * 100, 20 + j * 20);
            }
        }

    }

    public static void main(String[] args) {
        VQDTable vqdTable = new VQDTable();
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.add(vqdTable, BorderLayout.CENTER);
        frame.revalidate();
    }
}
