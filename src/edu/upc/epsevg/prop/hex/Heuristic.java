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
        int opponent = (player == PlayerType.PLAYER2) ? -1: 1;
        int[][] costs = new int[RC][RC];
        
        for(int i = 0; i < RC; i++)
        {
            for(int j = 0; j < RC; j++)
            {
                int cellColor = status.getPos(i,j);
                if(cellColor == playerV) costs[i][j] = 0;
                else if(cellColor == opponent) costs[i][j] = Integer.MAX_VALUE; //camí bloquejat
                else costs[i][j] = 1; //cel·la buida
            }
        }
        return costs;
     }
    
    public static List<int[]> dijkstra(int startX, int startY, PlayerType player, HexGameStatus status)
    {
       int RC = status.getSize();
       int[][] costs = generateCosts(status, player);
       boolean[][] visited = new boolean[RC][RC]; //matriz de los nodos visitados
       float[][] dists = new float[RC][RC]; //matriz de las distancias minimas
       int[][][] prev = new int[RC][RC][2]; //matriz que guarda los caminos previos
       
       for(float[] row : dists) //inicializamos todas las distancias a inf
       {
           Arrays.fill(row, Float.MAX_VALUE);
       }
       dists[startX][startY] = 0; //distancia del nodo inicial
       
       //cola de prioridad (procesar nodos con orden de menor a mayor distancia)
       PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingDouble(pos -> dists[pos[0]][pos[1]]));
       queue.add(new int[]{startX, startY});
       
       while(!queue.isEmpty())
       {
           int[] current = queue.poll(); //extrae el nodo con menor distancia
           int x = current[0];
           int y = current[1];
           if(visited[x][y]) continue; //si ya ha sido visitado sigue
           visited[x][y] = true; //visitado
           
           for(int[] neighbor : getNeighbors(x, y, RC)) //obtenemos los nodos vecinos del actual
           {
               int neighborX = neighbor[0];
               int neighborY = neighbor[1];
               if(!visited[neighborX][neighborY]) //miramos si estos vecinos han sido visitados
               {
                   float newDist = dists[x][y] + costs[neighborX][neighborY]; //nueva distancia
                   if(newDist < dists[neighborX][neighborY]) 
                   {
                       dists[neighborX][neighborY] = newDist;
                       prev[neighborX][neighborY] = new int[]{x, y};
                       queue.add(new int[]{neighborX, neighborY});
                   }
               }
           }
       }
       
       return shortestPath(dists, prev, player, RC); //devuelve el camino más corto
    }
    
    private static List<int[]> shortestPath(float[][] dists, int[][][] prev, PlayerType player, int RC)
    {
        float minDist = Float.MAX_VALUE; //distancia minimo inf
        int[] obj = null; //nodo objetivo
        if(player == PlayerType.PLAYER1) //si es player1 hay que buscar en la última fila
        {
            for(int j = 0; j < RC; j++)
            {
                if(dists[RC-1][j] < minDist)
                {
                    minDist = dists[RC-1][j];
                    obj = new int[]{j, RC-1}; //guardamos nodo objetivo
                }
            }
        }
        else 
        {
            for(int i = 0; i < RC; i++) //si es player2, entonces en la última columna
            {
                if(dists[i][RC-1] < minDist)
                {
                    minDist = dists[i][RC-1];
                    obj = new int[]{i, RC-1}; //rt 
                }
            }
        }
        if(obj == null) return List.of(new int[]{-1, -1}); //si no se ha encontrado return
        List<int[]> path = new ArrayList<>(); //lista para guardar the shortest path
        while(obj != null && obj[0] != -1)
        {
            path.add(obj); //añadimos nodo
            obj = prev[obj[0]][obj[1]]; //nos movemos al nodo anterior
        }
        Collections.reverse(path); //invertimos el camino (inicio --> objetivo)
        return path;
    }
    
    private static List<int[]> getNeighbors(int x, int y, int RC)
    {
        int[][] dir = {{-1,0}, {1,0}, {0,-1}, {0,1}, {-1,1}, {1,-1}}; //direcciones posibles
        List<int[]> nb = new ArrayList<>(); //lista neighbors
        
        for(int[] directions : dir) //iteramos sobre todas las direcciones
        {
            int neighborX = x + directions[0]; //coordenada x del vecino
            int neighborY = y + directions[1]; //coordenada y del vecino 
            if(neighborX >= 0 && neighborY >= 0 && neighborX < RC && neighborY < RC) //Vecino dentro de los límiteS? (nose, chatgpt me lo ha dado así esto)
                nb.add(new int[]{neighborX, neighborY});
        }
        return nb;
    }
    
    public int h(HexGameStatus s, int color)
    {
        int score = 0;
        int RC = s.getSize();
        PlayerType player = (color == 1) ? PlayerType.PLAYER1 : PlayerType.PLAYER2;
        
        for(int i = 0; i < RC; i++) //Recorremos filas o columnas según el jugador
        {
            List<int[]> path;
            if(player == PlayerType.PLAYER1) path = dijkstra(0, i, player, s); //player1 empieza en la primera fila
            else path = dijkstra(i, 0, player, s);//player2 empieza en la primera columna
            
            for(int[] coords : path) //recorremos los nodos del camino más corto
            {
                int x = coords[0];
                int y = coords[1];
                float cost = generateCosts(s, player)[x][y]; //obtenemos coste del nodo actual
                score = Math.max(score, (int) cost); //actualizamos
            }
        }
        return score;
    }
}
