/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.thevoid.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

import com.thevoid.core.Config;
import com.thevoid.util.VoidUtils;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class VoidEventHooks
{
	public static final VoidEventHooks instance = new VoidEventHooks();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderGameTextOverlay(RenderGameOverlayEvent.Text event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();
		EntityPlayer player = mc.thePlayer;

		if (player != null && player.dimension == Config.dimensionTheVoid)
		{
			if (mc.gameSettings.showDebugInfo)
			{
				event.left.add("dim: The Void");
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent17 event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.theWorld != null && mc.theWorld.provider.dimensionId == Config.dimensionTheVoid)
		{
			SoundCategory category = event.category;

			if (category != null && (category == SoundCategory.MUSIC || category == SoundCategory.WEATHER || category == SoundCategory.AMBIENT))
			{
				event.result = null;
			}
		}
	}

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			if (player.dimension == Config.dimensionTheVoid)
			{
				int x = MathHelper.floor_double(player.posX);
				int y = MathHelper.floor_double(player.posY);
				int z = MathHelper.floor_double(player.posZ);

				if (player.getServerForPlayer().isAirBlock(x, y - 1, z))
				{
					VoidUtils.teleportPlayer(player, player.dimension);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;
			WorldServer world = player.getServerForPlayer();
			int x = MathHelper.floor_double(player.posX);
			int y = MathHelper.floor_double(player.posY);
			int z = MathHelper.floor_double(player.posZ);

			if (player.dimension == Config.dimensionTheVoid)
			{
				player.timeUntilPortal = 60;
			}

			if (world.isAirBlock(x, y - 1, z))
			{
				VoidUtils.teleportPlayer(player, player.dimension);
			}
		}
	}

	@SubscribeEvent
	public void onLivingCheckSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		if (event.entityLiving.dimension == Config.dimensionTheVoid)
		{
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void onLivingSpecialSpawn(LivingSpawnEvent.SpecialSpawn event)
	{
		if (event.entityLiving.dimension == Config.dimensionTheVoid)
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onLivingPackSize(LivingPackSizeEvent event)
	{
		if (event.entityLiving.dimension == Config.dimensionTheVoid)
		{
			event.maxPackSize = 0;
			event.setResult(Result.ALLOW);
		}
	}
}