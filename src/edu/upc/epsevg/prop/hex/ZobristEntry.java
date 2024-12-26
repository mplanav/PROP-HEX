/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;


import edu.upc.epsevg.prop.hex.Heuristic;
import edu.upc.epsevg.prop.hex.HexGameStatus;
import edu.upc.epsevg.prop.hex.IAuto;
import edu.upc.epsevg.prop.hex.IPlayer;
import edu.upc.epsevg.prop.hex.PlayerMove;
import edu.upc.epsevg.prop.hex.PlayerType;
import edu.upc.epsevg.prop.hex.PointDist;
import edu.upc.epsevg.prop.hex.SearchType;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
/**
 *
 * @author marc
 */
public class ZobristEntry {
    public static final int EScore = 0;
    public static final int lower_lim = 1;
    public static final int upper_lim = 2;
    
    public int _value;
    public int _depth;
    public int _limStatus;
    public PointDist _bestMove;
    
    public ZobristEntry(int value, int depth, int limit, PointDist move)
    {
        this._value = value;
        this._depth = depth;
        this._limStatus = limit;
        this._bestMove = move;
    }
    
    public int getValue() {return _value;}
    public void setValue(int v) {this._value = v;}
    
    public int getDepth() {return _depth;}
    public void setDepth(int d) {this._depth = d;}
    
    public int getLimit() {return _limStatus;}
    public void setLimit(int lim) {this._limStatus = lim;}
    
    public PointDist getBestMove() {return _bestMove;}
    public void setBestMove(PointDist bm) {this._bestMove = bm;}
}
