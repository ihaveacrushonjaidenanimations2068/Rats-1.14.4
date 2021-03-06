package com.github.alexthe666.rats.server.world.gen;

import com.github.alexthe666.rats.server.blocks.RatsBlockRegistry;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.Template;

import java.util.Random;

public class RatsStructureProcessor extends StructureProcessor {

    private static final BlockState AIR = Blocks.AIR.getDefaultState();
    private float integrity = 1F;

    public RatsStructureProcessor(float integrity) {
        this.integrity = integrity;
    }

    public Template.BlockInfo process(IWorldReader worldIn, BlockPos pos, Template.BlockInfo blockInfoIn, Template.BlockInfo blockInfoIn2, PlacementSettings settings) {
        Random random = settings.getRandom(pos);
        if (random.nextFloat() <= integrity) {
            if (worldIn.getBlockState(pos).getBlock() == Blocks.VINE) {
                return null;
            }
            if (blockInfoIn.state.getBlock() == RatsBlockRegistry.MARBLED_CHEESE_BRICK) {
                BlockState state2 = RatStructure.getRandomCrackedBlock(null, random);
                return new Template.BlockInfo(pos, state2, null);
            }
            return blockInfoIn;
        }
        return null;
    }

    @Override
    protected IStructureProcessorType getType() {
        return IStructureProcessorType.BLOCK_ROT;
    }

    @Override
    protected <T> Dynamic<T> serialize0(DynamicOps<T> ops) {
        return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("rats_processor"), ops.createFloat(this.integrity))));
    }

}
