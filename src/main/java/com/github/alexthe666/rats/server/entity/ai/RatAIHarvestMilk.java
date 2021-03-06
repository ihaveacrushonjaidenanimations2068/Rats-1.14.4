package com.github.alexthe666.rats.server.entity.ai;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.server.entity.EntityRat;
import com.github.alexthe666.rats.server.entity.RatCommand;
import com.github.alexthe666.rats.server.entity.RatUtils;
import com.github.alexthe666.rats.server.items.RatsItemRegistry;
import com.github.alexthe666.rats.server.message.MessageUpdateRatFluid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class RatAIHarvestMilk extends Goal {
    private static final int RADIUS = 16;
    private final EntityRat entity;
    private Entity targetCow = null;
    private boolean reachedCow = false;
    private int fishingCooldown = 1000;
    private int throwCooldown = 0;
    private Random rand = new Random();
    private Predicate<LivingEntity> COW_PREDICATE = new com.google.common.base.Predicate<LivingEntity>() {
        public boolean apply(@Nullable LivingEntity entity) {
            return entity != null && RatUtils.isCow(entity) && !entity.isChild();
        }
    };

    public RatAIHarvestMilk(EntityRat entity) {
        super();
        this.entity = entity;
        this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean shouldExecute() {
        if (!this.entity.canMove() || !this.entity.isTamed() || this.entity.getCommand() != RatCommand.HARVEST || this.entity.isInCage() || !entity.hasUpgrade(RatsItemRegistry.RAT_UPGRADE_MILKER)) {
            return false;
        }
        if (this.entity.transportingFluid != null && this.entity.transportingFluid.amount >= this.entity.getMBTransferRate()) {
            return false;
        }
        resetTarget();
        return targetCow != null;
    }

    @Override
    public boolean shouldContinueExecuting() {
        return targetCow != null && (this.entity.transportingFluid == null || this.entity.transportingFluid.amount < this.entity.getMBTransferRate());
    }

    public void resetTask() {
        this.entity.getNavigator().clearPath();
        resetTarget();
    }


    @Override
    public void tick() {
        if (this.targetCow != null && this.targetCow.isAlive() && (this.entity.transportingFluid == null || this.entity.transportingFluid.amount < this.entity.getMBTransferRate())) {
            this.entity.getNavigator().tryMoveToEntityLiving(this.targetCow, 1D);
            if (entity.getDistance(targetCow) < 1.5D) {
                if (this.entity.transportingFluid == null) {
                    FluidBucketWrapper milkWrapper = new FluidBucketWrapper(new ItemStack(Items.MILK_BUCKET));
                    if (milkWrapper.getFluid() != null && (this.entity.transportingFluid == null || this.entity.transportingFluid.amount < this.entity.getMBTransferRate())) {
                        this.entity.transportingFluid = milkWrapper.getFluid().copy();
                        if (!this.entity.world.isRemote) {
                            RatsMod.sendMSGToAll(new MessageUpdateRatFluid(this.entity.getEntityId(), this.entity.transportingFluid));
                        }
                        this.entity.playSound(SoundEvents.ENTITY_COW_MILK, 1, 1);
                        this.targetCow = null;
                        this.resetTask();
                    }
                }
            }
        } else {
            this.resetTask();
        }
    }

    private void resetTarget() {
        List<LivingEntity> list = this.entity.world.<LivingEntity>getEntitiesWithinAABB(LivingEntity.class, this.entity.getBoundingBox().grow(RADIUS), (com.google.common.base.Predicate<? super LivingEntity>) COW_PREDICATE);
        LivingEntity closestCow = null;
        for (LivingEntity base : list) {
            if (closestCow == null || base.getDistanceSq(entity) < closestCow.getDistanceSq(entity)) {
                closestCow = base;
            }
        }
        if (closestCow != null) {
            this.targetCow = closestCow;
        }
    }

}