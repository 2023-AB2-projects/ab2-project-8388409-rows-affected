package server.jacksonclasses;

import java.util.List;

public class IndexFiles {
    private List<IndexFile> IndexFiles;

    public IndexFiles() {
    }

    public IndexFiles(List<IndexFile> indexFiles) {
        IndexFiles = indexFiles;
    }

    public List<IndexFile> getIndexFiles() {
        return IndexFiles;
    }

    public void setIndexFiles(List<IndexFile> indexFiles) {
        IndexFiles = indexFiles;
    }
}
