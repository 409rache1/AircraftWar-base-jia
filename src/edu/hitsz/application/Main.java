package edu.hitsz.application;

import javax.swing.*;
import java.awt.*;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    private static JFrame frame;

    public static void main(String[] args) {
        System.out.println("Hello Aircraft War");

        // 初始化 Frame
        frame = new JFrame("飞机大战");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null); // 居中显示
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 显示游戏设置界面
        showGameSettings();

        frame.setVisible(true);
    }

    /**
     * 显示游戏设置界面
     */
    public static void showGameSettings() {
        GameSettings gameSettings = new GameSettings();
        frame.setContentPane(gameSettings.getMainPanel());
        frame.revalidate();
        frame.repaint();
    }

    /**
     * 显示排行榜界面
     */
    public static void showScoreBoard(int score, int difficulty) {
        ScoreBoard scoreBoard = new ScoreBoard();
        frame.setContentPane(scoreBoard.getMainPanel());
        scoreBoard.setCurrentScore(score);
        scoreBoard.setCurrentDifficulty(difficulty);
        frame.revalidate();
        frame.repaint();
    }

    /**
     * 将难度字符串转换为数字
     */
    public static int getDifficultyLevel(String difficulty) {
        switch (difficulty) {
            case "简单": return 0;
            case "中等": return 1;
            case "困难": return 2;
            default: return 0;
        }
    }

    /**
     * 获取主窗口引用
     */
    public static JFrame getFrame() {
        return frame;
    }
}