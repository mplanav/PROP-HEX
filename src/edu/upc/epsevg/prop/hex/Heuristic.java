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
    HexGameStatus status;
    //color
    int color;
    
    public Heuristic(HexGameStatus status, int color)
    {
        this.status = status;
        this.color = color;
    }
    
   public static int[][] generateCosts(HexGameStatus status, PlayerType player)
    {
        int RC = status.getSize(); //Rows & columns
        int playerV = (player == PlayerType.PLAYER1) ? 1 : -1;
        int opponent = -playerV;
        int[][] costs = new int[RC][RC];
        
        for(int i = 0; i < RC; i++)
        {
            for(int j = 0; j < RC; j++)
            {
                int cellColor = status.getPos(i,j);
                if(cellColor == playerV) costs[i][j] = 0;
                else if(cellColor == opponent) costs[i][j] = 5; //camí bloquejat
                else costs[i][j] = 1; //cel·la buida
            }
        }
        return costs;
     }
    
    public static int dijkstra(int[][] costs, int RC, Point start, PlayerType player)
    {
       int[][] distances = new int[RC][RC];
        PriorityQueue<Point> queue = new PriorityQueue<>((a, b) -> Integer.compare(distances[a.x][a.y], distances[b.x][b.y]));
        boolean[][] visited = new boolean[RC][RC];

        // Inicializar distancias
        for (int i = 0; i < RC; i++) {
            for (int j = 0; j < RC; j++) {
                distances[i][j] = Integer.MAX_VALUE;
            }
        }
        distances[start.x][start.y] = 0;
        queue.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (visited[current.x][current.y]) continue;
            visited[current.x][current.y] = true;

            for (Point neighbor : getNeighbors(current.x, current.y, RC)) {
                if (!visited[neighbor.x][neighbor.y]) {
                    int cost = distances[current.x][current.y] + costs[neighbor.x][neighbor.y];
                    if (cost < distances[neighbor.x][neighbor.y]) {
                        distances[neighbor.x][neighbor.y] = cost;
                        queue.add(neighbor);
                    }
                }
            }
        }

        return shortestPath(distances, player, RC);
    }
    
    private static int shortestPath(int[][] dists, PlayerType player, int RC)
    {
        int minDist = Integer.MAX_VALUE;
        int myPlayer = (player == PlayerType.PLAYER1) ? 1 : -1;
        if(myPlayer == 1)
        {
            for(int j = 0; j < RC; j++)
            {
                minDist = Math.min(minDist, dists[RC -1][j]);
            }
        }
        else {
            for(int i = 0; i < RC; i++)
            {
                minDist = Math.min(minDist, dists[i][RC-1]);
            }
        }
        return minDist;
    }
    
    private static ArrayList<Point> getNeighbors(int x, int y, int RC)
    {
        int[] dirx = {-1, -1, 0, 0, 1, 1};
        int[] diry = {0, 1, -1, 1, -1, 0};
    ArrayList<Point> nb = new ArrayList<>();

    for (int i = 0; i < dirx.length; i++) {
        int nx = x + dirx[i];
        int ny = y + diry[i];
        if (nx >= 0 && nx < RC && ny >= 0 && ny < RC) {
            nb.add(new Point(nx, ny));
        }
    }

    return nb;
    }
    
    public int h(HexGameStatus s, PlayerType player)
    {
        int RC = s.getSize();
        int myPlayer = (player == PlayerType.PLAYER1) ? 1 : -1;
        int[][] costs = generateCosts(s, player);
        int bestDist = Integer.MAX_VALUE;

        //evaluacion del camino myPlayer
        for (int i = 0; i < RC; i++)
        {
            if (myPlayer == 1) 
            { // Player 1: izquierda a derecha
                Point start = new Point(0, i);
                bestDist = Math.min(bestDist, dijkstra(costs, RC, start, player));
            } 
            else 
            { // Player 2: arriba a abajo
                Point start = new Point(i, 0);
                bestDist = Math.min(bestDist, dijkstra(costs, RC, start, player));
            }
        }
        
        //evaluación del camino opPlayer
        PlayerType op = (player == PlayerType.PLAYER1) ? PlayerType.PLAYER2 : PlayerType.PLAYER1;
        int[][] opCosts = generateCosts(s, op);
        int opBestDist = Integer.MAX_VALUE;
        
        for(int i = 0; i < RC; i++)
        {
            if(myPlayer == -1) 
            {
                Point start = new Point(0,i);
                opBestDist = Math.min(opBestDist, dijkstra(opCosts, RC, start, op));
            }
            else 
            {
                Point start = new Point(i, 0);
                opBestDist = Math.min(opBestDist, dijkstra(opCosts, RC, start, op));
            }
        }
    int opShortest = (opBestDist < RC /2) ? (RC - opBestDist) *5 : 0;
    int score = RC*10 - bestDist - opShortest;
    score += evaluateConnections(s, player);
    return score;
    }
    
    private int evaluateConnections(HexGameStatus s, PlayerType player)
    {
        int score = 0;
        int RC = s.getSize();
        int playerV = (player == PlayerType.PLAYER1) ? 1 : -1;
        
        for(int i = 0; i < RC; i++)
        {
            for(int j = 0; j < RC; j++)
            {
                if(s.getPos(i,j) == playerV)
                {
                    for(Point neighbor : getNeighbors(i, j, RC)) 
                    {
                        if(s.getPos(neighbor.x, neighbor.y) == playerV) score += 5;
                    }
                }
            }
        }
        return score;
    }
}

/*

package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class Heuristic {
    HexGameStatus status;
    int color;

    public Heuristic(HexGameStatus status, int color) {
        this.status = status;
        this.color = color;
    }

    public static int[][] generateCosts(HexGameStatus status, PlayerType player) {
        int RC = status.getSize();
        int playerV = (player == PlayerType.PLAYER1) ? 1 : -1;
        int opponent = -playerV;
        int[][] costs = new int[RC][RC];

        for (int i = 0; i < RC; i++) {
            for (int j = 0; j < RC; j++) {
                int cellColor = status.getPos(i, j);
                if (cellColor == playerV) {
                    costs[i][j] = 0; // Propias sin costo
                } else if (cellColor == opponent) {
                    costs[i][j] = RC * 5; // Penalizar posiciones rivales
                } else {
                    costs[i][j] = 1; // Celdas vacías
                }
            }
        }
        return costs;
    }

    public static int dijkstra(int[][] costs, int RC, Point start, PlayerType player) {
        int[][] distances = new int[RC][RC];
        PriorityQueue<Point> queue = new PriorityQueue<>((a, b) -> Integer.compare(distances[a.x][a.y], distances[b.x][b.y]));
        boolean[][] visited = new boolean[RC][RC];

        for (int i = 0; i < RC; i++) {
            for (int j = 0; j < RC; j++) {
                distances[i][j] = Integer.MAX_VALUE;
            }
        }
        distances[start.x][start.y] = 0;
        queue.add(start);

        while (!queue.isEmpty()) {
            Point current = queue.poll();
            if (visited[current.x][current.y]) continue;
            visited[current.x][current.y] = true;

            for (Point neighbor : getNeighbors(current.x, current.y, RC)) {
                if (!visited[neighbor.x][neighbor.y]) {
                    int cost = distances[current.x][current.y] + costs[neighbor.x][neighbor.y];
                    if (cost < distances[neighbor.x][neighbor.y]) {
                        distances[neighbor.x][neighbor.y] = cost;
                        queue.add(neighbor);
                    }
                }
            }
        }

        return shortestPath(distances, player, RC);
    }

    private static int shortestPath(int[][] dists, PlayerType player, int RC) {
        int minDist = Integer.MAX_VALUE;
        int myPlayer = (player == PlayerType.PLAYER1) ? 1 : -1;

        if (myPlayer == 1) {
            for (int j = 0; j < RC; j++) {
                minDist = Math.min(minDist, dists[RC - 1][j]);
            }
        } else {
            for (int i = 0; i < RC; i++) {
                minDist = Math.min(minDist, dists[i][RC - 1]);
            }
        }
        return minDist;
    }

    private static ArrayList<Point> getNeighbors(int x, int y, int RC) {
        int[] dirx = {-1, -1, 0, 0, 1, 1};
        int[] diry = {0, 1, -1, 1, -1, 0};
        ArrayList<Point> nb = new ArrayList<>();

        for (int i = 0; i < dirx.length; i++) {
            int nx = x + dirx[i];
            int ny = y + diry[i];
            if (nx >= 0 && nx < RC && ny >= 0 && ny < RC) {
                nb.add(new Point(nx, ny));
            }
        }
        return nb;
    }

    public int h(HexGameStatus s, PlayerType player) {
        int RC = s.getSize();
        int myPlayer = (player == PlayerType.PLAYER1) ? 1 : -1;
        int[][] costs = generateCosts(s, player);
        int bestDist = Integer.MAX_VALUE;

        for (int i = 0; i < RC; ++i) {
            if (myPlayer == 1) {
                Point start = new Point(0, i);
                bestDist = Math.min(bestDist, dijkstra(costs, RC, start, player));
            } else {
                Point start = new Point(i, 0);
                bestDist = Math.min(bestDist, dijkstra(costs, RC, start, player));
            }
        }

        PlayerType opponent = (player == PlayerType.PLAYER1) ? PlayerType.PLAYER2 : PlayerType.PLAYER1;
        int[][] opponentCosts = generateCosts(s, opponent);
        int opponentBestDist = Integer.MAX_VALUE;

        for (int i = 0; i < RC; ++i) {
            if (myPlayer == -1) {
                Point start = new Point(0, i);
                opponentBestDist = Math.min(opponentBestDist, dijkstra(opponentCosts, RC, start, opponent));
            } else {
                Point start = new Point(i, 0);
                opponentBestDist = Math.min(opponentBestDist, dijkstra(opponentCosts, RC, start, opponent));
            }
        }

        int threatPenalty = (opponentBestDist < RC / 2) ? (RC - opponentBestDist) * 10 : 0;
        int strategicBonus = evaluateStrategicPatterns(s, player);

        int score = RC * 10 - bestDist - threatPenalty + strategicBonus;

        return score;
    }

    private int evaluateStrategicPatterns(HexGameStatus s, PlayerType player) {
        int score = 0;
        int RC = s.getSize();
        int playerV = (player == PlayerType.PLAYER1) ? 1 : -1;
        HashSet<Point> weakConnections = new HashSet<>();

        for (int i = 0; i < RC; i++) {
            for (int j = 0; j < RC; j++) {
                if (s.getPos(i, j) == playerV) {
                    for (Point neighbor : getNeighbors(i, j, RC)) {
                        if (s.getPos(neighbor.x, neighbor.y) == 0) {
                            weakConnections.add(neighbor);
                            score += 5; // Conexiones débiles disponibles
                        }
                    }
                } else if (s.getPos(i, j) == -playerV) {
                    score -= 5; // Penalizar densidad rival
                }
            }
        }

        score += weakConnections.size() * 10; // Incentivar consolidar conexiones
        return score;
    }
}


*/