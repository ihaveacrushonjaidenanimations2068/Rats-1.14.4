package com.github.alexthe666.rats.server.world;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.server.blocks.RatsBlockRegistry;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;
import java.util.stream.Collectors;

public class RatlantisTeleporter extends Teleporter {
    private final ServerWorld worldServerInstance;
    private final Random random;

    public RatlantisTeleporter(ServerWorld worldserver) {
        super(worldserver);
        this.worldServerInstance = worldserver;
        this.random = new Random(worldserver.getSeed());
    }

    @Override
    public boolean func_222268_a(Entity entity, float rotationYaw) {
        if (worldServerInstance.getDimension().getType().getId() == RatConfig.ratlantisDimensionId) {
            this.placeInPortal(entity);
            entity.setPositionAndRotation(0, 110, 0, 0, 0);
        } else {
            if (entity instanceof PlayerEntity && ((PlayerEntity) entity).getBedLocation() != null) {
                BlockPos bedPos = ((PlayerEntity) entity).getBedLocation();
                entity.setLocationAndAngles(bedPos.getX() + 0.5D, bedPos.getY() + 1.5D, bedPos.getZ() + 0.5D, 0.0F, 0.0F);
            } else {
                BlockPos height = entity.world.getHeight(Heightmap.Type.WORLD_SURFACE, entity.getPosition());
                entity.setLocationAndAngles(height.getX() + 0.5D, height.getY() + 0.5D, height.getZ() + 0.5D, entity.rotationYaw, 0.0F);
            }
        }
        return false;
    }

    public void placeInPortal(Entity entity) {
        entity.setMotion(0, 0, 0);
        BlockPos portalBottom = new BlockPos(1, 111, 1);
        for (BlockPos pos : BlockPos.getAllInBox(portalBottom.add(-2, 0, -2), portalBottom.add(2, 0, 2)).map(BlockPos::toImmutable).collect(Collectors.toList())) {
            worldServerInstance.setBlockState(pos, RatsBlockRegistry.MARBLED_CHEESE_TILE.getDefaultState());
            worldServerInstance.setBlockState(pos.up(4), RatsBlockRegistry.MARBLED_CHEESE_TILE.getDefaultState());
        }
        for (int i = 1; i < 4; i++) {
            worldServerInstance.setBlockState(portalBottom.add(2, 0, 2).up(i), RatsBlockRegistry.MARBLED_CHEESE_PILLAR.getDefaultState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y));
            worldServerInstance.setBlockState(portalBottom.add(2, 0, -2).up(i), RatsBlockRegistry.MARBLED_CHEESE_PILLAR.getDefaultState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y));
            worldServerInstance.setBlockState(portalBottom.add(-2, 0, 2).up(i), RatsBlockRegistry.MARBLED_CHEESE_PILLAR.getDefaultState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y));
            worldServerInstance.setBlockState(portalBottom.add(-2, 0, -2).up(i), RatsBlockRegistry.MARBLED_CHEESE_PILLAR.getDefaultState().with(RotatedPillarBlock.AXIS, Direction.Axis.Y));
        }
        worldServerInstance.setBlockState(portalBottom, RatsBlockRegistry.MARBLED_CHEESE_RAW.getDefaultState());
        worldServerInstance.setBlockState(portalBottom.up(), RatsBlockRegistry.RATLANTIS_PORTAL.getDefaultState());
        worldServerInstance.setBlockState(portalBottom.up(2), RatsBlockRegistry.RATLANTIS_PORTAL.getDefaultState());
        worldServerInstance.setBlockState(portalBottom.up(3), RatsBlockRegistry.MARBLED_CHEESE_RAW.getDefaultState());
    }

    @Override
    public boolean makePortal(Entity e) {
        return true;
    }
}