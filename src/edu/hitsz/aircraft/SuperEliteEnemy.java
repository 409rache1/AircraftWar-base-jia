// SuperEliteEnemy.java
package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.*;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.ScatteredShootStrategy;
import java.util.LinkedList;
import java.util.List;

public class SuperEliteEnemy extends AbstractAircraft {
    private int power = 30;
    private int shootNum = 3;
    private int direction = 1;

    public SuperEliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 修正后的参数顺序
        this.shootStrategy = new ScatteredShootStrategy(shootNum, 6, power, false);
    }

    @Override
    public void forward() {
        super.forward();
        if (locationX <= 0 || locationX >= 512) {
            speedX = -speedX;
        }
    }

    @Override
    protected List<BaseBullet> directShoot() {
        // 保留原有的射击逻辑作为后备
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;
        int speedY = this.getSpeedY() + direction * 5;

        for (int i = 0; i < shootNum; i++) {
            int speedX = (i - 1) * 2;
            BaseBullet bullet = new EnemyBullet(x + (i * 10 - 10), y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> props = new LinkedList<>();
        double r = Math.random();

        // SuperEliteEnemy的原有掉落逻辑：30%炸弹，30%加血，30%火力，10%无道具
        // 提高超级火力道具的掉落概率
        if(r < 0.2){
            propFactory = new BombPropFactory();
        } else if (r < 0.4) {
            propFactory = new BloodPropFactory();
        } else if (r < 0.6) {
            propFactory = new BulletPropFactory(); // 普通火力道具
        } else if (r < 0.95) {
            propFactory = new SuperBulletPropFactory(); // 超级火力道具（较高概率）
        } else {
            propFactory = null;
        }

        if (propFactory != null) {
            AbstractProp prop = propFactory.createProp(this.locationX, this.locationY);
            props.add(prop);
        }

        return props;
    }
}