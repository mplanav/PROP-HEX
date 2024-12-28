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
            { 1, 0, 0, 0, 0, 0, 0, 0, 0},                  // 0   Y
              { 1, 0, 0, 0, 0, 0, 0, 0, 0},                // 1
                { 1, 0, 0, 0, 0, 0, 0, 0, 0},              // 2
                  { 1, 0, 0, 0, 0, 0, 0, 0, 0},            // 3
                    { 1, 0, 0, 0, 0, 0, 0, 0, 0},          // 4  
                      { 1, 0, 0, 0, 0, 0, 0, 0, 0},       // 5    
                        { 1, 0, 0, 0, 0, 0, 0, 0, 0},      // 6      
                          { 1, 0, 0, 0, 0, 0, 0, 0, 0},    // 7       
                            { -1, 0, 0, 0, 0, 0, 0, 0, 0}   // 8    Y         
        };


        HexGameStatus gs = new HexGameStatus(board, PlayerType.PLAYER1);  
        PointDist best_move = new PointDist(new Point(0, 0), Integer.MAX_VALUE);
        dijkstraResult h_pruebas = Heuristic.h_pruebas(gs, new Point(0,0));
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
