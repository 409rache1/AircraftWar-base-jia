package edu.hitsz.bullet;

import edu.hitsz.observer.BombObserver;
import edu.hitsz.observer.BombExplosionEvent;

/**
 * @Author hitsz
 */
public class EnemyBullet extends BaseBullet implements BombObserver {

    public EnemyBullet(int locationX, int locationY, int speedX, int speedY, int power) {
        super(locationX, locationY, speedX, speedY, power);
    }

    /**
     * 炸弹爆炸事件处理 - 敌机子弹被清除
     */
    @Override
    public void onBombExplode(BombExplosionEvent event) {
        if (this.notValid()) {
            return;
        }

        // 检查是否在爆炸范围内
        if (event.isInRange(this.getLocationX(), this.getLocationY())) {
            // 敌机子弹被清除
            this.vanish();
            System.out.println("敌机子弹被炸弹清除");
        }
    }
}