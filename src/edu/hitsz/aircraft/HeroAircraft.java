// HeroAircraft.java
package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.ShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;
import edu.hitsz.strategy.ScatteredShootStrategy;
import edu.hitsz.strategy.CircularShootStrategy;
import java.util.LinkedList;
import java.util.List;

public class HeroAircraft extends AbstractAircraft {
    private int shootNum = 1;
    private int power = 30;
    private int direction = -1;

    private static HeroAircraft instance;

    //默认设置为直射
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new StraightShootStrategy(shootNum, 10, power, true);
    }

    public static HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
                }
            }
        }
        return instance;
    }

    // 道具激活方法，切换弹道策略
    public void activateProp(String propType) {
        switch(propType) {
            case "PropBullet": // 普通火力道具
                setShootStrategy(new ScatteredShootStrategy(3, 8, power, true));
                System.out.println("火力道具生效：切换为散射");
                break;
            case "SuperFireProp": // 超级火力道具
                setShootStrategy(new CircularShootStrategy(20, 6, power, true)); // 添加true参数
                System.out.println("超级火力道具生效：切换为环射");
                break;
            default:
                setShootStrategy(new StraightShootStrategy(shootNum, 10, power, true));
                System.out.println("恢复默认直射");
        }
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
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
            bullet = new HeroBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }

    @Override
    public List<AbstractProp> dropProps() {
        return new LinkedList<>();
    }
}