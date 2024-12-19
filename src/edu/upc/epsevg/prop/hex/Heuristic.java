/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import edu.upc.epsevg.prop.hex.HexGameStatus;
import java.awt.Point;
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
    
    public int h(HexGameStatus s, int color)
    {
        int score = 0;
        for(int x = 0; x < s.getSize(); x++) {
            for(int y = 0; y < s.getSize(); y++) {
                Point p = new Point(x, y);
                int posColor = s.getPos(p); //obtenim el color de cada posicio del tauler

                if(posColor == color) {
                    score += 10;
                }
                else if(posColor != 0) {
                    score -= 10;
                }
            }
        }
        return score;
    }
}
