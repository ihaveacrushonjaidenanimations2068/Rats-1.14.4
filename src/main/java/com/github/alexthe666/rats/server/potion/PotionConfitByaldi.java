package com.github.alexthe666.rats.server.potion;

import com.github.alexthe666.rats.RatsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PotionConfitByaldi extends Effect {

    public static final ResourceLocation TEXTURE = new ResourceLocation("rats:textures/gui/potion_effect.png");

    public PotionConfitByaldi() {
        super(EffectType.BENEFICIAL, 0XFFDD59);
        this.setRegistryName(RatsMod.MODID, "synesthesia");
        this.addAttributesModifier(SharedMonsterAttributes.ATTACK_SPEED, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 10.0D, AttributeModifier.Operation.ADDITION);
    }

    public void performEffect(LivingEntity LivingEntityIn, int amplifier) {
        if (LivingEntityIn.getHealth() < LivingEntityIn.getMaxHealth()) {
            LivingEntityIn.heal(1.0F);
        }
        if (LivingEntityIn instanceof PlayerEntity) {
            ((PlayerEntity) LivingEntityIn).getFoodStats().addStats(100, 1.0F);
        }
    }

    public boolean isReady(int duration, int amplifier) {
        return duration > 0;
    }

    public void removeAttributesModifiersFromEntity(LivingEntity LivingEntityIn, AbstractAttributeMap attributeMapIn, int amplifier) {
        super.removeAttributesModifiersFromEntity(LivingEntityIn, attributeMapIn, amplifier);
        LivingEntityIn.setAbsorptionAmount(LivingEntityIn.getAbsorptionAmount() - (float) (20 * (amplifier + 1)));
        if (LivingEntityIn.getHealth() > LivingEntityIn.getMaxHealth()) {
            LivingEntityIn.setHealth(LivingEntityIn.getMaxHealth());
        }
    }

    public void applyAttributesModifiersToEntity(LivingEntity LivingEntityIn, AbstractAttributeMap attributeMapIn, int amplifier) {
        LivingEntityIn.setAbsorptionAmount(LivingEntityIn.getAbsorptionAmount() + (float) (20 * (amplifier + 1)));
        super.applyAttributesModifiersToEntity(LivingEntityIn, attributeMapIn, amplifier);
    }

    public String getName() {
        return "rats.synesthesia";
    }

    @OnlyIn(Dist.CLIENT)
    public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, int x, int y, float z) {
        Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(x + 6, y + 7, 0, 0, 18, 18);
    }


    @OnlyIn(Dist.CLIENT)
    public void renderHUDEffect(EffectInstance effect, AbstractGui gui, int x, int y, float z, float alpha) {
        Minecraft.getInstance().getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(x + 3, y + 3, 0, 0, 18, 18);

    }

    @OnlyIn(Dist.CLIENT)
    private void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos((double) (x + 0), (double) (y + height), (double) 200).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y + height), (double) 200).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + height) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + width), (double) (y + 0), (double) 200).tex((double) ((float) (textureX + width) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
        bufferbuilder.pos((double) (x + 0), (double) (y + 0), (double) 200).tex((double) ((float) (textureX + 0) * 0.00390625F), (double) ((float) (textureY + 0) * 0.00390625F)).endVertex();
        tessellator.draw();
    }


}
