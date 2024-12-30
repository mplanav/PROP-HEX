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
 * Clase que sirve para almacenar un "shortest path" de dijkstra
 * @author marc
 */
public class dijkstraResult {
    int _cost; //distancia minima restante para llegar a conectar de extremo a extremo
    List<PointDist> _shortestPath; //lista con todos los puntos que conforman el camino más corto
    
    /**
     * Constructora de dijkstraResult
     * @param cost 
     * @param path 
     */
    public dijkstraResult(int cost, List<PointDist> path)
    {
        this._cost = cost;
        this._shortestPath = path;
    }
}
