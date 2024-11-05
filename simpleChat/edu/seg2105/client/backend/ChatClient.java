// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;
import ocsf.server.ConnectionToClient;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  /**
   * The loginid variable.  Client must enter loginID
   * as first argument for Client program to run.
   */
  private String loginid;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI,String loginid) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginid=loginid;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
//    clientUI.display((((String) msg).substring(((String) msg).indexOf(">")+1)).toString());
	  clientUI.display(msg.toString());
    
  }


  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
    	
    	if(message.startsWith("#")) {
    		handleCommand(message);
    	}
    	else {
//    		clientUI.display(message);
    		sendToServer(message);	
    	}
    	
    }
    catch(IOException e)
    {
      clientUI.display("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method handles all commands coming from the client UI            
   *
   * @param message The message from the UI.    
   */
  private void handleCommand(String message) throws IOException {
	  //based on the command entered, it performs different functions
	  if(message.equals("#quit")) {
		  quit();
	  }
	  else if(message.equals("#logoff")) {
		  closeConnection();
	  }
	  else if(message.contains("#sethost")) {
		  String host=message.substring(message.lastIndexOf(" ")+1);
		  setHost(host);
	  }
	  else if(message.contains("#setport")) {
		  int port=Integer.valueOf(message.substring(message.lastIndexOf(" ")+1));
		  setPort(port);
	  }
	  else if(message.contains("#login")) {
		  message=message.replaceAll("\\s+","");
		  clientUI.display(message);
		  if(message.equals("#login")){
			  if(!isConnected()) {
				  openConnection();
			  }
			  else {
				  clientUI.display("You are already connected to the server!");
			  }  
		  }
		  else {
			  this.sendToServer(message);
		  }
		  
	  }
	  else if(message.equals("#gethost")) {
		  clientUI.display(getHost());
	  }
	  else if(message.equals("#getport")) {
		  clientUI.display(String.valueOf(getPort()));
	  }
	  else {
		  clientUI.display("Please enter a valid command.");
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
    
  /**
	 * Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
	@Override
	protected void connectionException(Exception exception) {
		//UNE ERREUR DIFFERENTE?    CONNECTION TO CLIENT CLOSED....
		clientUI.display("The server has shut down.");
		System.exit(0);
	}
  
  
  /**
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  	@Override
	protected void connectionClosed() {
  		clientUI.display("Connection closed");
	}
  	
  	


	/**
	 * Hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
  	@Override
	protected void connectionEstablished() {
  		String messageOnConnect="#login "+loginid;
		try {
			sendToServer(messageOnConnect);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
//End of ChatClient class
