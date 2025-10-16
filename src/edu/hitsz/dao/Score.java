package edu.hitsz.dao;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 得分记录数值对象
 */
public class Score implements Comparable<Score> {
    private String playerName;
    private int score;
    private Date recordTime;

    public Score(String playerName, int score, Date recordTime) {
        this.playerName = playerName;
        this.score = score;
        this.recordTime = recordTime;
    }

    // Getter和Setter方法
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Date recordTime) {
        this.recordTime = recordTime;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        return String.format("%s, %d, %s", playerName, score, sdf.format(recordTime));
    }

    @Override
    public int compareTo(Score other) {
        // 按得分降序排列
        return Integer.compare(other.score, this.score);
    }
}