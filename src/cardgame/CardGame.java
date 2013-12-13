package cardgame;

import java.util.EventListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;

public class CardGame extends Thread implements CardGameListener {

	private final int numberOfPlayers;
	private final int handSize;
	private final Player[] players;
	private final CardDeck[] cardDecks;
	private CardDeck initialDeck;
	private volatile boolean gameOver = false;
    private Player winner = null;
    private UserInputThread userInputRunnable;
    private Thread userInputThread;
    private int playersConfirmed;
    private int playersTerminated;
    private volatile boolean inIntermediateState;
    private volatile boolean gamePaused = false;
    private int strategy;
    long startTime;



    CardGame(int numberOfPlayers, int handSize, CardDeck initialDeck) {
        this.initialDeck = initialDeck;
        this.numberOfPlayers = numberOfPlayers;
        this.handSize = handSize;
        this.players = new Player[numberOfPlayers];
        this.cardDecks = new CardDeck[numberOfPlayers];
        // remove and make static and final? what are final's implications?
        this.userInputRunnable = new UserInputThread();
        this.userInputThread = new Thread(this.userInputRunnable);
        this.playersConfirmed = 0;
        this.playersTerminated = 0;
        this.inIntermediateState = false;
        this.strategy = 1; // default to 1
    }

	public void run() {
        this.startTime = System.nanoTime();
        for (int i=0; i < this.numberOfPlayers; i++) {
        	// each deck should be able to hold at least 4 times the number of cards
        	cardDecks[i] = new CardDeck(i+1, this.handSize*this.numberOfPlayers);
        }
        for (int i=0; i < this.numberOfPlayers; i++) {
        	// handSize+1 because player should be able to hold k+1 cards
        	// cardDecks[(i+this.numberOfPlayers-1)%this.numberOfPlayers] gives a unique reference to a cardDeck in a circular fashion
        	this.players[i] = new Player(this, i+1, this.handSize+1, this.strategy, cardDecks[i], cardDecks[(i+this.numberOfPlayers-1)%this.numberOfPlayers]);
        }
        System.out.println("Distributing cards...");
        for (int i=0; i < this.handSize; i++) {
        	for (int j=0; j < this.numberOfPlayers; j++) {
        		this.players[j].push(this.initialDeck.pop());
        	}
        }
        // distribute cards among decks
        for (int i=0; i < this.handSize; i++) {
        	for (int j=0; j < this.numberOfPlayers; j++) {
        		this.cardDecks[j].push(this.initialDeck.pop());
        	}
        }
        System.out.println("Starting players...");
    	for (Player player : this.players)
    		player.start();
    }
    public synchronized void playerWonEventHandler(PlayerWonEvent event) {
		if (!this.gameOver) {
			Object winningPlayer = event.getSource();
			if (winningPlayer instanceof Player) {
				this.gameOver = true;
                this.winner = (Player) winningPlayer;
		        System.out.println("Game won by Player " + ((Player) winningPlayer).getPlayerIndex() );
			}

            this.userInputThread.interrupt();
            try {
                this.userInputThread.join();
            } catch (InterruptedException ex) {}
		    for (Player player : this.players) {
                synchronized(player) {
                    player.gameOverEventHandler( new GameOverEvent(this) );    
                }   
            }
		}
    }

    // combine pauseGame + resumeGame into one
    void pauseGame() {
        if (!this.gamePaused) {
            System.out.println("Pausing game...");
             for (Player player : this.players)
                player.pausePlayerEventHandler( new GameStateEvent(this) );
        } else {
            System.out.println("Game is already paused. Press r+Enter to resume.");
            notifyUserInputThread();
        }
    }
    void resumeGame() {
        if (gamePaused) {
            System.out.println("Resuming game...");
             for (Player player : this.players)
                player.resumePlayerEventHandler( new GameStateEvent(this) );
        } else {
            System.out.println("Game is already running. Press p+Enter to pause.");
            notifyUserInputThread();
        }   
    }


    // syncrhonizing incrementation of playersConfirmed
    public synchronized void confirmPlayerState( PlayerStateEvent event, String state ) {
        // verify String state corresponds to the current this.gamePaused state?
        this.playersConfirmed++;
        if (this.playersConfirmed == this.numberOfPlayers) {            
            String str;
            switch (state) {
                case "pause" : this.gamePaused = true; // synchronize(this) ? doubt
                               str = "Game paused."; break;
                case "resume": this.gamePaused = false; // synchronize(this) ? doubt
                               str = "Game resumed."; break;
                default:       throw new RuntimeException("invalid state");
            }
            System.out.println(str);
            this.playersConfirmed = 0;
            notifyUserInputThread();
        }
    }

    // test
    synchronized void confirmPlayerTerminated(PlayerStateEvent event) {
        this.playersTerminated++;
        if (this.playersTerminated == this.numberOfPlayers) {
            String deck_state;
            File output_file;
            for (int i=0; i < this.numberOfPlayers; i++) {
                deck_state = "deck " + this.cardDecks[i].getDeckIndex() + " contains cards: " + this.cardDecks[i].toString();
                output_file = new File("game_output/deck" + this.cardDecks[i].getDeckIndex() + "_output.txt");
                Helper.appendLineToFile( output_file, deck_state );
            }
            System.out.println("Game Over!");
            // DELETE
            System.out.println("\n\n\n\n\n\n\n"+ (System.nanoTime() - this.startTime) + "\n\n\n\n\n\n\n");

        }
    }

    void notifyUserInputThread() {
        this.inIntermediateState = false;
        synchronized(this.userInputThread) {
            this.userInputThread.notify();
        }
    }
    public void startInputThread() {
        this.userInputThread.start();
    }

    boolean isOver() {
        return this.gameOver;
    }
    Player getWinner() {
        if (isOver())
            return this.winner;
        throw new RuntimeException("No winner yet - The game hasn't ended yet.");
    }

    void setStrategy(int strategy) {
        this.strategy = strategy;
    }

    // TODO: remove unfixed exceptions
	public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length < 2) {
            System.err.println("\n Usage Error: CardGame number_of_players number_of_cards_per_player\n");
            System.exit(1);
        }
        // initialize to something, otherwise compiler complains
        int numberOfPlayers = -1;
        int handSize = -1;
        try {
            // throws NumberFormatException if handSize and numberOfPlayers are not strings
            numberOfPlayers = Integer.parseInt(args[0]);
            handSize = Integer.parseInt(args[1]);
            if (numberOfPlayers < 1 || handSize < 1) // Validate args are larger than 1
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.err.println("\nUsage Error: CardGame number_of_players number_of_cards_per_player"); 
            System.err.println("       Arguements number_of_players and number_of_cards_per_player must be integers larger than 0\n");
            System.exit(1);
        }

        // Ask for strategy
        int strategy = -1;
        while (strategy != 1 && strategy != 2) {        
            System.out.print("Please choose either strategy 1 or 2: ");
            try {
                strategy = Integer.parseInt((new BufferedReader(new InputStreamReader(System.in))).readLine());
                if (strategy != 1 && strategy != 2) {
                    System.out.println("That's an incorrect value, please enter number 1 or 2 for your strategy.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Number Format Error: please enter number 1 or 2 for your strategy ");
            } catch (IOException e) {
                System.out.println("Oops..somethign went wrong.");
                System.exit(1);
            }
        }

        // Get & validate files
        File f = Helper.readFileFromCommandLine();
        CardDeck initialDeck = null;
        try {
            initialDeck = Helper.fileToCardDeck(f, numberOfPlayers, handSize);
        } catch (NumberFormatException e) {
            System.err.println("\nCard Deck Error:\n" + e.getMessage());
            System.exit(1);   
        }
        Helper.createNewDirectory("game_output");
        System.out.println("Starting the game...");
        System.out.println("\n\nWhile the game is running press p+Enter to pause the game or r+Enter to resume.\nThis message will disappear in less than 1 sec.\n\n");
        // try { Thread.sleep(1000); } catch (InterruptedException e) {}
        CardGame game = new CardGame(numberOfPlayers, handSize, initialDeck);
        game.setStrategy(strategy);
        // ask and validate strategy number
        

        game.start();
        game.startInputThread();
	}




    private class UserInputThread implements Runnable {

        public void run() {
            BufferedReader br;
            String userInput = null;

            while(true) {
                if (inIntermediateState) {
                    synchronized(userInputThread) { // `this` doesn't work for some reason
                        try {
                            userInputThread.wait(); // a bit ugly (or not?), consider using a dedicated monitor obj
                        } catch (InterruptedException ex) {}
                    }
                }
                System.out.print("Press p+Enter to pause or r+Enter to resume: ");
                try {
                    br = (new BufferedReader(new InputStreamReader(System.in)));
                    try {
                        // wait until there's data to complete readLine()
                        while (!br.ready()) {
                          Thread.sleep(200);
                        }
                        userInput = br.readLine();
                    } catch (InterruptedException e) {
                        return;
                    }
                    // synchronized()? what if a player is reporting something meanwhile? There's nothing for him to report (except wining or exiting)
                    switch ( userInput ) {
                        case "p" :  inIntermediateState = true;
                                    pauseGame(); break;
                        case "r" :  inIntermediateState = true;
                                    resumeGame(); break;
                        default  : System.out.println("Incorrect choice. Try again."); // print full instructions
                    }

                } catch (IOException e) {
                    System.out.println("Oops..somethign went wrong in UserInputThread.");
                    System.exit(1);
                }                
            }
        }
    }
}
