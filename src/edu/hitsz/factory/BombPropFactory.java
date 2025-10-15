package edu.hitsz.factory;


import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropBomb;



public class BombPropFactory implements PropFactory{
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new PropBomb(locationX, locationY,0,5);
    }
}