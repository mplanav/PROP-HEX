package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.Heuristic;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.PointDist;
import edu.upc.epsevg.prop.hex.SearchType;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Jugador con heurística modificada para favorecer posiciones cercanas al centro.
 */
public class MyPlayer implements IPlayer, IAuto {

    private String _name;
    private long _exploredNodes;
    private boolean _IDS;
    private boolean _timeout = false;
    private int _depth;
    private PlayerType _myPlayer;

    public MyPlayer(String name, int depth, boolean IDS) {
        this._name = name;
        this._depth = depth;
        this._IDS = IDS;
    }

    /**
     * Decide el movimiento del jugador dado un tablero y el color de la pieza.
     *
     * @param s Tablero y estado actual del juego.
     * @return el movimiento que realiza el jugador.
     */
    @Override
public PlayerMove move(HexGameStatus s) {
    _exploredNodes = 0;
    _myPlayer = s.getCurrentPlayer();
    _timeout = false;
    SearchType ST;

    PointDist bestMove = null;
    int bestValue = Integer.MIN_VALUE;
   
    if(_IDS)
    {
        int depth = 1;
        ST = SearchType.MINIMAX_IDS;
        while(!_timeout)
        {
            PointDist currentBestMove = null;
            int currentBestValue = Integer.MIN_VALUE;
            
            List<PointDist> possibleMoves = getPossibleMoves(s);
            for(PointDist move : possibleMoves)
            {
                HexGameStatus auxStatus = new HexGameStatus(s);
                auxStatus.placeStone(move._point);
                
                int value = minimax(auxStatus, depth, false, Integer.MIN_VALUE, Integer.MAX_VALUE, move._point);
                
                if(value > currentBestValue)
                {
                    currentBestValue = value;
                    currentBestMove = move;
                }
                if(_timeout) break;
            }
            
            if(!_timeout)
            {
                bestValue = currentBestValue;
                bestMove = currentBestMove;
                depth++;
            }
        }
    }
    else 
    {
        ST = SearchType.MINIMAX;
        List<PointDist> possibleMoves = getPossibleMoves(s);
        for(PointDist move : possibleMoves)
        {
            HexGameStatus auxStatus = new HexGameStatus(s);
            auxStatus.placeStone(move._point);
            
            int value = minimax(auxStatus, _depth-1, false, Integer.MIN_VALUE, Integer.MAX_VALUE, move._point);
            
            if(value > bestValue)
            {
                bestValue = value;
                bestMove = move;
            }
        }
    }
    if(bestMove != null)
        if(bestMove != null)
            return new PlayerMove(
                    bestMove._point,
                    _exploredNodes,
                    _depth-1,
                    SearchType.MINIMAX_IDS
            );
    return null;
}

    /**
     * Implementación del algoritmo Minimax con poda alfa-beta.
     *
     * @param s Estado del juego.
     * @param depth Profundidad restante por explorar.
     * @param maximizing Indica si es el turno del jugador maximizador.
     * @param alpha Valor de poda alfa.
     * @param beta Valor de poda beta.
     * @param currentPoint Punto actual a evaluar.
     * @return Valor heurístico del mejor movimiento.
     */
public int minimax(HexGameStatus s, int depth, boolean maximizing, int alpha, int beta, Point currentPoint) {
    if (s.isGameOver() || depth == 0 || _timeout) {
        if (s.isGameOver()) {
            if (s.GetWinner() == PlayerType.PLAYER2) return 10000;
            else if (s.GetWinner() == PlayerType.PLAYER1) return -10000;
        }
        _exploredNodes++;
        return Heuristic.h(s, currentPoint)._cost;
    }

    int value = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;

    List<PointDist> possibleMoves = getPossibleMoves(s);

    for (PointDist movement : possibleMoves) {
        HexGameStatus newS = new HexGameStatus(s);
        newS.placeStone(movement._point);

        int eval = minimax(newS, depth - 1, !maximizing, alpha, beta, movement._point);

        if (maximizing) {
            value = Math.max(value, eval);
            alpha = Math.max(alpha, eval);
        } else {
            value = Math.min(value, eval);
            beta = Math.min(beta, eval);
        }

        if (alpha >= beta) break; // Corta la búsqueda si ocurre poda
    }

    return value;
}

    /**
     * Obtiene una lista de movimientos posibles priorizando los más cercanos al centro.
     *
     * @param s Estado actual del juego.
     * @return Lista de puntos posibles para jugar, ordenados por proximidad al centro.
     */
private List<PointDist> getPossibleMoves(HexGameStatus s) {
    List<PointDist> possibleMovesWithHeuristics = new ArrayList<>();
    for (int i = 0; i < s.getSize(); i++) {
        for (int j = 0; j < s.getSize(); j++) {
            if (s.getPos(i, j) == 0) { // Solo considerar celdas vacías
                Point currentPoint = new Point(i, j);
                PointDist heuristicResult = Heuristic.h(s, currentPoint); // Calcula heurística
                possibleMovesWithHeuristics.add(heuristicResult); // Agrega el resultado
            }
        }
    }
    return possibleMovesWithHeuristics; // Devuelve la lista de PointDist
}
    /**
     * Devuelve el nombre del jugador para la visualización.
     *
     * @return Nombre del jugador.
     */
    @Override
    public String getName() {
            return "Center-Focused Player";
    }  

    /**
     * Notifica que el tiempo de búsqueda ha terminado.
     */
    @Override
    public void timeout() {
        System.out.println("Timeout alcanzado.");
        _timeout = true;
    }

    
}
