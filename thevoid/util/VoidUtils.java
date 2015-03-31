/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package thevoid.util;

import thevoid.world.TeleporterDummy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class VoidUtils
{
	public static void setPlayerLocation(EntityPlayerMP player, double posX, double posY, double posZ)
	{
		setPlayerLocation(player, posX, posY, posZ, player.rotationYaw, player.rotationPitch);
	}

	public static void setPlayerLocation(EntityPlayerMP player, double posX, double posY, double posZ, float yaw, float pitch)
	{
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
		BlockPos pos = null;

		if (player.getBedLocation(dim) != null)
		{
			pos = EntityPlayer.getBedSpawnLocation(world, player.getBedLocation(dim), true);
		}

		if (pos == null)
		{
			pos = BlockPos.ORIGIN.up(64);
		}

		if (world.isAirBlock(pos) && world.isAirBlock(pos.up()))
		{
			do
			{
				pos = pos.down();
			}
			while (pos.getY() > 0 && world.isAirBlock(pos.down()));

			BlockPos pos2 = pos;
			pos = pos.up();

			if (!world.isAirBlock(pos2) && !world.getBlockState(pos2).getBlock().getMaterial().isLiquid())
			{
				setPlayerLocation(player, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);

				return true;
			}
		}
		else
		{
			int range = 64;

			for (int x = pos.getX() - range; x < pos.getX() + range; ++x)
			{
				for (int z = pos.getZ() - range; z < pos.getZ() + range; ++z)
				{
					for (int y = world.getActualHeight(); y > 0; --y)
					{
						BlockPos pos2 = new BlockPos(x, y, z);

						if (world.isAirBlock(pos2) && world.isAirBlock(pos2.up()))
						{
							do
							{
								pos2 = pos2.down();
							}
							while (pos2.getY() > 0 && world.isAirBlock(pos2.down()));

							BlockPos pos3 = pos2;
							pos2 = pos2.up();

							if (!world.isAirBlock(pos3) && !world.getBlockState(pos3).getBlock().getMaterial().isLiquid())
							{
								setPlayerLocation(player, pos2.getX() + 0.5D, pos2.getY() + 0.5D, pos2.getZ() + 0.5D);

								return true;
							}
						}
					}
				}
			}

			pos = BlockPos.ORIGIN.up(64);
			setPlayerLocation(player, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
			world.setBlockToAir(pos);
			world.setBlockToAir(pos.up());
			world.setBlockState(pos, Blocks.dirt.getDefaultState());
		}

		return false;
	}
}