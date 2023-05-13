package server.commands;

import server.Parser;

public class Szazezer {
    public Szazezer(Parser parser) {
        String currentDatabas = "db";
        String tablename = "tbl";
        for (int i = 0; i < 100000; i++) {
            String contents = i + ", " + i + i + ", " + "'string" + i + "'";
            new InsertIntoSilent(currentDatabas, tablename, contents, null);
        }
    }
}
