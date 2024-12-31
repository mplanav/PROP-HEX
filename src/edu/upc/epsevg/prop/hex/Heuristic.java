/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.PriorityQueue;
import org.w3c.dom.Node;
/**
 *
 * @author marc
 */
public class Heuristic {
    //status
    HexGameStatus _status;
    PlayerType _player;
    static Point[] diagonals = {
            new Point(1, -2),
            new Point(2, -1),
            new Point(1, 1),
            new Point(-1, 2),
            new Point(-2, 1),
            new Point(-1, -1),
        };
    static int diagonalCount;
    static int opDiagonalCount;
    static int connectionCount;
    static int opConnectionCount;
    static int upBorderCounter;
    static int downBorderCounter;
    static int leftBorderCounter;
    static int rightBorderCounter;
        
    /**
     * Constructor
     * 
     * @param status
     * @param player 
     */
    public Heuristic(HexGameStatus status, PlayerType player)
    {
        this._status = status;
        this._player = player;
    }
    
    public Heuristic()
    {
        
    }
    
    public static dijkstraResult h_pruebas(HexGameStatus s, Point p) {
        int heuristicValue = 0;
        int size = s.getSize();
        int[][] costs = generateCosts(s); // Generar costos del tablero
        int minDistance = Integer.MAX_VALUE;
        dijkstraResult bestResult = null; // Para almacenar el mejor resultado

        if (s.getCurrentPlayer() == PlayerType.PLAYER2) { // Conectar arriba-abajo
            for (int row = 0; row < size; row++) {
                PointDist dest = new PointDist(new Point(row, size - 1), 0);
                dijkstraResult result = dijkstra(costs, size, new PointDist(p, 0), dest, s.currentPlayer);

                // Evaluar si este camino es mejor
                if (result._cost < minDistance) {
                    minDistance = result._cost;
                    bestResult = result; // Guardar el mejor camino
                }
            }
        } else { // Conectar izquierda-derecha
            for (int col = 0; col < size; col++) {
                PointDist dest = new PointDist(new Point(size - 1, col), 0);
                dijkstraResult result = dijkstra(costs, size, new PointDist(p, 0), dest, s.currentPlayer);

                // Evaluar si este camino es mejor
                if (result._cost < minDistance) {
                    minDistance = result._cost;
                    bestResult = result; // Guardar el mejor camino del oponente
                }
            }
        }
        heuristicValue = minDistance;
        return bestResult;
    }

    /**
     * Metodo que calcula el camino más corto en una matriz de costos desde un punto de origen (source) hasta un punto de destino (dest).
     * 
     * @param costs
     * @param size
     * @param source
     * @param dest
     * @param player
     * @return 
     */
    private static dijkstraResult dijkstra(int[][] costs, int size, PointDist source, PointDist dest, PlayerType player) {
        int[][] distances = new int[size][size];
        boolean[][] visited = new boolean[size][size];
        PointDist[][] prev = new PointDist[size][size];
        PriorityQueue<PointDist> queue = new PriorityQueue<>(
            (a, b) -> Integer.compare(distances[a._point.x][a._point.y], distances[b._point.x][b._point.y])
        );

        // Inicializar matrices
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                distances[i][j] = Integer.MAX_VALUE;
                prev[i][j] = null;
            }
        }
        distances[source._point.x][source._point.y] = 0;
        queue.add(source);

        while (!queue.isEmpty()) {
            PointDist current = queue.poll();

            if (current._point.equals(dest._point)) break; // Camino más corto encontrado
            if (visited[current._point.x][current._point.y]) continue;
            visited[current._point.x][current._point.y] = true;

            // Explorar vecinos
            for (Point nb : getNeighbors(current._point.x, current._point.y, size)) {
                if (!visited[nb.x][nb.y] && costs[nb.x][nb.y] < 100000) {
                    int newCost = distances[current._point.x][current._point.y] + costs[nb.x][nb.y];
                    if (newCost < distances[nb.x][nb.y]) {
                        distances[nb.x][nb.y] = newCost;
                        prev[nb.x][nb.y] = current; // Guardar el nodo previo
                        queue.add(new PointDist(nb, newCost));
                    }
                }
            }
        }

        // Reconstruir el camino más corto (solo puntos vacíos)
        List<PointDist> path = new ArrayList<>();
        PointDist step = new PointDist(dest._point, distances[dest._point.x][dest._point.y]);
        if (prev[step._point.x][step._point.y] != null || step._point.equals(source._point)) {
            while (step != null) {
                // Filtrar solo celdas vacías (0)
                if (costs[step._point.x][step._point.y] == 1) {
                    path.add(step);
                }
                step = prev[step._point.x][step._point.y];
            }
            Collections.reverse(path); // Invertir para obtener el orden correcto
        }

        int cost = distances[dest._point.x][dest._point.y];
        if (cost == Integer.MAX_VALUE) path.clear(); // Si no hay camino, devolver lista vacía
        return new dijkstraResult(cost, path);
    }

    /**
     * Calcula el valor heurístico en un punto específico del tablero de HEX
     * 
     * @param s
     * @param p
     * @return 
     */
    public static PointDist h2(HexGameStatus s, Point p)
    {
        diagonalCount = 0;
        opDiagonalCount = 0;
        connectionCount = 0;
        opConnectionCount = 0; 
        int heuristicValue = 0;
        int size = s.getSize();
        int[][] costs = generateCosts(s);
        int minDistance = Integer.MAX_VALUE;
        int centerX = size / 2;
        int centerY = size / 2;
        
        dijkstraResult bestResult = null; // Para almacenar el mejor resultado
        
        if (s.getCurrentPlayer() == PlayerType.PLAYER2) { // Conectar arriba-abajo
            for (int row = 0; row < size; row++) {
                PointDist dest = new PointDist(new Point(row, size - 1), 0);
                dijkstraResult result = dijkstra(costs, size, new PointDist(p, 0), dest, s.currentPlayer);

                // Evaluar si este camino es mejor
                if (result._cost < minDistance) {
                    minDistance = result._cost;
                    bestResult = result; // Guardar el mejor camino
                }
            }
        } else { // Conectar izquierda-derecha
            for (int col = 0; col < size; col++) {
                PointDist dest = new PointDist(new Point(size - 1, col), 0);
                dijkstraResult result = dijkstra(costs, size, new PointDist(p, 0), dest, s.currentPlayer);

                // Evaluar si este camino es mejor
                if (result._cost < minDistance) {
                    minDistance = result._cost;
                    bestResult = result; // Guardar el mejor camino del oponente
                }
            }
        }
        heuristicValue += minDistance * 25;

        // Calcular distancia Manhattan al centro del tablero
        int distanceToCenter = Math.abs(p.x - centerX) + Math.abs(p.y - centerY);
        // Asignar mayor puntuación a los puntos más cercanos al centro
        heuristicValue += dynamicCosts(s, "center", bestResult) * (-distanceToCenter);
        
        //Miramos si hay bridges posibles en la posición que miramos
        for (Point d : diagonals) {
            Point diagonal = new Point(p.x + d.x, p.y + d.y);

            // Validar directamente en el bucle
            if (p.x + d.x >= 0 && p.x + d.x < s.getSize() &&
                p.y + d.y >= 0 && p.y + d.y < s.getSize())
            {
                boolean valid = validateDiagonal(d, p, s); //Se comprueba que la diagonal sea util en la partida
                if(valid){
                    if (s.getPos(diagonal) == 1 && !(diagonalCount >= 3)) {
                        Point bridge = new Point(p.x + d.x, p.y + d.y); 
                        int distanceFactorBridge = calculateDistanceFactor(s, p, bridge);
                        heuristicValue += dynamicCosts(s, "diagonal", bestResult) + distanceFactorBridge;
                        ++diagonalCount;
                    }
                    if (s.getPos(diagonal) == -1 && !(opDiagonalCount >= 2) && calculateMovesBoard(s) > 3) {
                        Point bridge = new Point(p.x + d.x, p.y + d.y); 
                        int distanceFactorBridge = calculateDistanceFactor(s, p, bridge);
                        heuristicValue += dynamicCosts(s, "diagonal", bestResult) + distanceFactorBridge * 2;
                        ++opDiagonalCount;
                    }
                }
            }
        }

        List<Point> neighbors = getNeighbors(p.x, p.y, size);
        for (Point neighbor : neighbors){
            if (s.getPos(neighbor.x, neighbor.y) == 1 && connectionCount < 3) ++connectionCount;
            else if (s.getPos(neighbor.x, neighbor.y) == -1 && connectionCount < 3) ++opConnectionCount;
        }
        
        heuristicValue += 5 * connectionCount;
        
        if (p.x - 1 >= 0 && p.x + 1 < s.getSize() &&
            p.y -1 >= 0 && p.y + 1 < s.getSize()){
                    if(isBridgeConnection(p, s)) heuristicValue += Integer.MAX_VALUE;
        }
        
        if(p.x == 0){
            if (p.x + 1 < s.getSize() &&
            p.y -1 >= 0 && p.y + 1 < s.getSize()){
                if((s.getPos(p.x + 1, p.y - 1) == 1 && s.getPos(p.x, p.y - 1) == -1)) heuristicValue += Integer.MAX_VALUE;
                if((s.getPos(p.x + 1, p.y) == 1) && (s.getPos(p.x, p.y - 1) == -1)) heuristicValue += Integer.MAX_VALUE;
            }
        }
        
        for(Point c : identifyCriticalMoves(s)){
            if(p == c) heuristicValue += 10;
        }
        
        return new PointDist(p, heuristicValue);
    }

    /**
     * Identifica los movimientos criticos en el estado actual del tablero
     * 
     * @param s
     * @return 
     */
    public static List<Point> identifyCriticalMoves(HexGameStatus s) {
        List<Point> criticalMoves = new ArrayList<>();
        int size = s.getSize();
        PlayerType currentPlayer = s.getCurrentPlayer();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (s.getPos(i, j) == 0) { // Celda vacía
                    HexGameStatus tempStatus = new HexGameStatus(s);
                    tempStatus.placeStone(new Point(i, j));
                    if (tempStatus.isGameOver() && tempStatus.GetWinner() == currentPlayer) {
                        criticalMoves.add(new Point(i, j));
                    }
                }
            }
        }

        return criticalMoves;
    }
    
    /**
     * Calcula un factor basado en la proximidad de dos puntos (p1 y p2) a los bordes del tablero
     * 
     * @param s
     * @param p1
     * @param p2
     * @return 
     */
    private static int calculateDistanceFactor(HexGameStatus s, Point p1, Point p2) {
        int size = s.getSize();
        PlayerType player = s.getCurrentPlayer();
        int distanceToStart, distanceToEnd;

        if (player == PlayerType.PLAYER2) {
            distanceToStart = Math.min(p1.x, p2.x); // Distancia al borde superior
            distanceToEnd = Math.min(size - 1 - p1.x, size - 1 - p2.x); // Distancia al borde inferior
        } else {
            distanceToStart = Math.min(p1.y, p2.y); // Distancia al borde izquierdo
            distanceToEnd = Math.min(size - 1 - p1.y, size - 1 - p2.y); // Distancia al borde derecho
        }

        // Penalizar menos si está cerca del inicio o del final
        int gamePhase = calculateGamePhase(s);
        int weight = (gamePhase == 0) ? 20 : (gamePhase == 1 ? 10 : 5);
        return weight * (size - Math.min(distanceToStart, distanceToEnd));
    }
    
    /**
     * Calcula la fase del juego dependiendo de las fichas que hay en el tablero.
     * 
     * @param s
     * @return 
     */
    private static int calculateGamePhase(HexGameStatus s) {
        int movesDone = calculateMovesBoard(s);
        int size = s.getSize();
        if (movesDone < size * size / 3) return 0; // Fase inicial
        if (movesDone < 2 * size * size / 3) return 1; // Fase intermedia
        return 2; // Fase final
    }
    
    /**
     * Verifica que la diagonal (bridge) que se está revisando vaya a ser útil en el juego.
     * 
     * @param d
     * @param p
     * @param s
     * @return 
     */
    private static boolean validateDiagonal(Point d, Point p, HexGameStatus s){
        boolean valid = true;
        if(d == diagonals[0]){
            if(s.getPos(p.x, p.y - 1) != 0 || s.getPos(p.x + 1, p.y - 1) != 0) valid = false;
        }
        else if(d == diagonals[1]){
            if(s.getPos(p.x + 1, p.y - 1) != 0 || s.getPos(p.x + 1, p.y) != 0) valid = false;
        }
        else if(d == diagonals[2]){
            if(s.getPos(p.x + 1, p.y) != 0 || s.getPos(p.x, p.y + 1) != 0) valid = false;
        }
        else if(d == diagonals[3]){
            if(s.getPos(p.x, p.y + 1) != 0 || s.getPos(p.x - 1, p.y + 1) != 0) valid = false;
        }
        else if(d == diagonals[4]){
            if(s.getPos(p.x - 1, p.y + 1) != 0 || s.getPos(p.x - 1, p.y) != 0) valid = false;
        }
        else if(d == diagonals[5]){
            if(s.getPos(p.x - 1, p.y) != 0 || s.getPos(p.x, p.y - 1) != 0) valid = false;
        }
        
        return valid;
    }
    
    /**
     * Comprueba que una de las dos posiciones criticas del bridge está ocupada por el oponente
     * 
     * @param p
     * @param s
     * @return 
     */
    private static boolean isBridgeConnection(Point p, HexGameStatus s){
        boolean bridgeConnection = false;
        int color = s.getCurrentPlayerColor();
        //Bridge Vertical
        if((s.getPos(p.x+1, p.y-1) == color && s.getPos(p.x, p.y+1) == color) &&
           (s.getPos(p.x+1, p.y) == -color)) bridgeConnection = true;
        if((s.getPos(p.x-1, p.y+1) == color && s.getPos(p.x, p.y-1) == color) &&
           (s.getPos(p.x-1, p.y) == -color)) bridgeConnection = true;
        
        //Bridge Diagonal Derecha
        if((s.getPos(p.x-1, p.y+1) == color && s.getPos(p.x+1, p.y) == color) &&
           (s.getPos(p.x, p.y+1) == -color)) bridgeConnection = true;
        if((s.getPos(p.x-1, p.y) == color && s.getPos(p.x+1, p.y-1) == color) &&
           (s.getPos(p.x, p.y+1) == -color)) bridgeConnection = true;
        
        //Bridge Diagonal Izquierda
        if((s.getPos(p.x, p.y-1) == color && s.getPos(p.x+1, p.y) == color) && 
           (s.getPos(p.x+1, p.y-1) == -color)) bridgeConnection = true;
        if((s.getPos(p.x-1, p.y) == color && s.getPos(p.x, p.y+1) == color) && 
           (s.getPos(p.x-1, p.y+1) == -color)) bridgeConnection = true;

        return bridgeConnection;
    }
   
    /**
     * Genera una matriz de costos que representa el estado actual del tablero para el jugador.
     * 
     * @param s
     * @return 
     */
    private static int[][] generateCosts(HexGameStatus s){
       int size = s.getSize();
       PlayerType player = s.getCurrentPlayer();
       int my = (player == PlayerType.PLAYER2) ? -1 : 1;
       int op = -my;
       
       int[][] costs = new int[size][size];
       for(int i = 0; i < size; i++)
       {
           for(int j = 0; j < size; j++)
           {
               int cell = s.getPos(i,j);
               if(cell == my) costs[i][j] = 0; 
               else if(cell == op) costs[i][j] = 100000;
               else costs[i][j] = 1;
           }
       }
       return costs;
    }

    /**
     * Devuelve todos los vecinos que tieneun punto.
     * 
     * @param x
     * @param y
     * @param size
     * @return 
     */
    private static List<Point> getNeighbors(int x, int y, int size)
    {
        int[] directionX = {-1, -1, 0, 0, 1, 1};
        int[] directionY = {0, 1, -1, 1, -1, 0};
        List<Point> neighbors = new ArrayList<>();
        
        for(int i = 0; i < directionX.length; i++)
        {
            int nbX = x + directionX[i];
            int nbY = y + directionY[i];
            
            if(nbX >= 0 && nbX < size && nbY >= 0 && nbY < size)
                neighbors.add(new Point(nbX, nbY));
        }
        return neighbors;
    }
    
    /**
     * Este método controla lo que se va a sumar en la heurística dependiendo de que estamos revisando y en que momento de partida estamos.
     * 
     * @param s
     * @param type
     * @param d
     * @return 
     */
    private static int dynamicCosts(HexGameStatus s, String type, dijkstraResult d)
    {
        int movesDone = calculateMovesBoard(s);
        int size = s.getSize();
        int gamePhase;
        int goCenter = movesDone > 25 ? 1 : 0; 
        if(d != null) gamePhase = d._cost < 6 ? 1 : 2;
        else gamePhase = 1;
        
        
        switch(type)
        {
            case "center":
                return (goCenter == 0) ? 20 : 0;
            case "distance":
                return (gamePhase == 1) ? 5 : 10;
            case "diagonal":
                return (gamePhase == 1) ? 30 : 40;
            case "border":
                return (gamePhase == 1) ? 1 : 3;
            case "virtualConnection":
                return (gamePhase == 0) ? 10 : (gamePhase == 1) ? 10 : 5;
            default: return 1;
        }
    }
    
    /**
     * Devuelve la cantidad de movimientos que se han realizado en total en un punto concreto de la partida.
     * 
     * @param s
     * @return 
     */
    private static int calculateMovesBoard(HexGameStatus s)
    {
        int count = 0; 
        for (int i = 0; i < s.getSize(); i++)
        {
            for(int j = 0; j < s.getSize(); j++)
            {
                if(s.getPos(i,j) != 0) count++;
            }
        }
        return count;
    }    
}
