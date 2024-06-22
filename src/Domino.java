import javafx.beans.property.DoubleProperty;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Collections;

public class Domino {

    //value in front of the domino
    private int front;

    //value in back of the domino
    private int back;

    //button for each domino
    private Button button;

    // Constructor
    public Domino(int back, int front) {
        this.front = front;
        this.back = back;

    }


    /**
     * A button corresponding to this domino
     * @return a button of this domino
     */
    Button getButton(){
        button.setText(toString());
        return button;
    }

    /**
     * Setting the button text to the the
     * equivalent string representation of this domino
     */
    void setButton(){
        button = new Button(toString());
    }

    /**
     * The front of the domino
     * @return the front face of this domino
     */
    int getFront(){
        return front;
    }

    /**
     * The back of this domino
     * @return the back face of this domino
     */
    int getBack(){
        return back;
    }

    /**
     * swaps the font face to the back face of this domino
     */
    public void rotate() {
        int buffer = front;
        front = back;
        back = buffer;
    }


    /**
     * Comapre to dominoes for equality
     * @param o is the Domino object to be compared with this domino
     * @return true if this domino is equal to domino object o
     */
    @Override
    public boolean equals(Object o){
        return getBack() == ((Domino) o).getBack() && getFront() == ((Domino) o).getFront() || getFront() == ((Domino) o).getBack() && getBack() == ((Domino) o).getFront();
    }

    /**
     * the string representaion of this domino
     * @return the string representation of this domino
     */
    @Override
    public String toString() {
        return "[" + back + "|" + front+ "]";
    }

    /**
     * Creating a deck of dominoes
     * @param maxDoublet is the biggest double faced domino
     * @return LinkedList of dominoes representing the dewck
     */
    public static LinkedList<Domino> generateDeck(int maxDoublet){
        LinkedList<Domino> deck = new LinkedList<>();
        for(int rows = 0; rows < maxDoublet; rows++){
            for(int cols = rows; cols <= maxDoublet; cols++){
                deck.addToEnd(new Domino(cols, rows));
            }
        }
        return deck;
    }

}
