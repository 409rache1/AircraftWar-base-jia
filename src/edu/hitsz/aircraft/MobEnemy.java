// MobEnemy.java
package edu.hitsz.aircraft;

import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.NoShootStrategy;
import java.util.LinkedList;
import java.util.List;

public class MobEnemy extends AbstractAircraft {
    public MobEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        // 设置不发射策略
        this.shootStrategy = new NoShootStrategy();
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
        return new LinkedList<>();
    }

    @Override
    public List<AbstractProp> dropProps() {
        return new LinkedList<>();
    }
}