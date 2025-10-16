package edu.hitsz.strategy;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.bullet.EnemyBullet;
import java.util.ArrayList;
import java.util.List;

public class StraightShootStrategy implements ShootStrategy {
    private int bulletCount;
    private int bulletSpeed;
    private int bulletPower; // 子弹威力
    private boolean isHero; // 区分英雄机和敌机子弹

    public StraightShootStrategy(int bulletCount, int bulletSpeed, int bulletPower, boolean isHero) {
        this.bulletCount = bulletCount;
        this.bulletSpeed = bulletSpeed;
        this.bulletPower = bulletPower;
        this.isHero = isHero;
    }

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new ArrayList<>();
        int x = aircraft.getLocationX();
        int y = aircraft.getLocationY();
        int direction = isHero ? -1 : 1; // 英雄机向上，敌机向下

        for (int i = 0; i < bulletCount; i++) {
            BaseBullet bullet;
            if (isHero) {
                bullet = new HeroBullet(x, y, 0, direction * bulletSpeed, bulletPower);
            } else {
                bullet = new EnemyBullet(x, y, 0, direction * bulletSpeed, bulletPower);
            }
            bullets.add(bullet);
        }
        return bullets;
    }
}
