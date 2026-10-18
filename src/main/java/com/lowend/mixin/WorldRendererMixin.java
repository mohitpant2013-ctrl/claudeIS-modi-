package com.lowend.mixin;

import com.lowend.Governor;
import com.lowend.HookCheck;
import com.lowend.LowEndConfig;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Governor-driven: stop drawing clouds and rain/snow when frames are slow. */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true, require = 0)
    private void lowend$clouds(CallbackInfo ci) {
        HookCheck.fired("clouds");
        if (Governor.level >= LowEndConfig.cloudsOffLevel) {
            ci.cancel();
        }
    }

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true, require = 0)
    private void lowend$weather(CallbackInfo ci) {
        HookCheck.fired("weather");
        if (Governor.level >= LowEndConfig.weatherOffLevel) {
            ci.cancel();
        }
    }
}
