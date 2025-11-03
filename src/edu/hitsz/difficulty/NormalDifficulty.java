package edu.hitsz.difficulty;

/**
 * 普通难度实现
 */
public class NormalDifficulty extends GameDifficultyTemplate {
    private int enemyMaxCount = 5;
    private int mobEnemyHp = 30;
    private int eliteEnemyHp = 60;
    private int superEliteEnemyHp = 90;
    private int heroShootCycle = 400;
    private int enemyShootCycle = 2000;
    private double eliteEnemyProb = 0.2;
    private double superEliteEnemyProb = 0.1;
    private int bossScoreThreshold = 300;
    private int bossHp = 150;
    private int cycleDuration = 600;

    // 难度增加相关变量
    private int difficultyIncreaseInterval = 30; // 每30秒增加一次难度
    private int lastDifficultyIncreaseTime = 0;

    @Override
    protected void setEnemyMaxCount() {
        System.out.println("普通模式：敌机最大数量 = " + enemyMaxCount);
    }

    @Override
    protected void setEnemyAttributes() {
        System.out.println("普通模式：普通敌机血量 = " + mobEnemyHp +
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
        System.out.println("普通模式：Boss得分阈值 = " + bossScoreThreshold +
                ", Boss血量 = " + bossHp);
    }

    @Override
    protected void setDifficultyIncrease() {
        System.out.println("普通模式：难度随时间增加（每" + difficultyIncreaseInterval + "秒）");
    }

    @Override
    public String getDifficultyName() {
        return "普通模式";
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
        if (gameTime - lastDifficultyIncreaseTime >= difficultyIncreaseInterval) {
            lastDifficultyIncreaseTime = gameTime;

            // 增加敌机属性
            mobEnemyHp += 5;
            eliteEnemyHp += 10;
            superEliteEnemyHp += 15;

            // 加快敌机射击速度
            if (enemyShootCycle > 1000) {
                enemyShootCycle -= 100;
            }

            // 增加精英敌机概率
            if (eliteEnemyProb < 0.4) {
                eliteEnemyProb += 0.05;
            }
            if (superEliteEnemyProb < 0.2) {
                superEliteEnemyProb += 0.02;
            }

            System.out.println("普通模式难度提升！当前游戏时间: " + gameTime + "秒");
            System.out.println("敌机血量提升，射击速度加快，精英敌机概率增加");
        }
    }
}