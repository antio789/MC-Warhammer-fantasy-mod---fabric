/*
 * Decompiled with CFR 0.2.2 (FabricMC 7c48b8c4).
 */
package warhammermod.Client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

@Environment(value=EnvType.CLIENT)
public class warpparticle
extends TextureSheetParticle {
    private static final RandomSource RANDOM = RandomSource.create();
    private final SpriteSet spriteProvider;
    private float defaultAlpha = 0.7f;

    warpparticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet spriteProvider) {
        super(world, x, y, z, 0.5 - RANDOM.nextDouble(), velocityY, 0.5 - RANDOM.nextDouble());
        this.friction = 0.96f;
        this.gravity = -0.1f;
        this.speedUpWhenYMotionIsBlocked = true;
        this.spriteProvider = spriteProvider;
        this.yd *= (double)0.2f;
        if (velocityX == 0.0 && velocityZ == 0.0) {
            this.xd *= (double)0.1f;
            this.zd *= (double)0.1f;
        }
        this.quadSize *= 0.75f;
        this.lifetime = (int)(6.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
        this.setSpriteFromAge(spriteProvider);
        this.setAlpha(defaultAlpha);
    }
    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteProvider);
    }

    @Environment(value=EnvType.CLIENT)
    public static class InstantFactory
            implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public InstantFactory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new warpparticle(clientWorld, d, e, f, g, h, i, this.spriteProvider);
        }

    }

}

