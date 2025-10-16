package edu.hitsz.dao;

import java.util.List;

/**
 * 得分数据访问对象接口
 */
public interface ScoreDao {
    /**
     * 获取所有得分记录
     */
    List<Score> getAllScores();

    /**
     * 添加得分记录
     */
    void addScore(Score score);

    /**
     * 删除指定玩家的记录
     */
    void deleteScore(String playerName);

    /**
     * 查找指定玩家的记录
     */
    Score findScore(String playerName);

    /**
     * 保存数据到文件
     */
    void saveToFile();

    /**
     * 从文件加载数据
     */
    void loadFromFile();
}