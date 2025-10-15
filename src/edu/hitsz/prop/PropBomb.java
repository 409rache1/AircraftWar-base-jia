package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;

public class PropBomb extends AbstractProp{
    public PropBomb(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void effect(AbstractAircraft aircraft) {
        System.out.println("BombSupply active!");
    }
}