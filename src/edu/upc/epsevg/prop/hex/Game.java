package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.players.HumanPlayer;
import edu.upc.epsevg.prop.hex.players.RandomPlayer;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.players.H_E_X_Player;
import edu.upc.epsevg.prop.hex.players.MyPlayer;


import javax.swing.SwingUtilities;

/**
 * Checkers: el joc de taula.
 * @author bernat
 */
public class Game {
        /**
     * @param args
     */
    public static void main(String[] args) { 
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                //IPlayer player1 = new H_E_X_Player(2/*GB*/);
                
                //IPlayer player1 = new RandomPlayer("perdedor");
                
                IPlayer player2 = new H_E_X_Player(2/*GB*/);
                
                //IPlayer player1 = new HumanPlayer("Human");
                
                IPlayer player1 = new MyPlayer("Winner", 4, true);
                
                //IPlayer player2 = new MyPlayer("Winner", 4, false);
                                
                new Board(player1 , player2, 5 /*mida*/,  5/*s*/, false);
             }
        });
    }
}