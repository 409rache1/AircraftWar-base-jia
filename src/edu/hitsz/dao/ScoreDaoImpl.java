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
        Collections.sort(sortedScores);
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
                        sdf.format(score.getRecordTime()));
            }
        } catch (IOException e) {
            System.err.println("保存得分记录失败: " + e.getMessage());
        }
    }

    @Override
    public void loadFromFile() {
        if (!dataFile.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(dataFile))) {
            scores.clear();
            String line;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String playerName = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    Date recordTime = sdf.parse(parts[2]);
                    scores.add(new Score(playerName, score, recordTime));
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("加载得分记录失败: " + e.getMessage());
        }
    }
}