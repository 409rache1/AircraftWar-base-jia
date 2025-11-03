package edu.hitsz.difficulty;

/**
 * 简单难度实现
 */
public class EasyDifficulty extends GameDifficultyTemplate {
    private int enemyMaxCount = 4;
    private int mobEnemyHp = 20;
    private int eliteEnemyHp = 40;
    private int superEliteEnemyHp = 60;
    private int heroShootCycle = 500;
    private int enemyShootCycle = 3000;
    private double eliteEnemyProb = 0.1;
    private double superEliteEnemyProb = 0.05;
    private int bossScoreThreshold = Integer.MAX_VALUE; // 无Boss
    private int bossHp = 0;
    private int cycleDuration = 800;

    @Override
    protected void setEnemyMaxCount() {
        System.out.println("简单模式：敌机最大数量 = " + enemyMaxCount);
    }

    @Override
    protected void setEnemyAttributes() {
        System.out.println("简单模式：普通敌机血量 = " + mobEnemyHp +
                ", 精英敌机血量 = " + eliteEnemyHp +
                ", 超级精英敌机血量 = " + superEliteEnemyHp);
    }

    @Override
    protected void setShootingCycle() {

    }

    @Override
    protected void setEliteEnemySettings() {

    }

    @Override
    protected void setBossEnemySettings() {
        System.out.println("简单模式：无Boss敌机");
    }

    @Override
    protected void setDifficultyIncrease() {
        System.out.println("简单模式：难度不随时间增加");
    }

    @Override
    public boolean hasBossEnemy() {
        return false;
    }

    @Override
    protected boolean canIncreaseDifficulty() {
        return false;
    }

    @Override
    public String getDifficultyName() {
        return "简单模式";
    }

    // 实现getter方法
    @Override public int getEnemyMaxCount() { return enemyMaxCount; }
    @Override public int getMobEnemyHp() { return mobEnemyHp; }
    @Override public int getEliteEnemyHp() { return eliteEnemyHp; }
    @Override public int getSuperEliteEnemyHp() { return superEliteEnemyHp; }
    @Override public int getHeroShootCycle() { return heroShootCycle; }
    @Override public int getEnemyShootCycle() { return enemyShootCycle; }
    @Override public double getEliteEnemyProb() { return eliteEnemyProb; }
    @Override public double getSuperEliteEnemyProb() { return superEliteEnemyProb; }
    @Override public int getBossScoreThreshold() { return bossScoreThreshold; }
    @Override public int getBossHp() { return bossHp; }
    @Override public int getCycleDuration() { return cycleDuration; }

    @Override
    public void increaseDifficulty(int gameTime) {
        // 简单模式不随时间增加难度
    }
}