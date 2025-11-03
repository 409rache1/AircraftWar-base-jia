package edu.hitsz.difficulty;

/**
 * 游戏难度模板抽象类
 * 定义游戏难度设置的算法骨架
 */
public abstract class GameDifficultyTemplate {
    // 模板方法 - 定义游戏难度设置的算法骨架
    public final void setupDifficulty() {
        setEnemyMaxCount();
        setEnemyAttributes();
        setShootingCycle();
        setEliteEnemySettings();
        setBossEnemySettings();
        setDifficultyIncrease();
        commonSetup();
    }

    // 具体方法 - 所有难度共有的设置
    protected void commonSetup() {
    }

    // 抽象方法 - 由子类实现
    protected abstract void setEnemyMaxCount();
    protected abstract void setEnemyAttributes();
    protected abstract void setShootingCycle();
    protected abstract void setEliteEnemySettings();
    protected abstract void setBossEnemySettings();
    protected abstract void setDifficultyIncrease();

    // 钩子方法 - 子类可以选择性重写
    public boolean hasBossEnemy() {
        return true;
    }

    protected boolean canIncreaseDifficulty() {
        return true;
    }

    protected int getBossHpIncrease() {
        return 0;
    }

    // 获取难度名称
    public abstract String getDifficultyName();

    // 获取难度参数的方法
    public abstract int getEnemyMaxCount();
    public abstract int getMobEnemyHp();
    public abstract int getEliteEnemyHp();
    public abstract int getSuperEliteEnemyHp();
    public abstract int getHeroShootCycle();
    public abstract int getEnemyShootCycle();
    public abstract double getEliteEnemyProb();
    public abstract double getSuperEliteEnemyProb();
    public abstract int getBossScoreThreshold();
    public abstract int getBossHp();
    public abstract int getCycleDuration();

    /**
     * 随时间增加难度的方法
     * @param gameTime 游戏时间（秒）
     */
    public abstract void increaseDifficulty(int gameTime);
}