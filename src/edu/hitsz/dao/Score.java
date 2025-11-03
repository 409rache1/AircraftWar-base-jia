package edu.hitsz.dao;

import java.io.Serializable;
import java.util.Date;

public class Score implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerName;
    private int score;
    private int difficulty; // 0-简单, 1-中等, 2-困难
    private Date recordTime;

    public Score(String playerName, int score, int difficulty, Date recordTime) {
        this.playerName = playerName;
        this.score = score;
        this.difficulty = difficulty;
        this.recordTime = recordTime;
    }

    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public Date getRecordTime() {
        return recordTime;
    }

    @Override
    public String toString() {
        return String.format("Score{playerName='%s', score=%d, difficulty=%d, recordTime=%s}",
                playerName, score, difficulty, recordTime);
    }
}