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
        //return 0;
        if(player == PlayerType.PLAYER1)
        {
            //idea boolea
            PointDist R = new PointDist(new Point(12, 6), 0);
            PointDist L = new PointDist(new Point(-1, 6), 0);
            return dijkstra(generateCosts(s, player), s.getSize(), R, L, player);
        }
        else
        {
            PointDist T = new PointDist(new Point(6, 12), 0);
            PointDist B = new PointDist(new Point(6, -1), 0);
            return dijkstra(generateCosts(s, player), s.getSize(), T, B, player);
        }
        
    }
    
    private static int[][] generateCosts(HexGameStatus s, PlayerType player)
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
    
    public static int dijkstra(int[][] costs, int size, PointDist source, PointDist dest, PlayerType player)
    {
        int[][] distances = new int[size][size];
        boolean[][] visited = new boolean[size][size];
        PriorityQueue<PointDist> queue = new PriorityQueue<>((a, b) -> Integer.compare(distances[a._point.x][a._point.y], distances[b._point.x][b._point.y]));
        
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                distances[i][j] = Integer.MAX_VALUE;
            }
        }
        //afegir veins del imaginari a la cua
        distances[source._point.x][source._point.y] = 0;
        //queue.add(new PointDist(, 0));
        
        while(!queue.isEmpty())
        {
            PointDist current = queue.poll();
            //comprovar si node final
            //if(current == destination) break;
            if(visited[current._point.x][current._point.y]) continue;
            visited[current._point.x][current._point.y] = true;
            
            for(Point neighbor : getNeighbors(current._point.x, current._point.y, size))
            {
                if(visited[neighbor.x][neighbor.y] == false)
                {
                    int newCost = costs[neighbor.x][neighbor.y];
                    if(newCost >= 100000) continue; //blocked cell
                    int cost = distances[current._point.x][current._point.y] + costs[neighbor.x][neighbor.y];
                    if(cost < distances[neighbor.x][neighbor.y])
                    {
                        distances[neighbor.x][neighbor.y] = cost;
                        PointDist nb = new PointDist(neighbor, cost);
                        //if de última columna, per afegir el veí imaginari
                        queue.add(nb);
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