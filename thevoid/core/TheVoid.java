/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package thevoid.core;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import thevoid.block.BlockVoidPortal;
import thevoid.handler.VoidEventHooks;
import thevoid.item.ItemVoidCore;
import thevoid.world.WorldProviderVoid;

@Mod(modid = "thevoid", guiFactory = "thevoid.client.config.VoidGuiFactory")
public class TheVoid
{
	public static final BlockVoidPortal void_portal = new BlockVoidPortal();

	public static final ItemVoidCore void_core = new ItemVoidCore();

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.syncConfig();

		GameRegistry.registerBlock(void_portal, "void_portal");

		GameRegistry.registerItem(void_core, "void_core");

		if (event.getSide().isClient())
		{
			ModelLoader.setCustomModelResourceLocation(void_core, 0, new ModelResourceLocation("thevoid:void_core", "inventory"));
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		int id = Config.dimensionTheVoid;

		DimensionManager.registerProviderType(id, WorldProviderVoid.class, true);
		DimensionManager.registerDimension(id, id);

		GameRegistry.addShapelessRecipe(new ItemStack(void_core), Items.ender_pearl, Items.map);

		MinecraftForge.EVENT_BUS.register(VoidEventHooks.instance);
	}
}