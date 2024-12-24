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
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
    private Map<Long, Integer> transpositionTable = new HashMap<>();
    private static long[][][] zbTable;

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

    if(zbTable == null) initZbTable(s.getSize());

    PointDist bestMove = null;
    int bestValue = Integer.MIN_VALUE;
    int depth = 1;
    SearchType st = _IDS ? SearchType.MINIMAX_IDS : SearchType.MINIMAX;
    
    while(_IDS && !_timeout)
    {
        bestMove = search(s, depth);
        depth++;
    }
    
    if(!_IDS) bestMove = search(s, _depth);
        
    if(bestMove == null)
    {
        List<PointDist> possibleMoves = getPossibleMoves(s);
        bestMove = possibleMoves.isEmpty() ? null : possibleMoves.get((int)(Math.random() * possibleMoves.size()));
    }
    
    return new PlayerMove(
        bestMove._point,
        _exploredNodes,
        _IDS ? _depth -1 : _depth,
        st);
}

private PointDist search(HexGameStatus s, int depth)
{
    List<PointDist> possibleMoves = getPossibleMoves(s);
    possibleMoves.sort((a, b) -> {
       int heuristicA = Heuristic.h(s, a._point)._cost;
       int heuristicB = Heuristic.h(s, b._point)._cost;
       return Integer.compare(heuristicB, heuristicA);
    });
    
    int alpha = Integer.MIN_VALUE;
    int beta = Integer.MAX_VALUE;
    PointDist bestMove = null;
    int bestValue = Integer.MIN_VALUE;
    
    for(int i = 0; i < possibleMoves.size(); i++)
    {
        if(_IDS && _timeout) break;
        PointDist move = possibleMoves.get(i);
        HexGameStatus auxStatus = new HexGameStatus(s);
        auxStatus.placeStone(move._point);
        
        long hash = hashing(auxStatus);
        int value;
        
        if(transpositionTable.containsKey(hash))
            value = transpositionTable.get(hash);
        else 
        {
            int reduction = (i >= 3) ? 1 : 0;
            value = minimax(auxStatus, depth -1 -reduction, false, alpha, beta, move._point);
            transpositionTable.put(hash, value);
        }
        
        if(value > bestValue)
        {
            bestValue = value;
            bestMove = move;
        }
        alpha = Math.max(alpha, bestValue);
        if(alpha >= beta) break;
    }
    return bestMove;
}

private void initZbTable(int size)
{
    Random rand = new Random(123456789);
    zbTable = new long[size][size][3];
    for(int i = 0; i < size; i++)
    {
        for(int j = 0; j < size; j++)
        {
            for(int player = 0; player < 3; player++)
            {
                zbTable[i][j][player] = rand.nextLong();
            }
        }
    }
}

private long hashing(HexGameStatus s)
{
    long hash = 0L;
    int size = s.getSize();
    for(int i = 0; i < size; i++)
    {
        for(int j = 0; j < size; j++)
        {
            int value = s.getPos(i,j);
            if(value != 0)
            {
                int pi = (value == 1) ? 1 : -1;
                hash ^= zbTable[i][j][pi];
            }
        }
    }
    return hash;
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
    
    possibleMoves.sort((a, b) -> {
        int heuristicA = Heuristic.h(s, a._point)._cost;
        int heuristicB = Heuristic.h(s, b._point)._cost;
        return Integer.compare(heuristicB, heuristicA); // Mayor a menor
    });
    
    int moveI = 0; //index current movement
    int thresold = 5; // thresold to apply reduction

    for (PointDist movement : possibleMoves) {
        HexGameStatus newS = new HexGameStatus(s);
        newS.placeStone(movement._point);
        
        int reduction = (moveI >= thresold) ? 1 : 0;
        moveI++;

        int eval = minimax(newS, depth - 1 - reduction, !maximizing, alpha, beta, movement._point);

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
