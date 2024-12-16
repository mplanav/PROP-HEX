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
    
    public static int h(HexGameStatus s, PlayerType player)
    {
        return dijkstra(generateCosts(s, s.getCurrentPlayer()), s.getSize(), new Point(0, 0), s.getCurrentPlayer());
    }
    
    public static int[][] generateCosts(HexGameStatus s, PlayerType player)
    {
        int size = s.getSize();
        int my = (player == PlayerType.PLAYER2) ? -1 : 1;
        int op = (player == PlayerType.PLAYER1) ? 1 : -1;
        
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
    
    public static int dijkstra(int[][] costs, int size, Point start, PlayerType player)
    {
        int[][] distances = new int[size][size];
        boolean[][] visited = new boolean[size][size];
        PriorityQueue<Point> queue = new PriorityQueue<>((a, b) -> Integer.compare(distances[a.x][a.y], distances[b.x][b.y]));
        
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                distances[i][j] = Integer.MAX_VALUE;
            }
        }
        distances[start.x][start.y] = 0;
        queue.add(start);
        
        while(!queue.isEmpty())
        {
            Point current = queue.poll();
            if(visited[current.x][current.y]) continue;
            visited[current.x][current.y] = true;
            
            for(Point neighbor : getNeighbors(current.x, current.y, size))
            {
                if(visited[neighbor.x][neighbor.y] == false)
                {
                    int newCost = costs[neighbor.x][neighbor.y];
                    if(newCost >= 100000) continue; //blocked cell
                    int cost = distances[current.x][current.y] + costs[neighbor.x][neighbor.y];
                    if(cost < distances[neighbor.x][neighbor.y])
                    {
                        distances[neighbor.x][neighbor.y] = cost;
                        queue.add(neighbor);
                    }
                }
            }
        }
        return shortestPath(distances, player, size);
    }
    
    private static int shortestPath(int[][] distances, PlayerType player, int size)
    {
        int minDist = Integer.MAX_VALUE;
        if(player == PlayerType.PLAYER2)
        {
            for(int j = 0; j < size; j++)
                minDist = Math.min(minDist, distances[size - 1][j]);
        }
        else 
        {
            for(int i = 0; i < size; i++)
            {
                minDist = Math.min(minDist, distances[i][size - 1]);
            }
        }
        
        if(minDist == Integer.MAX_VALUE) return -1000;
        return minDist;
    }
    
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