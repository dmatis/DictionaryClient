
import java.lang.System;
import java.io.*;
import java.net.*;

//
// This is an implementation of a simplified version of a command 
// line dictionary client. The program takes no arguments.
//


public class CSdict
{
    static final int MAX_LEN = 255;
    static final int PERMITTED_ARGUMENT_COUNT = 1;
    static Boolean debugOn = false;
    //PO
    private static Socket skt;
    private static PrintWriter out;
    private static BufferedReader in;
	private static String curDict = "*";
	private static boolean connectionOpen = false;
	private static String curServer = "";
	private static int curPort = 0; 

    
    public static void main(String [] args)
    {
		byte cmdString[] = new byte[MAX_LEN];
		
		if (args.length == PERMITTED_ARGUMENT_COUNT) {
		    debugOn = args[0].equals("[-d]");
		    if (debugOn) {
			System.out.println("Debugging output enabled");
		    } else {
			System.out.println("997 Invalid command line option - Only -d is allowed");
			return;
	            } 
		} else if (args.length > PERMITTED_ARGUMENT_COUNT) {
		    System.out.println("996 Too many command line options - Only -d is allowed");
		    return;
		}
			
		try {
		    for (int len = 1; len > 0;) {
		    	cmdString = new byte[MAX_LEN];
				System.out.print("csdict> ");
				len = System.in.read(cmdString);

				
				if (len <= 0){
				    break;
				}
				
				// Start processing the command here.
				String decoded = new String(cmdString, "UTF-8");
				String[] line = decoded.trim().split("\\s+");
				String command = line[0].toLowerCase().trim();
				
				if(command.toLowerCase().equals("open")){
					if(connectionOpen){
						System.out.println("903 Supplied command not expected at this time.");
					}
					else{
						int portNum = 2628;
						if(line.length != 2 && line.length != 3) {
							System.out.println("901 Incorrect number of arguments: " + line.length);
							displayLine(line);
						}
						else if (line.length == 3) {
							portNum = Integer.parseInt(line[2]);
						}
						try {
							curServer = line[1];
							curPort = portNum;
							openConnection(curServer,curPort);
						}
						catch (Exception e) {
							System.out.println("902 Invalid argument.");
						}
						setDictionary("*");
					}
				}
				
				else if(command.toLowerCase().equals("dict")){
					if (!connectionOpen) {
						System.out.println("903 Supplied command not expected at this time.");
					}
					else {
						if(line.length != 1){
							System.out.println("901 Incorrect number of arguments: " + line.length);
						}
						else{
							retrieveDictionaries();
						}
					}
				}
				else if(command.toLowerCase().equals("set")){
					if (!connectionOpen) {
						System.out.println("903 Supplied command not expected at this time.");
					}
					else {
						if(line.length != 2){
							System.out.println("901 Incorrect number of arguments.");
						}
						else{
							setDictionary(line[1]);
						}
					}
				}
				else if(command.toLowerCase().equals("currdict")){
					if (!connectionOpen) {
						System.out.println("903 Supplied command not expected at this time.");
					}
					else {
						if(line.length != 1){
							System.out.println("901 Incorrect number of arguments.");
						}
						else{
							displayDictionary();
						}
					}
				}
				else if(command.toLowerCase().equals("define")){
					if (!connectionOpen) {
						System.out.println("903 Supplied command not expected at this time.");
					}
					else {
						if(line.length != 2){
							System.out.println("901 Incorrect number of arguments.");
						}
						else{
							defineWord(line[1]);
						}
					}
				}
				else if(command.toLowerCase().equals("match")){
					if (!connectionOpen) {
						System.out.println("903 Supplied command not expected at this time.");
					}
					else {
						if(line.length != 2){
							System.out.println("901 Incorrect number of arguments.");
						}
						else{
							matchWord(line[1], false);
						}
					}
				}
				else if(command.toLowerCase().equals("prefixmatch")){
					if (!connectionOpen) {
						System.out.println("903 Supplied command not expected at this time.");
					}
					else {
						if(line.length != 2){
							System.out.println("901 Incorrect number of arguments.");
						}
						else{
							matchWord(line[1], true);
						}
					}
				}
				else if(command.toLowerCase().equals("close")){
					if (!connectionOpen) {
						System.out.println("903 Supplied command not expected at this time.");
					}
					else {
						if(line.length != 1){
							System.out.println("901 Incorrect number of arguments.");
						}
						else{
							closeConnection();
							System.out.println("Next expected command is open or quit");
						}
					}
				}
				else if(command.toLowerCase().equals("quit")){
					closeConnection();
					len = 0;
				}
				else if(command.equals("#") || command.isEmpty()){
					// fail silently
				}
				else{
					System.out.println("900 Invalid command.");
				}
		    }
		} catch (IOException exception) {
		    System.err.println("998 Input error while reading commands, terminating.");
		}
    }
    
	private static void debugServer(String s){
		if(debugOn){
			System.out.println("<-- " + s);
		}
	}
	
	private static void debugClient(String s){
		if(debugOn){
			System.out.println("--> " + s);
		}
	}
    private static void openConnection(String server, int port){
    	try {    		
	        skt = new Socket(server, port);
	        in = new BufferedReader(new InputStreamReader(skt.getInputStream()));
	        out = new PrintWriter(skt.getOutputStream(), true);
				
	        while (!in.ready()) {}
			debugServer(in.readLine()); // Read one line and output it
			connectionOpen = true;
	     }
    	catch(SocketTimeoutException  e) {
	        System.out.println("920 Control connection to " + curServer + " on port " + curPort + " failed to open");
	     }
		 catch(Exception e) {
	        System.out.println("902 Invalid argument.");
	     }
    }
    
    private static void closeConnection(){
    	try{
    		skt.close();
			connectionOpen = false;
			curServer = "";
			curPort = 0;
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private static void retrieveDictionaries(){
    	try{
			String command = "SHOW DB";
    		out.println(command);
			debugClient(command);
    		String fromServer;
    	
    		while((fromServer = in.readLine()) != null){
	    		
	    		if(fromServer.startsWith("110")){
					debugServer(fromServer);
				}
				else if(fromServer.startsWith("250 ok")){
					debugServer(fromServer);
	    			break;
				}
				else{
					System.out.println(fromServer);
				
				}
	    	}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}   
    }
	
	private static void setDictionary(String dictName){
    	curDict = dictName;
    }
    
    private static void displayDictionary(){
    	System.out.println(curDict);
    }
    
    private static void matchWord(String word, boolean prefixMatch){
		try{
			String fromServer;
	    	String strategy;
			if(prefixMatch){
				strategy = " prefix ";
			}
			else{
				strategy = " exact ";
			}
			String command = "MATCH " + curDict + strategy + word;
			debugClient(command);
    		out.println(command);
			fromServer = in.readLine();
			if (fromServer.startsWith("550")) {
				debugServer(fromServer);
    			System.out.println("930 Dictionary does not exist.");
    			return;
    		}
    		else if (fromServer.startsWith("552")) {
				debugServer(fromServer);
				System.out.println("****No matching word(s) found****");
				return;
			}
			while((fromServer = in.readLine()) != null){
	    		if(fromServer.startsWith("250")){
					debugServer(fromServer);
	    			return;
	    		}
	    		System.out.println(fromServer);
	    	}
		}
		catch(Exception e){
			e.printStackTrace();
		}
    }
    
    private static void defineWord(String word){
		try{
			String command = "DEFINE " + curDict + " " + word;
	    	debugClient(command);
			out.println(command);
    		String fromServer;
    		boolean returnedOtherDictResults = false;
			
			while((fromServer = in.readLine()) != null){
				if (fromServer.startsWith("150")) {
					debugServer(fromServer);				
				}
				else if (fromServer.startsWith("550")) {
					debugServer(fromServer);
					System.out.println("930 Dictionary does not exist.");
					break;
				}
				else if (fromServer.startsWith("552")) {
					debugServer(fromServer);
					System.out.println("**No definition found**");
					String command2 = "DEFINE * " + word;
					debugClient(command2);
					out.println(command2);
						
					while ((fromServer = in.readLine()) != null) {
						if(fromServer.startsWith("552")){
							debugServer(fromServer);
							System.out.println("***No dictionaries have a definition for this word***");
							break;
						}

						else if(fromServer.startsWith("250")){
							debugServer(fromServer);
							returnedOtherDictResults = true;
							break;
						}
						else if(fromServer.startsWith("237")){
							debugServer(fromServer);
							System.out.println(fromServer.substring(4 + word.length()));
						}
						else if(fromServer.startsWith("151")){
							debugServer(fromServer);
							System.out.println("@ " + fromServer.substring(7 + word.length()));
						}
						else{
							System.out.println(fromServer);
						}
					}
					break;
				}
				else if (fromServer.startsWith("250") || returnedOtherDictResults) {
					debugServer(fromServer);
					break;
				}
				else if(fromServer.startsWith("237")){
					debugServer(fromServer);
					System.out.println(fromServer.substring(4 + word.length()));
				}
				else if(fromServer.startsWith("151")){
					debugServer(fromServer);
					System.out.println("@ " + fromServer.substring(7 + word.length()));
				}
				else{
					System.out.println(fromServer);
				}
			}
    	}
		catch(Exception e){
			e.printStackTrace();
		}
	}

    private static void displayLine(String[] line){
    	for(String s : line){
    		System.out.println("Line item: " + s);
    	}
    }
}