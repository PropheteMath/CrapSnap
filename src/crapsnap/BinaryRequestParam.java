package crapsnap;

public class BinaryRequestParam extends RequestParam {
    private String fileName;
    private String contentType;
    private byte[] data;
    
    public BinaryRequestParam(String name, String fileName, String contentType, byte[] data) {
        this.name = name;
        this.fileName = fileName;
        this.contentType = contentType;
        this.data = data;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }
}
