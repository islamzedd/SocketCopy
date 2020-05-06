import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.lang.*;

public class Main 
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("Enter files and folders and to exit type q");
        ArrayList<String> files = new ArrayList<String>();
        do
        {
            System.out.println("Enter file name : ");
            Scanner in = new Scanner(System.in);
            String fileName = in.nextLine();
            if(fileName.equals("q") || fileName.equals(""))
            {
                break;
            }
            files.add(fileName);
        }while(true);
        Sender sender = new Sender(files);
        sender.send();
        sender.finalize();
    }
}