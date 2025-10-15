package edu.hitsz.factory;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.EliteEnemy;

public class EliteEnemyFactory implements AircraftFactory {
    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        return new EliteEnemy(locationX, locationY, speedX, speedY, hp);
    }
}
