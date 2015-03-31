/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package thevoid.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thevoid.core.Config;
import thevoid.util.VoidUtils;

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
				event.left.add("Dim: The Void");
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlaySound(PlaySoundEvent event)
	{
		Minecraft mc = FMLClientHandler.instance().getClient();

		if (mc.theWorld != null && mc.theWorld.provider.getDimensionId() == Config.dimensionTheVoid)
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
				if (player.getServerForPlayer().isAirBlock(player.getPosition().down()))
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

			if (player.dimension == Config.dimensionTheVoid)
			{
				player.timeUntilPortal = 60;
			}

			if (world.isAirBlock(player.getPosition().down()))
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