package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
//Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF serverUI; 
  
  String loginid;
  String loginkey="loginID";
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
//  public EchoServer(int port) 
//  {
//    super(port);
//  }
  public EchoServer(int port,ChatIF serverUI) 
  {
    super(port);
    this.serverUI=serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  String message=msg.toString();
	  String clientloginid = null;
	  
	  // If client is newly connected
	  if(message.contains("#login") && client.getInfo(loginkey) == null) {
		  
		  //Gets substring before " ", meaning the logindID
		  loginid=message.substring(message.lastIndexOf(" ")+1);
		  
		  //inputs each loginID in a hashmap
		  client.setInfo(loginkey, loginid);
		  
		  serverUI.display("Message received: "+message+" from "+clientloginid+".");
		  serverUI.display(message.substring(message.lastIndexOf(" ")+1)+" has logged on.");
	  }
	  // If client is already connected
	  else if(client.getInfo(loginkey) != null){
		  clientloginid=client.getInfo(loginkey).toString();
		  serverUI.display("Message received: " + message + " from " + clientloginid);	
		  if(message.contains("#login")) {
			  try {
				client.sendToClient("Client cannot use #login a second time!");
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }
		  this.sendToAllClients(clientloginid+": "+message);

	  }
	  
  }
    
  
  /**
   * This method handles all commands coming from the server UI            
   *
   * @param message The message from the UI.    
   */
  private void handleCommand(String message) throws IOException {
	  //based on the command entered, it performs different functions
	  if(message.equals("#quit")) {
		  serverUI.display("Server is now quitting.");
		  System.exit(0);	  }
	  else if(message.equals("#stop")) {
		  stopListening();
	  }
	  else if(message.equals("#close")) {
		  close();
	  }
	  else if(message.equals("#getport")) {
		  serverUI.display(String.valueOf(getPort()));
	  }
	  else if(message.contains("#setport")) {
		  if(!isListening()) {
			  int port=Integer.valueOf(message.substring(message.lastIndexOf(" ")+1));
			  setPort(port);
		  }
		  else {
			  serverUI.display("Server must be stopped to change port.");
		  }
		  
	  }
	  else if(message.equals("#start")) {
		  if(isListening()) {
			  serverUI.display("Server already is listening.");
		  }
		  else {
			  try {
				listen();
			} catch (IOException e) {
				e.printStackTrace();
			}
		  }
	  }
	  else {
		  serverUI.display("Please enter a valid command.");
	  }
  }
    
  
  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromServerUI(String message) throws IOException 
  {    
	  if(message.startsWith("#")) {
		  handleCommand(message);
		}
		else {
			serverUI.display("SERVER MSG> "+message);
		    this.sendToAllClients("SERVER MSG> "+message);
		}
  }
  
  
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
	 * Hook method called each time a new client connection is
	 * accepted. The default implementation does nothing.
	 * @param client the connection connected to the client.
	 */
  @Override
	protected void clientConnected(ConnectionToClient client) {
	  	serverUI.display("A new client has connected to the server.");
	}
  
  /**
	 * Hook method called each time a client disconnects.
	 * The default implementation does nothing. The method
	 * may be overridden by subclasses but should remains synchronized.
	 *
	 * @param client the connection with the client.
	 */
  @Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
	  serverUI.display(client.getInfo(loginkey).toString()+" has disconnected.");
	  
	  if(!client.toString().equals("null")) {
		  serverUI.display("CLIENT "+client+" just disconnected!");
	  }
  }
  
  
  
  /**
	 * Hook method called when the server is closed.
	 * The default implementation does nothing. This method may be
	 * overriden by subclasses. When the server is closed while still
	 * listening, serverStopped() will also be called.
	 */
  @Override
	protected void serverClosed() {
	}
  
  /**
	 * Hook method called each time an exception is thrown in a
	 * ConnectionToClient thread.
	 * The method may be overridden by subclasses but should remains
	 * synchronized.
	 *
	 * @param client the client that raised the exception.
	 * @param Throwable the exception thrown.
	 */
  @Override
	synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	}
}
//End of EchoServer class
