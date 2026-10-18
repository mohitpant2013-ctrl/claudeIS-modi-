package com.lowend;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** Records which mixin hooks actually ran, so a wrong method name shows up in the log instead of silently doing nothing. */
public final class HookCheck {
    private static final Set<String> FIRED = ConcurrentHashMap.newKeySet();
    private static final String[][] HOOKS = {
            {"entity", "ent"}, {"blockentity", "be"}, {"particles", "part"}, {"animations", "anim"},
            {"lighting", "light"}, {"clouds", "cld"}, {"weather", "wx"}, {"threads", "thr"}, {"leaves", "leaf"}
    };

    private HookCheck() {}

    public static void fired(String id) {
        FIRED.add(id);
    }

    public static String summary() {
        StringBuilder sb = new StringBuilder("hooks:");
        for (String[] h : HOOKS) {
            sb.append(' ').append(h[1]).append(FIRED.contains(h[0]) ? " ok" : " ?");
        }
        return sb.toString();
    }

    public static void logSummary(org.apache.logging.log4j.Logger log) {
        for (String[] h : HOOKS) {
            log.info("hook {} : {}", h[0], FIRED.contains(h[0]) ? "FIRED" : "not seen (may just not have happened yet, or the mixin target name is wrong)");
        }
    }
}
