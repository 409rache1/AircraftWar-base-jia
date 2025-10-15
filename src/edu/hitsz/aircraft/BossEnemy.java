package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.factory.BloodPropFactory;
import edu.hitsz.factory.BombPropFactory;
import edu.hitsz.factory.BulletPropFactory;
import edu.hitsz.factory.PropFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Boss敌机 - 实现环射弹道
 */
public class BossEnemy extends AbstractAircraft {
    private int power = 30;
    private int shootNum = 20;
    private int direction = 1;

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.power = power;
    }

    @Override
    public void forward() {
        super.forward();
        // Boss在屏幕上方左右移动
        if (locationX <= 0 || locationX >= 512) {
            speedX = -speedX;
        }

        // 保持在屏幕上方区域
        if (locationY > 150) {
            locationY = 150;
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;

        // 环射弹道：20颗子弹呈环形
        for (int i = 0; i < shootNum; i++) {
            double angle = 2 * Math.PI * i / shootNum;
            int speedX = (int) (5 * Math.cos(angle));
            int speedY = (int) (5 * Math.sin(angle)) + 2;

            BaseBullet bullet = new EnemyBullet(x, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }

    public List<AbstractProp> dropProps() {
        List<AbstractProp> props = new LinkedList<>();
        Random random = new Random();

        // 随机掉落<=3个道具
        int dropCount = random.nextInt(4);

        for (int i = 0; i < dropCount; i++) {
            int propType = random.nextInt(3);
            PropFactory factory;

            switch (propType) {
                case 0:
                    factory = new BloodPropFactory();
                    break;
                case 1:
                    factory = new BombPropFactory();
                    break;
                case 2:
                    factory = new BulletPropFactory();
                    break;
                default:
                    factory = new BloodPropFactory();
            }

            int offsetX = random.nextInt(41) - 20;
            props.add(factory.createProp(locationX + offsetX, locationY));
        }

        return props;
    }
}