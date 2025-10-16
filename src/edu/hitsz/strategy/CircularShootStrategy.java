// CircularShootStrategy.java - 修正版本
package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;  // 改为HeroBullet
import edu.hitsz.bullet.EnemyBullet;
import java.util.LinkedList;
import java.util.List;

public class CircularShootStrategy implements ShootStrategy {
    private int bulletCount;
    private int bulletSpeed;
    private int bulletPower;
    private boolean isHero;  // 新增：区分英雄机和敌机

    public CircularShootStrategy(int bulletCount, int bulletSpeed, int bulletPower, boolean isHero) {
        this.bulletCount = bulletCount;
        this.bulletSpeed = bulletSpeed;
        this.bulletPower = bulletPower;
        this.isHero = isHero;  // 设置是否为英雄机
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new LinkedList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();

        for (int i = 0; i < bulletCount; i++) {
            double angle = 2 * Math.PI * i / bulletCount;
            int speedX = (int) (bulletSpeed * Math.sin(angle));
            int speedY = (int) (bulletSpeed * Math.cos(angle));

            // 根据是否为英雄机创建不同的子弹
            BaseBullet bullet;
            if (isHero) {
                bullet = new HeroBullet(x, y, speedX, speedY, bulletPower);
            } else {
                // 如果是敌机使用环射（比如Boss），速度方向调整
                speedY = Math.abs(speedY); // 敌机子弹向下
                bullet = new EnemyBullet(x, y, speedX, speedY, bulletPower);
            }
            bullets.add(bullet);
        }
        return bullets;
    }
}