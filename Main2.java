import java.util.*;
import java.io.*;
import java.util.regex.*;
import java.lang.*;

public class Main2 
{
    public static void main(String[] args) throws IOException
    {
        Receiver receiver = new Receiver("localhost");
        receiver.receive();
        receiver.finalize();
    }
}