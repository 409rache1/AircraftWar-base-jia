package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.observer.BombExplosionEvent;
import edu.hitsz.prop.*;
import edu.hitsz.factory.*;
import edu.hitsz.strategy.StraightShootStrategy;
import java.util.LinkedList;
import java.util.List;

public class EliteEnemy extends AbstractAircraft implements BombObserver {
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
     * 炸弹爆炸事件处理 - 精英敌机消失
     */
    @Override
    public void onBombExplode(BombExplosionEvent event) {
        if (this.notValid()) {
            return;
        }
        // 检查是否在爆炸范围内
        if (event.isInRange(this.getLocationX(), this.getLocationY())) {
            // 普通敌机直接被清除
            this.vanish();
        }
    }



}