package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.Heuristic;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.SearchType;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Jugador amb heurística
 * @author marc i victor
 */
public class MyPlayer implements IPlayer, IAuto {

    private String _name;
    private long _exploredNodes;
    private boolean TimeFlag = false;
    private int _depth;
    private int _maxDepth;
    private PlayerType _myPlayer;
    private boolean isMaximizing = false;
    private Point bestMoveSoFar; // Para guardar la mejor jugada parcial

    public MyPlayer(String name, int depth) {
        this._name = name;
        this._depth = depth;
    }

    public MyPlayer(int depth) {
        this._depth = depth;
    }

    /**
     * Decideix el moviment del jugador donat un tauler i un color de peça que ha de posar.
     *
     * @param s Tauler i estat actual de joc.
     * @return el moviment que fa el jugador.
     */
    @Override
    public PlayerMove move(HexGameStatus s) {
        _exploredNodes = 0;
        _myPlayer = s.getCurrentPlayer();
        int alpha = -Integer.MAX_VALUE;
        int beta = Integer.MAX_VALUE;
        Point bestMove = null;
        int bestV = Integer.MIN_VALUE;
        
        List<Point> moves = getPossibleMoves(s);
        for(Point move : moves)
        {
            HexGameStatus auxS = new HexGameStatus(s);
            auxS.placeStone(move);
            int v = minimizing(auxS, _depth-1, alpha, beta);
            if(v > bestV)
            {
                bestV = v;
                bestMove = move;
            }
            alpha = Math.max(alpha, v);
            if(beta <= alpha) break;
        }
        
        return new PlayerMove(bestMove, _exploredNodes, _maxDepth, SearchType.MINIMAX);
    }

    
    private int minimizing(HexGameStatus s, int depth, int alpha, int beta)
    {
        if(s.isGameOver() || depth == 0)
        {
            if(s.isGameOver())
            {
                if(s.GetWinner() == PlayerType.PLAYER2) return 10000;
                else return -10000;
            }
            _exploredNodes++;
            return Heuristic.h(s, s.getCurrentPlayer());
        }
        int v = Integer.MAX_VALUE;
        List<Point> moves = getPossibleMoves(s);
        for(Point move : moves)
        {
            HexGameStatus auxS = new HexGameStatus(s);
            auxS.placeStone(move);
            v = Math.min(v, maximizing(auxS, depth-1, alpha, beta));
            beta = Math.min(beta, v);
            if(alpha >= beta) break;
        }
        return v;
    }
    
    private int maximizing(HexGameStatus s, int depth, int alpha, int beta)
    {
        if(s.isGameOver() || depth == 0)
        {
            if(s.isGameOver())
            {
                if(s.GetWinner() == PlayerType.PLAYER2) return 10000;
                else return -10000;
            }
            _exploredNodes++;
            return Heuristic.h(s, s.getCurrentPlayer());
        }
        
        int v = Integer.MIN_VALUE;
        List<Point> moves = getPossibleMoves(s);
        
        for(Point move : moves)
        {
            HexGameStatus auxS = new HexGameStatus(s);
            auxS.placeStone(move);
            v = Math.max(v, minimizing(auxS, depth-1, alpha, beta));
            alpha = Math.max(alpha, v);
            if(alpha >= beta) break;
        }
        return v;
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
    /*public int minimax(HexGameStatus s, int depth,  int alpha, int beta) {
        if (s.isGameOver() || depth == 0 || TimeFlag) {
            if (s.isGameOver()) {
                if (s.GetWinner() == PlayerType.PLAYER2) return 10000;
                else if (s.GetWinner() == PlayerType.PLAYER1) return -1000;
            }
            _exploredNodes++;
            return Heuristic.h(s, s.getCurrentPlayer());
        }

        int value;
        if (maximizing) {
            value = Integer.MIN_VALUE;
            for (int i = s.getSize() - 1; i >= 0; i--) {
                for (int j = s.getSize() - 1; j >= 0; j--) {
                    if (s.getPos(i, j) == 0) {
                        HexGameStatus newS = new HexGameStatus(s);
                        newS.placeStone(new Point(i, j));
                        int eval = minimax(newS, depth - 1, false, alpha, beta);
                        value = Math.max(value, eval);
                        alpha = Math.max(alpha, eval);
                        if (alpha >= beta) break;
                    }
                }
            }
        } else {
            value = Integer.MAX_VALUE;
            for (int i = s.getSize() - 1; i >= 0; i--) {
                for (int j = s.getSize() - 1; j >= 0; j--) {
                    if (s.getPos(i, j) == 0) {
                        HexGameStatus newS = new HexGameStatus(s);
                        newS.placeStone(new Point(i, j));
                        int eval = minimax(newS, depth - 1, true, alpha, beta);
                        value = Math.min(value, eval);
                        beta = Math.min(beta, eval);
                        if (alpha >= beta) break;
                    }
                }
            }
        }
        return value;
    }*/

    private List<Point> getPossibleMoves(HexGameStatus s) {
        List<Point> possibleMoves = new ArrayList<>();
        for (int i = 0; i < s.getSize(); i++) {
            for (int j = 0; j < s.getSize(); j++) {
                if (s.getPos(i, j) == 0) {
                    possibleMoves.add(new Point(i, j));
                }
            }
        }
        return possibleMoves;
    }

    /**
     * Ens avisa que hem de parar la cerca en curs perquè s'ha exhaurit el temps de joc.
     */
    @Override
    public void timeout() {
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
