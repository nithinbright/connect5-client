package com.example.connect5client;

import com.example.connect5client.domain.ChooseAndDropRequest;
import com.example.connect5client.domain.ChooseAndDropResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;
import java.util.Scanner;

@SpringBootApplication
public class Connect5ClientApplication {
	private static final String GET_BOARD_STATE_URL="http://localhost:8080/api/v1/grid";
	private static final String CHOOSE_AND_DROP_URL="http://localhost:8080/api/v1/drop/";
	private static final String GET_LAST_PLAYER_URL="http://localhost:8080/api/v1/lastplayer";
	private static final String GET_POLLING_URL= "http://localhost:8080/api/v1/poll";
	private static final String EXIT_PLAYER_URL= "http://localhost:8080/api/v1/exit/";
	private static final String ENTER_PLAYER_URL= "http://localhost:8080/api/v1/enter/";
	private String playerName;
	private Scanner scanner;
	private RestTemplate restTemplate;

	public Connect5ClientApplication(){
		scanner=new Scanner(System.in);
		restTemplate=new RestTemplate();

	}

	public static void main(String[] args) {
		SpringApplication.run(Connect5ClientApplication.class, args);



		Connect5ClientApplication connect5ClientApplication = new Connect5ClientApplication();
		connect5ClientApplication.startGame();


	}

	private  void startGame() {
			System.out.println("*****WELCOME TO CONNECT5 GAME******");
			//Ask for username
			askPlayerName();
			//Display the grid
		makeAMove();

	}

	private void makeAMove() {
		displayTheBoard();
		askForColumnSelectionOrExit();
		waitForTheNextMove();
	}

	private void waitForTheNextMove() {
		outer:
		while(true){
			//Check for the Turn
			if (waitForTheOtherPlayer()) break outer;
			//Check if the Other player won the game or it is a draw or the other player exited the game. If so, it is game over
			checkForBroadcastMessage();


			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//start the next move
		makeAMove();
	}

	private void checkForBroadcastMessage() {
		ResponseEntity<String> response
				= restTemplate.getForEntity(GET_POLLING_URL, String.class);
		if(response.getStatusCode()==HttpStatus.OK){
			String broadcastMessage= response.getBody();
			if(broadcastMessage!= null && broadcastMessage!= ""){
				//If there is a broadcast message, it means that it's game over.
				//Display the message
				System.out.println(broadcastMessage);

				//Display the Board
				displayTheBoard();

				//Exit the game
				System.exit(0);

			}
		}

	}

	private boolean waitForTheOtherPlayer() {
		System.out.println("###### WAITING FOR THE OTHER PLAYER TO MAKE HIS MOVE #####");
		//Check the last player
		ResponseEntity<String> response
				= restTemplate.getForEntity(GET_LAST_PLAYER_URL, String.class);
		if(response.getStatusCode()== HttpStatus.OK){
			String lastPlayer= response.getBody();
			if(lastPlayer!= null) {
				if (!playerName.equalsIgnoreCase(lastPlayer)){
					return true;
				}
			}
		}
		return false;
	}

	private void askPlayerName() {
		System.out.println("Enter your name: ");
		playerName = scanner.nextLine();
		//Enter the game
		enterTheGame(playerName);
	}

	private void displayTheBoard() {
		//Get the Board state from the Connect5 server
		ResponseEntity<String> response
				= restTemplate.getForEntity(GET_BOARD_STATE_URL, String.class);
		System.out.println(response.getBody());
	}

	private void askForColumnSelectionOrExit() {
		System.out.println("------------------------------------------------- ");
		System.out.println("Enter P to make your next move OR X for Exiting the Game");
		System.out.println("------------------------------------------------- ");
		String userInput= scanner.nextLine();
		if(userInput!= null && userInput!= ""){
			if(userInput.equalsIgnoreCase("P")){
				System.out.println("Enter the column number between 0-8 for the drop");
				String columnNumber= scanner.nextLine();
				int column= Integer.parseInt(columnNumber);
				makeTheMoveAndUpdateTheBoard(column);


			}else{
				//Let the Connect5 server know of the exit
				exitTheGame(playerName);
				System.exit(0);
			}

		}

	}

	private void exitTheGame(String playerName) {
		HttpEntity<String> request = new HttpEntity<>("");
		ResponseEntity<Boolean> response=restTemplate.postForEntity(EXIT_PLAYER_URL+playerName, request, Boolean.class);
		if(response.getStatusCode()== HttpStatus.OK){
			if(response.getBody()!= null){
				Boolean isExitSucessful= response.getBody();
				if(isExitSucessful){
					System.out.println("###You Successfully Exited the Game ####");
				}else{
					System.out.println("###There were some issues with the Exit ####");
				}
			}
		}
	}

	private void enterTheGame(String playerName) {
		HttpEntity<String> request = new HttpEntity<>("");
		ResponseEntity<String> response=restTemplate.postForEntity(ENTER_PLAYER_URL+playerName, request, String.class);
		if(response.getStatusCode()== HttpStatus.OK){
			if(response.getBody()!= null){
				String playerSymbol= response.getBody();
				if(playerSymbol!=null){
					System.out.println(playerSymbol);
				}
			}
		}
	}

	private void makeTheMoveAndUpdateTheBoard(int column) {
		ResponseEntity<ChooseAndDropResponse> response = getChooseAndDropResponseResponseEntity(column);
		if(response.getStatusCode()== HttpStatus.OK) {
			System.out.println(response.getBody().getMessage());
			displayTheBoard();
			//Check for end of the game
			ChooseAndDropResponse chooseAndDropResponse= response.getBody();
			if(chooseAndDropResponse!= null){
				if(chooseAndDropResponse.getGameOver()!= null) {
					if (chooseAndDropResponse.getGameOver() == true) {
						System.exit(0);
					}
				}
			}
		}else if(response.getStatusCode()==HttpStatus.BAD_REQUEST){
			System.out.println("###Please Wait for your Turn###");
		}
	}

	private ResponseEntity<ChooseAndDropResponse> getChooseAndDropResponseResponseEntity(int column) {
		ChooseAndDropRequest chooseAndDropRequest= new ChooseAndDropRequest();
		chooseAndDropRequest.setColumn(column);
		//Make a POST request to the Connect5 server
		HttpEntity<ChooseAndDropRequest> request = new HttpEntity<>(chooseAndDropRequest);
		return restTemplate.postForEntity(CHOOSE_AND_DROP_URL+playerName, request, ChooseAndDropResponse.class);
	}

}
