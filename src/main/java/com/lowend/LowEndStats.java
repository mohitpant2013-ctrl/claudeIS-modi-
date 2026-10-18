package com.lowend;

/** Per-frame counters shown in the overlay. Render thread only. */
public final class LowEndStats {
    public static int culledEntities;
    public static int culledBlockEntities;
    public static int shownEntities;
    public static int shownBlockEntities;

    /** Increments once per client tick; used so all animated sprites skip the same ticks. */
    public static volatile int clientTick = 0;

    private LowEndStats() {}

    public static void endFrame() {
        shownEntities = culledEntities;
        shownBlockEntities = culledBlockEntities;
        culledEntities = 0;
        culledBlockEntities = 0;
    }
}
