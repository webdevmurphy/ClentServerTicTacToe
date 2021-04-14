package murphy.proj4.TicTacToe;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;


//server
public class GameRunnable implements Runnable {
	private Socket socket;
	private GameController controller;
	private boolean done = false;

	/**
	 * This creates a game of tic tac toe
	 * @param socket -- Connection socket
	 * @param controller -- controller for 
	 */
	public GameRunnable(Socket socket, GameController controller) {
		this.socket = socket;
		this.controller = controller;
	}
	
	
	public void run() {

		PrintWriter out = null;
		BufferedReader in =null;

		try {
			
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true ); // true indicates print writer is auto flush format, println, printf, but write does not flush
			
			String request;

			while(!done) {
				request = in.readLine();
				String response = processRequest(request);
				out.println(response);	
			}
			
			
		}catch(Exception e){
			System.out.println("Error run!");
			System.out.println(e);	
		}	
		
	} //end run

	
	private String processRequest(String request) {
	String response = "";
	
	String[] resultList = request.split(" ");
	
	String command = resultList[0];	
	
		if(command.equals("play") ) {
		  	
			//TODO Make random x or o select
			Random rand = new Random();
			int first = rand.nextInt(2) + 1;
			if(first == 1) {
				response = "symbol o";
				controller.serverMove();
			}else {
				response = "symbol x";
			}
			
		}
		else if(command.equals("status")) {
			
			switch(controller.getState()) {
			
			case Running:
				response = "move";
				break;
				
			case Draw:
				response = "draw";
				break;
				
			case Win:
				int winner = controller.victoryCheck();
				response = "win " + winner;
				break;
				
			 default:
			 
			}			
		}
		
		else if(command.equals("move")) {
			
			int clientMove = Integer.parseInt(resultList[1]);
			
			boolean validMove = controller.clientMove(clientMove);
			
			if(!validMove) {
				response = "illegal";
			}else {
				
				if(controller.getState() == GameController.GameState.Running ) {
					
					int faqu = controller.serverMove();
					
					if(controller.getState() == GameController.GameState.Running) {
						response = "server " + faqu;
					}else {
						response = "ended";
					}
				}else {
					response = "ended";
				}
			}
		}
		
		else if(command.equals("board")) {
			
			response = "board " + controller.boardString();
			
		}
		else if(command.equals("quit")) {
			done = true;
			response = "quit";
		}
		else {
			response = "Error!!";
		}
		
		return response;
	}
	
	
	
	
	
	


}
