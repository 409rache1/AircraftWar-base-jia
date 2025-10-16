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
     * 添加游戏记录
     */
    public void addGameRecord(String playerName, int score) {
        Score newScore = new Score(playerName, score, new Date());
        scoreDao.addScore(newScore);
        System.out.println("游戏记录已保存: " + newScore);
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

        System.out.println("=" .repeat(50));
        System.out.println("            得分排行榜");
        System.out.println("=" .repeat(50));
        System.out.println("名次\t玩家名\t\t得分\t记录时间");
        System.out.println("-" .repeat(50));

        if (ranking.isEmpty()) {
            System.out.println("暂无游戏记录");
        } else {
            for (int i = 0; i < ranking.size(); i++) {
                Score score = ranking.get(i);
                System.out.printf("第%d名\t%-12s\t%d\t%s%n",
                        i + 1,
                        score.getPlayerName(),
                        score.getScore(),
                        new SimpleDateFormat("MM-dd HH:mm").format(score.getRecordTime()));
            }
        }
        System.out.println("=" .repeat(50));
    }

    /**
     * 删除指定玩家的记录
     */
    public void deleteRecord(String playerName) {
        scoreDao.deleteScore(playerName);
        System.out.println("已删除玩家 " + playerName + " 的记录");
    }

    /**
     * 查找指定玩家的记录
     */
    public Score findRecord(String playerName) {
        return scoreDao.findScore(playerName);
    }
}