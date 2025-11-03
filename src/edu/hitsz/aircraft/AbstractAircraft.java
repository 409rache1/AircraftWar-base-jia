package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.factory.PropFactory;
import edu.hitsz.strategy.ShootStrategy;
import java.util.List;

public abstract class AbstractAircraft extends AbstractFlyingObject {
    protected int maxHp;
    protected int hp;
    protected PropFactory propFactory;
    protected ShootStrategy shootStrategy; // 新增策略字段

    public AbstractAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    // 新增策略设置方法
    public void setShootStrategy(ShootStrategy strategy) {
        this.shootStrategy = strategy;
    }

    // 修改shoot方法使用策略模式
    public List<BaseBullet> shoot() {
        if (shootStrategy != null) {
            return shootStrategy.shoot(this);
        }
        return directShoot();
    }

    // 原有的抽象方法改为受保护的方法
    protected abstract List<BaseBullet> directShoot();

    // 原有其他方法保持不变...
    public void decreaseHp(int decrease){
        hp -= decrease;
        if(hp <= 0){
            hp=0;
            vanish();
        }
    }

    public void increaseHp(int increase){
        hp += increase;
        if(hp > maxHp){
            hp = maxHp;
        }
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public abstract List<AbstractProp> dropProps();
}