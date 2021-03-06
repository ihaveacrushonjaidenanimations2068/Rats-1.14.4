package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.client.model.ModelIllagerPiper;
import com.github.alexthe666.rats.server.entity.EntityIllagerPiper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class RenderIllagerPiper extends MobRenderer<EntityIllagerPiper, ModelIllagerPiper<EntityIllagerPiper>> {
    private static final ResourceLocation ILLUSIONIST = new ResourceLocation("rats:textures/entity/illager_piper.png");

    public RenderIllagerPiper() {
        super(Minecraft.getInstance().getRenderManager(), new ModelIllagerPiper(), 0.5F);
        this.addLayer(new HeldItemLayer(this) {
            protected void translateToHand(HandSide side) {
                ModelIllagerPiper model = ((ModelIllagerPiper) this.getEntityModel());
                model.getArm(side).postRender(0.0625F);
                GL11.glTranslatef(0.2F, 0.2F, 0.1F);
                //model.nose.rotateAngleX = (float) Math.toRadians(-10);
            }
        });
    }

    protected ResourceLocation getEntityTexture(EntityIllagerPiper entity) {
        return ILLUSIONIST;
    }

    protected void preRenderCallback(EntityIllagerPiper LivingEntityIn, float partialTickTime) {
        float f = 0.9375F;
        GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
    }

    public void renderName(EntityIllagerPiper entity, double x, double y, double z) {
        super.renderName(entity, x, y, z);
    }

    protected boolean isVisible(EntityIllagerPiper p_193115_1_) {
        return true;
    }
}