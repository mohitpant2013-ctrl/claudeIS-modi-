package com.lowend;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

public class LowEndClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("LowEnd");
    public static final String VERSION = "0.5.0";

    private static final FrameStats STATS = new FrameStats();
    private static KeyBinding toggleKey;
    private static KeyBinding probeKey;
    private static boolean show = true;
    private static boolean probed = false;
    private static long firstFrameNs = 0;
    private static boolean hooksLogged = false;

    @Override
    public void onInitializeClient() {
        LowEndConfig.load();
        show = LowEndConfig.overlay;

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lowend.toggle", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F8, "category.lowend"));
        probeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lowend.probe", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F9, "category.lowend"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            LowEndStats.clientTick++;
            while (toggleKey.wasPressed()) show = !show;
            while (probeKey.wasPressed()) Bottleneck.on = !Bottleneck.on;
        });

        HudRenderCallback.EVENT.register(LowEndClient::onHud);
        LOGGER.info("LowEnd Renderer v{} loaded", VERSION);
    }

    private static void onHud(MatrixStack matrices, float tickDelta) {
        if (!probed) {
            probed = true;
            firstFrameNs = System.nanoTime();
            GLProbe.run(LOGGER);
        }
        STATS.frame();
        Governor.update(STATS.lastMs);
        LowEndStats.endFrame();

        if (Bottleneck.on) {
            long t0 = System.nanoTime();
            GL11.glFinish();
            Bottleneck.add((System.nanoTime() - t0) / 1_000_000.0, STATS.lastMs);
        }

        if (!hooksLogged && System.nanoTime() - firstFrameNs > 60_000_000_000L) {
            hooksLogged = true;
            HookCheck.logSummary(LOGGER);
        }
        if (!show) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        String l1 = String.format("FPS %.0f | %.1f ms | 1%% low %.0f",
                STATS.fps, STATS.avgMs, STATS.low1PctFps);
        String l2 = "GL " + GLProbe.version.split(" ")[0] + " | " + GLProbe.flags();
        String l3 = "governor L" + Governor.level + "/" + Governor.MAX_LEVEL
                + " (target " + LowEndConfig.targetFps + ") | culled E" + LowEndStats.shownEntities
                + " BE" + LowEndStats.shownBlockEntities;
        String l4 = HookCheck.summary();

        mc.textRenderer.drawWithShadow(matrices, l1, 4f, 4f, 0xFFFFFF55);
        mc.textRenderer.drawWithShadow(matrices, l2, 4f, 14f, 0xFFAAAAAA);
        mc.textRenderer.drawWithShadow(matrices, l3, 4f, 24f, 0xFF88DD88);
        mc.textRenderer.drawWithShadow(matrices, l4, 4f, 34f, 0xFF999999);
        if (Bottleneck.on) {
            mc.textRenderer.drawWithShadow(matrices, Bottleneck.text(), 4f, 44f, 0xFF66CCFF);
        }
    }
}
