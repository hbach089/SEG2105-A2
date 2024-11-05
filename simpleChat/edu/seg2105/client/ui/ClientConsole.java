package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String host, int port,String loginid) 
  {
    try 
    {
      client= new ChatClient(host, port, this,loginid);
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        client.handleMessageFromClientUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
	if(message.contains("SERVER MSG>")) {
		System.out.println(message);
	}
	else {
		System.out.println("> " + message);
	}
    
  }

  
  /**
   * This checks if the given ip address is in correct 
   * IPv4 format.  
   *
   * @param The ip to be verified.
   * 
   * @return true or false based on validity.
   */
  private static boolean isValidIPv4(String ip) {
    // Separate the given string into an array of strings using the dot as delimiter
    String[] parts = ip.split("\\.");

    // Check if there are exactly 4 parts (valid IPv4)
    if (parts.length != 4) { 
        return false;
    }

    // Check each part for valid number (integer)
    for (String part : parts) {
        try {
            //Convert each part into an integer
            int num = Integer.parseInt(part);

            //Check whether the number lies in between 0 to 255
            if (num < 0 || num > 255) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    return true;
}
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
	String loginid="";
    String host = "";
    int port=0;
    
    try
    {
    //Checks if logidID is given by user. Displays error and quits if not.
	  if(args[0].equals("localhost") || isValidIPv4(args[0]) || args.length<1) {
	  	System.out.println("ERROR - No login ID specified.  Connection aborted.");
	  	System.exit(0);
	  }
	  loginid=args[0];
      host = args[1];
      port=Integer.parseInt(args[2]);
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      host = "localhost";
      port=DEFAULT_PORT;
    }
    catch(NumberFormatException no) {
    	port=DEFAULT_PORT;
    }
    //Launches a UI for the newly connected client
    ClientConsole chat= new ClientConsole(host, port,loginid);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
