package com.lowend.mixin;

import com.lowend.Governor;
import com.lowend.HookCheck;
import com.lowend.LowEndStats;
import net.minecraft.client.texture.Sprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** "Smart animations": at higher governor levels animated textures advance on fewer ticks (fewer texture uploads). */
@Mixin(Sprite.class)
public class SpriteMixin {

    @Inject(method = "tickAnimation", at = @At("HEAD"), cancellable = true, require = 0)
    private void lowend$throttle(CallbackInfo ci) {
        HookCheck.fired("animations");
        int d = Governor.animDivider();
        if (d > 1 && (LowEndStats.clientTick % d) != 0) {
            ci.cancel();
        }
    }
}
