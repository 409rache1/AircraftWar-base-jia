package edu.hitsz.application;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 音效管理器
 */
public class SoundManager {
    private static SoundManager instance;
    private Map<String, MusicThread> soundThreads;
    private boolean soundEnabled = true;

    // 音效类型常量
    public static final String BGM = "bgm";
    public static final String BGM_BOSS = "bgm_boss";
    public static final String BULLET_HIT = "bullet_hit";
    public static final String BULLET_SHOOT = "bullet_shoot";
    public static final String BOMB_EXPLOSION = "bomb_explosion";
    public static final String GET_SUPPLY = "get_supply";
    public static final String GAME_OVER = "game_over";

    private SoundManager() {
        soundThreads = new ConcurrentHashMap<>(); // 使用线程安全的Map
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            synchronized (SoundManager.class) {
                if (instance == null) {
                    instance = new SoundManager();
                }
            }
        }
        return instance;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        //System.out.println("音效设置: " + (enabled ? "开启" : "关闭"));
        if (!enabled) {
            stopAllSounds();
        }
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * 检查音频文件是否存在
     */
    private boolean checkAudioFile(String filePath) {
        File file = new File(filePath);
        boolean exists = file.exists();
        return exists;
    }

    /**
     * 播放音效（一次性）
     */
    public void playSound(String soundType, String filePath) {
        if (!soundEnabled) {
            return;
        }

        // 检查文件是否存在
        if (!checkAudioFile(filePath)) {
            return;
        }

        // 如果是已经播放的音效，先停止
        stopSound(soundType);

        try {
            MusicThread thread = new MusicThread(filePath);
            soundThreads.put(soundType, thread);
            thread.start();
            //System.out.println("开始播放音效: " + soundType);
        } catch (Exception e) {
            //System.err.println("播放音效失败: " + soundType + ", 文件: " + filePath);
            e.printStackTrace();
            soundThreads.remove(soundType); // 发生异常时移除
        }
    }

    /**
     * 播放背景音乐（循环）
     */
    public void playBackgroundMusic(String filePath) {
        if (!soundEnabled) {
            return;
        }

        // 检查文件是否存在
        if (!checkAudioFile(filePath)) {
            return;
        }

        stopBackgroundMusic();

        try {
            MusicThread bgmThread = new MusicThread(filePath);
            bgmThread.setLooping(true);
            soundThreads.put(BGM, bgmThread);
            bgmThread.start();
            //System.out.println("开始播放背景音乐: " + filePath);
        } catch (Exception e) {
            //System.err.println("播放背景音乐失败: " + filePath);
            e.printStackTrace();
            soundThreads.remove(BGM); // 发生异常时移除
        }
    }

    /**
     * 播放Boss背景音乐（循环）
     */
    public void playBossMusic(String filePath) {
        if (!soundEnabled) {
            return;
        }

        // 检查文件是否存在
        if (!checkAudioFile(filePath)) {
            return;
        }

        stopBackgroundMusic();

        try {
            MusicThread bossMusicThread = new MusicThread(filePath);
            bossMusicThread.setLooping(true);
            soundThreads.put(BGM_BOSS, bossMusicThread);
            bossMusicThread.start();
            //System.out.println("开始播放Boss音乐: " + filePath);
        } catch (Exception e) {
            //System.err.println("播放Boss音乐失败: " + filePath);
            e.printStackTrace();
            soundThreads.remove(BGM_BOSS); // 发生异常时移除
        }
    }

    /**
     * 停止指定音效
     */
    public void stopSound(String soundType) {
        try {
            MusicThread thread = soundThreads.get(soundType);
            if (thread != null) {
                thread.stopMusic();
                soundThreads.remove(soundType);
                //System.out.println("停止音效: " + soundType);
            }
        } catch (Exception e) {
            //System.err.println("停止音效时发生错误: " + soundType + ", " + e.getMessage());
            soundThreads.remove(soundType); // 发生异常时强制移除
        }
    }

    /**
     * 停止背景音乐
     */
    public void stopBackgroundMusic() {
        stopSound(BGM);
    }

    /**
     * 停止Boss音乐
     */
    public void stopBossMusic() {
        stopSound(BGM_BOSS);
    }

    /**
     * 停止所有音效
     */
    public void stopAllSounds() {
        try {
            // 创建副本避免并发修改
            Map<String, MusicThread> threadsCopy = new HashMap<>(soundThreads);
            for (Map.Entry<String, MusicThread> entry : threadsCopy.entrySet()) {
                String soundType = entry.getKey();
                MusicThread thread = entry.getValue();
                if (thread != null) {
                    try {
                        thread.stopMusic();
                    } catch (Exception e) {
                        System.err.println("停止音效 " + soundType + " 时发生错误: " + e.getMessage());
                    }
                }
            }
            soundThreads.clear();
            //System.out.println("停止所有音效");
        } catch (Exception e) {
            //System.err.println("停止所有音效时发生错误: " + e.getMessage());
            soundThreads.clear(); // 发生异常时强制清空
        }
    }

    /**
     * 恢复背景音乐（在Boss被击败后）
     */
    public void resumeBackgroundMusic(String filePath) {
        if (!soundEnabled) {
            return;
        }

        stopBossMusic();

        // 如果普通背景音乐没有在播放，重新播放
        if (!soundThreads.containsKey(BGM)) {
            playBackgroundMusic(filePath);
        }
    }
}