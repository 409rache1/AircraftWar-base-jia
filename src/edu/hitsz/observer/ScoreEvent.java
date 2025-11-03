package edu.hitsz.observer;

/**
 * 分数事件类，用于在观察者模式中传递分数信息
 */
public class ScoreEvent {
    private int points;
    private String reason;

    public ScoreEvent(int points, String reason) {
        this.points = points;
        this.reason = reason;
    }

    // Getters
    public int getPoints() {
        return points;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "ScoreEvent{points=" + points + ", reason='" + reason + "'}";
    }
}