package crapsnap;

public class RequestParam {
    protected String name;
    private String value;
    
    public RequestParam () {
        
    }
    
    public RequestParam (String name, String value) {
        this.name = name;
        this.value = value;
    }
    
    public byte[] getData() {
        return value.getBytes();
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}