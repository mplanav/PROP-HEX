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
    static Point[] diagonales = {
            new Point(1, 1),
            new Point(1, -1),
            new Point(-1, -1),
            new Point(-1, 1)
        };
        
    
    public Heuristic(HexGameStatus status, PlayerType player)
    {
        this._status = status;
        this._player = player;
    }
    
    public Heuristic()
    {
        
    }
    
    /*public static int center(HexGameStatus s) {
    int size = s.getSize();
    int centerX = size / 2;
    int centerY = size / 2;
    int[][] heuristicValues = generateCosts(s);
    
    // Ordenar las celdas por proximidad al centro y seleccionar las de mayor valor heurístico
    PriorityQueue<Point> centerQueue = new PriorityQueue<>((a, b) -> {
        int distA = Math.abs(a.x - centerX) + Math.abs(a.y - centerY);
        int distB = Math.abs(b.x - centerX) + Math.abs(b.y - centerY);
        return Integer.compare(distA, distB);
    });

    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            if (s.getPos(i, j) == 0) { // Solo considerar celdas vacías
                centerQueue.add(new Point(i, j));
            }
        }
    }

    int totalHeuristic = 0;
    while (!centerQueue.isEmpty()) {
        Point p = centerQueue.poll();
        totalHeuristic += heuristicValues[p.x][p.y];
    }

    return totalHeuristic;
}*/

    
 /*public static PointDist h(HexGameStatus s, Point currentPoint) {
    int size = s.getSize();
    int[][] costs = generateCosts(s);
    PlayerType player = s.getCurrentPlayer();

    int pathScore = 0; // Puntaje adicional para conectar con otras fichas
    int minDistance = Integer.MAX_VALUE;

    if (player == PlayerType.PLAYER1) { // Conectar arriba-abajo
        for (int row = 0; row < size; row++) {
            PointDist dest = new PointDist(new Point(row, size - 1), 0);
            int distance = dijkstra(costs, size, new PointDist(currentPoint, 0), dest, player);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
    } else { // Conectar izquierda-derecha
        for (int col = 0; col < size; col++) {
            PointDist dest = new PointDist(new Point(size - 1, col), 0);
            int distance = dijkstra(costs, size, new PointDist(currentPoint, 0), dest, player);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
    }

    // Evaluar conexión con otras fichas del jugador
    List<Point> neighbors = getNeighbors(currentPoint.x, currentPoint.y, size);
    for (Point neighbor : neighbors) {
        if (s.getPos(neighbor.x, neighbor.y) == (player == PlayerType.PLAYER1 ? 1 : 2)) {
            pathScore -= 5; // Incentivar conexión con fichas propias
        }
    }

    // Devolver heurística combinada
    return new PointDist(currentPoint, minDistance + pathScore);
}*/

public static PointDist h(HexGameStatus s, Point currentPoint) {
    int size = s.getSize();
    int centerX = size / 2;
    int centerY = size / 2;
    
    int[][] costs = generateCosts(s);
    PlayerType player = s.getCurrentPlayer();
    int pathScore = 0; // Puntaje adicional para conectar con otras fichas
    int minDistance = Integer.MAX_VALUE;

    // Calcular distancia Manhattan al centro del tablero
    int distanceToCenter = Math.abs(currentPoint.x - centerX) + Math.abs(currentPoint.y - centerY);
    // Asignar mayor puntuación a los puntos más cercanos al centro
    int heuristicValue = -distanceToCenter;
    
    if (player == PlayerType.PLAYER1) { // Conectar arriba-abajo
        for (int row = 0; row < size; row++) {
            PointDist dest = new PointDist(new Point(row, size - 1), 0);
            int distance = dijkstra(costs, size, new PointDist(currentPoint, 0), dest, player);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
    } else { // Conectar izquierda-derecha
        for (int col = 0; col < size; col++) {
            PointDist dest = new PointDist(new Point(size - 1, col), 0);
            int distance = dijkstra(costs, size, new PointDist(currentPoint, 0), dest, player);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
    }
    // Evaluar conexión con otras fichas del jugador
    List<Point> neighbors = getNeighbors(currentPoint.x, currentPoint.y, size);
    for (Point neighbor : neighbors) {
        if (s.getPos(neighbor.x, neighbor.y) == (player == PlayerType.PLAYER1 ? 1 : 2)) {
            pathScore -= 5; // Incentivar conexión con fichas propias
        }
    }
    
    heuristicValue -= minDistance + pathScore;
            
    for (Point d : diagonales) {
        Point diagonal = new Point(currentPoint.x + d.x, currentPoint.y + d.y);

        // Validar directamente en el bucle
        if (diagonal.x >= 0 && diagonal.x < s.getSize() &&
            diagonal.y >= 0 && diagonal.y < s.getSize())
        {
            if (s.getPos(diagonal) == s.getCurrentPlayerColor()) {
                heuristicValue -= 20;
            } else {
                heuristicValue -= 30;
            }
        }
    }

    return new PointDist(currentPoint, heuristicValue);
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
    
    private static int dijkstra(int[][] costs, int size, PointDist source, PointDist dest, PlayerType player)
    {
        int[][] distances = new int[size][size];
        boolean[][] visited = new boolean[size][size];
        PriorityQueue<PointDist> queue = new PriorityQueue<>(
            (a, b) -> Integer.compare(distances[a._point.x][a._point.y], distances[b._point.x][b._point.y])
        );
        
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                distances[i][j] = Integer.MAX_VALUE;
            }
        }
        
        distances[source._point.x][source._point.y] = 0;
        queue.add(new PointDist(source._point, 0));
        
        while(!queue.isEmpty())
        {
            PointDist current = queue.poll();
            
            if(current._point.equals(dest._point))
                return distances[current._point.x][current._point.y];
            
            if(visited[current._point.x][current._point.y]) continue;
            visited[current._point.x][current._point.y] = true;
            
            for(Point nb : getNeighbors(current._point.x, current._point.y, size))
            {
                if(!visited[nb.x][nb.y] && costs[nb.x][nb.y] < 100000)
                {
                    int auxCost = distances[current._point.x][current._point.y] + costs[nb.x][nb.y];
                    if(auxCost < distances[nb.x][nb.y])
                    {
                        distances[nb.x][nb.y] = auxCost;
                        queue.add(new PointDist(nb, auxCost));
                    }
                }
            }
        }
        return Integer.MAX_VALUE;
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
    
    /*
    
    private static int shortestPath(int[][] costs, PlayerType player, int size)
    {
        
    }
    
    */
}
   