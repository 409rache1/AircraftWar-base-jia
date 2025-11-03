package edu.hitsz.observer;

/**
 * 炸弹观察者接口
 * 所有需要响应炸弹爆炸的对象都应实现此接口
 */
public interface BombObserver {
    /**
     * 当炸弹爆炸时被调用
     * @param event 炸弹爆炸事件，包含爆炸位置等信息
     */
    void onBombExplode(BombExplosionEvent event);
}