#connect5-client

This is the client project for the connect5 game. It connects to the connect5-server application over http on port 8080 (Calling some
REST APIs).



#Usage
1. Do a mvn clean install
2. jar file is generated in the target folder
3. Run java -jar connect5-client-0.0.1-SNAPSHOT.jar (This starts the client program which accepts user input and displays
the grid.

#Pre-requisite
connect-5-server application must be running before the client applications can be launched.

# STEPS to play the game

1. Open 2 seperate terminals one for each player
2. Run java -jar connect5-client-0.0.1-SNAPSHOT.jar in each of the 2 terminals
3. On starting the game, the player is prompted to enter the Name
4. Then Player can press P to play and X to exit the game.
5. On pressing P , the player is shown the Grid/Board and asked to enter a column number between 0-8
6. Then the game waits for the second player to make his move.
7. In the second terminal, Player 2 is prompted with above inputs and once he/she enters the column number, it waits for the other player to make a move.
8. Now the Player 1 can continue as described above.

#NOTE

1.At any point, if the column selection results in a WIN or draw, the game exits for the winning player. Also, the other player is informed that the player 1 has won and it's
a game over.

2. Similarly, if any of the player presses X to exit the game, the other player is informed of the exit and the game is over.

#Limitations
1. On Exit/WIN/DRAW of PLAYER1, the PLAYER2 who is waiting for his turn is informed and the game exits only if PLAYER2 is waiting. PLAYER2 is notified only while he tries
to make his next move.

#Implementation Notes

1.connect5-client is a java application that handles the player moves by interacting with the connect5-server application over REST APIS

#Improvements Needed

Major tests have to be implemented. Only skeleton tests have been written due to time constraints.
Polling logic can be improved to consider different codes for different events such as player exit,win,draw,etc.
Code can be refactored further to avoid hardcoding in some places.
