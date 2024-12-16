/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.Heuristic;
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
    
    private String _name;
    private long _exploredNodes;
    private boolean TimeFlag = false;
    private int _depth;
    private int _maxDepth;
    private PlayerType _myPlayer;
    private boolean isMaximizing = false;
    
    public MyPlayer(String name, int depth) {
        this._name = name;
        this._depth = depth;
    }
    
    public MyPlayer(int depth)
    {
        this._depth = depth;
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
        _exploredNodes = 0;
        _myPlayer = s.getCurrentPlayer();
        boolean maximizing = isMaximizing;
        if(_myPlayer == PlayerType.PLAYER2) maximizing = true;
        int bestV = Integer.MIN_VALUE;
        int alpha = -Integer.MAX_VALUE;
        int beta = Integer.MAX_VALUE;
        
        Point bestMove = null;
        List<Point> possibleMoves = getPossibleMoves(s);
        
        for(Point movement : possibleMoves)
        {
            HexGameStatus newS = new HexGameStatus(s);
            newS.placeStone(movement);
            _exploredNodes += 1;
            int value = minimax(newS, _depth-1, maximizing, alpha, beta);
            if(value > bestV)
            {
                bestV = value;
                bestMove = movement;
            }
        }
        return new PlayerMove(bestMove, _exploredNodes, _maxDepth, SearchType.MINIMAX);
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
public int minimax(HexGameStatus s, int depth, boolean maximizing, int alpha, int beta) 
{
    if(TimeFlag || s.isGameOver() || depth == 0)
    {
        if(s.GetWinner() == s.getCurrentPlayer().PLAYER2) return 10000;
        else if(s.GetWinner() == s.getCurrentPlayer().PLAYER1) return -1000;
        else 
        {
            _exploredNodes++;
            return Heuristic.h(s, s.getCurrentPlayer());
        }
    }
    int value;
    if(maximizing)
    {
        value = Integer.MIN_VALUE;
        for(int i = s.getSize()-1; i >= 0; i--)
        {
            for(int j = s.getSize()-1; j >= 0; j--)
            {
                if(TimeFlag)break;
                HexGameStatus newS = new HexGameStatus(s);
                if(s.getPos(i, j) == 0)
                {
                    newS.placeStone(new Point(i, j));
                    int eval = minimax(newS, depth+1, false, alpha, beta);
                    value = Math.max(value, eval);
                    alpha = Math.max(alpha, eval);
                    if(alpha >= beta) break;
                }
            }
        }
    }
    else 
    {
        value = Integer.MAX_VALUE;
        for(int i = s.getSize()-1; i >= 0; i--)
        {
            for(int j = s.getSize()-1; j >= 0; j--)
            {
                if(TimeFlag)break;
                HexGameStatus newS = new HexGameStatus(s);
                if(s.getPos(i,j) == 0)
                {
                    newS.placeStone(new Point(i,j));
                    int eval = minimax(newS, depth+1, true, alpha, beta);
                    value = Math.min(value, eval);
                    beta = Math.min(beta, eval);
                    if(alpha >= beta) break;
                }
            }
        }
    }
    return value;
}

/*public int heuristic(HexGameStatus s, int color)
{

}*/
    
private List<Point> getPossibleMoves(HexGameStatus s) {
    List<Point> possibleMoves = new ArrayList<>();
    for (int i = 0; i < s.getSize(); i++) {
        for(int j = 0; j < s.getSize(); j++)
        {
          Point current = new Point(i, j);
          if(s.getPos(i, j) == 0) possibleMoves.add(current); 
        }
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
        System.out.print("Se acaba el tiempo");
        TimeFlag = true;
    }
    
    /**
     * Retorna el nom del jugador que s'utlilitza per visualització a la UI
     *
     * @return Nom del jugador
     */
    @Override
    public String getName() {
        return "Winner Player";
    }
}