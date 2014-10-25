package crapsnap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.HttpConnection;
import net.rim.device.api.io.transport.ConnectionDescriptor;

public class Sender {
	
    private static final int DATA_CHUNK_SIZE = 1024;
    private static final String MULTIPART_CONTENT_TYPE = "multipart/form-data; boundary=" + PostPrameters.BOUNDARY_MARKER;
    
    public static String send(byte[] bytes, String url) throws Exception {
        HttpConnection connection = null;
        OutputStream outputStream = null;

        try {
            MyConnectionFactory factory = new MyConnectionFactory();
            ConnectionDescriptor connDesc =  factory.getConnection(url);
            connection =  (HttpConnection)connDesc.getConnection();
            connection.setRequestMethod(HttpConnection.POST);
            connection.setRequestProperty("Content-Type", MULTIPART_CONTENT_TYPE);
            connection.setRequestProperty("Content-Length", String.valueOf(bytes.length));
            outputStream = connection.openOutputStream();
            int offset = 0;
            int chunk = DATA_CHUNK_SIZE;
            int len = bytes.length;
            while (offset < len) {
                if (offset + chunk >= len) {
                    chunk = len - offset;
                }
                outputStream.write(bytes, offset, chunk);
                offset += chunk;                
            }
            return getResponseAsSting(connection);
        } finally {
            safelyCloseStream(outputStream);
            safelyCloseStream(connection);
            if (bytes != null) {
                bytes = null; // notify VM it can safely free the RAM
            }
        }
    }
    
    private static String getResponseAsSting(HttpConnection conn) throws IOException {
        String result = "";
        InputStream inputStream = null;
        try {
            inputStream =  conn.openInputStream();
            int len;
            byte[] data = new byte[512];
            do {
                len = inputStream.read(data);
                if(len > 0){
                    result += new String(data, 0, len);
                }
            } while(len > 0);
        } catch (Exception e) {
            if (e instanceof IOException) {
                throw (IOException)e;
            }
        } finally { 
            safelyCloseStream(inputStream); 
        }
        return result;
    }
    
    private static void safelyCloseStream(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) { /* that's ok */ }
        }
    }
    
    private static void safelyCloseStream(OutputStream stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) { /* that's ok */ }
        }
    }
    
    private static void safelyCloseStream(HttpConnection stream) {
        if (stream != null) {
            try {
                stream.close();
                stream = null;
            } catch (Exception e) { /* that's ok */ }
        }
    }
}
