import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The sender class for a p2p file sharing app reading the files and sending them one by one
 * 
 * @author Islam Zidan
 * @version 0.1
 */
public class Sender
{
    
    //Instance variables
    private static final int PORT = 8888;
    private OutputStream os = null;
    private DataOutputStream dos = null;
    private FileInputStream fis = null;
    private BufferedInputStream bis = null;
    private Socket server = null;
    private ArrayList<String> paths = new ArrayList<String>();
    private ArrayList<String> files = new ArrayList<String>();
    private String path = "Ishare/";

    /**
     * Constructor takes the file or directory name to be sent
     * 
     * @param the file or directory name to be sent
     */
    public Sender(ArrayList<String> files)
    {
        this.files = files;
    }

    /**
     * setup socket and output stream
     * 
     */
    private void setupConnection()
    {
        try{
            //creating a server socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("waiting for receiver");
            server = serverSocket.accept();
            System.out.println("receiver is connected");
            
            //getting output streams
            os= server.getOutputStream();
            dos = new DataOutputStream(os);
        }
        catch(IOException e)
        {
           System.out.println("the port may be currently in use or the fire wall is blocking the application . please allow access then try again later\nexiting");
           System.exit(0);
        }
        catch(Exception e)
        {
            System.out.println("unexpected error . try again later\nexiting");
            System.exit(-1);
        }
    }
    
    
     /**
     * the sending method that will swnd the files
     * 
     */
    
    public void send()
    {
        setupConnection();
        try{
            for(String fileName : files){
                File file = new File(fileName);
                if(file.isFile())
                {
                    sendFile(file,fileName);
                }
                else
                {
                    sendDirectory(file);
                }
            }
            dos.writeInt(-5);
        }
        catch(Exception e)
        {
            System.out.println("unexpected error . try again later\nexiting");
            System.exit(-1);
        }
    }
    
     /**
     * a helper method to send individual files used by send() and sendDirectory()
     * 
     * @param file a file object representing an individual file
     */
    
    private void sendFile(File file,String path)
    {
        //setup connection
        try{
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            byte[] myByteArray = new byte[(int) file.length()];
            bis.read(myByteArray,0,(int)file.length());
            System.out.println("Sending file : "+file.getName());
            dos.writeInt((int)file.length());
            if(path.contains("Ishare/")){
                dos.writeUTF(path);
            }
            else
            {
                dos.writeUTF("Ishare/"+path);
            }
            os.write(myByteArray,0,(int)file.length());
            System.out.println("File sent\nexiting");
            fis.close();
            bis.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("this file doesn't exist\nexiting");
            System.exit(0);
        }
        catch(IOException e)
        {
           System.out.println("not enough memory or the file is corrupt or unreadable \nexiting");
           System.exit(0);
        }
        catch(Exception e)
        {
            System.out.println("unexpected error . try again later\nexiting");
            System.exit(-1);
        }
    }
    
     /**
     * a helper recursive method to send directories used by send()
     * 
     * @param a file object representing the directory to be sent
     */
    private void sendDirectory(File file)
    {
        path += file.getName()+"/";
        File[] files = file.listFiles();
        if(files != null)
        {
            if(files.length == 0)
            {
                paths.add(path);
                sendED();
            }
            for(File i : files)
            {
                if(i.isFile())
                {
                    paths.add(path+i.getName()+"/");
                    sendFile(i,paths.get(paths.size()-1));
                }
                else
                {
                    sendDirectory(i);
                    path = path.substring(0,path.length()-1);
                    path = path.substring(0,path.lastIndexOf("/")+1);
                }
            }
        }
    }
    /**
     * a helper method sends an empty directory used by sendDirectory()
     * 
     */
    private void sendED(){
        try
        {
            dos.writeInt(0);
            dos.writeUTF("ED:"+paths.get(paths.size()-1));
        }
         catch(IOException e)
        {
           System.out.println("not enough memory or the file is corrupt or unreadable\nexiting");
           System.exit(0);
        }
        catch(Exception e)
        {
            System.out.println("unexpected error . try again later\nexiting");
            System.exit(-1);
        }
    }
    
    /**
     * a method to close connection
     * 
     */
    
    public void finalize() throws IOException
    {
        server.close();
        dos.close();
        os.close();
        fis.close();
        bis.close();
   }
}
