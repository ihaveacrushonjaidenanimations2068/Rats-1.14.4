package com.github.alexthe666.rats.client;

import com.github.alexthe666.rats.RatsMod;
import com.github.alexthe666.rats.client.gui.GuiCheeseStaff;
import com.github.alexthe666.rats.client.gui.GuiRat;
import com.github.alexthe666.rats.client.gui.RatsGuiRegistry;
import com.github.alexthe666.rats.client.model.*;
import com.github.alexthe666.rats.client.particle.*;
import com.github.alexthe666.rats.client.render.NuggetColorRegister;
import com.github.alexthe666.rats.client.render.RenderNothing;
import com.github.alexthe666.rats.client.render.entity.*;
import com.github.alexthe666.rats.client.render.tile.*;
import com.github.alexthe666.rats.server.CommonProxy;
import com.github.alexthe666.rats.server.blocks.RatsBlockRegistry;
import com.github.alexthe666.rats.server.entity.*;
import com.github.alexthe666.rats.server.entity.tile.*;
import com.github.alexthe666.rats.server.items.RatsItemRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GrassColors;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

@Mod.EventBusSubscriber
public class ClientProxy extends CommonProxy {
    public static final ModelResourceLocation RAT_NUGGET_MODEL = new ModelResourceLocation(new ResourceLocation(RatsMod.MODID, "rat_nugget_ore"), "inventory");
    @OnlyIn(Dist.CLIENT)
    private static final RatsTEISR TEISR = new RatsTEISR();
    @OnlyIn(Dist.CLIENT)
    private static final ModelChefToque MODEL_CHEF_TOQUE = new ModelChefToque(1.0F);
    public static BlockPos refrencedPos;
    public static Direction refrencedFacing;
    protected static EntityRat refrencedRat;

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
      /*  for (int i = 0; i < 16; i++) {
            ModelLoader.setCustomStateMapper(RatsBlockRegistry.RAT_TUBE_COLOR[i], (new StateMapperGeneric("rat_tube")));
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(RatsBlockRegistry.RAT_TUBE_COLOR[i]), 0, new ModelResourceLocation("rats:rat_tube", "inventory"));
            ModelLoader.setCustomModelResourceLocation(RatsItemRegistry.RAT_IGLOOS[i], 0, new ModelResourceLocation("rats:rat_igloo", "inventory"));
            ModelLoader.setCustomModelResourceLocation(RatsItemRegistry.RAT_HAMMOCKS[i], 0, new ModelResourceLocation("rats:rat_hammock", "inventory"));
        }

        ModelLoader.setCustomMeshDefinition(RatsItemRegistry.RAT_NUGGET_ORE, stack -> RAT_NUGGET_MODEL);
        ModelBakery.registerItemVariants(RatsItemRegistry.RAT_NUGGET_ORE, RAT_NUGGET_MODEL);
        try {
            for (Field f : RatsBlockRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Block && !(obj instanceof ICustomRendered)) {
                    ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock((Block) obj), 0, new ModelResourceLocation("rats:" + ((Block) obj).getRegistryName().getPath(), "inventory"));
                } else if (obj instanceof Block[]) {
                    for (Block block : (Block[]) obj) {
                        if (!(block instanceof ICustomRendered)) {
                            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation("rats:" + block.getRegistryName().getPath(), "inventory"));
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try {
            for (Field f : RatsItemRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof Item && !(obj instanceof ICustomRendered)) {
                    ModelLoader.setCustomModelResourceLocation((Item) obj, 0, new ModelResourceLocation("rats:" + ((Item) obj).getRegistryName().getPath(), "inventory"));
                } else if (obj instanceof Item[]) {
                    for (Item item : (Item[]) obj) {
                        if (!(item instanceof ICustomRendered)) {
                            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation("rats:" + item.getRegistryName().getPath(), "inventory"));
                        }
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }*/
    }

    public void preInit() {
        //TinkersCompatBridge.loadTinkersClientCompat();
        RatsGuiRegistry.register();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void init() {
        MinecraftForge.EVENT_BUS.register(new com.github.alexthe666.rats.client.event.ClientEvents());
        RenderingRegistry.registerEntityRenderingHandler(EntityRat.class, manager -> new RenderRat());
        RenderingRegistry.registerEntityRenderingHandler(EntityIllagerPiper.class, manager -> new RenderIllagerPiper());
        RenderingRegistry.registerEntityRenderingHandler(EntityRatlanteanSpirit.class, manager -> new RenderRatlateanSpirit());
        RenderingRegistry.registerEntityRenderingHandler(EntityRatlanteanFlame.class, manager -> new RenderRatlanteanFlame());
        RenderingRegistry.registerEntityRenderingHandler(EntityMarbleCheeseGolem.class, manager -> new RenderMarbledCheeseGolem());
        RenderingRegistry.registerEntityRenderingHandler(EntityGolemBeam.class, manager -> new RenderGolemBeam());
        RenderingRegistry.registerEntityRenderingHandler(EntityFeralRatlantean.class, manager -> new RenderFeralRatlantean());
        RenderingRegistry.registerEntityRenderingHandler(EntityNeoRatlantean.class, manager -> new RenderNeoRatlantean());
        RenderingRegistry.registerEntityRenderingHandler(EntityLaserBeam.class, manager -> new RenderLaserBeam());
        RenderingRegistry.registerEntityRenderingHandler(EntityLaserPortal.class, manager -> new RenderLaserPortal());
        RenderingRegistry.registerEntityRenderingHandler(EntityThrownBlock.class, manager -> new RenderThrownBlock());
        RenderingRegistry.registerEntityRenderingHandler(EntityVialOfSentience.class, manager -> new SpriteRenderer(Minecraft.getInstance().getRenderManager(), Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(EntityPiratBoat.class, manager -> new RenderPiratBoat());
        RenderingRegistry.registerEntityRenderingHandler(EntityCheeseCannonball.class, manager -> new SpriteRenderer(Minecraft.getInstance().getRenderManager(), Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(EntityPirat.class, manager -> new RenderPirat());
        RenderingRegistry.registerEntityRenderingHandler(EntityPlagueDoctor.class, manager -> new RenderPlagueDoctor());
        RenderingRegistry.registerEntityRenderingHandler(EntityPurifyingLiquid.class, manager -> new SpriteRenderer(Minecraft.getInstance().getRenderManager(), Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(EntityBlackDeath.class, manager -> new RenderBlackDeath());
        RenderingRegistry.registerEntityRenderingHandler(EntityPlagueCloud.class, manager -> new RenderRatlateanSpirit());
        RenderingRegistry.registerEntityRenderingHandler(EntityPlagueBeast.class, manager -> new RenderPlagueBeast());
        RenderingRegistry.registerEntityRenderingHandler(EntityPlagueShot.class, manager -> new RenderPlagueShot());
        RenderingRegistry.registerEntityRenderingHandler(EntityRatCaptureNet.class, manager -> new SpriteRenderer(Minecraft.getInstance().getRenderManager(), Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(EntityRatDragonFire.class, manager -> new RenderNothing());
        RenderingRegistry.registerEntityRenderingHandler(EntityRatArrow.class, manager -> new RenderRatArrow());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRatHole.class, new RenderRatHole());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRatTrap.class, new RenderRatTrap());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAutoCurdler.class, new RenderAutoCurdler());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRatlantisPortal.class, new RenderRatlantisPortal());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRatCageDecorated.class, new RenderRatCageDecorated());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRatCageBreedingLantern.class, new RenderRatCageDecorated());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUpgradeCombiner.class, new RenderUpgradeCombiner());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityUpgradeSeparator.class, new RenderUpgradeSeparator());
        /*Item.getItemFromBlock(RatsBlockRegistry.RAT_HOLE).setTileItemEntityStackRenderer(TEISR);
        Item.getItemFromBlock(RatsBlockRegistry.RAT_TRAP).setTileItemEntityStackRenderer(TEISR);
        Item.getItemFromBlock(RatsBlockRegistry.AUTO_CURDLER).setTileItemEntityStackRenderer(TEISR);
        Item.getItemFromBlock(RatsBlockRegistry.RATLANTIS_PORTAL).setTileItemEntityStackRenderer(TEISR);*/


        //ModelBakery.registerItemVariants(RatsItemRegistry.RAT_SACK, new ResourceLocation("iceandfire:rat_sack"), new ResourceLocation("iceandfire:rat_sack_1"), new ResourceLocation("iceandfire:rat_sack_2"), new ResourceLocation("iceandfire:rat_sack_3"));
    }

    @SubscribeEvent
    public static void onBlockColors(ColorHandlerEvent.Block event) {
        RatsMod.LOGGER.info("loaded in block colorizer");
        event.getBlockColors().register(new IBlockColor() {
            @Override
            public int getColor(BlockState state, @Nullable IEnviromentBlockReader worldIn, @Nullable BlockPos pos, int colorIn) {
                Block block = state.getBlock();
                int meta = 0;
                for (int i = 0; i < RatsBlockRegistry.RAT_TUBE_COLOR.length; i++) {
                    if (block == RatsBlockRegistry.RAT_TUBE_COLOR[i]) {
                        meta = i;
                    }
                }
                DyeColor color = DyeColor.byId(meta);
                return color.getFireworkColor();
            }
        }, RatsBlockRegistry.RAT_TUBE_COLOR);
        event.getBlockColors().register(new IBlockColor() {
            @Override
            public int getColor(BlockState state, @Nullable IEnviromentBlockReader worldIn, @Nullable BlockPos pos, int colorIn) {
                return worldIn != null && pos != null ? BiomeColors.getFoliageColor(worldIn, pos) : GrassColors.get(0.5D, 1.0D);
            }
        }, RatsBlockRegistry.MARBLED_CHEESE_GRASS);
    }

    @SubscribeEvent
    public static void onItemColors(ColorHandlerEvent.Item event) {
        RatsMod.LOGGER.info("loaded in item colorizer");
        event.getItemColors().register(new IItemColor() {
            @Override
            public int getColor(ItemStack p_getColor_1_, int p_getColor_2_) {
                return GrassColors.get(0.5D, 1.0D);
            }
        }, Item.getItemFromBlock(RatsBlockRegistry.MARBLED_CHEESE_GRASS));
        event.getItemColors().register(new IItemColor() {
            @Override
            public int getColor(ItemStack p_getColor_1_, int p_getColor_2_) {
                Block block = Block.getBlockFromItem(p_getColor_1_.getItem());
                int meta = 0;
                for (int i = 0; i < RatsBlockRegistry.RAT_TUBE_COLOR.length; i++) {
                    if (block == RatsBlockRegistry.RAT_TUBE_COLOR[i]) {
                        meta = i;
                    }
                }
                DyeColor color = DyeColor.byId(meta);
                return color.getFireworkColor();
            }
        }, RatsBlockRegistry.RAT_TUBE_COLOR);
        event.getItemColors().register(new IItemColor() {
            @Override
            public int getColor(ItemStack p_getColor_1_, int p_getColor_2_) {
                int meta = 0;
                for (int i = 0; i < RatsItemRegistry.RAT_IGLOOS.length; i++) {
                    if (p_getColor_1_.getItem() == RatsItemRegistry.RAT_IGLOOS[i]) {
                        meta = i;
                    }
                }
                DyeColor color = DyeColor.byId(meta);
                return color.getFireworkColor();
            }
        }, RatsItemRegistry.RAT_IGLOOS);
        event.getItemColors().register(new IItemColor() {
            @Override
            public int getColor(ItemStack p_getColor_1_, int p_getColor_2_) {
                if (p_getColor_2_ == 0) {
                    int meta = 0;
                    for (int i = 0; i < RatsItemRegistry.RAT_HAMMOCKS.length; i++) {
                        if (p_getColor_1_.getItem() == RatsItemRegistry.RAT_HAMMOCKS[i]) {
                            meta = i;
                        }
                    }
                    DyeColor color = DyeColor.byId(meta);
                    return color.getFireworkColor();
                } else {
                    return -1;
                }
            }
        }, RatsItemRegistry.RAT_HAMMOCKS);
        event.getItemColors().register(new IItemColor() {
            @Override
            public int getColor(ItemStack p_getColor_1_, int p_getColor_2_) {
                if (p_getColor_2_ == 1) {
                    return NuggetColorRegister.getNuggetColor(p_getColor_1_);
                } else {
                    return -1;
                }
            }
        }, RatsItemRegistry.RAT_NUGGET_ORE);
    }

    @OnlyIn(Dist.CLIENT)
    public void postInit() {
        for (Map.Entry<Class<? extends Entity>, EntityRenderer<? extends Entity>> entry : Minecraft.getInstance().getRenderManager().renderers.entrySet()) {
            EntityRenderer render = entry.getValue();
            if (render instanceof LivingRenderer && LivingEntity.class.isAssignableFrom(entry.getKey())) {
                ((LivingRenderer) render).addLayer(new LayerPlague((LivingRenderer) render));
            }
        }
        for (Map.Entry<String, PlayerRenderer> entry : Minecraft.getInstance().getRenderManager().getSkinMap().entrySet()) {
            PlayerRenderer render = entry.getValue();
            render.addLayer(new LayerPlague(render));
        }
        //        Map<String, FallbackResourceManager> resManagers = ObfuscationReflectionHelper.getPrivateValue(SimpleReloadableResourceManager.class, (SimpleReloadableResourceManager)Minecraft.getInstance().getResourceManager(), "field_199014"+"_c");
        Field renderingRegistryField = ObfuscationReflectionHelper.findField(RenderingRegistry.class, "INSTANCE");
        Field entityRendersField = ObfuscationReflectionHelper.findField(RenderingRegistry.class, "entityRenderers");
        RenderingRegistry registry = null;
        try {
            Field modifier = Field.class.getDeclaredField("modifiers");
            modifier.setAccessible(true);
            registry = (RenderingRegistry) renderingRegistryField.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (registry != null) {
            Map<Class<? extends Entity>, IRenderFactory<? extends Entity>> entityRenders = null;
            try {
                Field modifier1 = Field.class.getDeclaredField("modifiers");
                modifier1.setAccessible(true);
                entityRenders = (Map<Class<? extends Entity>, IRenderFactory<? extends Entity>>) entityRendersField.get(registry);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (entityRenders != null) {
                for (Map.Entry<Class<? extends Entity>, IRenderFactory<? extends Entity>> entry : entityRenders.entrySet()) {
                    if (entry.getValue() != null) {
                        try {
                            EntityRenderer render = entry.getValue().createRenderFor(Minecraft.getInstance().getRenderManager());
                            if (render != null && render instanceof LivingRenderer && LivingEntity.class.isAssignableFrom(entry.getKey())) {
                                ((LivingRenderer) render).addLayer(new LayerPlague((LivingRenderer) render));
                            }
                        } catch (NullPointerException exp) {
                            RatsMod.LOGGER.warn("Rats: Could not apply plague render layer to " + entry.getKey().getSimpleName() + ", someone isn't registering their renderer properly... <.<");
                        }
                    }

                }
            }
        }
    }

    public boolean shouldRenderNameplates() {
        return Minecraft.getInstance().currentScreen == null || !(Minecraft.getInstance().currentScreen instanceof GuiRat) && !(Minecraft.getInstance().currentScreen instanceof GuiCheeseStaff);
    }

    @OnlyIn(Dist.CLIENT)
    public Object getArmorModel(int index) {
        if (index == 0) {
            return new ModelChefToque(1.0F);
        } else if (index == 1) {
            return new ModelPiperHat(1.0F);
        } else if (index == 2) {
            return new ModelPiratHat(1.0F);
        } else if (index == 3) {
            return new ModelArcheologistHat(1.0F);
        } else if (index == 4) {
            return new ModelFarmerHat(1.0F);
        } else if (index == 5) {
            return new ModelPlagueDoctorMask(1.0F);
        } else if (index == 6) {
            return new ModelRatFez(1.0F);
        } else if (index == 7) {
            return new ModelTopHat(1.0F);
        } else {
            return new ModelSantaHat(1.0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void openCheeseStaffGui() {
        if (refrencedRat != null) {
            Minecraft.getInstance().displayGuiScreen(new GuiCheeseStaff(refrencedRat));
        }
    }

    @Override
    public EntityRat getRefrencedRat() {
        return refrencedRat;
    }

    @Override
    public void setRefrencedRat(EntityRat rat) {
        refrencedRat = rat;
    }

    @Override
    public void setCheeseStaffContext(BlockPos pos, Direction facing) {
        refrencedPos = pos;
        refrencedFacing = facing;
    }

    @SubscribeEvent
    public static void registerParticles(final RegistryEvent.Register<ParticleType<?>> event) {
        try {
            for (Field f : RatsParticleRegistry.class.getDeclaredFields()) {
                Object obj = f.get(null);
                if (obj instanceof ParticleType) {
                    event.getRegistry().register((ParticleType) obj);
                } else if (obj instanceof ParticleType[]) {
                    for (ParticleType particle : (ParticleType[]) obj) {
                        event.getRegistry().register(particle);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addParticle(String name, double x, double y, double z, double motX, double motY, double motZ) {
        World world = Minecraft.getInstance().world;
        if (world == null) {
            return;
        }
        if (name.equals("rat_ghost")) {
            Minecraft.getInstance().particles.addEffect(new ParticleRatGhost(world, x, y, z, (float) motX, (float) motY, (float) motZ));
        }
        if (name.equals("rat_lightning")) {
            Minecraft.getInstance().particles.addEffect(new ParticleLightning(world, x, y, z, (float) motX, (float) motY, (float) motZ));
        }
        if (name.equals("flea")) {
            Minecraft.getInstance().particles.addEffect(new ParticleFlea(world, x, y, z, (float) motX, (float) motY, (float) motZ));
        }
        if (name.equals("upgrade_combiner")) {
            Minecraft.getInstance().particles.addEffect(new ParticleUpgradeCombiner(world, x, y, z, (float) motX, (float) motY, (float) motZ));
        }
        if (name.equals("saliva")) {
            Minecraft.getInstance().particles.addEffect(new ParticleSaliva(world, x, y, z, Fluids.WATER));
        }
        if (name.equals("black_death")) {
            Minecraft.getInstance().particles.addEffect(new ParticleBlackDeath.DeathFactory(null).makeParticle(RatsParticleRegistry.PARTICLE_BLACK_DEATH, world, x, y, z, (float) motX, (float) motY, (float) motZ));
        }
    }
}
