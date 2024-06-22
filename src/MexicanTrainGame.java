import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.*;


import static org.junit.Assert.assertTrue;

public class MexicanTrainGame extends Application {

    //keep track of number of players
    private static int playersTillNow=0;

    //The mexican train for the game
    static DominoTrain<Domino> mexicanTrain;

    //set the maximum number of players the game can support
    static final int maxPlayers = 4;

    //defualt number of players
    private static int numOfPlayers;

    //Array of players
    static Player[] players;

    //The deck for this game
    static LinkedList<Domino> gameDeck;

    //the maxdoublet (value in front of each train)
    static int frontDoublet; //can't modify unless non private

    //dimensions of the device
    static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    //the hbox for the mexican train
    static HBox mexicanTrainBox;

    //the mainstage vbox container
    static VBox mainStageVboxContainer;

    //keep track whether application has launched to make sure Junit will not try to use javax elements if not
    static boolean hasApplicationStarted = false;

    //the current player in the game
    static Player currentPlayer = new Player();

    //button to add to the mexican train
    static Button addToMexicanButton;

    //the scene for the main game display
    static Scene mainCommonScene;

    //keep track of what train are open
    static ArrayList<DominoTrain<Domino>> openTrains = new ArrayList<>();


    /**
     * constructing the game paramenter
     * @param dominosPerPlayer is the amount to deal to each player
     * @param numOfPlayers to play the game
     * @param biggestDoubletVal is the biggerst double domino to use in the game
     * @param shuffle to decide whether the deck should be shuffled or not (fot testing purposes)
     */
    public static void setup(int dominosPerPlayer, int numOfPlayers, int biggestDoubletVal, boolean shuffle){

        //make sure the number of dominoes per player does not exceed the number of dominoes in the pile
        if( dominosPerPlayer*numOfPlayers > ((biggestDoubletVal + 1)*(biggestDoubletVal + 2))/2 -1){
            throw new IllegalArgumentException("The deck created must be enough to fit the number of players and card per players");
        }

        playersTillNow = 0;
        frontDoublet = biggestDoubletVal;
        mexicanTrain = new DominoTrain<>(frontDoublet);
        openTrains.add(mexicanTrain);
        MexicanTrainGame.numOfPlayers = numOfPlayers;

        //decide whether the gameDeck should be shuffled or unshuffled
        gameDeck = (shuffle) ? Domino.generateDeck(frontDoublet).shuffle() : Domino.generateDeck(frontDoublet);

        players = new Player[numOfPlayers];

        for(int i = 0; i < numOfPlayers; i++){
            players[i] = new Player();
        }

        deal(players, dominosPerPlayer);
        MexicanTrainGame.currentPlayer = players[0];
    }

    /**
     * Starts the GUI
     * @param primaryStage the main window of the application
     */
    public void start(Stage primaryStage) throws Exception {
        hasApplicationStarted = true;
        welcomeSelectPlayerNum(primaryStage);
    }

    /**
     * The main method needed to run the application
     * @param args the command line arguments are currently ignored
     */
    public static void main(String[] args) {
        try{
            numOfPlayers = Integer.parseInt(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            numOfPlayers = 2;
        }

        System.out.println(Arrays.toString(args));
        launch(args);
    }


    /**
     * distributing the dominoes to the player
     * @param players how many players to deal to
     * @param dominoPerPlayer is the amount of domino to deal to each player
     */
    public static void deal(Player[] players, int dominoPerPlayer){
        for(Player player: players) {
            for (int i = 0; i < dominoPerPlayer; i++)
                player.hand.addToFront(gameDeck.removeFromFront());
        }
    }

    /**
     * setting the mainstage for the game
     * @param primaryStage is the primary window
     */
    public static void setMainStage(Stage primaryStage){
        setFx(); //setting the Javax elements for the player
        primaryStage.setTitle("THE MEXICAN TRAIN (by Ignas Kamugisha)");

        mainStageVboxContainer = new VBox(2);
        mainStageVboxContainer.setAlignment(Pos.CENTER);

        primaryStage.setY( (Player.stageHeight + 40) * numOfPlayers );
        primaryStage.setX( screenSize.getWidth()/8.5);
//        BorderPane(Node center, Node top, Node right, Node bottom, Node left
        BorderPane borderPane = new BorderPane();
        mexicanTrainBox = new HBox(2);

        for(Player p: players){
            mainStageVboxContainer.getChildren().add(p.trainBox);
        }
        mainStageVboxContainer.getChildren().add(mexicanTrainBox);

        mainCommonScene = new Scene(mainStageVboxContainer, screenSize.getWidth()/1.5, screenSize.getHeight() - (Player.stageHeight + 44) * (numOfPlayers*1.5) );
        mainCommonScene.setFill(Paint.valueOf("#fcc200"));
        addToMexicanButton = new Button("Add to MEXICAN");

        addToMexicanButton.setOnAction( Player.addToMexicanButtonOnClick() );
        mexicanTrainBox.getChildren().addAll( addToMexicanButton, new Button( mexicanTrain.firstElement().toString()));

        BackgroundImage myBI= new BackgroundImage(new Image("https://cdn.britannica.com/45/18445-050-59915B6F/Dominoes.jpg"),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        mainStageVboxContainer.setBackground(new Background(myBI));

        //setting up the players
        for(Player player: players){
            player.setPlayerStage(new Stage());
            player.displayStage();
            player.refreshScenes();
        }

        primaryStage.setScene(mainCommonScene);
        primaryStage.show();
    }

    /**
     * setting the javafx elements for the players
     */
    public static void setFx(){
        for(Player player: players){

            player.lockStateButton = new Button();
            player.lockStateButton.setOnAction(player.lockStateButtonOnClick());


            player.trainBox = new HBox(2);
            player.trainBox.getChildren().addAll(player.lockStateButton, new Button(mexicanTrain.firstElement().toString()) );

            player.hboxContainer = new VBox();
            player.hboxContainer.setAlignment(Pos.CENTER);
            player.handRow = new HBox(2);
            player.buttonRow = new HBox(3);

            player.drawButton = new Button("Draw");
            player.drawButton.setDisable( gameDeck.isEmpty() );

            player.endMyTurnButton = new Button("Pass");
            player.endMyTurnButton.setDisable( !gameDeck.isEmpty() );


            player.buttonRow.getChildren().add(player.drawButton);
            player.buttonRow.getChildren().add(player.endMyTurnButton);


            player.drawButton.setOnAction(e -> {
               player.drawFromPile();
               player.drawButton.setDisable(true);
               player.endMyTurnButton.setDisable(false);
               if(!player.hasPlayableDomino()) {
                   player.isTrainOpen = true;
                   openTrains.add(player.train);
                   player.goToNextPlayer();
               }
               player.refreshScenes();
            });

            //enabling a player to decide to pass his turn to strategically have an advantage
            player.endMyTurnButton.setOnAction(e -> {
                player.isTrainOpen = true;
                player.refreshScenes();
                player.goToNextPlayer();

            });
        }
    }

    /**
     * displaying the game condition in the terminal window for testing pursposes
     */
    static void display(){
        System.out.println("Drawing pile: " + gameDeck);
        System.out.println("Mexican Train: " + mexicanTrain + "\n");
        for(Player player : players)
            System.out.println(player + "\n");
    }

    /**
     * The stage to welcome the user and select the number of players for the game
     * @param primaryStage is the main window for the program
     */
    static void welcomeSelectPlayerNum(Stage primaryStage){
        Stage stage = new Stage();
        // Create a slider with a range from 2 to 8 maximum players due to screen limitations
        Slider slider = new Slider(2, 8, 2);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);

        // Ensure the slider only allows integer values
        slider.valueChangingProperty().addListener((observable, oldValue, isChanging) -> {
            if (!isChanging) {
                slider.setValue(Math.round(slider.getValue()));
            }
        });

        // Set major tick unit to 1 and minor tick count to 0 to mark integer positions
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);

        // Create a StringProperty to hold the slider value as a string
        StringProperty sliderValueProperty = new SimpleStringProperty();

        // Bind the StringProperty to the value of the slider
        sliderValueProperty.bind(Bindings.format("Start Game For %.0f Players!", slider.valueProperty()));

        // Create a button with the initial text from the StringProperty
        Button displayButton = new Button();
        displayButton.textProperty().bind(sliderValueProperty);

        // Create a vertical box to hold the slider and button, and center align it
        VBox vbox = new VBox(10); // 10 is the spacing between nodes
        vbox.getChildren().addAll(slider, displayButton);
        vbox.setAlignment(Pos.CENTER);

        // Create a scene and set it on the stage
        Scene scene = new Scene(vbox, 550, 150);
        stage.setTitle("Select the number of players for the Mexican Train");
        stage.setScene(scene);

        // Add an event handler to the button
        displayButton.setOnAction(e -> {
            int sliderValue = (int) slider.getValue();
            numOfPlayers = sliderValue;
            stage.hide();
            selectBiggestDoublet(primaryStage);
        });


        // Center the stage on the screen
        stage.centerOnScreen();

        // Show the stage
        stage.show();
    }

    /**
     * prompt the users to select the biggest doublet he wanna use in the game
     * @param primaryStage is the main window of the application
     */
    static void selectBiggestDoublet(Stage primaryStage){
        Stage stage = new Stage();
        // Create a slider with a range from 2 to 10
        Slider slider = new Slider(2, 9, 2);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);

        // Ensure the slider only allows integer values
        slider.valueChangingProperty().addListener((observable, oldValue, isChanging) -> {
            if (!isChanging) {
                slider.setValue(Math.round(slider.getValue()));
            }
        });

        // Set major tick unit to 1 and minor tick count to 0 to mark integer positions
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);

        // Create a StringProperty to hold the slider value as a string
        StringProperty sliderValueProperty = new SimpleStringProperty();

        // Bind the StringProperty to the value of the slider

        sliderValueProperty.bind(Bindings.format("Start with a [%.0f|%.0f] Doublet!", slider.valueProperty(),slider.valueProperty()));

        // Create a button with the initial text from the StringProperty
        Button displayButton = new Button();
        displayButton.textProperty().bind(sliderValueProperty);

        // Create a vertical box to hold the slider and button, and center align it
        VBox vbox = new VBox(10); // 10 is the spacing between nodes
        vbox.getChildren().addAll(slider, displayButton);
        vbox.setAlignment(Pos.CENTER);

        // Create a scene and set it on the stage
        Scene scene = new Scene(vbox, 550, 150);
        stage.setTitle("Select the Starting Doublet");
        stage.setScene(scene);

        // Add an event handler to the button
        displayButton.setOnAction(e -> {
            double sliderValue = slider.getValue();
            stage.hide();
            selectDominoPerPlayer( primaryStage, (int) sliderValue);
        });


        // Center the stage on the screen
        stage.centerOnScreen();

        // Show the stage
        stage.show();
    }

    /**
     * Prompt user to selectin the number of Dominoes to be played for each player
     * @param primaryStage is the main window of the game
     * @param biggestDoublet is the value of the biggest doublet to be used in the game
     */
    static void selectDominoPerPlayer(Stage primaryStage, int biggestDoublet){
        Stage stage = new Stage();
        // Create a slider with a range from 2 to the maximum
        Slider slider = new Slider(0, (int)(((biggestDoublet + 1)*(biggestDoublet + 2)/2 - 1)/numOfPlayers), 0);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);

        // Ensure the slider only allows integer values
        slider.valueChangingProperty().addListener((observable, oldValue, isChanging) -> {
            if (!isChanging) {
                slider.setValue(Math.round(slider.getValue()));
            }
        });

        // Set major tick unit to 1 and minor tick count to 0 to mark integer positions
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setSnapToTicks(true);

        // Create a StringProperty to hold the slider value as a string
        StringProperty sliderValueProperty = new SimpleStringProperty();

        // Bind the StringProperty to the value of the slider

        sliderValueProperty.bind(Bindings.format("Deal %.0f Dominoes To Each Player", slider.valueProperty()));

        // Create a button with the initial text from the StringProperty
        Button displayButton = new Button();
        displayButton.textProperty().bind(sliderValueProperty);

        // Create a vertical box to hold the slider and button, and center align it
        VBox vbox = new VBox(10); // 10 is the spacing between nodes
        vbox.getChildren().addAll(slider, displayButton);
        vbox.setAlignment(Pos.CENTER);

        // Create a scene and set it on the stage
        Scene scene = new Scene(vbox, 550, 150);
        stage.setTitle("Deal Dominoes to each Player");
        stage.setScene(scene);

        // Add an event handler to the button
        displayButton.setOnAction(e -> {
            double sliderValue = slider.getValue();
            stage.hide();
            setup( (int)slider.getValue(), numOfPlayers, biggestDoublet,true);
            setMainStage(primaryStage);
        });


        // Center the stage on the screen
        stage.centerOnScreen();

        // Show the stage
        stage.show();
    }


    /**
     * create a background template which could be used to fill any of the stages
     * @return a Background object representing a background with a color
     */
    static Background background1(){
        Color backgroundColor = Color.web("#5ab897");
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, null, null);
        return new Background(backgroundFill);
    }

    /**
     * the default background to be used by player stages
     * @return a Background object representing a color to fill a scene
     */
    static Background backgroundDefault(){
        Color backgroundColor = Color.web("#b6c1d4");
        BackgroundFill backgroundFill = new BackgroundFill(backgroundColor, null, null);
        return new Background(backgroundFill);
    }



    /**
     * A nested Class to represent A MexicanTrainGame Player
     */
    public static class Player implements Comparable<Player>{


        //A linkedlist representing the domino on players hand
        LinkedList<Domino> hand = new LinkedList<>();

        //The train of this player
        DominoTrain<Domino> train;

        //keep track whether player's train is open
        boolean isTrainOpen = false;

        //keep track whether during this player's turn is the first round
        static boolean firstRound = true;

        //name of the player
        String name;

        //number of the player
        int number;

        //the stage of the player
        Stage stage;

        //the drawButton of this player
        Button drawButton;

        //A button representing whether this player's train is playable or not
        Button lockStateButton;

        //a Hbox to contain the hand of the player
        HBox handRow;

        //HBox to contain the buttons of the player
        HBox buttonRow;
        
        //A vbox containing all the hboxes of this player
        VBox hboxContainer;

        //a hbox containing the train of this player
        HBox trainBox;

        //Keep track of the currently selected domino
        static Domino currentlySelectedDomino;

        //keep how big the stage of player should be based on their screensizes
        static double stageHeight = screenSize.getHeight()/14;

        //keep how wide the stage of a player should be based on their screensizes
        static double stageWidth = screenSize.getWidth();

        //the scene of this player
        Scene playerScene;

        //a button to allow this player to pass his turn (strategically)
        Button endMyTurnButton;

        /**
         * A constructor for a player
         */
        Player(){
            playersTillNow++;
            number = playersTillNow;
            this.name = "Player " + (playersTillNow);
            this.train = new DominoTrain<>(frontDoublet);
        }


        //overriding the string representing of a player
        @Override
        public String toString(){
//            return name + ((DominoTrain<Domino>) hand).toString(); /////////////
            return name + ": " + "\n" + "Hand: " + hand + "\n" + "Train: " + train;
        }

        /**
         * A method allowing a player to draw a domino from a bile
         * and return a boolean value of whether the draw was succesful or not
         * @return true if the draw was successful otherwise false
         */
        public boolean drawFromPile(){

            if(gameDeck.length() == 0)
                return false;
            Domino drawedDomino = gameDeck.removeFromFront();
            this.hand.addToFront( drawedDomino );

            if(hasApplicationStarted){ //make sure the JavaFx components are only called when application has started
                drawedDomino.setButton();
                drawedDomino.getButton().setOnAction(dominoOnClick(drawedDomino));
                handRow.getChildren().add(drawedDomino.getButton());
            }
            return true;
        }


        /**
         * Allowing a player to play to other player object
         * @param from is the player who is currently playing
         * @param to is the player to which the current player is playing to
         * @param domino the domino value to be played to the other player
         * @return true if the play was succesful otherwise returns false (for testing purposes)
         */
        public static boolean playPlayerToPlayer(Player from, Player to, Domino domino){
            if( (to.isTrainOpen || from == to) && from.hand.contains(domino) && to.train.addToFront(domino)){
                openTrains.remove(from.train);
                from.isTrainOpen = false;
                from.hand.remove(domino);
                return true;
            }
            return false;
        }

        /**
         * A method for this player to player to the mexican train
         * @param domino is the domino to played to the mexican train
         * @return true is the play was succeful otherwise return false
         */
         boolean addToMexicanTrain(Domino domino){
            if(hand.contains(domino) && mexicanTrain.addToFront(domino)){
                isTrainOpen = false;
                openTrains.remove(train);
                refreshScenes();
                hand.remove(domino);
                return true;
            }
            return false;
        }

        /**
         * setting the stage for the players
         * @param stage is a window
         */
        public void setPlayerStage(Stage stage){

            this.stage = stage;
            double screenDivisions = screenSize.getHeight()/numOfPlayers;
            stage.setX(0);
            stage.setY( (stageHeight + 40) * (number-1) );
            stage.setTitle(name);
        }

        /**
         * A method for the eventHandler of a button when you add to a mexican train
         * @return EventHandler<ActionEvent> which is the action should be taken when the button is clicked
         */
        static EventHandler<ActionEvent> addToMexicanButtonOnClick(){

            return ( e ->  {
                if(currentPlayer != null){
                    if(currentPlayer.addToMexicanTrain(currentlySelectedDomino)){

                        currentlySelectedDomino.getButton().setOnAction(ev ->{});
                        currentPlayer.refreshScenes();
                        mexicanTrainBox.getChildren().add( currentlySelectedDomino.getButton());
                        currentPlayer.handRow.getChildren().remove(currentlySelectedDomino.getButton());
                        currentPlayer.goToNextPlayer();
                        currentPlayer.refreshScenes();
                        System.out.println("Mexicana: " + MexicanTrainGame.mexicanTrain);
                    }
                }
            });
        }

        /**
         * Creating and storing the event which dominoes of players hand should do when clicked
         * @param domino is a domino
         * @return and EventHandler of type Action to dictate what should be done when this domino button in clicked
         */
        EventHandler<ActionEvent> dominoOnClick(Domino domino){

            return (e -> {
                if(currentlySelectedDomino != null){
                    currentlySelectedDomino.getButton().setStyle("");
                }
                currentlySelectedDomino = domino;
                currentlySelectedDomino.getButton().setStyle("-fx-background-color: grey;");
                System.out.println(domino);

//                    domino.getButton().setText(domino.toString());
            });
        }

        /**
         * A method of setting up what should be done when this lockstate button is clicked
         * @return and EventHandler of type action dictating what should be done when a player's lockstate button is clicked
         */
        EventHandler<ActionEvent> lockStateButtonOnClick(){
            return( e -> {
                if( Player.playPlayerToPlayer( currentPlayer, this,  currentlySelectedDomino)){
                    currentlySelectedDomino.getButton().setOnAction(ev ->{});
                    currentPlayer.refreshScenes();
                    this.trainBox.getChildren().add( currentlySelectedDomino.getButton());
                    currentPlayer.handRow.getChildren().remove(currentlySelectedDomino.getButton());
                    currentPlayer.goToNextPlayer();
                    System.out.println("Mexicana: " + MexicanTrainGame.mexicanTrain);
                }
            } );
        }

        /**
         * A method to determine whether this player has a playable domino on any open train
         * @return true if he has a domino he can player, otherwise return false
         */
        boolean hasPlayableDomino(){
            for(Domino domino: hand){
                for(DominoTrain<Domino> train: openTrains){
                    if(train.canAdd(domino))
                        return true;
                }
            }
            return false;
        }

        /**
         * Displaying the stages of each player
         */
        public void displayStage(){

            hboxContainer.getChildren().addAll(buttonRow, handRow);

            for( Domino domino : hand){

                domino.setButton();
                handRow.getChildren().add(domino.getButton());
                domino.getButton().setOnAction(dominoOnClick(domino));
            }

            playerScene = new Scene(hboxContainer, stageWidth, stageHeight);

            stage.setScene(playerScene);
            stage.show();
        }

        /**
         * A method to handle when a player turn ends
         */
        void goToNextPlayer(){
            currentPlayer.drawButton.setDisable( gameDeck.isEmpty() );
            currentPlayer.endMyTurnButton.setDisable( !gameDeck.isEmpty() || !currentPlayer.drawButton.isDisabled() );
            checkWinner();
            System.out.println("Dominoes in Deck: " + gameDeck.length());
            refreshScenes();
            currentPlayer.drawButton.setDisable( !gameDeck.isEmpty() );
            currentPlayer = players[ currentPlayer.number % numOfPlayers ];
            currentPlayer.drawButton.setDisable(gameDeck.isEmpty());
            refreshScenes();
        }

        /**
         * A method to check if there's condition for any of the player to win in a given round
         */
        private void checkWinner() {
            boolean someOneCanPlay = false;
            if(currentPlayer.hand.isEmpty()){
                displayWinners();
            }
            else if(gameDeck.isEmpty()) {
                for (Player player : players) {
                    if ( player.hasPlayableDomino() )
                        someOneCanPlay = true;
                }
                if(!someOneCanPlay)
                    displayWinners();
            }
        }

        /**
         * A method to display the leaderboard of winners and end the game
         */
        private void displayWinners(){
            currentPlayer.hboxContainer.setDisable(true);
            List<Player> list =  Arrays.asList(players);
            list.sort(comparatorByHandLength());

            VBox leaderBoard = new VBox();
            Button butt = new Button("dominos left = " + gameDeck.length() + " THE LEADERBOARD");
            HBox box = new HBox();
            box.getChildren().add(butt);
            box.setAlignment(Pos.CENTER);
            leaderBoard.getChildren().add(box);


            for(Player player: list){
                int pos = list.indexOf(player) + 1;
                String prefix = (  pos == 1 ) ? "st" : ( (pos == 2) ? "nd" : ( (pos == 3) ? "rd" : "th" ) );
                HBox boxx = new HBox();
                boxx.setAlignment(Pos.CENTER);
                boxx.getChildren().add( new Button( pos + prefix  + " is "+ player.name + " with " + player.hand.length() + " Domino" + ((player.hand.length() == 1 || player.hand.isEmpty()) ? "" : "s") +  " left on hand"));
                leaderBoard.getChildren().add(boxx);

            }

            Scene finalScene = new Scene( leaderBoard, 500, 600 );

            Stage finalStage = new Stage();
            finalStage.setScene(finalScene);
            finalStage.show();

        }


        /**
         * A method to refresh the scenes of the player after critical changes on the game parameters
         */
        void refreshScenes(){
            lockStateButton.setText( ( isTrainOpen ) ? "Add to: " + name + "'s" :  name + "'s Closed ");
            lockStateButton.setStyle( ( isTrainOpen ) ? "-fx-background-color: #67c268" : "-fx-background-color: #e0967e;");

            currentPlayer.lockStateButton.setText("Add to own Train");
            currentPlayer.lockStateButton.setStyle("-fx-background-color: #67c268;");

//            MexicanTrainGame.currentPlayer.trainBox.setDisable(true);

            for(Player player: MexicanTrainGame.players){
                if(player != currentPlayer){
                    player.hboxContainer.setBackground( MexicanTrainGame.backgroundDefault() );
                    player.hboxContainer.setDisable(true);
//                    player.handRow.setDisable(true);
                }
                else{
                    player.hboxContainer.setBackground( MexicanTrainGame.background1() );
                    player.hboxContainer.setDisable(false);
//                    player.handRow.setDisable(false);
                }
            }

        }

        /**
         * A method to get a comparotor for this player than compares by how many dominoes they have left in their hands
         * @return a Comparator<Player>
         */
        public static Comparator<Player> comparatorByHandLength(){
            return new CompareByHandLength();
        }


        /**
         * Compares this player and object o
         * @param o the object to be compared.
         * @return an int 1 if this player is greater than o -1 if it is less, otherwise returns 0
         */
        @Override
        public int compareTo(Player o) {
            return this.hand.length() - o.hand.length();
        }

        /**
         * A nested Class that implements the comparator and allows Players to be sorted and compared in different ways
         * this is used to create the leaderboard by comparing the length of their hands
         */
        private static class CompareByHandLength implements Comparator<Player> {

            @Override
            public int compare(Player o1, Player o2) {
                return o1.compareTo(o2);
            }
        }

    }



}


