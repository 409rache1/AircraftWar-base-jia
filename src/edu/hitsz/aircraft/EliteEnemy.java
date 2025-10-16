// EliteEnemy.java
package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.*;
import edu.hitsz.factory.*;
import edu.hitsz.strategy.StraightShootStrategy;
import java.util.LinkedList;
import java.util.List;

public class EliteEnemy extends AbstractAircraft {
    private int direction = 1;
    private int shootNum = 1;
    private int power = 30;

    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 修正后的参数顺序
        this.shootStrategy = new StraightShootStrategy(shootNum, 8, power, false);
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    @Override
    protected List<BaseBullet> directShoot() {
        // 保留原有的射击逻辑作为后备
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction * 5;
        BaseBullet bullet;
        for(int i = 0; i < shootNum; i++){
            bullet = new EnemyBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> props = new LinkedList<>();
        double r = Math.random();

        // EliteEnemy的原有掉落逻辑：30%炸弹，30%加血，30%火力，10%无道具
        // 现在将部分火力道具改为超级火力道具
        if(r < 0.25){
            propFactory = new BombPropFactory();
        } else if (r < 0.5) {
            propFactory = new BloodPropFactory();
        } else if (r < 0.7) {
            propFactory = new BulletPropFactory(); // 普通火力道具
        } else if (r < 0.9) {
            propFactory = new SuperBulletPropFactory(); // 超级火力道具
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