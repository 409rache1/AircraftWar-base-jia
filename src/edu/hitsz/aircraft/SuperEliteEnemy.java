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

/**s
 * 超级精英敌机 - 实现散射弹道
 */
public class SuperEliteEnemy extends AbstractAircraft {
    private int power = 30;
    private int shootNum = 3;
    private int direction = 1; // 向下发射

    public SuperEliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.power = power;
    }

    @Override
    public void forward() {
        super.forward();
        // 左右边界检测
        if (locationX <= 0 || locationX >= 512) {
            speedX = -speedX;
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;
        int speedY = this.getSpeedY() + direction * 5;

        // 散射弹道：3颗子弹呈扇形
        for (int i = 0; i < shootNum; i++) {
            int speedX = (i - 1) * 2; // -2, 0, 2 形成扇形
            BaseBullet bullet = new EnemyBullet(x + (i * 10 - 10), y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> props = new LinkedList<>();
        double r = Math.random();

        if(r < 0.3){
            propFactory = new BombPropFactory();
        } else if (r < 0.6) {
            propFactory = new BloodPropFactory();
        } else if (r < 0.9) {
            propFactory  = new BulletPropFactory();
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