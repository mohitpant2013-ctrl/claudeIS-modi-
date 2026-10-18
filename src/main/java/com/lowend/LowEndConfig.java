package com.lowend;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Config file: .minecraft/config/lowend.properties (created on first launch). */
public final class LowEndConfig {
    public static boolean overlay = true;

    // --- culling ---
    public static boolean entityCulling = true;
    public static int entityDistance = 48;        // mobs / animals (blocks)
    public static int otherEntityDistance = 32;   // items, xp orbs, misc (blocks)
    public static boolean blockEntityCulling = true;
    public static int blockEntityDistance = 32;   // chests, signs, banners (blocks)

    // --- governor (auto quality) ---
    public static boolean governor = true;
    public static int targetFps = 50;             // governor lowers quality below this

    // --- governor-driven modules ---
    public static boolean particleThrottle = true;
    public static boolean smartAnimations = true;

    // --- extra cheap wins ---
    public static boolean forceSimpleLighting = true; // no smooth lighting / AO (cheaper chunk rebuilds)
    public static int cloudsOffLevel = 2;             // governor level at which clouds stop drawing (5 = never)
    public static int weatherOffLevel = 3;            // governor level at which rain/snow stops drawing (5 = never)

    // --- chunk loading ---
    public static int chunkBuilderThreads = 2;        // max chunk-mesh worker threads (0 = vanilla default)

    // --- experimental (visual change, default OFF) ---
    public static boolean leafCulling = false;        // hide faces between touching leaf blocks (hollow canopy interior)

    private LowEndConfig() {}

    public static void load() {
        Path file = FabricLoader.getInstance().getConfigDir().resolve("lowend.properties");
        Properties p = new Properties();
        if (Files.exists(file)) {
            try (InputStream in = Files.newInputStream(file)) {
                p.load(in);
            } catch (IOException e) {
                LowEndClient.LOGGER.warn("Could not read config, using defaults", e);
            }
        }
        overlay = bool(p, "overlay", overlay);
        entityCulling = bool(p, "entityCulling", entityCulling);
        entityDistance = num(p, "entityDistance", entityDistance, 8, 256);
        otherEntityDistance = num(p, "otherEntityDistance", otherEntityDistance, 8, 256);
        blockEntityCulling = bool(p, "blockEntityCulling", blockEntityCulling);
        blockEntityDistance = num(p, "blockEntityDistance", blockEntityDistance, 8, 256);
        governor = bool(p, "governor", governor);
        targetFps = num(p, "targetFps", targetFps, 15, 240);
        particleThrottle = bool(p, "particleThrottle", particleThrottle);
        smartAnimations = bool(p, "smartAnimations", smartAnimations);
        forceSimpleLighting = bool(p, "forceSimpleLighting", forceSimpleLighting);
        cloudsOffLevel = num(p, "cloudsOffLevel", cloudsOffLevel, 0, 5);
        weatherOffLevel = num(p, "weatherOffLevel", weatherOffLevel, 0, 5);
        chunkBuilderThreads = num(p, "chunkBuilderThreads", chunkBuilderThreads, 0, 8);
        leafCulling = bool(p, "leafCulling", leafCulling);
        save(file);
    }

    private static void save(Path file) {
        Properties p = new Properties();
        p.setProperty("overlay", String.valueOf(overlay));
        p.setProperty("entityCulling", String.valueOf(entityCulling));
        p.setProperty("entityDistance", String.valueOf(entityDistance));
        p.setProperty("otherEntityDistance", String.valueOf(otherEntityDistance));
        p.setProperty("blockEntityCulling", String.valueOf(blockEntityCulling));
        p.setProperty("blockEntityDistance", String.valueOf(blockEntityDistance));
        p.setProperty("governor", String.valueOf(governor));
        p.setProperty("targetFps", String.valueOf(targetFps));
        p.setProperty("particleThrottle", String.valueOf(particleThrottle));
        p.setProperty("smartAnimations", String.valueOf(smartAnimations));
        p.setProperty("forceSimpleLighting", String.valueOf(forceSimpleLighting));
        p.setProperty("cloudsOffLevel", String.valueOf(cloudsOffLevel));
        p.setProperty("weatherOffLevel", String.valueOf(weatherOffLevel));
        p.setProperty("chunkBuilderThreads", String.valueOf(chunkBuilderThreads));
        p.setProperty("leafCulling", String.valueOf(leafCulling));
        try (OutputStream out = Files.newOutputStream(file)) {
            p.store(out, "LowEnd Renderer config (distances are in blocks)");
        } catch (IOException e) {
            LowEndClient.LOGGER.warn("Could not write config", e);
        }
    }

    private static boolean bool(Properties p, String k, boolean def) {
        String v = p.getProperty(k);
        return v == null ? def : Boolean.parseBoolean(v.trim());
    }

    private static int num(Properties p, String k, int def, int min, int max) {
        String v = p.getProperty(k);
        if (v == null) return def;
        try {
            int n = Integer.parseInt(v.trim());
            return Math.max(min, Math.min(max, n));
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
