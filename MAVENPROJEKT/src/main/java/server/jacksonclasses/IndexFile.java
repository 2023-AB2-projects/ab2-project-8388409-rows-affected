package server.jacksonclasses;

import java.util.List;

public class IndexFile {
    private List<IndexAttribute> IndexAttributes;
    private String _indexName;
    private String _keyLength;
    private String _isUnique;

    public IndexFile() {
    }

    public IndexFile(List<IndexAttribute> indexAttributes, String _indexName, String _keyLength, String _isUnique) {
        IndexAttributes = indexAttributes;
        this._indexName = _indexName;
        this._keyLength = _keyLength;
        this._isUnique = _isUnique;
    }

    public List<IndexAttribute> getIndexAttributes() {
        return IndexAttributes;
    }

    public void setIndexAttributes(List<IndexAttribute> indexAttributes) {
        IndexAttributes = indexAttributes;
    }

    public String get_indexName() {
        return _indexName;
    }

    public void set_indexName(String _indexName) {
        this._indexName = _indexName;
    }

    public String get_keyLength() {
        return _keyLength;
    }

    public void set_keyLength(String _keyLength) {
        this._keyLength = _keyLength;
    }

    public String get_isUnique() {
        return _isUnique;
    }

    public void set_isUnique(String _isUnique) {
        this._isUnique = _isUnique;
    }
}
