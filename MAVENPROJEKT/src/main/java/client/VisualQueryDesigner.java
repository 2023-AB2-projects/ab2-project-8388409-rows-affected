package client;

import javax.swing.*;

public class VisualQueryDesigner extends JFrame {

    public VisualQueryDesigner(){

        this.setTitle("Visual Query Designer");
        this.setSize(1000, 700);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        this.setVisible(true);
    }

    public static void main(String[] args) {
        new VisualQueryDesigner();
    }
}
