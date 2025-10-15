package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.SuperEliteEnemy;

/**
 * 超级精英敌机工厂 - 简化版
 * 只负责创建SuperEliteEnemy实例，具体属性由Game类设置
 */
public class SuperEliteEnemyFactory implements AircraftFactory {
    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        // 直接使用传入的参数创建敌机
        return new SuperEliteEnemy(locationX, locationY, speedX, speedY, hp);
    }
}