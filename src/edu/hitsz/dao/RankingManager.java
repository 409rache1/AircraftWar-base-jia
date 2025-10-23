package edu.hitsz.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 排行榜管理类 - 业务逻辑层
 */
public class RankingManager {
    private ScoreDao scoreDao;

    public RankingManager() {
        this.scoreDao = new ScoreDaoImpl();
    }

    /**
     * 添加游戏记录（带难度）
     */
    public void addGameRecord(String playerName, int score, int difficulty) {
        Score newScore = new Score(playerName, score, difficulty, new Date());
        scoreDao.addScore(newScore);
        System.out.println("游戏记录已保存: " + newScore + ", 难度: " + getDifficultyText(difficulty));
    }

    /**
     * 添加游戏记录（不带难度，兼容旧版本）
     */
    public void addGameRecord(String playerName, int score) {
        addGameRecord(playerName, score, 0); // 默认简单难度
    }

    /**
     * 获取排行榜列表
     */
    public List<Score> getRankingList() {
        return scoreDao.getAllScores();
    }

    /**
     * 打印排行榜到控制台
     */
    public void printRankingList() {
        List<Score> ranking = getRankingList();

        System.out.println("=" .repeat(60));
        System.out.println("            得分排行榜");
        System.out.println("=" .repeat(60));
        System.out.println("名次\t玩家名\t\t得分\t难度\t记录时间");
        System.out.println("-" .repeat(60));

        if (ranking.isEmpty()) {
            System.out.println("暂无游戏记录");
        } else {
            for (int i = 0; i < ranking.size(); i++) {
                Score score = ranking.get(i);
                System.out.printf("第%d名\t%-12s\t%d\t%s\t%s%n",
                        i + 1,
                        score.getPlayerName(),
                        score.getScore(),
                        getDifficultyText(score.getDifficulty()),
                        new SimpleDateFormat("MM-dd HH:mm").format(score.getRecordTime()));
            }
        }
        System.out.println("=" .repeat(60));
    }

    /**
     * 删除指定玩家的记录
     */
    public void deleteRecord(String playerName) {
        scoreDao.deleteScore(playerName);
        System.out.println("已删除玩家 " + playerName + " 的记录");
    }

    /**
     * 删除指定索引的记录
     */
    public void deleteRecord(int index) {
        List<Score> scores = getRankingList();
        if (index >= 0 && index < scores.size()) {
            Score record = scores.get(index);
            deleteRecord(record.getPlayerName());
        }
    }

    /**
     * 查找指定玩家的记录
     */
    public Score findRecord(String playerName) {
        return scoreDao.findScore(playerName);
    }

    /**
     * 将难度数字转换为文本
     */
    private String getDifficultyText(int difficulty) {
        switch (difficulty) {
            case 0: return "简单";
            case 1: return "中等";
            case 2: return "困难";
            default: return "未知";
        }
    }
}