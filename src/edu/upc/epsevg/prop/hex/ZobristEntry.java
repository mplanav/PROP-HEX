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
 * Clase que representa una entrada en la tabla zobrist. Almacena información
 * relevante sobre el estado de la partida y sus correspondientes evaluaciones.
 * 
 * 
 * @author marc i victor
 */
public class ZobristEntry {
    
    //Constantes para identificar los límites de la poda alfa-beta
    public static final int EScore = 0;
    public static final int lower_lim = 1;
    public static final int upper_lim = 2;
    
    //Atributos de a entrada zobrist
    public int _value; //valor heurístico asociado al estado
    public int _depth; //profundidad de la búsqueda en la que se evaluó el estado
    public int _limStatus; //Estado de los límites de la poda alfa-beta
    public PointDist _bestMove; //mejor movimiento para el estado actual
    
     /**
     * Constructor de la clase ZobristEntry.
     *
     * @param value Valor heurístico asociado al estado.
     * @param depth Profundidad de búsqueda alcanzada.
     * @param limit Estado de los límites de poda alfa-beta.
     * @param move Mejor movimiento calculado para este estado.
     */
    public ZobristEntry(int value, int depth, int limit, PointDist move)
    {
        this._value = value;
        this._depth = depth;
        this._limStatus = limit;
        this._bestMove = move;
    }
    
    /**
     * Obtiene el valor heurístico asociado al estado
     * @return valor heurístico
     */
    public int getValue() {return _value;}
    
    /**
     * Establece el valor heurístico asociado al estado
     * @param v Nuevo valor heurístico
     */
    public void setValue(int v) {this._value = v;}
    
    /**
     * Obtiene la profundidad en la que se ha evaluado el estado
     * @return Profundidad de la búsqueda
     */
    public int getDepth() {return _depth;}
    
    /**
     * Establece la profundidad de búsqueda en la que se ha evaluado el estado 
     * @param d Nueva profundidad
     */
    public void setDepth(int d) {this._depth = d;}
    
    /**
     * Obtiene el estado de los límites de la poda alfa-beta
     * @return Estado de los límites (EScore, lower-lim, upper,lim)
     */
    public int getLimit() {return _limStatus;}
    
    /**
     * Establece el estado de los límites de la poda alfa-beta
     * @param lim Nuevo estado de los límites
     */
    public void setLimit(int lim) {this._limStatus = lim;}
    
    /**
     * Obtiene el mejor movimiento calculada para este estado
     * @return Mejor movimiento como objeto PointDist
     */
    public PointDist getBestMove() {return _bestMove;}
    
    /**
     * Establece el mejor movimiento calculado para este estado
     * @param bm Nuevo mejor movimiento PointDist
     */
    public void setBestMove(PointDist bm) {this._bestMove = bm;}
}
