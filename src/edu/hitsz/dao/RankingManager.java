package edu.hitsz.dao;

import java.io.*;
import java.util.*;

public class RankingManager {
    private static final String SCORE_FILE = "scores.dat";
    private Map<Integer, List<Score>> difficultyScores; // 按难度存储分数列表

    public RankingManager() {
        difficultyScores = new HashMap<>();
        // 初始化三个难度的分数列表
        for (int i = 0; i < 3; i++) {
            difficultyScores.put(i, new ArrayList<>());
        }
        loadFromFile();
    }

    /**
     * 添加游戏记录（包含难度）
     */
    public void addGameRecord(String playerName, int score, int difficulty) {
        List<Score> scores = difficultyScores.get(difficulty);
        if (scores == null) {
            scores = new ArrayList<>();
            difficultyScores.put(difficulty, scores);
        }

        Score newScore = new Score(playerName, score, difficulty, new Date());
        scores.add(newScore);

        // 按分数降序排序
        scores.sort((s1, s2) -> s2.getScore() - s1.getScore());

        // 只保留前10名
        if (scores.size() > 10) {
            scores = new ArrayList<>(scores.subList(0, 10));
            difficultyScores.put(difficulty, scores);
        }

        saveToFile();
    }

    /**
     * 获取指定难度的排行榜
     */
    public List<Score> getRankingList(int difficulty) {
        List<Score> scores = difficultyScores.get(difficulty);
        if (scores == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(scores);
    }

    /**
     * 获取所有难度的排行榜（用于兼容旧代码）
     */
    public List<Score> getRankingList() {
        List<Score> allScores = new ArrayList<>();
        for (List<Score> scores : difficultyScores.values()) {
            allScores.addAll(scores);
        }
        // 按分数降序排序
        allScores.sort((s1, s2) -> s2.getScore() - s1.getScore());
        return allScores;
    }

    /**
     * 删除指定难度的记录
     */
    public void deleteRecord(String playerName, int difficulty) {
        List<Score> scores = difficultyScores.get(difficulty);
        if (scores != null) {
            scores.removeIf(score -> score.getPlayerName().equals(playerName));
            saveToFile();
        }
    }

    /**
     * 删除指定记录（包含难度信息）
     */
    public void deleteRecord(String playerName, int score, int difficulty) {
        List<Score> scores = difficultyScores.get(difficulty);
        if (scores != null) {
            scores.removeIf(s -> s.getPlayerName().equals(playerName) && s.getScore() == score);
            saveToFile();
        }
    }

    /**
     * 从文件加载数据
     */
    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SCORE_FILE))) {
            difficultyScores = (Map<Integer, List<Score>>) ois.readObject();
            System.out.println("排行榜数据加载成功");
        } catch (FileNotFoundException e) {
            System.out.println("排行榜文件不存在，将创建新文件");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("加载排行榜数据失败: " + e.getMessage());
        }
    }

    /**
     * 保存数据到文件
     */
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORE_FILE))) {
            oos.writeObject(difficultyScores);
            System.out.println("排行榜数据保存成功");
        } catch (IOException e) {
            System.err.println("保存排行榜数据失败: " + e.getMessage());
        }
    }

    /**
     * 打印排行榜（用于调试）
     */
    public void printRankingList() {
        for (int difficulty = 0; difficulty < 3; difficulty++) {
            List<Score> scores = getRankingList(difficulty);
            System.out.println("=== " + getDifficultyText(difficulty) + "难度排行榜 ===");
            if (scores.isEmpty()) {
                System.out.println("暂无记录");
            } else {
                for (int i = 0; i < scores.size(); i++) {
                    Score score = scores.get(i);
                    System.out.printf("%d. %s - %d分 - %s%n",
                            i + 1, score.getPlayerName(), score.getScore(),
                            score.getRecordTime());
                }
            }
            System.out.println();
        }
    }

    private String getDifficultyText(int difficulty) {
        switch (difficulty) {
            case 0: return "简单";
            case 1: return "中等";
            case 2: return "困难";
            default: return "未知";
        }
    }
}