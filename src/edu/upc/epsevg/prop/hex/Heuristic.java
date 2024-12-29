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

    if (s.getCurrentPlayer() == PlayerType.PLAYER1) { // Conectar arriba-abajo
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

    public static PointDist h2(HexGameStatus s, Point p)
    {
        PlayerType op = (s.getCurrentPlayer() == PlayerType.PLAYER1) ? PlayerType.PLAYER2 : PlayerType.PLAYER1;
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
        
        if (s.getCurrentPlayer() == PlayerType.PLAYER1) { // Conectar arriba-abajo
            for (int row = 0; row < size; row++) {
                PointDist dest = new PointDist(new Point(row, size - 1), 0);
                dijkstraResult myresult = dijkstra(costs, size, new PointDist(p, 0), dest, s.currentPlayer);

                // Evaluar si este camino es mejor
                if (myresult._cost < minDistance) {
                    minDistance = myresult._cost;
                    bestResult = myresult; // Guardar el mejor camino
                }
            }
        } else { // Conectar izquierda-derecha
            for (int col = 0; col < size; col++) {
                PointDist dest = new PointDist(new Point(size - 1, col), 0);
                dijkstraResult opresult = dijkstra(costs, size, new PointDist(p, 0), dest, s.currentPlayer);

                // Evaluar si este camino es mejor
                if (opresult._cost < minDistance) {
                    minDistance = opresult._cost;
                    bestResult = opresult; // Guardar el mejor camino del oponente
                }
            }
        }
        heuristicValue += minDistance * -10;
        
        

        // Calcular distancia Manhattan al centro del tablero
        int distanceToCenter = Math.abs(p.x - centerX) + Math.abs(p.y - centerY);
        // Asignar mayor puntuación a los puntos más cercanos al centro
        heuristicValue += dynamicCosts(s, "center") * (-distanceToCenter);
        
        //Miramos si hay bridges posibles en la posición que miramos
        for (Point d : diagonals) {
            Point diagonal = new Point(p.x + d.x, p.y + d.y);

            // Validar directamente en el bucle
            if (p.x + d.x >= 0 && p.x + d.x < s.getSize() &&
                p.y + d.y >= 0 && p.y + d.y < s.getSize())
            {
                boolean valid = validateDiagonal(d, p, s); //Se comprueba que la diagonal sea util en la partida
                if(valid){
                    if (s.getPos(diagonal) == 1 && !(diagonalCount >= 2)) {
                        heuristicValue += dynamicCosts(s, "diagonal");
                        ++diagonalCount;
                    }
                    if (s.getPos(diagonal) == -1 && !(opDiagonalCount >= 2)) {
                        heuristicValue += dynamicCosts(s, "diagonal");
                        ++opDiagonalCount;
                    }
                }
            }
        }
        
        if(p.x == 0 || p.y == 0 || p.x == size-1 || p.y == size-1){
            leftBorderCounter = 0;
            rightBorderCounter = 0;   
            upBorderCounter = 0;
            downBorderCounter = 0;
            if(s.getCurrentPlayer() == PlayerType.PLAYER2){
                for(int i = 0; i < size; i++){
                    if(s.getPos(0, i) == s.getCurrentPlayerColor()) ++leftBorderCounter;
                }
                heuristicValue += 1 * (size-leftBorderCounter);
                for(int i = 0; i < size; i++){
                    if(s.getPos(0, i) == s.getCurrentPlayerColor()) ++rightBorderCounter;
                }
                heuristicValue += 1 * (size-rightBorderCounter);
            } else{
                for(int i = 0; i < size; i++){
                    if(s.getPos(i, 0) == s.getCurrentPlayerColor()) ++upBorderCounter;
                }
                heuristicValue += 1 * (size-upBorderCounter);
                for(int i = 0; i < size; i++){
                    if(s.getPos(i, 0) == s.getCurrentPlayerColor()) ++downBorderCounter;
                }
                heuristicValue += 1 * (size-downBorderCounter);
            }  
        }
        
        //Comprobamos las conexiones directas de la posición que miramos
        List<Point> neighbors = getNeighbors(p.x, p.y, size);
        for (Point neighbor : neighbors){
            if (s.getPos(neighbor.x, neighbor.y) == 1 && !(connectionCount >= 2)) {
                ++connectionCount;
                heuristicValue += dynamicCosts(s, "connection"); // Incentivar conexión con fichas propias
            }
            if (s.getPos(neighbor.x, neighbor.y) == -1 && !(opConnectionCount >= 2)) {
                ++opConnectionCount;
                heuristicValue += dynamicCosts(s, "connection"); // Incentivar no conexión del oponente
            }
        }
        
        return new PointDist(p, heuristicValue);
    }

    private static boolean validateDiagonal(Point d, Point p, HexGameStatus s){
        boolean valid = true;
        if(d == diagonals[0]){
            if(s.getPos(p.x, p.y - 1) == -(s.getCurrentPlayerColor()) 
               && s.getPos(p.x + 1, p.y - 1) == -(s.getCurrentPlayerColor())) valid = false;
        }
        else if(d == diagonals[1]){
            if(s.getPos(p.x + 1, p.y - 1) == -(s.getCurrentPlayerColor()) 
               && s.getPos(p.x + 1, p.y) == -(s.getCurrentPlayerColor())) valid = false;
        }
        else if(d == diagonals[2]){
            if(s.getPos(p.x + 1, p.y) == -(s.getCurrentPlayerColor()) 
               && s.getPos(p.x, p.y + 1) == -(s.getCurrentPlayerColor())) valid = false;
        }
        else if(d == diagonals[3]){
            if(s.getPos(p.x, p.y + 1) == -(s.getCurrentPlayerColor()) 
               && s.getPos(p.x - 1, p.y + 1) == -(s.getCurrentPlayerColor())) valid = false;
        }
        else if(d == diagonals[4]){
            if(s.getPos(p.x - 1, p.y + 1) == -(s.getCurrentPlayerColor()) 
               && s.getPos(p.x - 1, p.y) == -(s.getCurrentPlayerColor())) valid = false;
        }
        else if(d == diagonals[5]){
            if(s.getPos(p.x - 1, p.y) == -(s.getCurrentPlayerColor()) 
               && s.getPos(p.x, p.y - 1) == -(s.getCurrentPlayerColor())) valid = false;
        }
        
        return valid;
    }
   
    private static int[][] generateCosts(HexGameStatus s)
    {
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
    
    
    //Falta retocar
    private static int dynamicCosts(HexGameStatus s, String type)
    {
        int movesDone = calculateMovesBoard(s);
        int size = s.getSize();
        int gamePhase = movesDone < (size * size / 3) ? 0 : (movesDone < (2 * size * size / 3) ? 1 : 2);
        int goCenter = movesDone > 23 ? 1 : 0; 
        
        
        switch(type)
        {
            case "center":
                return (goCenter == 0) ? 2 : 0;
            case "distance":
                return (gamePhase == 0) ? 1 : (gamePhase == 1) ? 5 : 10;
            case "connection":
                return (gamePhase == 0) ? 2 : (gamePhase == 1) ? 10 : 15;
            case "diagonal":
                return 20;
            case "block":
                return (gamePhase == 0) ? 15 : (gamePhase == 1) ? 15 : 10;
            case "virtualConnection":
                return (gamePhase == 0) ? 10 : (gamePhase == 1) ? 10 : 5;
            default: return 1;
        }
    }
    
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
    
    public static void printDistances(int[][] distances, int size) {
    System.out.println("Final distances:");
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            System.out.print((distances[i][j] == Integer.MAX_VALUE ? "INF" : distances[i][j]) + "\t");
        }
        System.out.println();
    }
}
}
   