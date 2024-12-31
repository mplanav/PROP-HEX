/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.Heuristic;
import edu.upc.epsevg.prop.hex.players.MyPlayer;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus2;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus3;
import edu.upc.epsevg.prop.hex.players.ProfeGameStatus3.Result;
import java.awt.Point;
/**
 *
 * @author bernat
 */
public class UnitTesting {
    
    
    
    public static void main(String[] args) {
    
        
        byte[][] board = {
        //X  0  1  2  3  4  5  6  7  8
            { 0, 0, 0, 0, 0, 0, 0, 0, 0},                  // 0   Y
              { 0, 0, 0, 0, 0, 0, -1, 0, 0},               // 1
                { 0, 0, 0, 0, 0, 0, 0, 0, 0},              // 2
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0},            // 3
                    { 0, 0, 0, 0, 0, 0, 0, 0, 0},          // 4  
                      { 0, 0, 0, 0, 0, 0, 0, 0, 0},        // 5    
                        { 0, 1, 0, 0, 0, 0, 0, 0, 0},      // 6      
                          { 0, 0, 0, 0, 0, 0, 0, 0, 0},    // 7       
                            { 0, 0, 0, 0, 0, 0, 0, 0, 0}   // 8    Y         
        };


        HexGameStatus gs = new HexGameStatus(board, PlayerType.PLAYER1); 
        //HexGameStatus gs2 = new HexGameStatus(board, PlayerType.PLAYER1);
        PointDist best_move = new PointDist(new Point(0, 0), Integer.MIN_VALUE);
        dijkstraResult h_pruebas = null;
        h_pruebas = Heuristic.h_pruebas(gs, new Point(0,7));
 
        //dijkstraResult h_pruebas2 = Heuristic.h_pruebas(gs2, new Point(0,0));
        
        for(int i = 0; i < board.length; ++i){
            for(int j = 0; j < board.length; ++j){
                //PointDist h_pruebas = Heuristic.h_pruebas(gs, new Point(i,j));
                PointDist h2 = Heuristic.h2(gs, new Point(i,j));
                System.out.println("Move:"+ h2._point + "//" + h2._cost+"\n");
                if(best_move._cost < h2._cost){
                    best_move._cost = h2._cost;
                    best_move._point = h2._point;
                }
            }
        }
        System.out.println("Move:"+ best_move._point + "//" + best_move._cost+"\n");
        
                       
        System.out.println("Tamaño del camino más corto: " + h_pruebas._shortestPath.size());   
        // Imprimir solo el camino más corto
        if (h_pruebas != null) {
            System.out.println("Camino más corto:");
            for (int i = 0; i < h_pruebas._shortestPath.size(); i++) {
                System.out.println("Punto " + (i + 1) + ": " + h_pruebas._shortestPath.get(i)._point);
            }
        }
    }
    
}
