package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.ShootStrategy;
import edu.hitsz.strategy.StraightShootStrategy;
import edu.hitsz.strategy.ScatteredShootStrategy;
import edu.hitsz.strategy.CircularShootStrategy;
import java.util.LinkedList;
import java.util.List;

public class HeroAircraft extends AbstractAircraft {
    private int shootNum = 1;
    private int power = 30;
    private int direction = -1;

    private static HeroAircraft instance;

    // 保存初始状态
    private final int initialLocationX;
    private final int initialLocationY;
    private final int initialSpeedX;
    private final int initialSpeedY;
    private final int initialHp;
    private final ShootStrategy initialShootStrategy;

    // 多线程控制相关字段
    private Thread powerUpThread;
    private volatile boolean isPowerUpActive = false;
    private static final int POWER_UP_DURATION = 3000; // 3秒持续时间

    //默认设置为直射
    private HeroAircraft(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);

        // 保存初始状态
        this.initialLocationX = locationX;
        this.initialLocationY = locationY;
        this.initialSpeedX = speedX;
        this.initialSpeedY = speedY;
        this.initialHp = hp;
        this.initialShootStrategy = new StraightShootStrategy(shootNum, 10, power, true);

        this.shootStrategy = initialShootStrategy;
    }

    public static HeroAircraft getInstance(int locationX, int locationY, int speedX, int speedY, int hp) {
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft(locationX, locationY, speedX, speedY, hp);
                }
            }
        }
        return instance;
    }

    /**
     * 获取单例实例（不创建新实例）
     */
    public static HeroAircraft getInstance() {
        return instance;
    }

    /**
     * 重置英雄机状态到初始状态
     */
    public void reset() {
        System.out.println("重置英雄机状态");
        this.locationX = initialLocationX;
        this.locationY = initialLocationY;
        this.speedX = initialSpeedX;
        this.speedY = initialSpeedY;
        this.hp = initialHp;
        this.shootStrategy = initialShootStrategy;

        // 清理线程
        if (powerUpThread != null && powerUpThread.isAlive()) {
            powerUpThread.interrupt();
        }
        isPowerUpActive = false;

        System.out.println("英雄机重置完成，HP: " + this.hp + ", 位置: (" + this.locationX + ", " + this.locationY + ")");
    }

    /**
     * 重置英雄机状态到指定值
     */
    public void reset(int locationX, int locationY, int hp) {
        System.out.println("重置英雄机状态到指定值");
        this.locationX = locationX;
        this.locationY = locationY;
        this.hp = hp;
        this.speedX = 0;
        this.speedY = 0;
        this.shootStrategy = initialShootStrategy;

        // 清理线程
        if (powerUpThread != null && powerUpThread.isAlive()) {
            powerUpThread.interrupt();
        }
        isPowerUpActive = false;

        System.out.println("英雄机重置完成，HP: " + this.hp + ", 位置: (" + this.locationX + ", " + this.locationY + ")");
    }

    /**
     * 使用多线程激活道具效果
     */
    public void activateProp(String propType) {
        // 如果已有道具效果在运行，先中断
        if (isPowerUpActive && powerUpThread != null && powerUpThread.isAlive()) {
            powerUpThread.interrupt();
        }

        // 创建新线程处理道具效果
        powerUpThread = new Thread(() -> {
            try {
                isPowerUpActive = true;

                switch(propType) {
                    case "PropBullet": // 普通火力道具
                        setShootStrategy(new ScatteredShootStrategy(3, 8, power, true));
                        break;
                    case "SuperFireProp": // 超级火力道具
                        setShootStrategy(new CircularShootStrategy(20, 6, power, true));
                        break;
                    default:
                        setShootStrategy(new StraightShootStrategy(shootNum, 10, power, true));
                }

                // 显示视觉反馈
                showPowerUpEffect(propType);

                // 持续指定时间
                Thread.sleep(POWER_UP_DURATION);

            } catch (InterruptedException e) {
                // 被新道具中断，直接退出
                Thread.currentThread().interrupt();
            } finally {
                // 确保最终恢复默认射击模式
                if (!Thread.currentThread().isInterrupted()) {
                    setShootStrategy(initialShootStrategy);
                    isPowerUpActive = false;
                    hidePowerUpEffect();
                }
            }
        });

        powerUpThread.start();
    }

    /**
     * 显示火力道具生效的视觉效果
     */
    private void showPowerUpEffect(String propType) {
        // 这里可以添加视觉效果，比如：
        // - 英雄机发光
        // - 改变英雄机颜色
        // - 显示特效动画
    }

    /**
     * 隐藏火力道具的视觉效果
     */
    private void hidePowerUpEffect() {
        // 恢复英雄机正常外观
    }

    /**
     * 检查是否有道具效果激活
     */
    public boolean isPowerUpActive() {
        return isPowerUpActive;
    }

    /**
     * 游戏结束时清理线程
     */
    public void cleanup() {
        if (powerUpThread != null && powerUpThread.isAlive()) {
            powerUpThread.interrupt();
        }
        isPowerUpActive = false;
    }

    @Override
    public void forward() {
        // 英雄机由鼠标控制，不通过forward函数移动
    }

    @Override
    protected List<BaseBullet> directShoot() {
        // 保留原有的射击逻辑作为后备
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;
        int speedX = 0;
        int speedY = this.getSpeedY() + direction * 5;
        BaseBullet bullet;
        for(int i = 0; i < shootNum; i++){
            bullet = new HeroBullet(x + (i * 2 - shootNum + 1) * 10, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }

    @Override
    public List<AbstractProp> dropProps() {
        return new LinkedList<>();
    }
}