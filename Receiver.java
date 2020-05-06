import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.File;
import java.net.UnknownHostException;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * The receiver class for a p2p file sharing app receiving the files and writing them one by one
 * 
 * @author Islam Zidan 
 * @version 0.1
 */
public class Receiver
{
    private static final int PORT = 8888;
    private String host;
    private Socket socket = null;
    private InputStream is = null;
    private DataInputStream dis = null;
    private FileOutputStream fos = null;
    private BufferedOutputStream bos = null;
    
    /**
     * Constructor for objects of class Receiver
     * 
     * @param String host representing the host to connect to
     */
    public Receiver(String host)
    {
        // initialise instance variables
        this.host = host;
    }

    /**
     * setup socket and output stream
     * 
     */
    private void setupConnection()
    {
        try{
            //creating a client socket
            socket = new Socket(host,PORT);
            
            //getting input streams
            is= socket.getInputStream();
            dis = new DataInputStream(is);
        }
        catch(UnknownHostException e)
        {
            System.out.println("the sender is not online\nexiting");
            System.exit(0);
        }
        catch(IOException e)
        {
           System.out.println("the port is currently in use or the firewall is blocking the application please allow access then try again later\nexiting");
           System.exit(0);
        }
        catch(Exception e)
        {
            System.out.println("unexpected error . try again later\nexiting");
            System.exit(-1);
        }
    }
    
    public void receive()
    {
        try
        {
            setupConnection();
            int length = 0;
            while((length = dis.readInt()) != -5)
            {
                readFile(length);
            }
        }        
        catch(Exception e)
        {
            System.out.println("the port is busy . try again later\nexiting");
            System.exit(0);
        }
    }
    
    private void readFile(int length1)
    {
        try{
            //get file length
            System.out.println("recieving file");
            int length = length1;
            System.out.println("i read the length and it is equal to " + length);
            String fileName = dis.readUTF();
            System.out.println("i read the fileName and it is equal to " + fileName);
            
            if((length == 0) && (fileName.substring(0,3).equals("ED:")))
            {
                File file = new File(fileName.substring(3,fileName.length()));
                file.mkdirs();
                return;
            }
            
                
            //read the file from the streams
            File file = new File(fileName);
            file.getParentFile().mkdirs();
            file.createNewFile();
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            byte[] myByteArray = new byte[length];
            is.read(myByteArray,0,length);
            bos.write(myByteArray,0,length);
                
            System.out.println("File recieved\nexiting");
            bos.close();
            fos.close();
        }
        catch(IOException e)
        {
            System.out.println("not enough memory or file is corrupt . try again later\nexiting");
            System.exit(0);
        }
        catch(Exception e)
        {
            System.out.println("unexpected error . try again later\nexiting");
            System.exit(-1);
        }
    }
    
    public void finalize() throws IOException
    {
            //close the socket and streams
            socket.close();
            bos.close();
            fos.close();
            dis.close();
            is.close();
    }
}
