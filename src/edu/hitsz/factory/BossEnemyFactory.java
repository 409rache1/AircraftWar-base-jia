package edu.hitsz.factory;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.application.Main;

/**
 * Boss敌机工厂
 */
public class BossEnemyFactory implements AircraftFactory {

    @Override
    public AbstractAircraft createAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        // 确保Boss敌机在屏幕上方
        int bossY = Math.min(locationY, 150); // 确保不会太低

        // 创建Boss敌机
        BossEnemy boss = new BossEnemy(locationX, bossY, speedX, speedY, hp);

        // 设置移动边界，考虑敌机宽度
        int margin = 50; // 边界留白
        int bossWidth = boss.getWidth(); // 使用重写的getWidth方法
        boss.setMoveBounds(margin + bossWidth / 2, Main.WINDOW_WIDTH - margin - bossWidth / 2);

        return boss;
    }
}