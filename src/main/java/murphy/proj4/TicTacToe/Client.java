package murphy.proj4.TicTacToe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class Client extends Application {

	private final int Port = 8764;
    private Socket server = null;
    private InputStream serverIn = null;
    private OutputStream serverOut = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private boolean isConnected = false;
	private ArrayList<Label> gameTiles = new ArrayList();
	private String playerIs;
	private String serverIs;
	
	
public void start(Stage primaryStage) throws Exception {

	Button Connection = new Button ("Connect");
     
     Connection.setOnAction( (event) -> {
    	 connect();
     });
     
     GridPane gameBoard =new GridPane(); 
     
     DropShadow dropShadow = new DropShadow();
     dropShadow.setRadius(5.0);
     dropShadow.setOffsetX(3.0);
     dropShadow.setOffsetY(3.0);
     dropShadow.setColor(Color.color(0.4, 0.5, 0.5));
     
    //create the game board grid capture mouse events. 
     for(int i = 0; i < 3; i ++) {
    	 
    	 for(int j = 0; j < 3; j++) {
    		 Label tile = new Label(" ");
    	     tile.setMinWidth(100);
    	     tile.setMinHeight(100);
    	     tile.setEffect(dropShadow);
    	     tile.setStyle("-fx-border-color: red;" + "-fx-font-size: 80px; ");
    	     tile.setAlignment(Pos.CENTER);

    	     int position = (3 * i) + j;
    	     
    	     tile.setOnMouseClicked((event) -> {
    	    	 move(position);
    	     });
    	     gameBoard.add(tile,i, j);
    	     gameTiles.add(tile);
    	 }
    	 
     }
     
     
     Scene scene = new Scene(gameBoard,300,400);   
     gameBoard.addRow(3, Connection); 
    
     
     primaryStage.setScene(scene);  
     primaryStage.setResizable(false);
     
     primaryStage.sizeToScene();	
     
     
     primaryStage.show();  
}
	

public void play() {

	  out.println("play");
      isConnected = true;
      
      for(Label tile : gameTiles) {
    	  tile.setStyle("-fx-border-color: black;" + "-fx-font-size: 80px; ");
    	  tile.setText(" ");
      }

      try {
    	  
      	String input = in.readLine();
      	
    	String[] resultList = input.split(" ");

    	if(resultList[0].equals("symbol")) {
    		playerIs =  resultList[1];
    		
    		if(playerIs.equals("x")) {
    			serverIs = "o";
    		}
    		else {
    			serverIs="x";
    		}
    	}
    	else {
    		//junk defender
    		System.out.println("If you are seeing this something is severly wrong, Server gave junk!");
    		return;
    	}
      	
		} catch (IOException e) {

			e.printStackTrace();
		}
      
      status();
}


public void move(int position) {

	if(!isConnected) {
		return;
	}

	out.println("move " + position);	
	
	try {
		String input = in.readLine();
		

		String[] moveRes = input.split(" ");
		
		if(moveRes[0].equals("illegal")) {
			return;
		}
		else if(moveRes[0].equals("server")) {
			status();
		}
		else if(moveRes[0].equals("ended")) {
			status();
		}
		else {
			System.out.println("Server gave junk, just in case... ");
			return;
		}
	}catch(Exception e) {
		
		System.out.println(e);
		
	}	
}//end move

public void status() {
	
	out.println("status");
	
	try {
		String input = in.readLine();
		
		String[] statusRes = input.split(" ");

		if(statusRes[0].equals("move")) {
			board();
			return;
		}
		else if(statusRes[0].equals("win")) {
			board();
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Game Over");
			alert.setHeaderText(null);
			
			if(statusRes[1].equals("1")) {
				alert.setContentText("Computer is the Winner!");
			}else {
				alert.setContentText("You are the winner!");
			}

			alert.showAndWait();
			quit();
		}
		else if(statusRes[0].equals("draw")) {
			board();
			
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Game Over");
			alert.setHeaderText(null);
			alert.setContentText("Draw, No Winners");
			alert.showAndWait();
			
			quit();
		}
		else {
			System.out.println("Server gave junk, just in case... ");
			return;
		}
	}catch(Exception e) {
		
		System.out.println(e);
		
	}	
}

public void board() {
	out.println("board");
	
	try {
		String input = in.readLine();
		
		String[] boardRes = input.split(" ");

		if(boardRes[0].equals("board")) {
			
			for(int i = 0; i < 9; i++) {
				String servRes = boardRes[1].substring( i , i+1);
				
				if(servRes.equals("1")) {
					gameTiles.get(i).setText(serverIs);
				}
				else if(servRes.equals("2")) {
					gameTiles.get(i).setText(playerIs);
				}

			}
			
		}else {
			System.out.println("Server giving junk, bad touch!");
		}
		
	}catch(Exception e) {
		
		System.out.println(e);
	}
}

public void quit() {
	out.println("quit");
	
	 for(Label tile : gameTiles) {
   	  tile.setStyle("-fx-border-color: red;" + "-fx-font-size: 80px; ");
     }
     
	try {
		String input = in.readLine();
		
		String[] quitRes = input.split(" ");
	
		if(quitRes[0].equals("quit")) {
			
		disconnect();
		
		}else {
			System.out.println("Server giving junk, bad touch!");
		}
		
	}catch(Exception e) {
		
		System.out.println(e);
	}
}


public void connect() {
	
	//if already connected don't try to connect again!
	if(isConnected) {
		return;
	}

       // Establish connection to game
       try {
           server = new Socket("localhost", Port);
           serverIn = server.getInputStream();
           serverOut = server.getOutputStream();
           in = new BufferedReader(new InputStreamReader(serverIn));
           out = new PrintWriter(serverOut, true);
       } catch (UnknownHostException e) {
           System.out.println("Error: Could not find host.");
       } catch (IOException e) {
           System.out.println("Error: Could not initialize connection.");
       }
       
       
        play();
}


public void disconnect() {
	
	   try {
           in.close();
           out.close();
           serverIn.close();
           serverOut.close();
           server.close();
       } catch (IOException | NullPointerException e) {
           System.out.println("Error: Could not close connection.");
       }
         isConnected = false;
}

	public static void main(String[] args) {
	
	           launch(args);

	    } // end of main			
} //end of class


