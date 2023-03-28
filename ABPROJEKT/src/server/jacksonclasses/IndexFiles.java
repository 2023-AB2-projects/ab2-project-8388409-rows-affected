package server.jacksonclasses;

public class IndexFiles {
    private IndexFile[] IndexFiles;

    public IndexFiles(IndexFile[] indexFiles) {
        IndexFiles = indexFiles;
    }

    public IndexFile[] getIndexFiles() {
        return IndexFiles;
    }

    public void setIndexFiles(IndexFile[] indexFiles) {
        IndexFiles = indexFiles;
    }
}
