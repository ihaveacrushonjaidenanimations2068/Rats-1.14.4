package com.github.alexthe666.rats.client.render.entity;

import com.github.alexthe666.rats.server.entity.EntityRat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class RenderPirat extends RenderRat {

    public RenderPirat() {
        super();
    }

    protected void preRenderCallback(EntityRat rat, float partialTickTime) {
        super.preRenderCallback(rat, partialTickTime);
        GL11.glScaled(1.6F, 1.6F, 1.6F);
    }
}
