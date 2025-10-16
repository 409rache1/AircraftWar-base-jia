// BossEnemy.java
package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.*;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.CircularShootStrategy;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BossEnemy extends AbstractAircraft {
    private int power = 30;
    private int shootNum = 20;
    private int direction = 1;

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new CircularShootStrategy(shootNum, 5, power, false); // 添加false参数
    }

    @Override
    public void forward() {
        super.forward();
        if (locationX <= 0 || locationX >= 512) {
            speedX = -speedX;
        }
        if (locationY > 150) {
            locationY = 150;
        }
    }

    @Override
    protected List<BaseBullet> directShoot() {
        // 保留原有的射击逻辑作为后备
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;

        for (int i = 0; i < shootNum; i++) {
            double angle = 2 * Math.PI * i / shootNum;
            int speedX = (int) (5 * Math.cos(angle));
            int speedY = (int) (5 * Math.sin(angle)) + 2;

            BaseBullet bullet = new EnemyBullet(x, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> props = new LinkedList<>();
        Random random = new Random();

        // Boss敌机掉落0-3个道具
        int dropCount = random.nextInt(4);
        for (int i = 0; i < dropCount; i++) {
            int propType = random.nextInt(4); // 0-3，现在有4种道具
            PropFactory factory;

            switch (propType) {
                case 0:
                    factory = new BloodPropFactory();
                    break;
                case 1:
                    factory = new BombPropFactory();
                    break;
                case 2:
                    factory = new BulletPropFactory(); // 普通火力道具
                    break;
                case 3:
                    factory = new SuperBulletPropFactory(); // 超级火力道具
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