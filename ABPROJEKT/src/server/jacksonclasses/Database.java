package server.jacksonclasses;

public class Database {
    private String _dataBaseName;
    private Table[] Tables;

    public Database(String _dataBaseName, Table[] tables) {
        this._dataBaseName = _dataBaseName;
        Tables = tables;
    }

    public String get_dataBaseName() {
        return _dataBaseName;
    }

    public void set_dataBaseName(String _dataBaseName) {
        this._dataBaseName = _dataBaseName;
    }

    public Table[] getTables() {
        return Tables;
    }

    public void setTables(Table[] tables) {
        Tables = tables;
    }
}
