/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop.hex;

import java.awt.Point;
import java.util.Objects;

/**
 *
 * @author marc
 */
public class PointDist {
    public int _cost;
    public Point _point;
    
    public PointDist(Point p, int cost)
    {
        this._cost = cost;
        this._point = new Point(p);
    }
    
    public int getCost() {return _cost;}
    public Point getPoint() {return new Point(_point);}
    
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;
        PointDist that = (PointDist) obj;
        return _cost == that._cost && _point.equals(that._point);
    }
    
    @Override
    public int hashCode() {return Objects.hash(_cost, _point);}
    
    @Override 
    public String toString()
    {
        return "PointDist{" + 
                "_point=" + _point +
                ", _cost=" + _cost +
                '}';
    }
}