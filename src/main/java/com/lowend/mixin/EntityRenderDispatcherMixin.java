package com.lowend.mixin;

import com.lowend.Governor;
import com.lowend.HookCheck;
import com.lowend.LowEndConfig;
import com.lowend.LowEndStats;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Distance-culls far entities. Players, projectiles, riders, glowing and huge mobs are never culled. */
@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true, require = 0)
    private <E extends Entity> void lowend$distanceCull(E entity, Frustum frustum,
                                                         double x, double y, double z,
                                                         CallbackInfoReturnable<Boolean> cir) {
        HookCheck.fired("entity");
        if (!LowEndConfig.entityCulling) return;
        if (entity instanceof PlayerEntity) return;
        if (entity instanceof ProjectileEntity) return;
        if (entity.hasPassengers() || entity.hasVehicle()) return;
        if (entity.isGlowing()) return;
        if (entity.getWidth() > 4.0F) return;

        double base = (entity instanceof LivingEntity) ? LowEndConfig.entityDistance
                                                        : LowEndConfig.otherEntityDistance;
        double limit = base * Governor.distScale();
        if (entity.squaredDistanceTo(x, y, z) > limit * limit) {
            LowEndStats.culledEntities++;
            cir.setReturnValue(false);
        }
    }
}
