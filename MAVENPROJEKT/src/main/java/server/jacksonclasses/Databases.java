package server.jacksonclasses;

import java.util.ArrayList;
import java.util.List;

public class Databases {
    private List<Database> databases;

    public Databases() {
    }

    public Databases(List<Database> databases) {
        this.databases = databases;
    }

    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }
}
