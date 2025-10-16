// PropSuperBullet.java
package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;

public class PropSuperBullet extends AbstractProp {
    public PropSuperBullet(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void effect(AbstractAircraft aircraft) {
        System.out.println("SuperFireSupply active!");

        // 检查是否是英雄机
        if (aircraft instanceof HeroAircraft) {
            HeroAircraft hero = (HeroAircraft) aircraft;
            // 调用英雄机的道具激活方法，切换为环射
            hero.activateProp("SuperFireProp");
        }
    }
}