package com.github.alexthe666.rats.server.entity;

import com.github.alexthe666.rats.server.entity.ai.PiratBoatPathNavigate;
import com.github.alexthe666.rats.server.items.RatsItemRegistry;
import com.github.alexthe666.rats.server.recipes.RatsRecipeRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class EntityPiratBoat extends MobEntity implements IRatlantean {
    public static final ItemStack BANNER = generateBanner();
    private static final List<ItemStack> EMPTY_EQUIPMENT = Collections.emptyList();
    private static final DataParameter<Boolean> FIRING = EntityDataManager.createKey(EntityPiratBoat.class, DataSerializers.BOOLEAN);
    private final float[] paddlePositions;
    protected int navigatorType;
    private boolean prevFire;
    private int fireCooldown = 0;
    private double waterLevel;
    private BoatEntity.Status status;
    private BoatEntity.Status previousStatus;
    private double lastYd;

    public EntityPiratBoat(EntityType type, World worldIn) {
        super(type, worldIn);
        this.paddlePositions = new float[2];
        switchNavigator(0);
    }

    private static ItemStack generateBanner() {
        ItemStack itemstack = new ItemStack(Items.BLACK_BANNER);
        CompoundNBT compoundnbt = itemstack.getOrCreateChildTag("BlockEntityTag");
        ListNBT listnbt = (new BannerPattern.Builder()).func_222477_a(RatsRecipeRegistry.RAT_AND_CROSSBONES_PATTERN, DyeColor.WHITE).func_222476_a();
        compoundnbt.put("Patterns", listnbt);
        return itemstack;
    }

    protected void switchNavigator(int type) {
        if (type == 1) {//land
            this.navigator = new GroundPathNavigator(this, world);
            this.navigatorType = 1;
        } else {//sea
            this.navigator = new PiratBoatPathNavigate(this, world);
            this.navigatorType = 0;
        }
    }

    public boolean writeUnlessPassenger(CompoundNBT compound) {
        String s = this.getEntityString();
        compound.putString("id", s);
        super.writeUnlessPassenger(compound);
        return true;
    }

    public boolean canBeSteered() {
        return true;
    }

    public boolean canPassengerSteer() {
        return false;
    }

    @Override
    public Entity getControllingPassenger() {
        if (!this.getPassengers().isEmpty()) {
            for (Entity entity : this.getPassengers()) {
                if (entity instanceof EntityPirat) {
                    return entity;
                }
            }
        }
        return null;
    }

    public double getYOffset() {
        return 0.45D;
    }

    public void updatePassenger(Entity passenger) {
        super.updatePassenger(passenger);
        passenger.setPosition(this.posX, this.posY + 0.45D, this.posZ);
        if (passenger instanceof LivingEntity) {
            ((LivingEntity) passenger).renderYawOffset = this.renderYawOffset;
        }
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FIRING, Boolean.valueOf(false));
    }

    public boolean isFiring() {
        return this.dataManager.get(FIRING).booleanValue();
    }

    public void setFiring(boolean male) {
        this.dataManager.set(FIRING, Boolean.valueOf(male));
    }

    protected void onDeathUpdate() {
        ++this.deathTime;
        Vec3d vec3d = this.getMotion();
        this.setMotion(vec3d.mul(1.0D, 0.6D, 1.0D));
        this.livingSoundTime = 20;
        if (this.deathTime >= 80) {
            if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT))) {
                int i = this.getExperiencePoints(this.attackingPlayer);
                i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
                while (i > 0) {
                    int j = ExperienceOrbEntity.getXPSplit(i);
                    i -= j;
                    this.world.addEntity(new ExperienceOrbEntity(this.world, this.posX, this.posY, this.posZ, j));
                }
            }
            if (!this.world.isRemote) {
                for (int j = 0; j < rand.nextInt(3); j++) {
                    this.entityDropItem(new ItemStack(Items.STICK), 0.0F);
                }
                for (int j = 0; j < rand.nextInt(3); j++) {
                    this.entityDropItem(new ItemStack(Blocks.OAK_PLANKS), 0.0F);
                }
                if (rand.nextInt(3) == 0) {
                    this.entityDropItem(BANNER.copy(), 0.0F);
                }
                if (rand.nextInt(2) == 0) {
                    this.entityDropItem(new ItemStack(RatsItemRegistry.CHEESE_CANNONBALL), 0.0F);
                }
            }
            this.remove();
            for (int k = 0; k < 20; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.addParticle(ParticleTypes.EXPLOSION, this.posX + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.posY + (double) (this.rand.nextFloat() * this.getHeight()), this.posZ + (double) (this.rand.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), d2, d0, d1);
            }
        }
    }

    public void tick() {
        this.previousStatus = this.status;
        this.status = this.getBoatStatus();
        super.tick();
        boolean groundNavigate = !this.isInWater() && this.status != BoatEntity.Status.IN_WATER;
        if (!world.isRemote) {
            if (groundNavigate && navigatorType != 1) {
                switchNavigator(1);
            }
            if (!groundNavigate && navigatorType != 0) {
                switchNavigator(0);
            }
        }
        if (this.getRidingEntity() != null) {
            if (!this.getRidingEntity().isPassenger()) {
                this.getRidingEntity().startRiding(this, true);
            }
        }
        if (this.prevFire != isFiring()) {
            fireCooldown = 4;
        }
        if (isFiring() && fireCooldown == 0) {
            setFiring(false);
        }
        if (fireCooldown > 0) {
            fireCooldown--;
        }
        prevFire = this.isFiring();
        if (!this.isBeingRidden() && !world.isRemote) {
            this.attackEntityFrom(DamageSource.DROWN, 1000);
        }
        if (this.getControllingPassenger() != null) {
            this.updateMotion();
            if (this.getControllingPassenger() instanceof LivingEntity) {
                LivingEntity riding = (LivingEntity) this.getControllingPassenger();
                this.moveStrafing = riding.moveStrafing;
                this.moveForward = riding.moveForward;
                this.moveRelative(0.10F, new Vec3d(moveStrafing, 0, moveForward));
                this.rotationYaw = riding.rotationYaw;
                this.rotationYawHead = riding.rotationYawHead;
                this.prevRotationYaw = riding.prevRotationYaw;
            }
        }
        this.doBlockCollisions();
        List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox().grow((double)0.2F, (double)-0.01F, (double)0.2F), EntityPredicates.pushableBy(this));

        if (!list.isEmpty()) {
            boolean flag = !this.world.isRemote && !(this.getControllingPassenger() instanceof PlayerEntity);

            for (int j = 0; j < list.size(); ++j) {
                Entity entity = list.get(j);

                if (!entity.isPassenger(this)) {
                    if (flag && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getWidth() < this.getWidth() && entity instanceof EntityPirat) {
                        entity.startRiding(this);
                    } else {
                        this.applyEntityCollision(entity);
                    }
                }
            }
        }
    }

    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1D);
        this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(64.0D);
        this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
        this.getAttribute(SWIM_SPEED).setBaseValue(0.1D);
    }

    public void applyEntityCollision(Entity entityIn) {
        if (entityIn instanceof BoatEntity) {
            if (entityIn.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.applyEntityCollision(entityIn);
            }
        } else if (entityIn.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.applyEntityCollision(entityIn);
        }
    }

    protected void doWaterSplashEffect() {
    }

    @OnlyIn(Dist.CLIENT)
    public float getRowingTime(int side, float limbSwing) {
        return (float) MathHelper.clampedLerp((double) this.paddlePositions[side] - 0.39269909262657166D, (double) this.paddlePositions[side], (double) limbSwing);
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return EMPTY_EQUIPMENT;
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
    }

    @Override
    public HandSide getPrimaryHand() {
        return HandSide.RIGHT;
    }

    public void shoot(EntityPirat pirat) {
        //world.updateEntityWithOptionalForce(this, true);

        LivingEntity target = pirat.getAttackTarget();
        if (target == null) {
            target = world.getClosestPlayer(this, 30);
        }
        if (target != null) {
            {
                double d0 = target.posX - this.posX;
                double d2 = target.posZ - this.posZ;
                float f = (float) (MathHelper.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                this.renderYawOffset = this.rotationYaw = f % 360;
            }
            EntityCheeseCannonball cannonball = new EntityCheeseCannonball(RatsEntityRegistry.CHEESE_CANNONBALL, world, pirat);
            //cannonball.ignoreEntity = this;
            float radius = 1.6F;
            float angle = (0.01745329251F * (this.renderYawOffset));
            double extraX = (double) (radius * MathHelper.sin((float) (Math.PI + angle))) + posX;
            double extraZ = (double) (radius * MathHelper.cos(angle)) + posZ;
            double extraY = 0.8 + posY;
            double d0 = target.posY + (double) target.getEyeHeight();
            double d1 = target.posX - extraX;
            double d3 = target.posZ - extraZ;
            double d2 = d0 - extraY;
            float f = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.65F;
            float velocity = this.getDistance(target) * 0.045F;
            cannonball.setPosition(extraX, extraY, extraZ);
            cannonball.shoot(d1, d2 + (double) f, d3, velocity, 0.4F);
            this.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 3.0F, 2.3F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
            if (!world.isRemote) {
                this.world.addEntity(cannonball);
            }
            this.setFiring(true);
        }
    }

    public boolean canBreatheUnderwater() {
        return true;
    }

    public float getWaterLevelAbove() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.ceil(axisalignedbb.maxY - this.lastYd);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);

        try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            label161:
            for(int k1 = k; k1 < l; ++k1) {
                float f = 0.0F;

                for(int l1 = i; l1 < j; ++l1) {
                    for(int i2 = i1; i2 < j1; ++i2) {
                        blockpos$pooledmutableblockpos.setPos(l1, k1, i2);
                        IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutableblockpos);
                        if (ifluidstate.isTagged(FluidTags.WATER)) {
                            f = Math.max(f, ifluidstate.func_215679_a(this.world, blockpos$pooledmutableblockpos));
                        }

                        if (f >= 1.0F) {
                            continue label161;
                        }
                    }
                }

                if (f < 1.0F) {
                    float f2 = (float)blockpos$pooledmutableblockpos.getY() + f;
                    return f2;
                }
            }

            float f1 = (float)(l + 1);
            return f1;
        }
    }

    private boolean checkInWater() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY - 0.5D);
        int l = MathHelper.ceil(axisalignedbb.maxY + 0.001D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        this.waterLevel = Double.MIN_VALUE;
        try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(int k1 = i; k1 < j; ++k1) {
                for(int l1 = k; l1 < l; ++l1) {
                    for(int i2 = i1; i2 < j1; ++i2) {
                        blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                        IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutableblockpos);
                        if (ifluidstate.isTagged(FluidTags.WATER)) {
                            float f = (float)l1 + ifluidstate.func_215679_a(this.world, blockpos$pooledmutableblockpos);
                            this.waterLevel = Math.max((double)f, this.waterLevel);
                            flag |= axisalignedbb.minY < (double)f;
                        }
                    }
                }
            }
        }

        return flag || this.isOverWater();
    }

    private boolean isOverWater() {
        return this.isInWater();
    }

    private BoatEntity.Status getBoatStatus() {
        BoatEntity.Status BoatEntity$status = this.getUnderwaterStatus();

        if (BoatEntity$status != null) {
            this.waterLevel = this.getBoundingBox().minY;
            return BoatEntity$status;
        } else if (this.checkInWater()) {
            return BoatEntity.Status.IN_WATER;
        } else {
            return BoatEntity.Status.ON_LAND;
        }
    }

    @Nullable
    private BoatEntity.Status getUnderwaterStatus() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001D;
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.ceil(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.ceil(d0);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.ceil(axisalignedbb.maxZ);
        boolean flag = false;
        try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(int k1 = i; k1 < j; ++k1) {
                for(int l1 = k; l1 < l; ++l1) {
                    for(int i2 = i1; i2 < j1; ++i2) {
                        blockpos$pooledmutableblockpos.setPos(k1, l1, i2);
                        IFluidState ifluidstate = this.world.getFluidState(blockpos$pooledmutableblockpos);
                        if (ifluidstate.isTagged(FluidTags.WATER) && d0 < (double)((float)blockpos$pooledmutableblockpos.getY() + ifluidstate.func_215679_a(this.world, blockpos$pooledmutableblockpos))) {
                            if (!ifluidstate.isSource()) {
                                BoatEntity.Status boatentity$status = BoatEntity.Status.UNDER_FLOWING_WATER;
                                return boatentity$status;
                            }

                            flag = true;
                        }
                    }
                }
            }
        }

        return flag ? BoatEntity.Status.UNDER_WATER : null;
    }

    private void updateMotion() {
        double d0 = (double)-0.04F;
        double d1 = this.hasNoGravity() ? 0.0D : (double)-0.04F;
        double d2 = 0.0D;
        float momentum = 0.05F;
        if (this.previousStatus == BoatEntity.Status.IN_AIR && this.status != BoatEntity.Status.IN_AIR && this.status != BoatEntity.Status.ON_LAND) {
            this.waterLevel = this.getBoundingBox().minY + (double)this.getHeight();
            this.setPosition(this.posX, (double)(this.getWaterLevelAbove() - this.getHeight()) + 0.101D, this.posZ);
            this.setMotion(this.getMotion().mul(1.0D, 0.0D, 1.0D));
            this.lastYd = 0.0D;
            this.status = BoatEntity.Status.IN_WATER;
        } else {
            if (this.status == BoatEntity.Status.IN_WATER) {
                d2 = (this.waterLevel - this.getBoundingBox().minY) / (double)this.getHeight();
                momentum = 0.9F;
            } else if (this.status == BoatEntity.Status.UNDER_FLOWING_WATER) {
                d1 = -7.0E-4D;
                momentum = 0.9F;
            } else if (this.status == BoatEntity.Status.UNDER_WATER) {
                d2 = (double)0.01F;
                momentum = 0.45F;
            } else if (this.status == BoatEntity.Status.IN_AIR) {
                momentum = 0.9F;
            } else if (this.status == BoatEntity.Status.ON_LAND) {
               /* momentum = this.boatGlide;
                if (this.getControllingPassenger() instanceof PlayerEntity) {
                    this.boatGlide /= 2.0F;
                }*/
            }

            Vec3d vec3d = this.getMotion();
            this.setMotion(vec3d.x * (double)momentum, vec3d.y + d1, vec3d.z * (double)momentum);
            if (d2 > 0.0D) {
                Vec3d vec3d1 = this.getMotion();
                this.setMotion(vec3d1.x, (vec3d1.y + d2 * 0.06153846016296973D) * 0.75D, vec3d1.z);
            }
        }
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_WOOD_BREAK;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.BLOCK_WOOD_BREAK;
    }

    public boolean shouldDismountInWater(Entity rider) {
        return false;
    }

    public void travel(Vec3d p_213352_1_) {
        /*if (this.isServerWorld() && (this.isInWater() || status == BoatEntity.Status.IN_WATER)) {
            float forwards = forward;
            float strafes = strafe * 0.5F;
            this.moveRelative(0, new Vec3d(forwards, vertical, strafes));
            this.motionX *= 0.8999999761581421D;
            this.motionY *= 0.8999999761581421D;
            this.motionZ *= 0.8999999761581421D;
            this.move(MoverType.SELF, this.motionX, this.isInWater() ? this.motionY : 0, this.motionZ);
        } else {
        }*/
        super.travel(p_213352_1_);

    }

}
