package edu.hitsz.observer;

/**
 * 炸弹爆炸事件类
 * 包含爆炸的相关信息，传递给观察者
 */
public class BombExplosionEvent {
    private int explosionX;
    private int explosionY;
    private int explosionRadius;

    public BombExplosionEvent(int x, int y) {
        this.explosionX = x;
        this.explosionY = y;
        this.explosionRadius = 1000; // 默认大半径，影响全屏
    }

    public BombExplosionEvent(int x, int y, int radius) {
        this.explosionX = x;
        this.explosionY = y;
        this.explosionRadius = radius;
    }

    // Getters
    public int getExplosionX() {
        return explosionX;
    }

    public int getExplosionY() {
        return explosionY;
    }

    public int getExplosionRadius() {
        return explosionRadius;
    }

    /**
     * 计算对象是否在爆炸范围内
     */
    public boolean isInRange(int targetX, int targetY) {
        double distance = Math.sqrt(Math.pow(targetX - explosionX, 2) + Math.pow(targetY - explosionY, 2));
        return distance <= explosionRadius;
    }
}