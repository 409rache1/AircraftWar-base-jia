package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;

public class PropBlood extends AbstractProp{
    public PropBlood(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void effect(AbstractAircraft aircraft) {

        aircraft.increaseHp(30);

    }
}