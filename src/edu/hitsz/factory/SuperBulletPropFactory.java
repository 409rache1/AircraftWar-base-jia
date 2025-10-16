package edu.hitsz.factory;

import edu.hitsz.prop.AbstractProp;
import edu.hitsz.prop.PropSuperBullet;

public class SuperBulletPropFactory implements PropFactory {
    @Override
    public AbstractProp createProp(int locationX, int locationY) {
        return new PropSuperBullet(locationX, locationY, 0, 5);
    }
}