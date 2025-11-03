package edu.hitsz.observer;

/**
 * 分数观察者接口
 */
public interface ScoreObserver {
    /**
     * 当有分数需要添加时被调用
     * @param event 分数事件，包含分数值和原因
     */
    void onScoreAdded(ScoreEvent event);
}