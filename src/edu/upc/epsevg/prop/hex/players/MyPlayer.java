/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.MoveNode;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.SearchType;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *  Jugador amb heurística
 * @author marc i victor
 */
public class MyPlayer implements IPlayer, IAuto{
    
    String name;
    public int maxDepth;
    public long exploredNodes;
    
    
    public MyPlayer(String name) {
        this.name = name;
    }
    
    public MyPlayer(int maxDepth)
    {
        this.maxDepth = maxDepth; 
    }
    
    public MyPlayer(long exploredNodes){
        this.exploredNodes = exploredNodes;
    }

    
    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que
     * ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public PlayerMove move(HexGameStatus s) {
        PlayerMove movement = minimax(s, maxDepth-1, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0, SearchType.MINIMAX);
        return movement;
    }
    
    /**
     * Algorisme Minimax amb poda alfa-beta per calcular el millor moviment.
     *
     * @param depth la profunditat restant per explorar
     * @param alpha el valor de poda alfa
     * @param beta el valor de poda beta
     * @param HexGameStatus el status del joc
     * @return el valor heurístic del millor moviment
     */
public PlayerMove minimax(HexGameStatus s, int depth, Integer alpha, Integer beta, long nodesExplored, int maxDepth, SearchType st) {
    // Caso base: si se alcanza la profundidad máxima o el juego ha terminado
    st = SearchType.MINIMAX;
    if (depth == 0 || s.isGameOver()) {
        int value = heuristic(s, s.getCurrentPlayerColor());
        return new PlayerMove(null, exploredNodes+1, maxDepth, SearchType.MINIMAX);
    }
    
    // Obtener posibles movimientos
    List<Point> possibleMoves = getPossibleMoves(s);
    PlayerType player = s.getCurrentPlayer();
    Point bestMove = new Point(0,0);
    if (player.equals(this)) {
        int maxEval = Integer.MIN_VALUE;
        for (Point move : possibleMoves) {
            HexGameStatus newState = new HexGameStatus(s);
            newState.placeStone(move); // Colocar piedra en la posición (x, y)

            // Llamada recursiva
            PlayerMove res = minimax(newState, depth -1, alpha, beta, nodesExplored+1, Math.max(maxDepth, depth), st);
            int eval = heuristic(newState, s.getCurrentPlayerColor());
            if(eval > maxEval) {
                maxEval = eval;
                bestMove = move;
            }
            alpha = Math.max(alpha, eval);

            // Poda alfa-beta
            if (beta <= alpha) {
                break;
            }
        }
        return new PlayerMove(bestMove, nodesExplored, maxDepth, st);
    } else {
        int minEval = Integer.MAX_VALUE;
        for (Point move : possibleMoves) {
            HexGameStatus newState = new HexGameStatus(s);
            newState.placeStone(move); // Colocar piedra en la posición (x, y)

            // Llamada recursiva
            PlayerMove res = minimax(newState, depth -1, alpha, beta, nodesExplored+1, Math.max(maxDepth, depth), st);
            int eval = heuristic(newState, s.getCurrentPlayerColor());
            
            if(eval < minEval) {
                minEval = eval; 
                bestMove = move;
            }
            beta = Math.min(beta, eval);

            // Poda alfa-beta
            if (beta <= alpha) {
                break;
            }
        }
        return new PlayerMove(bestMove, nodesExplored, maxDepth, st);
    }
}

public int heuristic(HexGameStatus s, int color)
{
    int score = 0;
    for(int x = 0; x < s.getSize(); x++) {
        for(int y = 0; y < s.getSize(); y++) {
            Point p = new Point(x, y);
            int posColor = s.getPos(p); //obtenim el color de cada posicio del tauler
            
            if(posColor == color) {
                score += 10;
            }
            else if(posColor != 0) {
                score -= 10;
            }
        }
    }
    return score;
}
    
private List<Point> getPossibleMoves(HexGameStatus s) {
    List<MoveNode> moves = s.getMoves(); // Obtener movimientos válidos
    List<Point> possibleMoves = new ArrayList<>();

    for (MoveNode move : moves) {
        Point p = move.getPoint(); // Obtén el punto del movimiento
        possibleMoves.add(p);
    }
    return possibleMoves;
}
    
    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps
     * de joc.
     */
    @Override
    public void timeout() {
        // Bah! Humans do not enjoy timeouts, oh, poor beasts !
        System.out.println("Bah! You are so slow...");
    }
    
    /**
     * Retorna el nom del jugador que s'utlilitza per visualització a la UI
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return "si";
    }
}


    

    

    
