// ScatteredShootStrategy.java
package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.bullet.EnemyBullet;
import java.util.LinkedList;
import java.util.List;

public class ScatteredShootStrategy implements ShootStrategy {
    private int bulletCount;
    private int bulletSpeed;
    private int bulletPower;
    private boolean isHero;

    public ScatteredShootStrategy(int bulletCount, int bulletSpeed, int bulletPower, boolean isHero) {
        this.bulletCount = bulletCount;
        this.bulletSpeed = bulletSpeed;
        this.bulletPower = bulletPower;
        this.isHero = isHero;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();
        int direction = isHero ? -1 : 1; // 英雄机向上，敌机向下

        for (int i = 0; i < bulletCount; i++) {
            // 散射：中间子弹直射，两边子弹有横向偏移
            int speedX = (i - bulletCount/2) * 2; // 产生 -2, 0, 2 的速度
            int speedY = direction * bulletSpeed;

            BaseBullet bullet;
            if (isHero) {
                // 英雄机子弹：横向偏移位置，向上发射
                bullet = new HeroBullet(x + (i - bulletCount/2) * 15, y, speedX, speedY, bulletPower);
            } else {
                // 敌机子弹：向下发射
                bullet = new EnemyBullet(x + (i - bulletCount/2) * 15, y, speedX, speedY, bulletPower);
            }
            bullets.add(bullet);
        }
        return bullets;
    }
}