package murphy.proj4.TicTacToe;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
	
	private static final int PORT_NUMBER = 8764;
	
	public static void main(String[] args) {
		
		ServerSocket server = null;
		
		
		try {
			server = new ServerSocket(PORT_NUMBER);
			while(true) {
				//wait for connection
				//create new runnable 
				//pass the new socket
				//start the thread
				System.out.println("Socket wait for client......");
				Socket socket = server.accept(); //waits till clients connects
				System.out.println("Client Connected");
				GameRunnable g = new GameRunnable(socket, new GameController());
				System.out.println("Game Created");
				Thread t = new Thread(g);
				t.start();
				
			}
			
		}catch(IOException ex) {
			System.err.println("Unable to start server.");
		}finally {
			//close everything
			try {
				if (server != null)
					server.close();
			}catch(IOException ex) {
				ex.printStackTrace();
			}
			
			
			
			
		}

	}//end main
	
}
