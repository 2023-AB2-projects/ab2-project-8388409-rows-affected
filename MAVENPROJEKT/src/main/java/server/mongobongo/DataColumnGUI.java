package server.mongobongo;

import server.mongobongo.DataColumnModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.ArrayList;

import static com.sun.java.accessibility.util.SwingEventMonitor.addChangeListener;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;
public class DataColumnGUI extends JPanel {

    private final DataColumnModel column;
    private final ArrayList<ResizeLabel> valueLabels;
    private final int visibleLabelCount = 10; // Number of labels visible at a time
    private final JScrollPane scrollPane;
    private final JPanel contentPanel;
    private int startIndex; // Starting index of visible labels

    public DataColumnGUI(DataColumnModel column) {
        this.column = column;
//        this.scrollPane = scrollPane;

        add(new JLabel(column.getName()), BorderLayout.NORTH);
        add(new JLabel(column.getType()), BorderLayout.SOUTH);

        setLayout(new BorderLayout());
        valueLabels = new ArrayList<>();
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(new ScrollChangeListener());

        add(scrollPane, BorderLayout.CENTER);
        setVisible(true);
        startIndex = 0;
        addVisibleLabels();
    }

    private void addVisibleLabels() {
        int endIndex = Math.min(startIndex + visibleLabelCount, column.getValues().size());

        for (int i = startIndex; i < endIndex; i++) {
            ResizeLabel label = getLabel(column.getValues().get(i));
            valueLabels.add(label);
            contentPanel.add(label);
        }

        revalidate();
        repaint();
    }

    private void addLazy(ResizeLabel label) {
        valueLabels.add(label);
        contentPanel.add(label);
        revalidate();
        repaint();
    }

    private ResizeLabel getLabel(String text) {
        ResizeLabel label = new ResizeLabel(text, "", valueLabels);
        label.setVisible(true); // Set visibility to true
        return label;
    }

    private class ScrollChangeListener implements AdjustmentListener {
        @Override
        public void adjustmentValueChanged(AdjustmentEvent e) {
            JScrollBar scrollBar = (JScrollBar) e.getAdjustable();
            int value = scrollBar.getValue();
            int extent = scrollBar.getModel().getExtent();
            int maximum = scrollBar.getMaximum();

            // Check if the scroll bar is at the bottom
            if (value + extent >= maximum) {
                startIndex = Math.min(startIndex + visibleLabelCount, column.getValues().size());
                addVisibleLabels();
            }
        }
    }


    public void addButtons(String buttonName, int size) {
        for (int i = 0; i < size; i++) {
            ResizeLabel label = getLabel("");
            label.addButton(buttonName);
            this.valueLabels.add(label);
            addLazy(label);
        }
    }

    public String getRow(int index) {
        ArrayList<String> ret = new ArrayList<>();
        for (ResizeLabel label : this.valueLabels) {
            ret.add(label.getTextFromLabel());
        }
        return ret.get(index);
    }

    public ArrayList<ResizeLabel> getValueLabels() {
        return valueLabels;
    }

    public void addInputField(int size) {
        for (int i = 0; i < size; i++) {
            ResizeLabel label = getLabel("");
            label.addInput();
            this.valueLabels.add(label);
            addLazy(label);
        }
    }

    public ArrayList<JButton> getButtons() {
        ArrayList<JButton> ret = new ArrayList<>();
        for (ResizeLabel label : this.valueLabels) {
            JButton button = label.getButton();
            if (button != null) {
                ret.add(button);
            }
        }
        return ret;
    }

    public String getColumnName() {
        return this.column.getName();
    }

    public String get(int index) {
        return this.column.getValues().get(index);
    }

    public DataColumnModel getDataColumnModel() {
        return this.column;
    }


}
