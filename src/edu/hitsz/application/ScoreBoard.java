package edu.hitsz.application;

import edu.hitsz.dao.RankingManager;
import edu.hitsz.dao.Score;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class ScoreBoard {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JScrollPane scrollPane;
    private JTable scoreTable;
    private JPanel buttonPanel;
    private JButton deleteButton;
    private JButton backButton;
    private JComboBox<String> difficultyComboBox; // 难度选择下拉框

    private DefaultTableModel tableModel;
    private RankingManager rankingManager;
    private int currentScore;
    private int currentDifficulty;

    public ScoreBoard() {
        // 使用统一的排行榜管理器
        this.rankingManager = new RankingManager();
        this.currentScore = 0;
        this.currentDifficulty = 0;

        // 初始化表格和组件
        initializeUI();
        loadScoreData();
        setupEventListeners();
    }

    private void initializeUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255));  // 淡蓝色背景

        // 标题面板
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(240, 248, 255));

        // 标题
        titleLabel = new JLabel("排行榜", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        titleLabel.setForeground(new Color(25, 25, 112));  // 深蓝色
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        // 难度选择面板
        JPanel difficultyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        difficultyPanel.setBackground(new Color(240, 248, 255));

        JLabel difficultyLabel = new JLabel("选择难度:");
        difficultyLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        difficultyLabel.setForeground(new Color(25, 25, 112));

        difficultyComboBox = new JComboBox<>(new String[]{"简单", "普通", "困难"});
        difficultyComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        difficultyComboBox.setBackground(Color.WHITE);
        difficultyComboBox.addActionListener(e -> loadScoreData()); // 选择难度时重新加载数据

        difficultyPanel.add(difficultyLabel);
        difficultyPanel.add(difficultyComboBox);
        difficultyPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        titlePanel.add(difficultyPanel, BorderLayout.CENTER);

        mainPanel.add(titlePanel, BorderLayout.NORTH);

        // 表格面板
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 248, 255));

        // 初始化表格
        String[] columnNames = {"排名", "玩家名", "得分", "时间"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        scoreTable = new JTable(tableModel);
        scoreTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scoreTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        scoreTable.setRowHeight(30);  // 设置行高
        scoreTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 16));  // 设置表头字体

        scrollPane = new JScrollPane(scoreTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // 按钮面板
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(240, 248, 255));

        // 删除按钮
        deleteButton = createButton("删除记录", new Color(255, 69, 0));  // 红色
        backButton = createButton("返回", new Color(70, 130, 180));  // 蓝色

        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 18));
        button.setBackground(color);  // 设置按钮背景颜色
        button.setForeground(Color.WHITE);  // 设置按钮文字颜色
        button.setFocusPainted(false);  // 去掉焦点边框
        button.setPreferredSize(new Dimension(120, 40));  // 设置按钮大小
        button.setBorder(BorderFactory.createRaisedBevelBorder());  // 按钮边框
        return button;
    }

    private void loadScoreData() {
        // 清空现有数据
        tableModel.setRowCount(0);

        // 获取当前选择的难度
        int selectedDifficulty = difficultyComboBox.getSelectedIndex();

        // 从排行榜管理器加载指定难度的得分数据
        List<Score> scores = rankingManager.getRankingList(selectedDifficulty);

        // 填充表格
        int rank = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");

        for (Score record : scores) {
            tableModel.addRow(new Object[]{
                    rank++,
                    record.getPlayerName(),
                    record.getScore(),
                    dateFormat.format(record.getRecordTime())
            });
        }

        // 更新标题显示当前难度
        String difficultyText = getDifficultyText(selectedDifficulty);
        titleLabel.setText(difficultyText + "难度排行榜");

        System.out.println("加载了 " + scores.size() + " 条 " + difficultyText + " 难度记录");
    }

    private void setupEventListeners() {
        // 删除按钮事件
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedScore();
            }
        });

        // 返回按钮事件
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToMainMenu();
            }
        });
    }

    private void deleteSelectedScore() {
        int selectedRow = scoreTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(mainPanel, "请选择要删除的记录！", "提示",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String playerName = (String) tableModel.getValueAt(selectedRow, 1);
        int score = (int) tableModel.getValueAt(selectedRow, 2);
        int selectedDifficulty = difficultyComboBox.getSelectedIndex();

        int result = JOptionPane.showConfirmDialog(mainPanel,
                "确定要删除玩家 '" + playerName + "' 的 " + getDifficultyText(selectedDifficulty) +
                        "难度得分记录 (" + score + "分) 吗？",
                "确认删除", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            // 使用统一的排行榜管理器删除记录（指定难度）
            rankingManager.deleteRecord(playerName, score, selectedDifficulty);
            loadScoreData();  // 重新加载数据
            JOptionPane.showMessageDialog(mainPanel, "记录删除成功！", "成功",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void returnToMainMenu() {
        // 返回游戏设置界面
        Main.showGameSettings();
    }

    private String getDifficultyText(int difficulty) {
        switch (difficulty) {
            case 0: return "简单";
            case 1: return "普通";
            case 2: return "困难";
            default: return "未知";
        }
    }

    public void setCurrentScore(int score) {
        this.currentScore = score;

        if (score > 0) {
            // 使用SwingWorker来处理弹窗，避免阻塞界面显示
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    // 短暂延迟确保界面已经显示
                    Thread.sleep(500);
                    return null;
                }

                @Override
                protected void done() {
                    // 在EDT中执行弹窗
                    String playerName = JOptionPane.showInputDialog(mainPanel,
                            "游戏结束！你的得分为 " + score +
                                    "\n难度：" + getDifficultyText(currentDifficulty) +
                                    "\n请输入玩家名字：",
                            "记录得分", JOptionPane.QUESTION_MESSAGE);

                    if (playerName != null && !playerName.trim().isEmpty()) {
                        // 使用统一的排行榜管理器添加记录（带难度）
                        rankingManager.addGameRecord(
                                playerName.trim(),
                                currentScore,
                                currentDifficulty
                        );

                        // 刷新表格显示当前难度的记录
                        difficultyComboBox.setSelectedIndex(currentDifficulty);
                        loadScoreData();

                        JOptionPane.showMessageDialog(mainPanel,
                                "得分记录添加成功！\n难度：" + getDifficultyText(currentDifficulty),
                                "成功", JOptionPane.INFORMATION_MESSAGE);
                    }

                    // 重置当前分数
                    currentScore = 0;
                }
            };
            worker.execute();
        }
    }

    public void setCurrentDifficulty(int difficulty) {
        this.currentDifficulty = difficulty;
        // 设置下拉框为当前难度
        difficultyComboBox.setSelectedIndex(difficulty);
        // 更新标题
        titleLabel.setText(getDifficultyText(difficulty) + "难度排行榜");
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}