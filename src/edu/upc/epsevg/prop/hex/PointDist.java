/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;

/**
 *
 * @author marc
 */
public class PointDist {
    String _name;
    int _cost;
    Point _point;
    
    public PointDist(Point p, int cost)
    {
        this._cost = cost;
        this._point = p;
    }
}
