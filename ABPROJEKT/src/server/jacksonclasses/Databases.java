package server.jacksonclasses;

public class Databases {
    private Database[] databases;

    public Databases() {
    }

    public Databases(Database[] databases) {
        this.databases = databases;
    }

    public Database[] getDatabases() {
        return databases;
    }

    public void setDatabases(Database[] databases) {
        this.databases = databases;
    }
}
