/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.thevoid.handler;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingPackSizeEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;

import com.thevoid.core.Config;
import com.thevoid.core.TheVoid;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
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

	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if (event.player instanceof EntityPlayerMP)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.player;

			if (player.dimension == Config.dimensionTheVoid)
			{
				player.timeUntilPortal = 60;
			}
		}
	}

	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.entityPlayer instanceof EntityPlayerMP && event.action == Action.RIGHT_CLICK_BLOCK)
		{
			EntityPlayerMP player = (EntityPlayerMP)event.entityPlayer;
			ItemStack current = player.getCurrentEquippedItem();
			WorldServer world = player.getServerForPlayer();

			if (current != null && current.getItem() == Items.ender_pearl)
			{
				int x = event.x;
				int y = event.y;
				int z = event.z;

				switch (event.face)
				{
					case 0:
						--y;
						break;
					case 1:
						++y;
						break;
					case 2:
						--z;
						break;
					case 3:
						++z;
						break;
					case 4:
						--x;
						break;
					case 5:
						++x;
						break;
				}

				if (TheVoid.void_portal.func_150000_e(world, x, y, z))
				{
					world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, Block.soundTypeGlass.func_150496_b(), 1.0F, 2.0F);

					if (!player.capabilities.isCreativeMode && --current.stackSize <= 0)
					{
						player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					}

					event.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingCheckSpawn(LivingSpawnEvent.CheckSpawn event)
	{
		if (event.entityLiving.dimension == Config.dimensionTheVoid && event.entityLiving instanceof IMob)
		{
			event.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
	public void onLivingSpecialSpawn(LivingSpawnEvent.SpecialSpawn event)
	{
		if (event.entityLiving.dimension == Config.dimensionTheVoid && event.entityLiving instanceof IMob)
		{
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onLivingPackSize(LivingPackSizeEvent event)
	{
		if (event.entityLiving.dimension == Config.dimensionTheVoid && event.entityLiving instanceof IMob)
		{
			event.maxPackSize = 0;
			event.setResult(Result.ALLOW);
		}
	}
}