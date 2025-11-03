package edu.hitsz.application;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameSettings {
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JButton easyButton;
    private JButton mediumButton;
    private JButton hardButton;
    private JComboBox<String> soundComboBox;
    private JLabel soundLabel;

    // 新增组件用于美化
    private JPanel buttonPanel;
    private JPanel soundPanel;
    private JPanel contentPanel;

    private String selectedDifficulty = "简单";
    private boolean soundEnabled = true;

    public GameSettings() {
        // 初始化界面样式
        initUI();

        // 初始化音效下拉框
        soundComboBox.addItem("开");
        soundComboBox.addItem("关");
        soundComboBox.setSelectedIndex(0);

        // 添加按钮事件监听
        easyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedDifficulty = "简单";
                startGame();
            }
        });

        mediumButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedDifficulty = "普通";
                startGame();
            }
        });

        hardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedDifficulty = "困难";
                startGame();
            }
        });

        soundComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                soundEnabled = soundComboBox.getSelectedIndex() == 0;
            }
        });
    }

    /**
     * 初始化界面样式
     */
    private void initUI() {
        // 设置主面板背景
        mainPanel.setBackground(new Color(30, 30, 60)); // 深蓝色背景
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(40, 60, 40, 60)); // 增加内边距

        // 创建内容面板
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false); // 透明背景

        // 美化标题
        titleLabel.setText("飞机大战 - 游戏设置");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 32));
        titleLabel.setForeground(new Color(255, 215, 0)); // 金色
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 40, 0)); // 增加底部间距

        // 创建按钮面板 - 放大这个面板
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3, 25, 0)); // 增加水平间距到25
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 180), 2, true),
                "选择难度",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 18), // 增大字体
                new Color(200, 200, 255)
        ));
        buttonPanel.setPreferredSize(new Dimension(500, 120)); // 设置首选大小
        buttonPanel.setMaximumSize(new Dimension(500, 120)); // 设置最大大小

        // 美化按钮 - 增大按钮尺寸
        styleButton(easyButton, "简单", new Color(76, 175, 80));   // 绿色
        styleButton(mediumButton, "普通", new Color(33, 150, 243)); // 蓝色
        styleButton(hardButton, "困难", new Color(244, 67, 54));    // 红色

        // 创建音效设置面板 - 缩小这个面板
        soundPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8)); // 减小间距
        soundPanel.setOpaque(false);
        soundPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 180), 2, true),
                "音效设置",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("微软雅黑", Font.PLAIN, 14), // 使用较小字体
                new Color(200, 200, 255)
        ));
        soundPanel.setPreferredSize(new Dimension(300, 70)); // 设置较小的大小
        soundPanel.setMaximumSize(new Dimension(300, 70)); // 设置较小的大小

        // 美化音效标签
        soundLabel.setText("音效:");
        soundLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16)); // 使用较小字体
        soundLabel.setForeground(Color.WHITE);

        // 美化下拉框
        soundComboBox.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        soundComboBox.setBackground(new Color(50, 50, 80));
        soundComboBox.setForeground(Color.WHITE);
        soundComboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 180), 2),
                BorderFactory.createEmptyBorder(4, 8, 4, 8) // 减小内边距
        ));
        soundComboBox.setPreferredSize(new Dimension(80, 35)); // 设置下拉框大小
        soundComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(new Font("微软雅黑", Font.PLAIN, 14));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (isSelected) {
                    label.setBackground(new Color(70, 130, 180));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(new Color(50, 50, 80));
                    label.setForeground(Color.WHITE);
                }
                return label;
            }
        });

        // 组装界面
        soundPanel.add(soundLabel);
        soundPanel.add(soundComboBox);

        buttonPanel.add(easyButton);
        buttonPanel.add(mediumButton);
        buttonPanel.add(hardButton);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(30)); // 增加垂直间距
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(20)); // 垂直间距
        contentPanel.add(soundPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // 添加底部装饰
        JLabel footerLabel = new JLabel("哈尔滨工业大学（深圳） - 软件构造实验", SwingConstants.CENTER);
        footerLabel.setFont(new Font("微软雅黑", Font.ITALIC, 12));
        footerLabel.setForeground(new Color(180, 180, 220));
        footerLabel.setBorder(new EmptyBorder(20, 0, 0, 0));
        mainPanel.add(footerLabel, BorderLayout.SOUTH);
    }

    /**
     * 美化按钮样式
     */
    private void styleButton(JButton button, String text, Color color) {
        button.setText(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 22)); // 增大字体
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        // 设置按钮颜色
        button.setBackground(color);
        button.setForeground(Color.WHITE);

        // 添加鼠标悬停效果
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = color;

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        // 设置按钮大小和边框 - 增大按钮尺寸
        button.setPreferredSize(new Dimension(140, 70)); // 增大按钮尺寸
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(15, 25, 15, 25) // 增大内边距
        ));
    }

    /**
     * 开始游戏
     */
    private void startGame() {
        // 显示游戏开始信息（美化对话框）
        UIManager.put("OptionPane.background", new Color(40, 40, 70));
        UIManager.put("Panel.background", new Color(40, 40, 70));
        UIManager.put("OptionPane.messageFont", new Font("微软雅黑", Font.PLAIN, 14));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);

        JOptionPane.showMessageDialog(mainPanel,
                "<html><div style='text-align: center;'>" +
                        "<b>开始 " + selectedDifficulty + " 难度游戏！</b><br>" +
                        "音效: " + (soundEnabled ? "<font color='#4CAF50'>开</font>" : "<font color='#F44336'>关</font>") +
                        "</div></html>",
                "游戏开始",
                JOptionPane.INFORMATION_MESSAGE);

        // 恢复默认UI设置
        UIManager.put("OptionPane.background", null);
        UIManager.put("Panel.background", null);
        UIManager.put("OptionPane.messageFont", null);
        UIManager.put("OptionPane.messageForeground", null);

        // 创建游戏实例
        Game game = new Game();

        // 设置游戏难度
        int difficulty = Main.getDifficultyLevel(selectedDifficulty);
        System.out.println("设置游戏难度: " + selectedDifficulty + " (级别: " + difficulty + ")");
        game.setGameDifficulty(difficulty);  // 确保调用这个

        // 设置背景图片（根据难度）
        ImageManager.setBackgroundByDifficulty(difficulty);

        // 设置音效
        game.setSoundEnabled(soundEnabled);

        // 设置游戏结束回调
        game.setGameOverCallback(new Game.GameOverCallback() {
            @Override
            public void onGameOver(int score, int difficulty) {
                // 游戏结束后切换到排行榜界面
                Main.showScoreBoard(score, difficulty);
            }
        });

        // 切换到游戏界面
        Main.getFrame().setContentPane(game);
        Main.getFrame().revalidate();
        Main.getFrame().repaint();

        // 启动游戏
        game.action();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}