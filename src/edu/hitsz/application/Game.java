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

    // 音效管理器
    private SoundManager soundManager;
    private boolean soundEnabled = true;

    // 音效文件路径常量
    private static final String SOUND_BGM = "src/videos/bgm.wav";
    private static final String SOUND_BGM_BOSS = "src/videos/bgm_boss.wav";
    private static final String SOUND_BULLET_HIT = "src/videos/bullet_hit.wav";
    private static final String SOUND_BULLET_SHOOT = "src/videos/bullet.wav";
    private static final String SOUND_BOMB_EXPLOSION = "src/videos/bomb_explosion.wav";
    private static final String SOUND_GET_SUPPLY = "src/videos/get_supply.wav";
    private static final String SOUND_GAME_OVER = "src/videos/game_over.wav";

    // 游戏结束回调接口和难度
    public interface GameOverCallback {
        void onGameOver(int score, int difficulty);
    }

    private GameOverCallback gameOverCallback;
    private int gameDifficulty = 0; // 默认简单难度

    public Game() {
        // 获取英雄机单例并重置状态
        heroAircraft = HeroAircraft.getInstance(
                Main.WINDOW_WIDTH / 2,
                Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                0, 0, 1000
        );

        // 重置英雄机状态
        if (heroAircraft != null) {
            heroAircraft.reset();
        } else {
            System.err.println("错误：无法获取英雄机实例");
        }

        // 初始化统一的排行榜管理器
        this.rankingManager = new RankingManager();

        // 初始化音效管理器
        this.soundManager = SoundManager.getInstance();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        props = new LinkedList<>();

        // 重置其他游戏状态
        resetGameState();

        /**
         * Scheduled 线程池，用于定时任务调度
         */
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);
    }

    /**
     * 重置游戏状态
     */
    private void resetGameState() {

        // 重置游戏变量
        backGroundTop = 0;
        score = 0;
        time = 0;
        cycleTime = 0;
        gameOverFlag = false;
        gameOverProcessed = false;
        bossSpawned = false;
        lastBossTime = 0;

        // 清空所有列表
        enemyAircrafts.clear();
        heroBullets.clear();
        enemyBullets.clear();
        props.clear();

        // 重置敌机工厂等
        factory = null;

    }

    /**
     * 设置音效开关
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        soundManager.setSoundEnabled(enabled);

        if (enabled) {
            // 如果开启音效，播放背景音乐
            soundManager.playBackgroundMusic(SOUND_BGM);
        } else {
            // 如果关闭音效，停止所有声音
            soundManager.stopAllSounds();
        }
    }

    /**
     * 开始游戏时的音效初始化
     */
    public void startGameSounds() {
        if (soundEnabled) {
            soundManager.playBackgroundMusic(SOUND_BGM);
        }
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {
        // 确保游戏状态已重置
        if (gameOverFlag) {
            System.out.println("警告：尝试启动已结束的游戏实例，强制重置");
            resetGameState();
        }

        // 确保英雄机状态正常
        if (heroAircraft.getHp() <= 0) {
            System.out.println("警告：英雄机HP异常，强制重置");
            heroAircraft.reset(
                    Main.WINDOW_WIDTH / 2,
                    Main.WINDOW_HEIGHT - ImageManager.HERO_IMAGE.getHeight(),
                    1000
            );
        }

        System.out.println("开始新游戏，英雄机HP: " + heroAircraft.getHp());

        // 启动游戏音效
        startGameSounds();

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
        // 清理英雄机的道具效果线程
        if (heroAircraft != null) {
            heroAircraft.cleanup();
        }


        // 播放游戏结束音效
        if (soundEnabled) {
            soundManager.playSound(SoundManager.GAME_OVER, SOUND_GAME_OVER);
            soundManager.stopBackgroundMusic();
            soundManager.stopBossMusic();
        }

        // 注意：这里先不保存记录，等玩家在ScoreBoard中输入名字后再保存
        // 实际的保存将在 ScoreBoard 中完成

        // 打印排行榜
        System.out.println("\n" + "=".repeat(50));
        System.out.println("游戏结束！最终得分: " + score);
        rankingManager.printRankingList();

        // 在EDT线程中执行界面切换
        SwingUtilities.invokeLater(() -> {

            // 调用游戏结束回调，切换到排行榜界面
            if (gameOverCallback != null) {
                System.out.println("正在调用游戏结束回调，分数: " + score + ", 难度: " + gameDifficulty);
                try {
                    gameOverCallback.onGameOver(score, gameDifficulty);
                } catch (Exception e) {
                    e.printStackTrace();

                    // 备用方案：直接显示游戏结束信息
                    JOptionPane.showMessageDialog(this,
                            "游戏结束！\n最终得分: " + score + "\n\n请返回主菜单查看排行榜",
                            "游戏结束",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                // 如果没有设置回调，直接显示游戏结束信息
                JOptionPane.showMessageDialog(this,
                        "游戏结束！\n最终得分: " + score + "\n\n请返回主菜单查看排行榜",
                        "游戏结束",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

    }

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

            // 在屏幕中央生成Boss敌机
            int bossX = Main.WINDOW_WIDTH / 2;
            int bossY = 100; // 在屏幕上方固定位置

            BossEnemy boss = (BossEnemy) factory.createAircraft(
                    bossX,                   // 在屏幕中央出现
                    bossY,                   // 在屏幕上方固定位置
                    3,                       // 水平移动速度
                    0,                       // 垂直速度为0（悬浮）
                    150                      // Boss生命值
            );

            enemyAircrafts.add(boss);

            bossSpawned = true;
            lastBossTime = currentTime;

            // 播放Boss音乐
            if (soundEnabled) {
                soundManager.playBossMusic(SOUND_BGM_BOSS);
            }
        }
    }

    private void shootAction() {
        // 敌机射击
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
        // 英雄射击
        List<BaseBullet> heroBulletsShot = heroAircraft.shoot();
        if (!heroBulletsShot.isEmpty()) {
            heroBullets.addAll(heroBulletsShot);

            // 播放英雄机子弹发射音效
            if (soundEnabled) {
                soundManager.playSound(SoundManager.BULLET_SHOOT, SOUND_BULLET_SHOOT);
            }
        }
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

                // 播放子弹击中音效
                if (soundEnabled && bullet.getPower() > 0) {
                    soundManager.playSound(SoundManager.BULLET_HIT, SOUND_BULLET_HIT);
                }
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

                    // 播放子弹击中音效
                    if (soundEnabled && bullet.getPower() > 0) {
                        soundManager.playSound(SoundManager.BULLET_HIT, SOUND_BULLET_HIT);
                    }

                    if (enemyAircraft.notValid()) {
                        // 获得分数，产生道具补给
                        props.addAll(enemyAircraft.dropProps());

                        // 根据不同敌机类型给予不同分数
                        if (enemyAircraft instanceof BossEnemy) {
                            score += 500; // Boss给500分
                            bossSpawned = false; // Boss被击毁，重置标志

                            // 停止Boss音乐，恢复普通背景音乐
                            soundManager.resumeBackgroundMusic(SOUND_BGM);

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

                    // 播放爆炸音效
                    if (soundEnabled) {
                        soundManager.playSound(SoundManager.BOMB_EXPLOSION, SOUND_BOMB_EXPLOSION);
                    }
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

                // 播放获得道具音效
                if (soundEnabled) {
                    soundManager.playSound(SoundManager.GET_SUPPLY, SOUND_GET_SUPPLY);
                }
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

        // 绘制背景,图片滚动 - 使用动态背景
        BufferedImage currentBackground = ImageManager.getCurrentBackground();
        g.drawImage(currentBackground, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(currentBackground, 0, this.backGroundTop, null);
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

    /**
     * 设置游戏难度
     */
    public void setGameDifficulty(int difficulty) {
        this.gameDifficulty = difficulty;
    }

    /**
     * 设置游戏结束回调
     */
    public void setGameOverCallback(GameOverCallback callback) {
        this.gameOverCallback = callback;
    }

    /**
     * 游戏结束时的清理工作
     */
    public void cleanup() {
        // 停止所有音效
        soundManager.stopAllSounds();
        // 清理英雄机的线程
        if (heroAircraft != null) {
            heroAircraft.cleanup();
        }
    }
}