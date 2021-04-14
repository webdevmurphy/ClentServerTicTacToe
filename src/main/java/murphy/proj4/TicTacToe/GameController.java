package murphy.proj4.TicTacToe;


//will be your thread
public class GameController {
	
	public enum GameState{
		Running, Draw, Win;
	}
	
	private GameState state;
	private int[] board = new int[9];
	private static final int SERVER = 1;
    private static final int CLIENT = 2;
    private static final int  EMPTY = 0;
	
public GameController() {
	this.state = GameController.GameState.Running;
}

public GameState getState() {
	return state;
}

public boolean moveValidator(int position) {
	
	if(position < 0 || position > 8 ) {
		return false;
	}
	
	if(board[position] != EMPTY) {
		return false;
	}
	
	return true;
}

public String boardString() {
	String result = "";
	//Enhanced for loop// tech: foreach loop
	for(int i: board) {
		result = result + i;
	}

	return result;
}

public int victoryCheck() {
	//top rows
	if(board[0] != EMPTY && board[0] == board[1] && board[0] == board[2]) {
		state = GameController.GameState.Win;
		return board[0];
	}
	//middle rows
	if(board[3] != EMPTY && board[3] == board[4] && board[3] == board[5]) {
		state = GameController.GameState.Win;
		return board[3];
	}
	//check the bottom rows
	if(board[6] != EMPTY && board[6] == board[7] && board[6] == board[8]) {
		state = GameController.GameState.Win;
		return board[6];
	}
	//first left column
	if(board[0] != EMPTY && board[0] == board[3] && board[0] == board[6]) {
		state = GameController.GameState.Win;
		return board[0];
	}
	//middle column
	if(board[1] != EMPTY && board[1] == board[4] && board[1] == board[7]) {
		state = GameController.GameState.Win;
		return board[1];
	}
	//last column
	if(board[2] != EMPTY && board[2] == board[5] && board[2] == board[8]) {
		state = GameController.GameState.Win;
		return board[2];
	}
	//topl botr diag
	if(board[0] != EMPTY && board[0] == board[4] && board[0] == board[8]) {
		state = GameController.GameState.Win;
		return board[0];
	}
	//other diag
	if(board[2] != EMPTY && board[2] == board[4] && board[2] == board[6]) {
		state = GameController.GameState.Win;
		return board[2];
	}
	
	//check for a draw
	if(!boardString().contains("0")) {
		state = GameController.GameState.Draw;
	}
	
  return 0;
}

public int serverMove() {

	int position = 0;

	for(int i = 0; i < board.length; i++) {
		if(board[i] == EMPTY) {
			board[i] = SERVER;
			position = i;
			break;
		}
	}
	
	victoryCheck();

	return position;
}

public boolean clientMove(int position) {
	
	boolean isValid = moveValidator(position);
	
	if(!isValid) {
		return false;
	}
	
	//setting the int value of the board position
	board[position] = CLIENT;
	
	victoryCheck();
	
	return true;
}

}
