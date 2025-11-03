// BombObserverManager.java
package edu.hitsz.observer;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public class BombObserverManager {
    private static BombObserverManager instance;
    private BombSubject bombSubject;

    private BombObserverManager() {
        bombSubject = BombSubject.getInstance();
    }

    public static BombObserverManager getInstance() {
        if (instance == null) {
            instance = new BombObserverManager();
        }
        return instance;
    }

    /**
     * 注册单个敌机作为观察者
     */
    public void registerAircraft(AbstractAircraft aircraft) {
        if (aircraft instanceof BombObserver && !aircraft.notValid()) {
            bombSubject.registerObserver((BombObserver) aircraft);
        }
    }

    /**
     * 注册单个子弹作为观察者
     */
    public void registerBullet(BaseBullet bullet) {
        if (bullet instanceof BombObserver && !bullet.notValid()) {
            bombSubject.registerObserver((BombObserver) bullet);
        }
    }

    /**
     * 批量注册敌机列表
     */
    public void registerAircraftList(List<AbstractAircraft> aircrafts) {
        for (AbstractAircraft aircraft : aircrafts) {
            registerAircraft(aircraft);
        }
    }

    /**
     * 批量注册子弹列表
     */
    public void registerBulletList(List<BaseBullet> bullets) {
        for (BaseBullet bullet : bullets) {
            registerBullet(bullet);
        }
    }

    /**
     * 移除所有观察者
     */
    public void clearAllObservers() {
        bombSubject.clearObservers();
    }

    /**
     * 获取当前观察者数量
     */
    public int getObserverCount() {
        return bombSubject.getObserverCount();
    }
}