package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.factory.*;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.observer.BombExplosionEvent;
import edu.hitsz.prop.AbstractProp;
import edu.hitsz.strategy.CircularShootStrategy;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BossEnemy extends AbstractAircraft implements BombObserver {
    private int power = 30;
    private int shootNum = 20;
    private int direction = 1;
    private int moveSpeed = 3;

    // 移动边界
    private int leftBound = 50;  // 左边界留白
    private int rightBound;      // 右边界将在构造函数中设置

    public BossEnemy(int locationX, int locationY, int speedX, int speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp);
        this.shootStrategy = new CircularShootStrategy(shootNum, 5, power, false);

        // 设置右边界（屏幕宽度减去留白）
        this.rightBound = Main.WINDOW_WIDTH - 10;

        // 确保Boss敌机悬浮（垂直速度为0）
        this.speedY = 0;
        // 设置水平移动速度
        this.speedX = moveSpeed;
        // 初始向右移动
        this.direction = 1;

        // 确保初始位置在屏幕上方
        if (locationY > 150) {
            this.locationY = 150;
        }
    }

    @Override
    public void forward() {
        // 只在水平方向移动
        locationX += speedX * direction;

        // 边界检测，碰到边界则反弹
        if (locationX <= leftBound) {
            locationX = leftBound;
            direction = 1; // 向右移动
        } else if (locationX >= rightBound) {
            locationX = rightBound;
            direction = -1; // 向左移动
        }

        // 确保Boss敌机不会下降
        if (locationY > 150) {
            locationY = 150;
        }
    }

    @Override
    protected List<BaseBullet> directShoot() {
        // 保留原有的射击逻辑作为后备
        List<BaseBullet> res = new LinkedList<>();
        int x = this.getLocationX();
        int y = this.getLocationY() + direction * 2;

        for (int i = 0; i < shootNum; i++) {
            double angle = 2 * Math.PI * i / shootNum;
            int speedX = (int) (5 * Math.cos(angle));
            int speedY = (int) (5 * Math.sin(angle)) + 2;

            BaseBullet bullet = new EnemyBullet(x, y, speedX, speedY, power);
            res.add(bullet);
        }
        return res;
    }

    @Override
    public List<AbstractProp> dropProps() {
        List<AbstractProp> props = new LinkedList<>();
        Random random = new Random();

        // Boss敌机掉落0-3个道具
        int dropCount = random.nextInt(4);
        for (int i = 0; i < dropCount; i++) {
            int propType = random.nextInt(4); // 0-3，现在有4种道具
            PropFactory factory;

            switch (propType) {
                case 0:
                    factory = new BloodPropFactory();
                    break;
                case 1:
                    factory = new BombPropFactory();
                    break;
                case 2:
                    factory = new BulletPropFactory(); // 普通火力道具
                    break;
                case 3:
                    factory = new SuperBulletPropFactory(); // 超级火力道具
                    break;
                default:
                    factory = new BloodPropFactory();
            }

            int offsetX = random.nextInt(41) - 20;
            props.add(factory.createProp(locationX + offsetX, locationY));
        }

        return props;
    }

    /**
     * 设置移动边界
     */
    public void setMoveBounds(int left, int right) {
        this.leftBound = left;
        this.rightBound = right;
    }

    /**
     * 重写父类的getWidth方法，返回Boss敌机的宽度
     * 必须保持public访问权限
     */
    @Override
    public int getWidth() {
        // 这里需要根据实际图像返回宽度
        // 如果使用ImageManager，可以这样获取：
        return ImageManager.BOSS_ENEMY_IMAGE.getWidth();
    }

    /**
     * 炸弹爆炸事件处理 - Boss敌机免疫炸弹效果
     */
    @Override
    public void onBombExplode(BombExplosionEvent event) {
        // Boss敌机不受炸弹影响
    }
}