package com.lowend.mixin;

import com.lowend.Governor;
import com.lowend.HookCheck;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Drops a share of particles when the governor is on a higher level. PvP hit particles are always kept. */
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    private static int lowend$counter = 0;

    @Inject(method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void lowend$throttle(ParticleEffect effect, double x, double y, double z,
                                  double vx, double vy, double vz,
                                  CallbackInfoReturnable<Particle> cir) {
        HookCheck.fired("particles");
        if (Governor.level == 0) return;
        if (effect.getType() == ParticleTypes.CRIT
                || effect.getType() == ParticleTypes.ENCHANTED_HIT
                || effect.getType() == ParticleTypes.DAMAGE_INDICATOR) return;
        if (Governor.dropParticle(++lowend$counter)) {
            cir.setReturnValue(null);
        }
    }
}
