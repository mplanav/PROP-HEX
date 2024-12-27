/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.Objects;

/** 
 * Clase que almacena un punto del tablero junto con un valor de coste asociado a él
 * Se usa para representar los movimientos posibles en el tablero con su respectiva 
 * evaluación heurística (coste)
 * @author marc i victor
 */
public class PointDist {
    
    //Atributos de la clase
    public int _cost; //valor heuristico || cost asociado al punto
    public Point _point; //Coordenadas del punto en el tablero
    
    /**
     * Constructor de la clase PointDist.
     *
     * @param p Punto en el tablero representado como un objeto de la clase Point.
     * @param cost Coste heurístico asociado al punto.
     */
    public PointDist(Point p, int cost)
    {
        this._cost = cost;
        this._point = new Point(p);
    }
    
    /**
     * Obtiene el coste heurístico asociado al punto.
     *
     * @return El valor del coste heurístico.
     */
    public int getCost() {return _cost;}
    
    /**
     * Obtiene el punto asociado.
     *
     * @return Una copia del objeto Point que representa las coordenadas del punto.
     */
    public Point getPoint() {return new Point(_point);}
}