package com.lowend.mixin;

import com.lowend.Governor;
import com.lowend.HookCheck;
import com.lowend.LowEndConfig;
import com.lowend.LowEndStats;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Distance-culls far block entities (chests, signs, banners). Beacons and gateways stay visible. */
@Mixin(BlockEntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
    private <E extends BlockEntity> void lowend$distanceCull(E blockEntity, float tickDelta,
                                                              MatrixStack matrices,
                                                              VertexConsumerProvider vertexConsumers,
                                                              CallbackInfo ci) {
        HookCheck.fired("blockentity");
        if (!LowEndConfig.blockEntityCulling) return;
        if (blockEntity instanceof BeaconBlockEntity || blockEntity instanceof EndGatewayBlockEntity) return;

        Vec3d cam = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
        BlockPos pos = blockEntity.getPos();
        double dx = pos.getX() + 0.5 - cam.x;
        double dy = pos.getY() + 0.5 - cam.y;
        double dz = pos.getZ() + 0.5 - cam.z;
        double limit = LowEndConfig.blockEntityDistance * Governor.distScale();
        if (dx * dx + dy * dy + dz * dz > limit * limit) {
            LowEndStats.culledBlockEntities++;
            ci.cancel();
        }
    }
}
