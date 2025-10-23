package edu.hitsz.dao;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 得分数据访问对象实现类
 * 使用文件存储数据
 */
public class ScoreDaoImpl implements ScoreDao {
    private List<Score> scores;
    private File dataFile;
    private static final String FILE_NAME = "scores.txt";

    public ScoreDaoImpl() {
        this.scores = new ArrayList<>();
        this.dataFile = new File(FILE_NAME);
        loadFromFile();
    }

    @Override
    public List<Score> getAllScores() {
        // 返回排序后的列表（按得分降序）
        List<Score> sortedScores = new ArrayList<>(scores);

        // 使用自定义比较器按分数降序排列
        Collections.sort(sortedScores, new Comparator<Score>() {
            @Override
            public int compare(Score s1, Score s2) {
                // 按分数降序排列（分数高的排在前面）
                return Integer.compare(s2.getScore(), s1.getScore());
            }
        });

        return sortedScores;
    }

    @Override
    public void addScore(Score score) {
        scores.add(score);
        saveToFile();
    }

    @Override
    public void deleteScore(String playerName) {
        scores.removeIf(score -> score.getPlayerName().equals(playerName));
        saveToFile();
    }

    @Override
    public Score findScore(String playerName) {
        for (Score score : scores) {
            if (score.getPlayerName().equals(playerName)) {
                return score;
            }
        }
        return null;
    }

    @Override
    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(dataFile))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Score score : scores) {
                writer.println(score.getPlayerName() + "," +
                        score.getScore() + "," +
                        score.getDifficulty() + "," +  // 新增难度字段
                        sdf.format(score.getRecordTime()));
            }
            System.out.println("成功保存 " + scores.size() + " 条记录到文件");
        } catch (IOException e) {
            System.err.println("保存得分记录失败: " + e.getMessage());
        }
    }

    @Override
    public void loadFromFile() {
        if (!dataFile.exists()) {
            System.out.println("得分记录文件不存在，将创建新文件");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            scores.clear();
            String line;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int loadedCount = 0;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String playerName = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    Date recordTime;

                    // 处理旧格式（3个字段）和新格式（4个字段）的兼容
                    if (parts.length == 3) {
                        // 旧格式：playerName,score,recordTime
                        recordTime = sdf.parse(parts[2]);
                        scores.add(new Score(playerName, score, 0, recordTime)); // 默认简单难度
                    } else {
                        // 新格式：playerName,score,difficulty,recordTime
                        int difficulty = Integer.parseInt(parts[2]);
                        recordTime = sdf.parse(parts[3]);
                        scores.add(new Score(playerName, score, difficulty, recordTime));
                    }
                    loadedCount++;
                }
            }
            System.out.println("从文件加载了 " + loadedCount + " 条得分记录");
        } catch (IOException | ParseException | NumberFormatException e) {
            System.err.println("加载得分记录失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}