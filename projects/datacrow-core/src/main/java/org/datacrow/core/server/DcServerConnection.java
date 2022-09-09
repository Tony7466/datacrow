package org.datacrow.core.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.NoSuchPaddingException;

import org.apache.logging.log4j.Logger;
import org.datacrow.core.log.DcLogManager;

public class DcServerConnection {

    private transient static Logger logger = DcLogManager.getLogger(DcServerConnection.class.getName());
    
    private boolean isAvailable = true;
    
    private Socket socket;

    private ObjectInputStream is;
    private ObjectOutputStream os;
    
    public DcServerConnection(Connector conn) throws IOException, SocketException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException {
        socket = new Socket(conn.getServerAddress(), conn.getApplicationServerPort());
        socket.setKeepAlive(true);
        
        is = new ObjectInputStream(socket.getInputStream());
        os = new ObjectOutputStream(socket.getOutputStream());
    }
    
    public ObjectInputStream getInputStream() {
        return is;
    }
    
    public ObjectOutputStream getOutputStream() {
        return os;
    }
    
    public void disconnect() {
        try {
            is.close();
            os.close();
        } catch (Exception e) {
            logger.error("Error while closing connections", e);
        }
    }
    
    public void setAvailable(boolean b) {
        this.isAvailable = b;
    }
    
    public boolean isAvailable() {
        return isAvailable;
    }
    
    public boolean isActive() {
        return !socket.isClosed();
    }
}
