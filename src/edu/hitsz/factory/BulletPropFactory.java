package edu.hitsz.factory;


import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropBullet;



public class BulletPropFactory implements PropFactory{
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new PropBullet(locationX, locationY,0,5);
    }
}