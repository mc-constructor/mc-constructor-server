package net.dandoes.minecraft.nodesupport.serialization.objects;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;

public class DamageSourceData {
    public final String msgId;

    public static DamageSourceData forSource(final DamageSource damageSource) {
        if (damageSource instanceof EntityDamageSource) {
            return new EntityDamageSourceData(((EntityDamageSource) damageSource));
        }
        return new DamageSourceData(damageSource);
    }

    public DamageSourceData(final DamageSource damageSource) {
        this.msgId = damageSource.getMsgId();
    }

    public static class EntityDamageSourceData extends DamageSourceData {
        public final EntityData attacker;

        public EntityDamageSourceData(final EntityDamageSource entityDamageSource) {
            super(entityDamageSource);

            this.attacker = EntityData.forEntity(entityDamageSource.getEntity());
        }
    }
}
