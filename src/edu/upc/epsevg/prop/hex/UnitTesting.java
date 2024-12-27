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
              { 0, 0, 0, 0, 0, 0, 0, 0, 0},                // 1
                { 0, 0, 0, 0, 0, 0, 0, 0, 0},              // 2
                  { 0, 0, 0, 0, 0, 0, 0, 0, 0},            // 3
                    { 0, 0, 0, 0, 0, 0, 0, 0, 0},          // 4  
                      { 0, 0, 0, 0, 0, 0, 0, 0, 0},       // 5    
                        { 0, 0, 0, 0, 0, 0, 0, 0, 0},      // 6      
                          { 0, 0, 0, 0, 0, 0, 0, 0, 0},    // 7       
                            { 0, 0, 0, 0, 0, 0, 0, 0, 0}   // 8    Y         
        };


        HexGameStatus gs = new HexGameStatus(board, PlayerType.PLAYER1);  
        PointDist best_move = new PointDist(new Point(0, 0), Integer.MAX_VALUE);
        for(int i = 0; i < board.length; ++i){
            for(int j = 0; j < board.length; ++j){
                //PointDist h_pruebas = Heuristic.h_pruebas(gs, new Point(i,j));
                PointDist h_pruebas = Heuristic.h2(gs, new Point(i,j));
                System.out.println("Move:"+ h_pruebas._point + "//" + h_pruebas._cost+"\n");
                if(best_move._cost > h_pruebas._cost){
                    best_move._cost = h_pruebas._cost;
                    best_move._point = h_pruebas._point;
                }
            }
        }
        System.out.println("------------------------------------------------\n");
        System.out.println("Move:"+ best_move._point + "//" + best_move._cost);
 
    }
    
}
