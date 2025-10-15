package edu.hitsz.factory;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.MobEnemy;

public class MobEnemyFactory implements AircraftFactory {
    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new MobEnemy(locationX, locationY, speedX, speedY, hp);
    }

}
