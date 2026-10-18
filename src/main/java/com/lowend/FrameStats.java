package com.lowend;

import java.util.Arrays;

/** Rolling frame-time stats: average, FPS, and 1% low (worst frames). */
public final class FrameStats {
    private static final int N = 240;
    private final long[] times = new long[N];
    private int idx = 0;
    private int count = 0;
    private long last = 0;

    public double avgMs, fps, low1PctFps;
    public double lastMs;
    private long lastRecalc = 0;

    public void frame() {
        long now = System.nanoTime();
        if (last != 0) {
            lastMs = (now - last) / 1_000_000.0;
            times[idx] = now - last;
            idx = (idx + 1) % N;
            if (count < N) count++;
        }
        last = now;

        // recompute a few times per second so the numbers are readable
        if (now - lastRecalc > 250_000_000L && count > 10) {
            lastRecalc = now;
            recalc();
        }
    }

    private void recalc() {
        long[] copy = Arrays.copyOf(times, count);
        long sum = 0;
        for (long t : copy) sum += t;
        double avgNs = (double) sum / count;
        avgMs = avgNs / 1_000_000.0;
        fps = 1_000_000_000.0 / avgNs;

        Arrays.sort(copy);
        int from = Math.max(0, (int) (count * 0.99));
        long worstSum = 0;
        int n = 0;
        for (int i = from; i < count; i++) { worstSum += copy[i]; n++; }
        if (n == 0) { worstSum = copy[count - 1]; n = 1; }
        low1PctFps = 1_000_000_000.0 / ((double) worstSum / n);
    }
}
