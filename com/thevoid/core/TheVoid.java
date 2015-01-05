/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.thevoid.core;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.Level;

import com.thevoid.block.BlockVoidPortal;
import com.thevoid.handler.VoidEventHooks;
import com.thevoid.plugin.thaumcraft.ThaumcraftPlugin;
import com.thevoid.world.WorldProviderVoid;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod
(
	modid = "thevoid",
	acceptedMinecraftVersions = "[1.7.10,)"
)
public class TheVoid
{
	public static final BlockVoidPortal void_portal = new BlockVoidPortal("voidPortal");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		Config.syncConfig();

		GameRegistry.registerBlock(void_portal, "void_portal");
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		int id = Config.dimensionTheVoid;

		DimensionManager.registerProviderType(id, WorldProviderVoid.class, true);
		DimensionManager.registerDimension(id, id);

		FMLCommonHandler.instance().bus().register(VoidEventHooks.instance);

		MinecraftForge.EVENT_BUS.register(VoidEventHooks.instance);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		try
		{
			if (ThaumcraftPlugin.enabled())
			{
				ThaumcraftPlugin.invoke();
			}
		}
		catch (Throwable e)
		{
			FMLLog.log(Level.WARN, e, "Failed to trying invoke plugin: ThaumcraftPlugin");
		}
	}
}