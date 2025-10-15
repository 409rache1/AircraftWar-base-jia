package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;

public class PropBullet extends AbstractProp{
    public PropBullet(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void effect(AbstractAircraft aircraft) {
        System.out.println("FireSupply active!");
    }
}