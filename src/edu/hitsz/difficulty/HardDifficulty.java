package edu.hitsz.difficulty;

/**
 * 困难难度实现
 */
public class HardDifficulty extends GameDifficultyTemplate {
    private int enemyMaxCount = 6;
    private int mobEnemyHp = 60;
    private int eliteEnemyHp = 90;
    private int superEliteEnemyHp = 120;
    private int heroShootCycle = 300;
    private int enemyShootCycle = 1500;
    private double eliteEnemyProb = 0.3;
    private double superEliteEnemyProb = 0.15;
    private int bossScoreThreshold = 200;
    private int bossHp = 300;
    private int bossHpIncrease = 50; // 每次召唤Boss血量增加
    private int cycleDuration = 400;

    // 难度增加相关变量
    private int difficultyIncreaseInterval = 20; // 每20秒增加一次难度
    private int lastDifficultyIncreaseTime = 0;
    private int bossSpawnCount = 0;

    @Override
    protected void setEnemyMaxCount() {
        System.out.println("困难模式：敌机最大数量 = " + enemyMaxCount);
    }

    @Override
    protected void setEnemyAttributes() {
        System.out.println("困难模式：普通敌机血量 = " + mobEnemyHp +
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
        System.out.println("困难模式：Boss得分阈值 = " + bossScoreThreshold +
                ", Boss初始血量 = " + bossHp +
                ", 每次召唤血量增加 = " + bossHpIncrease);
    }

    @Override
    protected void setDifficultyIncrease() {
        System.out.println("困难模式：难度随时间显著增加（每" + difficultyIncreaseInterval + "秒）");
    }

    @Override
    protected int getBossHpIncrease() {
        return bossHpIncrease;
    }

    @Override
    public String getDifficultyName() {
        return "困难模式";
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
    @Override public int getBossHp() {
        // 每次召唤Boss时血量增加
        return bossHp + (bossSpawnCount * bossHpIncrease);
    }
    @Override public int getCycleDuration() { return cycleDuration; }

    public void incrementBossSpawnCount() {
        bossSpawnCount++;
    }

    @Override
    public void increaseDifficulty(int gameTime) {
        if (gameTime - lastDifficultyIncreaseTime >= difficultyIncreaseInterval) {
            lastDifficultyIncreaseTime = gameTime;

            // 显著增加敌机属性
            mobEnemyHp += 10;
            eliteEnemyHp += 20;
            superEliteEnemyHp += 30;

            // 显著加快敌机射击速度
            if (enemyShootCycle > 800) {
                enemyShootCycle -= 150;
            }

            // 显著增加精英敌机概率
            if (eliteEnemyProb < 0.6) {
                eliteEnemyProb += 0.08;
            }
            if (superEliteEnemyProb < 0.3) {
                superEliteEnemyProb += 0.05;
            }

            // 增加敌机最大数量
            if (enemyMaxCount < 15) {
                enemyMaxCount += 1;
            }

            System.out.println("困难模式难度大幅提升！当前游戏时间: " + gameTime + "秒");
            System.out.println("敌机血量大幅提升，射击速度显著加快，精英敌机概率大幅增加，敌机数量增加");
        }
    }
}