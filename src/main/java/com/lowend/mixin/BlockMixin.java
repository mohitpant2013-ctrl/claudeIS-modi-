package com.lowend.mixin;

import com.lowend.HookCheck;
import com.lowend.LowEndConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** EXPERIMENTAL (off by default): hide faces between touching leaf blocks. Fewer faces in forests, hollow canopy interior. */
@Mixin(Block.class)
public class BlockMixin {
    private static boolean lowend$seen = false;

    @Inject(method = "shouldDrawSide", at = @At("HEAD"), cancellable = true, require = 0)
    private static void lowend$leaves(BlockState state, BlockView world, BlockPos pos, Direction side,
                                      CallbackInfoReturnable<Boolean> cir) {
        if (!lowend$seen) {
            lowend$seen = true;
            HookCheck.fired("leaves");
        }
        if (!LowEndConfig.leafCulling) return;
        if (!(state.getBlock() instanceof LeavesBlock)) return;
        if (world.getBlockState(pos.offset(side)).getBlock() instanceof LeavesBlock) {
            cir.setReturnValue(false);
        }
    }
}
