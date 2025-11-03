package edu.hitsz.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 炸弹主题（被观察者）
 * 管理观察者列表并通知它们炸弹爆炸事件
 */
public class BombSubject {
    private List<BombObserver> observers = new ArrayList<>();
    private static BombSubject instance;

    // 私有构造函数，单例模式
    private BombSubject() {}

    /**
     * 获取单例实例
     */
    public static BombSubject getInstance() {
        if (instance == null) {
            instance = new BombSubject();
        }
        return instance;
    }

    /**
     * 注册观察者
     */
    public void registerObserver(BombObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public List<BombObserver> getObservers() {
        return new ArrayList<>(observers); // 返回观察者列表的副本
    }

    /**
     * 移除观察者
     */
    public void removeObserver(BombObserver observer) {
        observers.remove(observer);
    }

    /**
     * 移除所有观察者
     */
    public void clearObservers() {
        observers.clear();
    }

    /**
     * 通知所有观察者炸弹爆炸
     */
    public void notifyObservers(BombExplosionEvent event) {
        // 使用新列表避免在迭代过程中修改导致的异常
        List<BombObserver> observersToNotify = new ArrayList<>(observers);

        for (BombObserver observer : observersToNotify) {
            try {
                observer.onBombExplode(event);
            } catch (Exception e) {
                System.err.println("通知观察者时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前观察者数量
     */
    public int getObserverCount() {
        return observers.size();
    }
}