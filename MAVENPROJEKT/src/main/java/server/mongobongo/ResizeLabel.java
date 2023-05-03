package server.mongobongo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

public class ResizeLabel extends JPanel implements MouseListener, MouseMotionListener {
    private final JLabel label;
    private int startX;
    private int labelWidth;

    private ArrayList<ResizeLabel> resizeLabels;

    public ResizeLabel() {
        label = new JLabel("Hello, world!");
        label.addMouseListener(this);
        label.addMouseMotionListener(this);
        add(label, BorderLayout.CENTER);
        setVisible(true);
    }

    public ResizeLabel(String text, String top) {
        label = new JLabel(text);
        label.addMouseListener(this);
        label.addMouseMotionListener(this);
        add(label, BorderLayout.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        if (top.equals("top")) {
            label.setBackground(new Color(203, 203, 203, 255));
            label.setForeground(new Color(49, 49, 49, 255));
        } else {
            label.setBackground(new Color(49, 49, 49, 255));
            label.setForeground(new Color(203, 203, 203, 255));
        }
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        label.setBorder(BorderFactory.createCompoundBorder(
                label.getBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        setVisible(true);
    }

    public ResizeLabel(String text, String top, ArrayList<ResizeLabel> resizeLabels) {
        label = new JLabel(text);
        label.addMouseListener(this);
        label.addMouseMotionListener(this);
        label.setPreferredSize(new Dimension(100, 30));
        add(label, BorderLayout.CENTER);
        label.setHorizontalAlignment(JLabel.CENTER);
        if (top.equals("top")) {
            label.setBackground(new Color(77, 77, 77, 255));
            label.setForeground(new Color(203, 203, 203, 255));
        } else {

            label.setBackground(new Color(203, 203, 203, 255));
            label.setForeground(new Color(49, 49, 49, 255));
        }
        label.setOpaque(true);
        label.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        int padding = 7;
        label.setBorder(BorderFactory.createCompoundBorder(
                label.getBorder(),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding)));
        setVisible(true);
        this.resizeLabels = resizeLabels;

    }


    @Override
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        labelWidth = label.getWidth();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int deltaX = e.getX() - startX;
        int newWidth = labelWidth + deltaX;

        if (newWidth < 25) {
            newWidth = 25;
        }

        setLabelWidth(newWidth);
        if (resizeLabels != null) {
            for (ResizeLabel resizeLabel : resizeLabels) {
                resizeLabel.setLabelWidth(newWidth);
            }
        }
    }

    public void setLabelWidth(int newWidth) {
        label.setPreferredSize(new Dimension(newWidth, label.getHeight()));
        revalidate();
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }


}