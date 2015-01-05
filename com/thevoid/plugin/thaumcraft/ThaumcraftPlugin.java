/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.thevoid.plugin.thaumcraft;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import com.thevoid.core.TheVoid;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional.Method;

public class ThaumcraftPlugin
{
	public static final String MODID = "Thaumcraft";

	public static boolean enabled()
	{
		return Loader.isModLoaded(MODID);
	}

	@Method(modid = MODID)
	public static void invoke()
	{
		ThaumcraftApi.registerObjectTag(new ItemStack(TheVoid.void_portal, 1, OreDictionary.WILDCARD_VALUE), new AspectList().add(Aspect.TRAVEL, 4).add(Aspect.VOID, 5));
	}
}