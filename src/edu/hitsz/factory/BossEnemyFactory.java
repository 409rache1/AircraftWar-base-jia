package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.BossEnemy;

/**
 * Boss敌机工厂 - 简化版
 * 只负责创建BossEnemy实例，具体属性由Game类设置
 */
public class BossEnemyFactory implements AircraftFactory {
    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        // 直接使用传入的参数创建敌机
        return new BossEnemy(locationX, locationY, speedX, speedY, hp);
    }
}