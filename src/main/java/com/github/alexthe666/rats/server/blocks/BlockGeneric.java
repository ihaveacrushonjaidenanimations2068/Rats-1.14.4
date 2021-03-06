package com.github.alexthe666.rats.server.blocks;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.BlockRenderLayer;

public class BlockGeneric extends Block {
    public BlockGeneric(String name, Material mat, float hardness, float resistance, SoundType sound) {
        super(Block.Properties.create(mat).sound(sound).hardnessAndResistance(hardness, resistance));
        this.setRegistryName(RatsMod.MODID, name);
    }

    public BlockGeneric(String name, Material mat, float hardness, float resistance, SoundType sound, int light) {
        super(Block.Properties.create(mat).sound(sound).hardnessAndResistance(hardness, resistance).lightValue(light));
        this.setRegistryName(RatsMod.MODID, name);
    }

    public BlockRenderLayer getRenderLayer() {
        if (this == RatsBlockRegistry.MARBLED_CHEESE_DIRT) {
            return BlockRenderLayer.CUTOUT;
        } else {
            return super.getRenderLayer();
        }
    }

}
