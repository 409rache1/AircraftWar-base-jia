package edu.hitsz.aircraft;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.prop.AbstractProp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 英雄机类单元测试
 * 采用黑盒测试和白盒测试方法设计测试用例
 * 测试方法：getInstance(), shoot(), decreaseHp() (父类方法)
 * 英雄机初始生命值：1000
 */
class HeroAircraftTest {

    private HeroAircraft heroAircraft;
    private static final int INITIAL_HP = 1000;

    @BeforeEach
    void setUp() throws Exception {
        // 白盒测试：使用反射重置单例实例，确保测试独立性
        resetSingleton();
        // 创建英雄机实例，初始生命值为1000
        heroAircraft = HeroAircraft.getInstance(100, 200, 0, 0, INITIAL_HP);
    }

    @AfterEach
    void tearDown() throws Exception {
        // 清理单例实例
        resetSingleton();
        heroAircraft = null;
    }

    /**
     * 白盒测试：使用反射重置单例实例
     */
    private void resetSingleton() throws Exception {
        Field instanceField = HeroAircraft.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }

    // ============================ 测试方法1: getInstance() ============================

    /**
     * 黑盒测试：测试单例模式的正确性
     * 测试用例：多次调用getInstance应返回相同对象
     */
    @Test
    @DisplayName("黑盒测试-单例模式: 多次调用返回相同实例")
    void getInstance_ShouldReturnSameInstance() {
        // 测试步骤
        HeroAircraft instance1 = HeroAircraft.getInstance(100, 200, 0, 0, INITIAL_HP);
        HeroAircraft instance2 = HeroAircraft.getInstance(150, 250, 5, 5, 1500);

        // 预期结果：两次调用返回同一实例
        assertSame(instance1, instance2, "单例模式应保证实例唯一性");
    }

    /**
     * 白盒测试：测试单例模式的懒加载特性
     * 测试用例：第一次创建后，后续调用应忽略参数
     */
    @Test
    @DisplayName("白盒测试-单例懒加载: 参数只在第一次创建时生效")
    void getInstance_ShouldUseParametersOnlyOnFirstCall() {
        // 第一次创建
        HeroAircraft instance1 = HeroAircraft.getInstance(100, 200, 0, 0, INITIAL_HP);

        // 第二次获取，预期参数被忽略
        HeroAircraft instance2 = HeroAircraft.getInstance(999, 999, 999, 999, 9999);

        // 验证状态与第一次创建时一致
        assertEquals(100, instance2.getLocationX(), "X坐标应保持初始值");
        assertEquals(200, instance2.getLocationY(), "Y坐标应保持初始值");
        assertEquals(INITIAL_HP, instance2.getHp(), "生命值应保持初始值" + INITIAL_HP);
    }

    /**
     * 黑盒测试：边界测试 - 单例实例不为null
     */
    @Test
    @DisplayName("黑盒测试-边界测试: 单例实例不应为null")
    void getInstance_ShouldNotReturnNull() {
        HeroAircraft instance = HeroAircraft.getInstance(100, 200, 0, 0, INITIAL_HP);
        assertNotNull(instance, "单例实例不应为null");
    }

    // ============================ 测试方法2: shoot() ============================

    /**
     * 黑盒测试：测试射击功能的基本行为
     * 测试用例：默认情况下应发射1颗子弹
     */
    @Test
    @DisplayName("黑盒测试-射击功能: 默认发射1颗子弹")
    void shoot_ShouldReturnOneBulletByDefault() {
        // 执行测试
        List<BaseBullet> bullets = heroAircraft.shoot();

        // 验证结果
        assertNotNull(bullets, "子弹列表不应为null");
        assertEquals(1, bullets.size(), "默认情况下应发射1颗子弹");
    }

    /**
     * 黑盒测试：测试子弹类型
     * 测试用例：发射的子弹应为HeroBullet类型
     */
    @Test
    @DisplayName("黑盒测试-子弹类型: 应为HeroBullet")
    void shoot_ShouldReturnHeroBullets() {
        List<BaseBullet> bullets = heroAircraft.shoot();

        assertFalse(bullets.isEmpty(), "子弹列表不应为空");
        assertTrue(bullets.get(0) instanceof HeroBullet,
                "英雄机发射的子弹应为HeroBullet类型");
    }

    /**
     * 白盒测试：测试子弹位置计算逻辑
     * 测试用例：子弹位置应基于英雄机位置正确计算
     */
    @Test
    @DisplayName("白盒测试-子弹位置: 基于英雄机位置计算")
    void shoot_ShouldCalculateBulletPositionCorrectly() {
        // 设置测试数据
        int heroX = heroAircraft.getLocationX();
        int heroY = heroAircraft.getLocationY();

        // 执行测试
        List<BaseBullet> bullets = heroAircraft.shoot();
        BaseBullet bullet = bullets.get(0);

        // 验证内部逻辑：子弹在英雄机前方2个单位
        assertEquals(heroX, getBulletLocationX(bullet),
                "子弹X坐标应与英雄机中心对齐");
        assertEquals(heroY - 2, getBulletLocationY(bullet),
                "子弹Y坐标应在英雄机前方2个单位位置");
    }

    /**
     * 白盒测试：测试多子弹散射逻辑
     * 测试用例：当shootNum>1时，子弹应水平分散
     */
    @Test
    @DisplayName("白盒测试-多子弹散射: 子弹应水平分散排列")
    void shoot_ShouldSpreadBulletsHorizontally() throws Exception {
        // 白盒测试：修改内部状态
        setShootNum(3);

        List<BaseBullet> bullets = heroAircraft.shoot();

        assertEquals(3, bullets.size(), "应发射3颗子弹");

        // 验证内部散射逻辑
        int bullet1X = getBulletLocationX(bullets.get(0));
        int bullet2X = getBulletLocationX(bullets.get(1));
        int bullet3X = getBulletLocationX(bullets.get(2));

        // 子弹应按公式 x + (i*2 - shootNum + 1)*10 分散
        assertTrue(bullet1X < bullet2X && bullet2X < bullet3X,
                "子弹应按从左到右顺序排列");
    }

    /**
     * 黑盒测试：测试子弹列表不为空
     */
    @Test
    @DisplayName("黑盒测试-子弹列表: 不应返回空列表")
    void shoot_ShouldNotReturnEmptyList() {
        List<BaseBullet> bullets = heroAircraft.shoot();
        assertFalse(bullets.isEmpty(), "射击方法不应返回空列表");
    }

    /**
     * 黑盒测试：测试多次射击的稳定性
     */
    @Test
    @DisplayName("黑盒测试-稳定性: 多次射击结果一致")
    void shoot_ShouldBeConsistentAcrossMultipleCalls() {
        List<BaseBullet> bullets1 = heroAircraft.shoot();
        List<BaseBullet> bullets2 = heroAircraft.shoot();

        assertEquals(bullets1.size(), bullets2.size(),
                "多次射击应产生相同数量的子弹");
    }

    // ============================ 测试方法3: decreaseHp() - 父类方法 ============================

    /**
     * 黑盒测试：测试正常生命值减少
     * 测试用例：减少指定数值的生命值
     */
    @Test
    @DisplayName("黑盒测试-生命值减少: 正常减少指定数值")
    void decreaseHp_ShouldReduceHpBySpecifiedAmount() {
        // 初始状态
        int initialHp = heroAircraft.getHp();
        int damage = 300;

        // 执行测试
        heroAircraft.decreaseHp(damage);

        // 验证结果
        assertEquals(initialHp - damage, heroAircraft.getHp(),
                "生命值应减少指定的伤害值");
    }

    /**
     * 黑盒测试：边界测试 - 生命值不会变为负数
     * 测试用例：当伤害值大于当前生命值时，生命值应为0
     */
    @Test
    @DisplayName("黑盒测试-边界测试: 生命值不低于0")
    void decreaseHp_ShouldNotGoBelowZero() {
        // 执行超过生命值的伤害
        heroAircraft.decreaseHp(1500);

        // 验证边界情况
        assertEquals(0, heroAircraft.getHp(),
                "生命值不应低于0，即使伤害值超过当前生命值");
    }

    /**
     * 黑盒测试：等价类划分 - 零伤害
     * 测试用例：伤害值为0时，生命值不变
     */
    @Test
    @DisplayName("黑盒测试-等价类划分: 零伤害不改变生命值")
    void decreaseHp_WithZeroDamage_ShouldNotChangeHp() {
        int initialHp = heroAircraft.getHp();

        heroAircraft.decreaseHp(0);

        assertEquals(initialHp, heroAircraft.getHp(),
                "零伤害不应改变生命值");
    }

    /**
     * 黑盒测试：等价类划分 - 负伤害处理
     */
    @Test
    @DisplayName("黑盒测试-等价类划分: 负伤害处理")
    void decreaseHp_WithNegativeDamage() {
        int initialHp = heroAircraft.getHp();

        // 假设负伤害不被支持，可能抛出异常或忽略
        assertDoesNotThrow(() -> heroAircraft.decreaseHp(-10),
                "负伤害不应导致异常");
    }

    /**
     * 白盒测试：测试生命值状态机
     * 测试用例：多次减少生命值的状态变化
     */
    @Test
    @DisplayName("白盒测试-状态机: 多次减少生命值")
    void decreaseHp_StateMachineTest() {
        // 初始状态
        assertEquals(INITIAL_HP, heroAircraft.getHp(), "初始生命值应为" + INITIAL_HP);

        // 第一次减少
        heroAircraft.decreaseHp(300);
        assertEquals(700, heroAircraft.getHp(), "第一次减少300后应为700");

        // 第二次减少
        heroAircraft.decreaseHp(200);
        assertEquals(500, heroAircraft.getHp(), "第二次减少200后应为500");

        // 第三次减少到边界
        heroAircraft.decreaseHp(600);
        assertEquals(0, heroAircraft.getHp(), "超过生命值时应为0");
    }

    /**
     * 黑盒测试：测试英雄机初始状态
     * 测试用例：验证初始生命值为1000
     */
    @Test
    @DisplayName("黑盒测试-初始状态: 生命值应为1000")
    void testInitialHp() {
        assertEquals(INITIAL_HP, heroAircraft.getHp(),
                "英雄机初始生命值应为" + INITIAL_HP);
    }

    // ============================ 辅助方法 ============================

    /**
     * 白盒测试辅助方法：使用反射设置shootNum字段
     */
    private void setShootNum(int shootNum) throws Exception {
        Field shootNumField = HeroAircraft.class.getDeclaredField("shootNum");
        shootNumField.setAccessible(true);
        shootNumField.set(heroAircraft, shootNum);
    }

    /**
     * 获取子弹的X坐标（兼容不同实现）
     */
    private int getBulletLocationX(BaseBullet bullet) {
        // 尝试不同的方法名来获取位置
        try {
            // 尝试调用getLocationX方法
            return bullet.getLocationX();
        } catch (NoSuchMethodError e) {
            try {
                // 尝试调用getX方法
                java.lang.reflect.Method getXMethod = bullet.getClass().getMethod("getX");
                return (Integer) getXMethod.invoke(bullet);
            } catch (Exception ex) {
                // 如果都没有，使用反射直接访问字段
                try {
                    Field locationXField = bullet.getClass().getDeclaredField("locationX");
                    locationXField.setAccessible(true);
                    return locationXField.getInt(bullet);
                } catch (Exception ex2) {
                    fail("无法获取子弹的X坐标: " + ex2.getMessage());
                    return -1; // 永远不会执行
                }
            }
        }
    }

    /**
     * 获取子弹的Y坐标（兼容不同实现）
     */
    private int getBulletLocationY(BaseBullet bullet) {
        // 尝试不同的方法名来获取位置
        try {
            // 尝试调用getLocationY方法
            return bullet.getLocationY();
        } catch (NoSuchMethodError e) {
            try {
                // 尝试调用getY方法
                java.lang.reflect.Method getYMethod = bullet.getClass().getMethod("getY");
                return (Integer) getYMethod.invoke(bullet);
            } catch (Exception ex) {
                // 如果都没有，使用反射直接访问字段
                try {
                    Field locationYField = bullet.getClass().getDeclaredField("locationY");
                    locationYField.setAccessible(true);
                    return locationYField.getInt(bullet);
                } catch (Exception ex2) {
                    fail("无法获取子弹的Y坐标: " + ex2.getMessage());
                    return -1; // 永远不会执行
                }
            }
        }
    }
}