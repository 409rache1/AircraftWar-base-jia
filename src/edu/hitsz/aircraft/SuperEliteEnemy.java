package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.observer.BombExplosionEvent;
import edu.hitsz.factory.*;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.ScatteredShootStrategy;
import java.util.LinkedList;
import java.util.List;

public class SuperEliteEnemy extends AbstractAircraft implements BombObserver {
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

        PropFactory propFactory = null;

        if(r < 0.25){
            // 需要传递Game引用给炸弹道具工厂
            propFactory = new BombPropFactory();
        } else if (r < 0.5) {
            propFactory = new BloodPropFactory();
        } else if (r < 0.7) {
            propFactory = new BulletPropFactory();
        } else if (r < 0.9) {
            propFactory = new SuperBulletPropFactory();
        } else {
            propFactory = null;
        }

        if (propFactory != null) {
            AbstractProp prop = propFactory.createProp(this.locationX, this.locationY);
            props.add(prop);
        }

        return props;
    }

    /**
     * 炸弹爆炸事件处理 - 超级精英敌机血量减少
     */
    @Override
    public void onBombExplode(BombExplosionEvent event) {
        if (this.notValid()) {
            return;
        }

        // 检查是否在爆炸范围内
        if (event.isInRange(this.getLocationX(), this.getLocationY())) {
            // 超级精英敌机血量减少（但不立即消失）
            int damage = this.getHp() / 3; // 减少三分之一血量
            this.decreaseHp(damage);

            // 如果血量降至0以下，则消失
            if (this.getHp() <= 0) {
                this.vanish();
            }
        }
    }
}