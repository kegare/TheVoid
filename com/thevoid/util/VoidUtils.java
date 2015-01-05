/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.thevoid.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;

import com.thevoid.world.TeleporterDummy;

public class VoidUtils
{
	public static void setPlayerLocation(EntityPlayerMP player, double posX, double posY, double posZ)
	{
		setPlayerLocation(player, posX, posY, posZ, player.rotationYaw, player.rotationPitch);
	}

	public static void setPlayerLocation(EntityPlayerMP player, double posX, double posY, double posZ, float yaw, float pitch)
	{
		int x = MathHelper.floor_double(posX);
		int z = MathHelper.floor_double(posZ);
		IChunkProvider provider = player.getServerForPlayer().getChunkProvider();

		provider.loadChunk(x - 3 >> 4, z - 3 >> 4);
		provider.loadChunk(x + 3 >> 4, z - 3 >> 4);
		provider.loadChunk(x - 3 >> 4, z + 3 >> 4);
		provider.loadChunk(x + 3 >> 4, z + 3 >> 4);

		player.mountEntity(null);
		player.playerNetServerHandler.setPlayerLocation(posX, posY, posZ, yaw, pitch);
	}

	public static boolean transferPlayer(EntityPlayerMP player, int dim)
	{
		if (dim != player.dimension)
		{
			if (!DimensionManager.isDimensionRegistered(dim))
			{
				return false;
			}

			player.isDead = false;
			player.forceSpawn = true;
			player.timeUntilPortal = player.getPortalCooldown();
			player.mcServer.getConfigurationManager().transferPlayerToDimension(player, dim, new TeleporterDummy(player.mcServer.worldServerForDimension(dim)));
			player.addExperienceLevel(0);

			return true;
		}

		return false;
	}

	public static boolean teleportPlayer(EntityPlayerMP player, int dim)
	{
		transferPlayer(player, dim);

		WorldServer world = player.getServerForPlayer();
		int originX = MathHelper.floor_double(player.posX);
		int originZ = MathHelper.floor_double(player.posZ);
		int range = 256;

		for (int x = originX - range; x < originX + range; ++x)
		{
			for (int z = originZ - range; z < originZ + range; ++z)
			{
				for (int y = world.getActualHeight() - 5; y > 1; --y)
				{
					if (world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
					{
						while (y > 1 && world.isAirBlock(x, y - 1, z))
						{
							--y;
						}

						if (!world.isAirBlock(x, y - 1, z) && !world.getBlock(x, y - 1, z).getMaterial().isLiquid())
						{
							setPlayerLocation(player, x + 0.5D, y + 0.5D, z + 0.5D);

							return true;
						}
					}
				}
			}
		}

		return respawnPlayer(player, 0);
	}

	public static boolean respawnPlayer(EntityPlayerMP player, int dim)
	{
		transferPlayer(player, dim);

		WorldServer world = player.getServerForPlayer();
		ChunkCoordinates spawn = player.getBedLocation(player.dimension);

		if (spawn != null)
		{
			spawn = EntityPlayer.verifyRespawnCoordinates(world, spawn, true);
		}

		if (spawn == null)
		{
			spawn = world.getSpawnPoint();
		}

		int x = spawn.posX;
		int y = spawn.posY;
		int z = spawn.posZ;

		if (world.isAirBlock(x, y, z) && world.isAirBlock(x, y + 1, z))
		{
			while (world.isAirBlock(x, y - 1, z))
			{
				--y;
			}

			if (!world.isAirBlock(x, y - 1, z) && !world.getBlock(x, y - 1, z).getMaterial().isLiquid())
			{
				setPlayerLocation(player, x + 0.5D, y + 0.5D, z + 0.5D);

				return true;
			}
		}

		return teleportPlayer(player, dim);
	}
}