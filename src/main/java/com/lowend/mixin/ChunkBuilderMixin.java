package com.lowend.mixin;

import com.lowend.HookCheck;
import com.lowend.LowEndConfig;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Caps chunk-mesh worker threads so they don't fight the render thread on a dual-core CPU. */
@Mixin(ChunkBuilder.class)
public class ChunkBuilderMixin {

    @Redirect(method = "<init>",
            at = @At(value = "INVOKE", target = "Ljava/lang/Runtime;availableProcessors()I"),
            require = 0)
    private int lowend$cpus(Runtime runtime) {
        HookCheck.fired("threads");
        int real = runtime.availableProcessors();
        int cap = LowEndConfig.chunkBuilderThreads;
        return cap > 0 ? Math.max(1, Math.min(real, cap)) : real;
    }
}
