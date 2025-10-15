package edu.hitsz.factory;


import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropBlood;



public class BloodPropFactory implements PropFactory{
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new PropBlood(locationX, locationY,0,5);
    }
}