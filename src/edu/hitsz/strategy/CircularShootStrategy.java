// CircularShootStrategy.java
package edu.hitsz.strategy;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
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

        // 计算每颗子弹的发射角度（均匀分布）
        double angleStep = 2 * Math.PI / bulletCount;  // 每颗子弹之间的角度

        for (int i = 0; i < bulletCount; i++) {
            // 计算子弹的发射角度
            double angle = i * angleStep;
            int speedX = (int) (bulletSpeed * Math.cos(angle));  // X方向的速度
            int speedY = (int) (bulletSpeed * Math.sin(angle));  // Y方向的速度

            // 根据是否为英雄机创建不同的子弹
            BaseBullet bullet;
            if (isHero) {
                bullet = new HeroBullet(x, y, speedX, speedY, bulletPower);
            } else {
                // 如果是敌机，确保敌机子弹向下发射
                speedY = Math.abs(speedY);  // 敌机子弹始终向下
                bullet = new EnemyBullet(x, y, speedX, speedY, bulletPower);
            }
            bullets.add(bullet);
        }
        return bullets;
    }
}
