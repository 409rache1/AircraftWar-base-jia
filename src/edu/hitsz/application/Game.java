package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.prop.*;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import edu.hitsz.factory.*;
import edu.hitsz.dao.RankingManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public class Game extends JPanel {

    private int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<AbstractProp> props;

    private RankingManager rankingManager;
    private String playerName = "Player";

    /**
     * 屏幕中出现的敌机最大数量
     */
    private int enemyMaxNumber = 5;

    /**
     * 当前得分
     */
    private int score = 0;
    /**
     * 当前时刻
     */
    private int time = 0;

    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;

    /**
     * 游戏结束标志
     */
    private boolean gameOverFlag = false;

    /**
     * 是否已经处理过游戏结束逻辑
     */
    private boolean gameOverProcessed = false;

    /**
     * 游戏结束显示的字体
     */
    private Font gameOverFont = new Font("SansSerif", Font.BOLD, 50);
    private Font scoreFont = new Font("SansSerif", Font.BOLD, 30);

    protected AircraftFactory factory;

    // Boss相关属性
    private int bossScoreThreshold = 500;
    private boolean bossSpawned = false;
    private long lastBossTime = 0;
    private static final long BOSS_COOLDOWN = 10000; // 10秒冷却

    public Game() {
        heroAircraft = HeroAircraft.getInstance(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 1000
        );

        // 初始化DAO组件
        this.rankingManager = new RankingManager();
        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        /**
         * Scheduled 线程池，用于定时任务调度
         * 关于alibaba code guide：可命名的 ThreadFactory 一般需要第三方包
         * apache 第三方库： org.apache.commons.lang3.concurrent.BasicThreadFactory
         */
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            // 如果游戏已经结束，不再执行游戏逻辑
            if (gameOverFlag) {
                return;
            }

            time += timeInterval;

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                System.out.println(time);
                // 新敌机产生
                enemyGenerateAction();

                // 飞机射出子弹
                shootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 道具移动
            propsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            // 游戏结束检查英雄机是否存活
            if (heroAircraft.getHp() <= 0 && !gameOverProcessed) {
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;
                gameOverProcessed = true;

                // 处理游戏结束逻辑
                gameOverAction();

                // 强制重绘界面以显示游戏结束信息
                repaint();
            }

        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);
    }

    /**
     * 游戏结束处理逻辑
     */
    private void gameOverAction() {
        System.out.println("Game Over!");
        System.out.println("Final Score: " + score);

        // 保存得分记录到排行榜
        rankingManager.addGameRecord(playerName, score);

        // 打印排行榜
        System.out.println("\n" + "=".repeat(50));
        System.out.println("游戏结束！最终得分: " + score);
        rankingManager.printRankingList();

//        // 可选：弹出对话框显示游戏结束信息
//        SwingUtilities.invokeLater(() -> {
//            int option = JOptionPane.showConfirmDialog(this,
//                    "游戏结束！\n最终得分: " + score + "\n\n是否查看排行榜？",
//                    "游戏结束",
//                    JOptionPane.YES_NO_OPTION);
//
//            if (option == JOptionPane.YES_OPTION) {
//                // 可以在这里添加显示排行榜的对话框
//                showRankingDialog();
//            }
//        });
    }

//    /**
//     * 显示排行榜对话框（可选功能）
//     */
//    private void showRankingDialog() {
//        List<edu.hitsz.dao.Score> ranking = rankingManager.getRankingList();
//        StringBuilder rankingText = new StringBuilder();
//        rankingText.append("得分排行榜\n");
//        rankingText.append("=".repeat(30)).append("\n");
//
//        if (ranking.isEmpty()) {
//            rankingText.append("暂无游戏记录\n");
//        } else {
//            rankingText.append("名次\t玩家名\t得分\t时间\n");
//            rankingText.append("-".repeat(30)).append("\n");
//
//            for (int i = 0; i < Math.min(ranking.size(), 10); i++) { // 显示前10名
//                edu.hitsz.dao.Score score = ranking.get(i);
//                rankingText.append(String.format("第%d名\t%s\t%d\t%s%n",
//                        i + 1,
//                        score.getPlayerName(),
//                        score.getScore(),
//                        new java.text.SimpleDateFormat("MM-dd HH:mm").format(score.getRecordTime())));
//            }
//        }
//
//        JOptionPane.showMessageDialog(this,
//                rankingText.toString(),
//                "得分排行榜",
//                JOptionPane.INFORMATION_MESSAGE);
//    }

    //***********************
    //      Action 各部分
    //***********************

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    /**
     * 敌机生成动作
     * 包括普通敌机、精英敌机、超级精英敌机和Boss敌机
     */
    private void enemyGenerateAction() {
        // 普通敌机和精英敌机生成（受数量限制）
        if (enemyAircrafts.size() < enemyMaxNumber) {
            double rand = Math.random();

            if (rand < 0.1) {
                // 10% 概率生成超级精英敌机
                factory = new SuperEliteEnemyFactory();
                enemyAircrafts.add(factory.createAircraft(
                        (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                        (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                        (int) (Math.random() * 5 - 2),  // 随机水平速度 -2 到 2
                        5, // speedY
                        90 // hp - 超级精英敌机生命值更高
                ));
                System.out.println("超级精英敌机生成！");
            } else if (rand < 0.3) {
                // 20% 概率生成精英敌机
                factory = new EliteEnemyFactory();
                enemyAircrafts.add(factory.createAircraft(
                        (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.ELITE_ENEMY_IMAGE.getWidth())),
                        (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                        0,
                        5,
                        60
                ));
            } else {
                // 70% 概率生成普通敌机
                factory = new MobEnemyFactory();
                enemyAircrafts.add(factory.createAircraft(
                        (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                        (int) (Math.random() * Main.WINDOW_HEIGHT * 0.05),
                        0,
                        5,
                        30
                ));
            }
        }

        // Boss敌机生成逻辑（独立于敌机数量限制）
        long currentTime = System.currentTimeMillis();
        if (score >= bossScoreThreshold && !bossSpawned &&
                (currentTime - lastBossTime) > BOSS_COOLDOWN) {
            factory = new BossEnemyFactory();
            enemyAircrafts.add(factory.createAircraft(
                    Main.WINDOW_WIDTH / 2, // 在屏幕中央出现
                    100,                   // 在屏幕上方
                    3,                     // 水平移动速度
                    0,                     // 垂直速度为0（悬浮）
                    150                   // Boss生命值
            ));

            bossSpawned = true;
            lastBossTime = currentTime;
            System.out.println("Boss敌机出现！");
        }
    }

    private void shootAction() {
        // 敌机射击
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction(){
        for (AbstractProp prop : props){
            prop.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet) || bullet.crash(heroAircraft)) {
                // 英雄机撞击到敌机子弹
                // 英雄机损失一定生命值
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // 获得分数，产生道具补给
                        props.addAll(enemyAircraft.dropProps());

                        // 根据不同敌机类型给予不同分数
                        if (enemyAircraft instanceof BossEnemy) {
                            score += 500; // Boss给500分
                            bossSpawned = false; // Boss被击毁，重置标志
                            System.out.println("Boss敌机被击毁！获得500分");
                        } else if (enemyAircraft instanceof SuperEliteEnemy) {
                            score += 100; // 超级精英给100分
                        } else if (enemyAircraft instanceof EliteEnemy) {
                            score += 50; // 精英给50分
                        } else {
                            score += 10; // 普通敌机给10分
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得道具，道具生效
        for (AbstractProp prop : props) {
            if (prop.notValid()) {
                continue;
            }
            if (heroAircraft.crash(prop) || prop.crash(heroAircraft)) {
                // 英雄机撞击到道具
                prop.effect(heroAircraft);
                prop.vanish();
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }

    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param  g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, props);

        paintImageWithPositionRevised(g, enemyAircrafts);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

        // 如果游戏结束，显示游戏结束信息
        if (gameOverFlag) {
            paintGameOver(g);
        }
    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }

    /**
     * 绘制游戏结束界面
     */
    private void paintGameOver(Graphics g) {
        // 创建半透明背景
        Color semiTransparent = new Color(0, 0, 0, 128); // 半透明黑色
        g.setColor(semiTransparent);
        g.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

        // 设置字体和颜色
        g.setColor(Color.RED);
        g.setFont(gameOverFont);

        // 计算文字位置使其居中
        FontMetrics fm = g.getFontMetrics();
        String gameOverText = "Game Over!";
        int gameOverX = (Main.WINDOW_WIDTH - fm.stringWidth(gameOverText)) / 2;
        int gameOverY = Main.WINDOW_HEIGHT / 2 - 50;

        // 绘制"Game Over!"
        g.drawString(gameOverText, gameOverX, gameOverY);

        // 设置分数显示的字体和颜色
        g.setColor(Color.WHITE);
        g.setFont(scoreFont);

        // 计算分数文字位置
        String scoreText = "Final Score: " + score;
        fm = g.getFontMetrics();
        int scoreX = (Main.WINDOW_WIDTH - fm.stringWidth(scoreText)) / 2;
        int scoreY = gameOverY + 80;

        // 绘制最终分数
        g.drawString(scoreText, scoreX, scoreY);
    }
}