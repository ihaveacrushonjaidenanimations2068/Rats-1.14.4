package com.github.alexthe666.rats.server.items;

import com.github.alexthe666.rats.RatConfig;
import com.github.alexthe666.rats.RatsMod;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;

public class ItemChunkyCheeseToken extends Item {

    public ItemChunkyCheeseToken() {
        super(new Item.Properties().group(RatsMod.TAB));
        this.setRegistryName(RatsMod.MODID, "chunky_cheese_token");
    }

    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        String formattedChance = NumberFormat.getNumberInstance().format(RatConfig.tokenDropRate);
        tooltip.add(new TranslationTextComponent("item.rats.chunky_cheese_token.desc0", formattedChance));
        if (!RatConfig.disableRatlantis) {
            tooltip.add(new TranslationTextComponent("item.rats.chunky_cheese_token.desc1"));
        }
    }

    public Rarity getRarity(ItemStack stack) {
        return Rarity.UNCOMMON;
    }
}
