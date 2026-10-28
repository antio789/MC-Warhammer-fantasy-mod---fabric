package warhammermod.Client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;

/**
 * Warpstone particle, ported from the 1.21.3 warpparticle (SpriteBillboardParticle
 * -> SingleQuadParticle with translucent terrain layer).
 */
public class warpparticle extends SingleQuadParticle {

    private final SpriteSet sprites;
    private static final float DEFAULT_ALPHA = 0.7f;

    warpparticle(ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SpriteSet sprites) {
        super(world, x, y, z, 0.5 - world.getRandom().nextDouble(), velocityY, 0.5 - world.getRandom().nextDouble(), null);
        this.friction = 0.96f;
        this.gravity = -0.1f;
        this.sprites = sprites;
        this.yd *= 0.2f;
        if (velocityX == 0.0 && velocityZ == 0.0) {
            this.xd *= 0.1f;
            this.zd *= 0.1f;
        }
        this.quadSize *= 0.75f;
        this.lifetime = (int) (6.0 / (Math.random() * 0.8 + 0.2));
        this.hasPhysics = false;
        this.setSpriteFromAge(sprites);
        this.alpha = DEFAULT_ALPHA;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT_TERRAIN;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    public static class InstantFactory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public InstantFactory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double vx, double vy, double vz, RandomSource random) {
            return new warpparticle(world, x, y, z, vx, vy, vz, this.sprites);
        }
    }
}
