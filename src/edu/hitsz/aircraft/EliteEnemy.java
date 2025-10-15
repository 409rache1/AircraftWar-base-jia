package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.prop.*;
import edu.hitsz.factory.*;
import java.util.LinkedList;
import java.util.List;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */



public class EliteEnemy extends AbstractAircraft {

    private int direction = 1;
    private int shootNum = 1;
    private int power = 30;


    public EliteEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= Main.WINDOW_HEIGHT ) {
            vanish();
        }
    }

    @Override
    public List<BaseBullet> shoot() {
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction*2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction*5;
        BaseBullet bullet;
        for(int i=0; i<shootNum; i++){
            // 子弹发射位置相对飞机位置向前偏移
            // 多个子弹横向分散
            bullet = new EnemyBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
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

    // @Override
    // public void update(){
    //     this.vanish();
    // }
}