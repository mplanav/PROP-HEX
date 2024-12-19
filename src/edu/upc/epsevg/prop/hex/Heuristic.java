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
    
    public Heuristic(HexGameStatus status, PlayerType player)
    {
        this._status = status;
        this._player = player;
    }
    
    public static int h(HexGameStatus s, PlayerType player) {
    int size = s.getSize();
    int[][] costs = generateCosts(s, player);

    if (player == PlayerType.PLAYER1) {
        // Simula source (columna izquierda) y dest (columna derecha)
        int minDistance = Integer.MAX_VALUE;
        for (int row = 0; row < size; row++) {
            PointDist source = new PointDist(new Point(row, 0), 0); // Columna izquierda
            PointDist dest = new PointDist(new Point(row, size - 1), 0); // Columna derecha
            minDistance = Math.min(minDistance, dijkstra(costs, size, source, dest, player));
        }
        return minDistance;
    } else {
        // Simula source (fila superior) y dest (fila inferior)
        int minDistance = Integer.MAX_VALUE;
        for (int col = 0; col < size; col++) {
            PointDist source = new PointDist(new Point(0, col), 0); // Fila superior
            PointDist dest = new PointDist(new Point(size - 1, col), 0); // Fila inferior
            minDistance = Math.min(minDistance, dijkstra(costs, size, source, dest, player));
        }
        return minDistance;
    }
}
    
    private static int[][] generateCosts(HexGameStatus s, PlayerType player) {
    int size = s.getSize();
    int my = (player == PlayerType.PLAYER2) ? -1 : 1;
    int opPlayer = -my;

    int[][] costs = new int[size][size];
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            int cell = s.getPos(i, j);
            if (cell == opPlayer) {
                costs[i][j] = 0; // Oponente: bajo costo
            } else if (cell == my) {
                costs[i][j] = 100000; // Jugador actual: costo muy alto
            } else {
                costs[i][j] = 1; // Celdas vacías: costo estándar
            }
        }
    }
    return costs;
}

    
    public static int dijkstra(int[][] costs, int size, PointDist source, PointDist dest, PlayerType player) {
    int[][] distances = new int[size][size];
    boolean[][] visited = new boolean[size][size];
    PriorityQueue<PointDist> queue = new PriorityQueue<>(
        (a, b) -> Integer.compare(distances[a._point.x][a._point.y], distances[b._point.x][b._point.y])
    );

    // Inicializamos las distancias al valor máximo
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            distances[i][j] = Integer.MAX_VALUE;
        }
    }

    // Distancia al punto inicial es 0
    distances[source._point.x][source._point.y] = 0;
    queue.add(new PointDist(source._point, 0));

    while (!queue.isEmpty()) {
        PointDist current = queue.poll();

        // Si llegamos al destino
        if (current._point.equals(dest._point)) {
            return distances[current._point.x][current._point.y];
        }

        // Si ya fue visitado, continuamos
        if (visited[current._point.x][current._point.y]) continue;
        visited[current._point.x][current._point.y] = true;

        // Procesar vecinos
        for (Point neighbor : getNeighbors(current._point.x, current._point.y, size)) {
            if (!visited[neighbor.x][neighbor.y] && costs[neighbor.x][neighbor.y] < 100000) {
                int newCost = distances[current._point.x][current._point.y] + costs[neighbor.x][neighbor.y];
                if (newCost < distances[neighbor.x][neighbor.y]) {
                    distances[neighbor.x][neighbor.y] = newCost;
                    queue.add(new PointDist(neighbor, newCost));
                }
            }
        }
    }

    // Si no hay camino al destino, retorna infinito
    return Integer.MAX_VALUE;
    }
    
  /*  
   private static int shortestPathCost(int[][] costs, PlayerType player, int size) {
    int minDist = Integer.MAX_VALUE;

    for (int i = 0; i < size; i++) {
        PointDist source, dest;
        if (player == PlayerType.PLAYER1) {
            source = new PointDist(new Point(i, 0), 0); // Borde izquierdo
            dest = new PointDist(new Point(i, size - 1), 0); // Borde derecho
        } else {
            source = new PointDist(new Point(0, i), 0); // Borde superior
            dest = new PointDist(new Point(size - 1, i), 0); // Borde inferior
        }

        int pathCost = dijkstra(costs, size, source, dest, player);
        minDist = Math.min(minDist, pathCost);
    }

    // Debugging: Verifica los costos
    System.out.println("Shortest path cost for player " + player + ": " + minDist);

    return minDist;
}*/

    
    private static List<Point> getNeighbors(int x, int y, int size)
    {
        int[] dirX = {-1, -1, 0, 0, 1, 1};
        int[] dirY = {0, 1, -1, 1, -1, 0};
        List<Point> neighbors = new ArrayList<>();
        
        for(int i = 0; i < dirX.length; i++)
        {
            int neighborX = x + dirX[i];
            int neighborY = y + dirY[i];
            
            if(neighborX >= 0 && neighborX < size && neighborY >= 0 && neighborY < size)
                neighbors.add(new Point(neighborX, neighborY));
        }
        return neighbors;
    }
    /*
    private static boolean isCriticalPoint(int x, int y, HexGameStatus s, PlayerType player)
    {
        int size = s.getSize();
        int my = (player == PlayerType.PLAYER2) ? -1 : 1;
        if(s.getPos(x, y) != 0) return false;
        
        List<Point> neighbors = getNeighbors(x, y, size);
        int con = 0;
        for(Point nb : neighbors)
        {
            if(s.getPos(nb.x, nb.y) == my) con++;
        }
        if(con >= 2) return true;
        
        boolean nearL = (x == 0);
        boolean nearR = (x == size-1);
        boolean nearT = (y == 0);
        boolean nearB = (y == size-1);
        
        if(player == PlayerType.PLAYER1) return nearL || nearR;
        else return nearT || nearB;
    }
    
   private static Point findBestDefensiveMove(HexGameStatus s, PlayerType opponent) {
    int size = s.getSize();
    int[][] costs = generateCosts(s, opponent);
    int initPath = shortestPathCost(costs, opponent, size);

    Point bestMove = null;
    int maxImpact = Integer.MIN_VALUE;

    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            if (s.getPos(i, j) == 0) { // Solo considerar celdas vacías
                HexGameStatus auxS = new HexGameStatus(s);
                auxS.placeStone(new Point(i, j));

                int[][] newCosts = generateCosts(auxS, opponent);
                int afterPath = shortestPathCost(newCosts, opponent, size);

                int impact = afterPath - initPath;

                // Debugging: Verifica costos y movimientos
               // System.out.println("Move: (" + i + ", " + j + ")");
               // System.out.println("Init path cost: " + initPath + ", After path cost: " + afterPath + ", Impact: " + impact);

                if (impact > maxImpact) {
                    maxImpact = impact;
                    bestMove = new Point(i, j);
                }
            }
        }
    }
       //System.out.println("Best Defensive move: " + bestMove.x + " " + bestMove.y);

    return bestMove;
}*/


}
   