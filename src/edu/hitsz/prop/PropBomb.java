package edu.hitsz.prop;

import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.MobEnemy;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.SuperEliteEnemy;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.application.SoundManager;
import edu.hitsz.observer.BombObserver;
import edu.hitsz.observer.BombSubject;
import edu.hitsz.observer.BombExplosionEvent;
import edu.hitsz.observer.ScoreSubject;

import java.util.List;
import java.util.ArrayList;

public class PropBomb extends AbstractProp {
    private BombSubject bombSubject;

    // 爆炸音效文件路径
    private static final String SOUND_BOMB_EXPLOSION = "src/videos/bomb_explosion.wav";

    // 爆炸范围配置
    private static final int EXPLOSION_RADIUS = 1000;

    // 超级精英敌机血量减少值
    private static final int SUPER_ELITE_HP_REDUCTION = 30;

    public PropBomb(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
        this.bombSubject = BombSubject.getInstance();
    }

    @Override
    public void effect(AbstractAircraft aircraft) {

        // 播放爆炸音效
        playBombSound();

        // 在爆炸前获取所有观察者并诊断问题
        List<BombObserver> observers = bombSubject.getObservers();
        diagnoseObservers(observers);

        // 创建爆炸事件，传入爆炸范围
        BombExplosionEvent event = new BombExplosionEvent(
                this.getLocationX(), this.getLocationY(), EXPLOSION_RADIUS);

        // 处理不同类型的观察者并计算分数
        int totalScore = processObserversAndCalculateScore(observers, event);

        // 触发分数事件
        if (totalScore > 0) {
            ScoreSubject.triggerScoreEvent(totalScore, "炸弹清除敌机");
        }

        // 爆炸后清除所有观察者，为下一次爆炸做准备
        bombSubject.clearObservers();

        this.vanish();
    }

    /**
     * 诊断观察者列表，找出异常情况
     */
    private void diagnoseObservers(List<BombObserver> observers) {


        int aircraftCount = 0;
        int bulletCount = 0;
        int mobCount = 0;
        int eliteCount = 0;
        int superEliteCount = 0;
        int bossCount = 0;
        int heroCount = 0;
        int otherCount = 0;

        // 统计不同类型的观察者
        for (BombObserver observer : observers) {
            if (observer instanceof AbstractAircraft) {
                aircraftCount++;
                AbstractAircraft aircraft = (AbstractAircraft) observer;

                if (aircraft instanceof HeroAircraft) {
                    heroCount++;
                } else if (aircraft instanceof BossEnemy) {
                    bossCount++;
                } else if (aircraft instanceof SuperEliteEnemy) {
                    superEliteCount++;
                } else if (aircraft instanceof EliteEnemy) {
                    eliteCount++;
                } else if (aircraft instanceof MobEnemy) {
                    mobCount++;
                } else {
                    otherCount++;
                }
            } else if (observer instanceof BaseBullet) {
                bulletCount++;
            } else {
                otherCount++;
            }
        }

    }

    /**
     * 播放炸弹爆炸音效
     */
    private void playBombSound() {
        try {
            SoundManager soundManager = SoundManager.getInstance();
            if (soundManager.isSoundEnabled()) {
                soundManager.playSound(SoundManager.BOMB_EXPLOSION, SOUND_BOMB_EXPLOSION);
                System.out.println("播放炸弹爆炸音效");
            }
        } catch (Exception e) {
            System.err.println("播放炸弹爆炸音效失败: " + e.getMessage());
        }
    }

    /**
     * 处理不同类型的观察者并计算分数
     * @return 获得的总分数
     */
    private int processObserversAndCalculateScore(List<BombObserver> observers, BombExplosionEvent event) {
        int totalScore = 0;
        int mobEnemyCount = 0;
        int eliteEnemyCount = 0;
        int superEliteEnemyCount = 0;
        int bossEnemyCount = 0;
        int enemyBulletCount = 0;
        int heroBulletCount = 0;
        List<AbstractAircraft> destroyedSuperElites = new ArrayList<>();

        for (BombObserver observer : observers) {
            // 处理子弹
            if (observer instanceof BaseBullet) {
                BaseBullet bullet = (BaseBullet) observer;
                if (bullet instanceof EnemyBullet) {
                    enemyBulletCount++;
                    bullet.vanish(); // 清除敌机子弹
                } else if (bullet instanceof HeroBullet) {
                    heroBulletCount++;
                    // 英雄机子弹不清除
                }
                continue;
            }

            // 处理敌机
            if (observer instanceof AbstractAircraft) {
                AbstractAircraft aircraft = (AbstractAircraft) observer;

                // 排除英雄机
                if (aircraft instanceof HeroAircraft) {
                    continue;
                }

                // 使用 notValid() 方法检查对象是否已经无效
                if (aircraft.notValid()) {
                    continue;
                }

                if (aircraft instanceof BossEnemy) {
                    // Boss敌机不受影响
                    bossEnemyCount++;
                } else if (aircraft instanceof SuperEliteEnemy) {
                    // 超级精英敌机血量减少
                    superEliteEnemyCount++;
                    int originalHp = aircraft.getHp();
                    aircraft.decreaseHp(SUPER_ELITE_HP_REDUCTION);

                    // 检查是否被击毁（使用 notValid() 方法）
                    if (aircraft.notValid()) {
                        destroyedSuperElites.add(aircraft);
                    }
                } else if (aircraft instanceof EliteEnemy) {
                    // 精英敌机被清除
                    eliteEnemyCount++;
                    aircraft.vanish();
                    totalScore += 50; // 精英给50分
                } else if (aircraft instanceof MobEnemy) {
                    // 普通敌机被清除
                    mobEnemyCount++;
                    aircraft.vanish();
                    totalScore += 10; // 普通敌机给10分
                } else {
                    aircraft.vanish();
                    totalScore += 10;
                }
            }
        }

        // 处理被击毁的超级精英敌机
        for (AbstractAircraft superElite : destroyedSuperElites) {
            totalScore += 100; // 超级精英被击毁给100分
        }


        return totalScore;
    }
}