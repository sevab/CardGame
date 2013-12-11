package cardgame;

import java.util.EventListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Scanner;

/**
 *
 * @author sevabaskin
 */
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
    private volatile boolean inIntermediateState;
    private volatile boolean gamePaused = false;



    CardGame(int numberOfPlayers, int handSize, CardDeck initialDeck) {
    	//FEATURE: validate deck if it's possible to win with that deck given player num? (e.g. 1 player && num of preffered cards < k)
        validateDeck(initialDeck, numberOfPlayers, handSize); // numberOfPlayers & handSize are validated in the main() method
        this.initialDeck = initialDeck;
        this.numberOfPlayers = numberOfPlayers;
        this.handSize = handSize;
        this.players = new Player[numberOfPlayers];
        this.cardDecks = new CardDeck[numberOfPlayers];
        // remove and make static and final? what are final's implications?
        this.userInputRunnable = new UserInputThread();
        this.userInputThread = new Thread(this.userInputRunnable);
        this.playersConfirmed = 0;
        this.inIntermediateState = false;
    }

    // do we join the thread or explicitly stop it once the run() completes?
	public void run() {
        System.out.println(Thread.currentThread().getName() + " I am the CardGame");
        for (int i=0; i < this.numberOfPlayers; i++) {
        	// each deck should be able to hold at least twice the number of cards
        	// research if can minimize this furhter
        	cardDecks[i] = new CardDeck(i+1, this.handSize*2);
        }
        for (int i=0; i < this.numberOfPlayers; i++) {
            // maybe a better way to register the game would be through the player.addGame(CardGame game) method, rather than passing directly? Same with decks? More readable.
        	// handSize+1 because player should be able to hold k+1 cards
        	// cardDecks[(i+this.numberOfPlayers-1)%this.numberOfPlayers] gives a unique reference to a cardDeck in a circular fashion
        	// TODO: store strategy in this.strategy
        	this.players[i] = new Player(this, i+1, this.handSize+1, 1, cardDecks[i], cardDecks[(i+this.numberOfPlayers-1)%this.numberOfPlayers]);
        }
        System.out.println("Distributing cards...");
        // distribute cards among players
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
		// make sure all subsequent PlayerWonEvents aren't accepted or do change the state of the game
		if (!this.gameOver) {
			Object winningPlayer = event.getSource();
			if (winningPlayer instanceof Player) {
				this.gameOver = true;
                this.winner = (Player) winningPlayer;
		        System.out.println("Game won by Player " + ((Player) winningPlayer).getPlayerIndex() );
			    System.out.println("Game Over!"); // TODO: print gameover once it's trully over?
			}


            // synchronize?
            

            System.out.println("interrupting userInputThread");
            this.userInputThread.interrupt();
            System.out.println("userInputThread interrupted");

            try {
                System.out.println("userInputThread joining...");
                this.userInputThread.join();
                System.out.println("userInputThread joined");
            } catch (InterruptedException ex) {}
            // or wait for Players to report shutdown and then just System.exit(0);

            System.out.println("\nSending shutdown notices...\n");
		    for (Player player : this.players) {

                synchronized(player) {
                    player.gameOverEventHandler( new GameOverEvent(this) );    
                }
                
            }
		    	
                System.out.println("\n Notifications sent out...\n");
            // try {
            //     Thread.sleep(3000);
            // } catch (InterruptedException e) {}
            // System.out.println(Thread.currentThread().getName() + "is still active");
            // System.exit(0);
            // System.out.println();
            // interrupt input thread BEFORE SHUTTING DOWN players. No pause/resume after shutdown
            // 10DEC: still hangs sometimes. Receive player confirmations and force shutdown the game for this scenarios.
            
            // Strategy 0: it seems like you don't need to close System.in to interrupt a thread. Try avoid using it.
            // Strategy 1: ok, try interrupting Bufferedreader
		}
        System.out.println("playerWonEventHandler completed");
        // System.out.println(Thread.currentThread().getName() + "is still active");
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

    // void pauseGame()  {pauseOrResumeGame("resume");}
    // void resumeGame() {pauseOrResumeGame("pause");}
    // void pauseOrResumeGame(String action) {
    //     Str str = null;
    //     switch (action) {
    //         case "pause" : str = (this.gamePaused) ?
    //                         "Game is already paused. Press r+Enter to resume." :
    //                         "Pausing game..."; break;
    //         case "resume": str = (!this.gamePaused) ?
    //                         "Game is already running. Press p+Enter to pause." :
    //                         "Resuming game..."; break;
    //     }
    //     System.out.println(str);
    //     for (Player player : this.players)
    //         player.resumePlayerEventHandler( new GameStateEvent(this) );
    // }
    // syncrhonizing incrementation of playersConfirmed
    synchronized void confirmPlayerState( PlayerStateEvent event, String state ) {
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

    void notifyUserInputThread() {
        this.inIntermediateState = false;
        synchronized(this.userInputThread) {
            this.userInputThread.notify();
        }
    }
    public void startInputThread() {
        this.userInputThread.start();
    }

    void validateDeck(CardDeck deck, int numberOfPlayers, int handSize) {
    	if ( deck.getSize() < 2 * numberOfPlayers * handSize)
    		throw new RuntimeException("Insufficient number of cards in the initial deck. Please, import a larger deck");
    }
    boolean isOver() {
        return this.gameOver;
    }
    Player getWinner() {
        if (isOver())
            return this.winner;
        throw new RuntimeException("No winner yet - The game hasn't ended yet.");
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
            numberOfPlayers = Integer.parseInt(args[0]);
            handSize = Integer.parseInt(args[1]);
            if (numberOfPlayers < 1 || handSize < 1) // Validate args are larger than 1
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.err.println("\nUsage Error: CardGame number_of_players number_of_cards_per_player"); 
            System.err.println("       Arguements number_of_players and number_of_cards_per_player must be integers larger than 0\n");
            System.exit(1);
        }

        // Get & validate files
        File f = Helper.readFileFromCommandLine();
        CardDeck initialDeck = Helper.fileToCardDeck(f);
        Helper.createNewDirectory("game_output");
        System.out.println("Starting the game...");
        System.out.println("\n\nWhile the game is running press p+Enter to pause the game or r+Enter to resume.\nThis message will disappear in less than 1 sec.\n\n");
        // try { Thread.sleep(1000); } catch (InterruptedException e) {}
        CardGame game = new CardGame(numberOfPlayers, handSize, initialDeck);
        game.start();
        game.startInputThread();
	}



    // if the game is hanging on exit, make sure all instances of this are termindated
    // if game hangs, make sure that locks in the synchronized blocks in UserInputThread and confirmPlayerState are referencing the right objects
    private class UserInputThread implements Runnable {
        // private InputStreamReader isr;
        // private BufferedReader br;


        // InputStreamReader isr;
        // BufferedReader br;
        // UserInputThread() {
        //     InputStreamReader isr = new InputStreamReader(System.in);
        //     BufferedReader br = new BufferedReader(isr);
        // }

        public void run() {
            BufferedReader br;
            String userInput = null;

            while(true) {
                if (inIntermediateState) {
                    synchronized(userInputThread) { // `this` doesn't work for some reason
                        try {
                            // -- System.out.println("userInputThread is now waiting for a notification");
                            userInputThread.wait(); // a bit ugly (or not?), consider using a dedicated monitor obj
                            // -- System.out.println("UserInputThread received notification to stop waiting.");
                        } catch (InterruptedException ex) {}
                    }
                }
                System.out.print("Press p+Enter to pause or r+Enter to resume: ");
                try { // move String userInput to constructor?
                    // does splitting this into several bits allows multiple-line input?
                    br = (new BufferedReader(new InputStreamReader(System.in)));

                    // userInput  = br.readLine();

                    try {
                        // wait until we have data to complete a readLine()
                        while (!br.ready()) {
                          Thread.sleep(200);
                        }
                        userInput = br.readLine();
                    } catch (InterruptedException e) {
                        System.out.println("UserInputThread interrupted");
                        return;
                        // how about we abort right here by throwing something else, or not catching the exception
                    }
                    

                    // System.out.println("User Input\n===========\n"+ userInput +"\n\n");

                    // synchronized()?
                    try {
                        switch ( userInput ) {
                            case "p" :  inIntermediateState = true;
                                        pauseGame(); break;
                            case "r" :  inIntermediateState = true;
                                        resumeGame(); break;
                            default  : System.out.println("Incorrect choice. Try again."); // print full instructions
                        }
                    } catch(NullPointerException e) {
                        System.out.println("userInput is null. LOL, who cares.");
                    }
                } catch (IOException e) {
                    System.out.println("Oops..somethign went wrong in UserInputThread.run().");
                    System.exit(1);
                }                
            }
        }
    }


}

/*
*            /Users/sevabaskin/Dropbox/2nd Year/Java/CW2/CardGame/test/cardgame/testDeck.txt
*/
