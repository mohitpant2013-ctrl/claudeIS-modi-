package com.lowend;

/**
 * GPU-vs-CPU bottleneck probe (F9). Calls glFinish() once per frame and measures how long the CPU
 * has to wait for the GPU to catch up. Large wait = GPU-bound; tiny wait = CPU-bound.
 * It slightly distorts timing while on, so use it only to measure, then turn it off.
 */
public final class Bottleneck {
    public static boolean on = false;
    private static double waitEma = 0;
    private static double frameEma = 0;

    private Bottleneck() {}

    public static void add(double waitMs, double frameMs) {
        if (frameMs <= 0 || frameMs > 500) return;
        waitEma = waitEma == 0 ? waitMs : waitEma * 0.95 + waitMs * 0.05;
        frameEma = frameEma == 0 ? frameMs : frameEma * 0.95 + frameMs * 0.05;
    }

    public static String text() {
        if (frameEma <= 0) return "probe: collecting...";
        double share = waitEma / frameEma;
        String verdict = share > 0.45 ? "GPU-bound (resolution scale / LOD should help)"
                : share < 0.20 ? "CPU-bound (culling / chunk loading changes should help)"
                : "mixed";
        return String.format("probe: GPU wait %.1f of %.1f ms (%.0f%%) -> %s", waitEma, frameEma, share * 100, verdict);
    }
}
