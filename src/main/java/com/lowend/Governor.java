package com.lowend;

/**
 * Frame-time governor. Watches a smoothed frame time against the target and moves between
 * quality levels 0..4. Goes UP fast when frames are slow, comes DOWN slowly when frames are fast.
 * Other modules read the scale values below; nothing here touches OpenGL.
 */
public final class Governor {
    public static final int MAX_LEVEL = 4;

    private static final double[] DIST_SCALE = {1.00, 0.85, 0.70, 0.55, 0.45};
    private static final int[] PARTICLE_KEEP_OF_10 = {10, 8, 6, 4, 2};
    private static final int[] ANIM_DIVIDER = {1, 1, 2, 2, 4};

    public static volatile int level = 0;

    private static double emaMs = 0;
    private static long overSince = 0;
    private static long underSince = 0;
    private static long lastChange = 0;

    private Governor() {}

    /** Call once per rendered frame with that frame's time in ms. */
    public static void update(double frameMs) {
        if (!LowEndConfig.governor) {
            level = 0;
            return;
        }
        if (frameMs <= 0 || frameMs > 500) return; // ignore loading hitches and pauses

        emaMs = (emaMs == 0) ? frameMs : emaMs * 0.95 + frameMs * 0.05;
        double target = 1000.0 / LowEndConfig.targetFps;
        long now = System.nanoTime();

        if (emaMs > target * 1.15) {
            underSince = 0;
            if (overSince == 0) overSince = now;
            if (now - overSince > 1_500_000_000L && now - lastChange > 2_000_000_000L && level < MAX_LEVEL) {
                level++;
                lastChange = now;
                overSince = 0;
            }
        } else if (emaMs < target * 0.85) {
            overSince = 0;
            if (underSince == 0) underSince = now;
            if (now - underSince > 6_000_000_000L && now - lastChange > 2_000_000_000L && level > 0) {
                level--;
                lastChange = now;
                underSince = 0;
            }
        } else {
            overSince = 0;
            underSince = 0;
        }
    }

    public static double distScale() {
        return DIST_SCALE[level];
    }

    /** True if this particle should be dropped right now. n is any increasing counter. */
    public static boolean dropParticle(int n) {
        if (!LowEndConfig.particleThrottle) return false;
        int keep = PARTICLE_KEEP_OF_10[level];
        return keep < 10 && (n % 10) >= keep;
    }

    public static int animDivider() {
        return LowEndConfig.smartAnimations ? ANIM_DIVIDER[level] : 1;
    }
}
