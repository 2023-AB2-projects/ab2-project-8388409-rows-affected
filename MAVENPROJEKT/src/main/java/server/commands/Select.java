package server.commands;

import server.Parser;
import server.mongobongo.DataTable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Select {

    private final String[] selectedColums;
    private final String fromTables;
    private final String[] joinClause;
    private final String[] whereClause;
    private String[] groupBy;

    private final String database;
    private final Parser parser;

    public Select(String currentDatabase, String text, Parser parser) {
        this.parser = parser;
        database = currentDatabase;

        selectedColums = selectedColums(text);
        System.out.println("Selected columns: ");
        for (String an : selectedColums) {
            System.out.print(an + " ");
        }
        System.out.println();
        fromTables = fromTables(text);
        System.out.println("Table: " + fromTables);

        System.out.println();
        joinClause = joinClause(text);
        System.out.println("Join clause: ");
        for (String an : joinClause) {
            System.out.print(an + " ");
        }
        System.out.println();

        whereClause = whereClause(text);
        System.out.println("Where clause: ");
        for (String an : whereClause) {
            System.out.print(an + " ");
        }
        System.out.println();


        if (selectedColums[0].equals("*")) {
            if (selectedColums.length == 1) {
                getBaseTables();
            } else {
                parser.setParserError(true);
            }
        } else {
            getBaseTables();
        }
    }

    public String betweenString(String text, String start, String end) {
        String ans = "";
        String textUpper = text.toUpperCase();
        int startindex = textUpper.indexOf(start);
        int endindex = textUpper.indexOf(end);

        if (startindex == -1 && endindex == -1) {
            return ans;
        }

        if (startindex == -1) {
            startindex = 0;
        }
        if (endindex == -1) {
            endindex = text.length();
        }


        ans = text.substring(startindex + start.length(), endindex);
//        System.out.println("ans =" + ans);
        return ans;
    }

    public String[] selectedColums(String text) {

//        SELECT ans FROM

        String data = betweenString(text, "SELECT", "FROM");
        String[] ans = data.split(",");
        return ans;
    }

    public String fromTables(String text) {
//        FROM ans JOIN vagy FROM WHERE
        String data = betweenString(text, "FROM", "INNER JOIN");
        return data.trim();
    }

    public String[] joinClause(String text) {
//        INNER JOIN ans WHERE
        String data = betweenString(text, "INNER JOIN", "WHERE");
        String[] ans = data.split(",");
        return ans;
    }

    public String[] whereClause(String text) {

        String data = betweenString(text, "WHERE", "ORDER BY");
        String[] ans = data.split(",");
        return ans;
    }

    public ArrayList<DataTable> getBaseTables() {

        ArrayList<DataTable> tables = new ArrayList<>();
        JFrame frame = new JFrame("Select");

        System.out.println("\nDatabase: |" + database + "| Table: |" + fromTables + "|\n");
        tables.add(new DataTable(database, fromTables, parser));

        JFrame jf = new JFrame();
        jf.setSize(400, 300);
        jf.setLayout(new FlowLayout());
        jf.setBackground(new Color(203, 141, 141));
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        System.out.println("Tables: ");
        for (DataTable table : tables) {
//            System.out.println(table.getTableName());
            jf.add(new DataTable(table));
        }
        jf.setVisible(true);
        return tables;

    }

    public DataTable getResultTable() {
        DataTable resultTable = null;
        return resultTable;
    }
}
