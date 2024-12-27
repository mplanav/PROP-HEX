package edu.upc.epsevg.prop.hex.players;

import edu.upc.epsevg.prop.hex.Heuristic;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.PointDist;
import edu.upc.epsevg.prop.hex.SearchType;
import edu.upc.epsevg.prop.hex.ZobristEntry;

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
    private Map<Long, ZobristEntry> zobristTable = new HashMap<>();
    private static long[][][] _table;
    private Point bestParcialMove;

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
    _timeout = false;
    _myPlayer = s.getCurrentPlayer();
    if(_table == null) initZbTable(s.getSize());
    
    List<PointDist> possibleMoves = getPossibleMoves(s);
    for(int i = 0; i < possibleMoves.size(); i++) 
    if(possibleMoves.isEmpty()) return new PlayerMove(
                                                null,
                                                _exploredNodes,
                                                _depth,
                                                SearchType.MINIMAX);
    
    
    if(_IDS) 
    {
        PlayerMove move = IDSearch(s, possibleMoves);
        return move;
    }
    else return new PlayerMove(search(s, possibleMoves)._point, _exploredNodes, _depth, SearchType.MINIMAX);
}

private PlayerMove IDSearch(HexGameStatus s, List<PointDist> moves)
{
    Point bestMove =null;
    int bestValue = Integer.MIN_VALUE;
    int depth = 0;
    while(!_timeout)
    {
        depth++;
        List<PointDist> possibleMoves = new ArrayList<>(moves);
        long hash = hashing(s);
        ZobristEntry zobrist = zobristTable.get(hash);
        if (zobrist != null && zobrist._bestMove != null && possibleMoves.contains(zobrist.getBestMove())) {
            possibleMoves.remove(zobrist._bestMove);
            possibleMoves.add(0, zobrist._bestMove); // Priorizar el mejor movimiento previo
        }
        
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        PointDist newBm = null;
        int newBv = Integer.MIN_VALUE;
        for(PointDist move : possibleMoves)
        {
            if(_timeout) return new PlayerMove(newBm._point != null ? newBm._point : moves.get(0)._point, _exploredNodes, depth - 1, SearchType.MINIMAX_IDS);
            HexGameStatus auxStatus = new HexGameStatus(s);
            auxStatus.placeStone(move._point);
            int value = minimax(auxStatus, depth-1, false, alpha, beta, move._point);
            if(value > newBv)
            {
                newBv = value;
                newBm = move;
            }
            
            alpha = Math.max(alpha, newBv);
            if(beta <= alpha || newBv == Integer.MAX_VALUE) break;
        }
        if(!_timeout) 
        {
            bestMove = (newBm != null) ? newBm._point : bestMove;
            bestValue = newBv;
        }
        else if(_timeout) return new PlayerMove(newBm._point != null ? newBm._point : moves.get(0)._point, _exploredNodes, depth - 1, SearchType.MINIMAX_IDS);
    }
    if(bestMove == null && !moves.isEmpty()) bestMove = moves.get(0)._point;
    return new PlayerMove(bestMove, _exploredNodes, depth, SearchType.MINIMAX_IDS);
}

private PointDist search(HexGameStatus s, List<PointDist> moves)
{
    long hash = hashing(s);
    ZobristEntry zobrist = zobristTable.get(hash);
    if(zobrist != null && zobrist.getBestMove() != null && moves.contains(zobrist.getBestMove()))
    {
        moves.remove(zobrist.getBestMove());
        moves.add(0, zobrist.getBestMove());
    }
    
    int alpha = Integer.MIN_VALUE;
    int beta = Integer.MAX_VALUE;
    PointDist bestMove = null;
    int bestValue = Integer.MIN_VALUE;
    
    for(PointDist move : moves)
    {
        HexGameStatus auxStatus = new HexGameStatus(s);
        auxStatus.placeStone(move._point);
        
        int value = minimax(auxStatus, _depth-1, false, alpha, beta, move._point);
        if(value > bestValue)
        {
            bestValue = value;
            bestMove = move;
        }
        alpha = Math.max(alpha, bestValue);
        if(alpha >= beta) break;
    }
    saveZobrist(hash, _depth, bestValue, alpha, beta, bestMove);
    return bestMove;
}

/**
     * Implementación del algoritmo Minimax con poda alfa-beta.
     *
     * @param s Estado del juego.
     * @param depth Profundidad restante por explorar.
     * @param maximizing Indica si es el turno del jugador maximizador.
     * @param alpha Valor de poda alfa.
     * @param beta Valor de poda beta.
     * @param current Punto actual a evaluar.
     * @return Valor heurístico del mejor movimiento.
     */
public int minimax(HexGameStatus s, int depth, boolean maximizing, int alpha, int beta, Point current)
{
    if(depth == 0 || s.isGameOver() || _timeout)
    {
        _exploredNodes++;
        return Heuristic.h2(s,current)._cost;
    }
    
    List<PointDist> possibleMoves = getPossibleMoves(s);
    possibleMoves.sort((a, b) -> {
        int heuristicA = Heuristic.h2(s, a._point)._cost;
        int heuristicB = Heuristic.h2(s, b._point)._cost;
        return Integer.compare(heuristicB, heuristicA);
    });
    
    int value = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    for(PointDist move : possibleMoves)
    {
        HexGameStatus auxStatus = new HexGameStatus(s);
        auxStatus.placeStone(move._point);
        int eval = minimax(auxStatus, depth-1, !maximizing, alpha, beta, move._point);
        if(maximizing)
        {
            value = Math.max(value, eval);
            alpha = Math.max(alpha, eval);
        }
        else
        {
            value = Math.min(value, eval);
            beta = Math.min(beta, eval);
        }
        if(alpha >= beta) break;
    }
    return value;
}

private void initZbTable(int size)
{
    Random rand = new Random(123456789);
    _table = new long[size][size][3];
    for(int i = 0; i < size; i++)
    {
        for(int j = 0; j < size; j++)
        {
            for(int player = 0; player < 3; player++)
            {
                _table[i][j][player] = rand.nextLong();
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
                int pi = (value == 1) ? 1 : 2;
                hash ^= _table[i][j][pi];
            }
        }
    }
    return hash;
}

    /**
     * Obtiene una lista de movimientos posibles priorizando los más cercanos al centro.
     *
     * @param s Estado actual del juego.
     * @return Lista de puntos posibles para jugar, ordenados por proximidad al centro.
     */
private List<PointDist> getPossibleMoves(HexGameStatus s) {
    List<PointDist> possibleMoves = new ArrayList<>();
    for(int i = 0; i < s.getSize(); i++)
    {
        for(int j = 0; j < s.getSize(); j++)
        {
            if(s.getPos(i,j) == 0)
            {
                Point current = new Point(i,j);
                PointDist hResult = Heuristic.h2(s, current);
                possibleMoves.add(hResult);
            }
        }
    }
    possibleMoves.sort(Comparator.comparingInt(a -> -a._cost));
    return possibleMoves;
}

private void saveZobrist(long hash, int depth, int v, int alpha, int beta, PointDist bestMove)
{
    int limit;
    if(v <= alpha) limit = ZobristEntry.upper_lim;
    else if(v >= beta) limit = ZobristEntry.lower_lim;
    else limit = ZobristEntry.EScore;
    ZobristEntry zb = new ZobristEntry(v, depth, limit, bestMove);
    zobristTable.put(hash, zb);
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
