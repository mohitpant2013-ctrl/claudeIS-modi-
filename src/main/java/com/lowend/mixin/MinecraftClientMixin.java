package com.lowend.mixin;

import com.lowend.HookCheck;
import com.lowend.LowEndConfig;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** No smooth lighting / ambient occlusion: chunk meshing skips the expensive per-vertex light blending. */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "isAmbientOcclusionEnabled", at = @At("HEAD"), cancellable = true, require = 0)
    private static void lowend$noAo(CallbackInfoReturnable<Boolean> cir) {
        HookCheck.fired("lighting");
        if (LowEndConfig.forceSimpleLighting) {
            cir.setReturnValue(false);
        }
    }
}
