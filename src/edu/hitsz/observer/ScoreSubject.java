package edu.hitsz.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 分数主题（被观察者）
 * 管理分数观察者列表并通知它们分数事件
 */
public class ScoreSubject {
    private List<ScoreObserver> observers = new ArrayList<>();
    private static ScoreSubject instance;

    // 私有构造函数，单例模式
    private ScoreSubject() {}

    /**
     * 获取单例实例
     */
    public static ScoreSubject getInstance() {
        if (instance == null) {
            instance = new ScoreSubject();
        }
        return instance;
    }

    /**
     * 注册观察者
     */
    public void registerObserver(ScoreObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    /**
     * 移除观察者
     */
    public void removeObserver(ScoreObserver observer) {
        observers.remove(observer);
    }

    /**
     * 通知所有观察者有分数需要添加
     */
    public void notifyObservers(ScoreEvent event) {
        for (ScoreObserver observer : observers) {
            try {
                observer.onScoreAdded(event);
            } catch (Exception e) {
                System.err.println("通知分数观察者时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 触发分数事件（便捷方法）
     */
    public static void triggerScoreEvent(int points, String reason) {
        ScoreEvent event = new ScoreEvent(points, reason);
        getInstance().notifyObservers(event);
    }
}