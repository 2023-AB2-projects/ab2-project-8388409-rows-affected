package server.jacksonclasses;

public class Table {
    private IndexFiles IndexFiles;
    private String _tableName;
    private String _rowLength;
    private UniqueKey[] UniqueKeys;
    private PrimaryKey[] PrimaryKeys;
    private ForeignKey[] ForeignKeys;
    private Structure Structure;
    private String _fileName;

    public Table(server.jacksonclasses.IndexFiles indexFiles, String _tableName, String _rowLength, UniqueKey[] uniqueKeys, PrimaryKey[] primaryKeys, ForeignKey[] foreignKeys, server.jacksonclasses.Structure structure, String _fileName) {
        IndexFiles = indexFiles;
        this._tableName = _tableName;
        this._rowLength = _rowLength;
        UniqueKeys = uniqueKeys;
        PrimaryKeys = primaryKeys;
        ForeignKeys = foreignKeys;
        Structure = structure;
        this._fileName = _fileName;
    }

    public server.jacksonclasses.IndexFiles getIndexFiles() {
        return IndexFiles;
    }

    public void setIndexFiles(server.jacksonclasses.IndexFiles indexFiles) {
        IndexFiles = indexFiles;
    }

    public String get_tableName() {
        return _tableName;
    }

    public void set_tableName(String _tableName) {
        this._tableName = _tableName;
    }

    public String get_rowLength() {
        return _rowLength;
    }

    public void set_rowLength(String _rowLength) {
        this._rowLength = _rowLength;
    }

    public UniqueKey[] getUniqueKeys() {
        return UniqueKeys;
    }

    public void setUniqueKeys(UniqueKey[] uniqueKeys) {
        UniqueKeys = uniqueKeys;
    }

    public PrimaryKey[] getPrimaryKeys() {
        return PrimaryKeys;
    }

    public void setPrimaryKeys(PrimaryKey[] primaryKeys) {
        PrimaryKeys = primaryKeys;
    }

    public ForeignKey[] getForeignKeys() {
        return ForeignKeys;
    }

    public void setForeignKeys(ForeignKey[] foreignKeys) {
        ForeignKeys = foreignKeys;
    }

    public server.jacksonclasses.Structure getStructure() {
        return Structure;
    }

    public void setStructure(server.jacksonclasses.Structure structure) {
        Structure = structure;
    }

    public String get_fileName() {
        return _fileName;
    }

    public void set_fileName(String _fileName) {
        this._fileName = _fileName;
    }
}
