/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.thevoid.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.thevoid.core.Config;
import com.thevoid.world.TeleporterVoid;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockVoidPortal extends BlockPortal
{
	public BlockVoidPortal(String name)
	{
		super();
		this.setBlockName(name);
		this.setBlockTextureName("thevoid:portal");
		this.setBlockUnbreakable();
		this.setStepSound(soundTypeGlass);
		this.setTickRandomly(false);
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.disableStats();
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);

		if (meta == 0)
		{
			if (world.getBlock(x - 1, y, z) != this && world.getBlock(x + 1, y, z) != this)
			{
				meta = 2;
			}
			else
			{
				meta = 1;
			}

			if (world instanceof World && !((World)world).isRemote)
			{
				((World)world).setBlockMetadataWithNotify(x, y, z, meta, 2);
			}
		}

		float var1 = 0.15F;
		float var2 = 0.15F;

		if (meta % 2 != 0)
		{
			var1 = 0.5F;
		}
		else
		{
			var2 = 0.5F;
		}

		setBlockBounds(0.5F - var1, 0.0F, 0.5F - var2, 0.5F + var1, 1.0F, 0.5F + var2);
	}

	@Override
	public boolean func_150000_e(World world, int x, int y, int z)
	{
		Size size = new Size(world, x, y, z, 1);

		if (size.canCreatePortal() && size.portalBlockCount == 0)
		{
			size.setPortalBlocks();

			return true;
		}

		size = new Size(world, x, y, z, 2);

		if (size.canCreatePortal() && size.portalBlockCount == 0)
		{
			size.setPortalBlocks();

			return true;
		}

		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		int meta = world.getBlockMetadata(x, y, z);
		Size size = new Size(world, x, y, z, 1);

		if (meta == 1 && (!size.canCreatePortal() || size.portalBlockCount < size.portalWidth * size.portalHeight))
		{
			world.setBlockToAir(x, y, z);

			return;
		}

		size = new Size(world, x, y, z, 2);

		if (meta == 2 && (!size.canCreatePortal() || size.portalBlockCount < size.portalWidth * size.portalHeight))
		{
			world.setBlockToAir(x, y, z);
		}
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
	{
		int meta = 0;

		if (world.getBlock(x, y, z) == this)
		{
			meta = world.getBlockMetadata(x, y, z);

			if (meta == 0)
			{
				return false;
			}

			if (meta % 2 == 0 && side != 5 && side != 4)
			{
				return false;
			}

			if (meta % 2 != 0 && side != 3 && side != 2)
			{
				return false;
			}
		}

		boolean flag = world.getBlock(x - 1, y, z) == this && world.getBlock(x - 2, y, z) != this;
		boolean flag1 = world.getBlock(x + 1, y, z) == this && world.getBlock(x + 2, y, z) != this;
		boolean flag2 = world.getBlock(x, y, z - 1) == this && world.getBlock(x, y, z - 2) != this;
		boolean flag3 = world.getBlock(x, y, z + 1) == this && world.getBlock(x, y, z + 2) != this;
		boolean flag4 = flag || flag1 || meta == 1;
		boolean flag5 = flag2 || flag3 || meta == 2;

		return flag4 && side == 4 ? true : flag4 && side == 5 ? true : flag5 && side == 2 ? true : flag5 && side == 3;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if (world.isRemote || entity.isDead)
		{
			return;
		}

		if (entity.timeUntilPortal <= 0)
		{
			MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
			int dimOld = entity.dimension;
			int dimNew = dimOld == Config.dimensionTheVoid ? entity.getEntityData().getInteger("TheVoid:LastDim") : Config.dimensionTheVoid;
			WorldServer worldOld = server.worldServerForDimension(dimOld);
			WorldServer worldNew = server.worldServerForDimension(dimNew);

			if (worldOld == null || worldNew == null)
			{
				return;
			}

			Teleporter teleporter = new TeleporterVoid(worldNew);

			entity.worldObj.removeEntity(entity);
			entity.isDead = false;
			entity.timeUntilPortal = entity.getPortalCooldown();

			if (entity instanceof EntityPlayerMP)
			{
				EntityPlayerMP player = (EntityPlayerMP)entity;

				if (!player.isSneaking() && !player.isPotionActive(Potion.blindness))
				{
					worldOld.playSoundToNearExcept(player, "thevoid:portal", 0.5F, 1.0F);

					server.getConfigurationManager().transferPlayerToDimension(player, dimNew, teleporter);

					worldNew.playSoundAtEntity(player, "thevoid:portal", 0.75F, 1.0F);

					player.getEntityData().setInteger("TheVoid:LastDim", dimOld);
				}
			}
			else
			{
				entity.dimension = dimNew;

				server.getConfigurationManager().transferEntityToWorld(entity, dimOld, worldOld, worldNew, teleporter);

				Entity target = EntityList.createEntityByName(EntityList.getEntityString(entity), worldNew);

				if (target != null)
				{
					worldOld.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, "thevoid:portal", 0.25F, 1.15F);

					target.copyDataFrom(entity, true);
					target.forceSpawn = true;

					worldNew.spawnEntityInWorld(target);
					worldNew.playSoundAtEntity(target, "thevoid:portal", 0.5F, 1.15F);

					target.forceSpawn = false;
					target.getEntityData().setInteger("TheVoid:LastDim", dimOld);
				}

				entity.setDead();

				worldOld.resetUpdateEntityTick();
				worldNew.resetUpdateEntityTick();
			}
		}
		else
		{
			entity.timeUntilPortal = entity.getPortalCooldown();
		}
	}

	@Override
	public boolean func_149698_L()
	{
		return false;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random)
	{
		if (random.nextInt(5) == 0)
		{
			double ptX = x + random.nextFloat();
			double ptY = y + 0.5D;
			double ptZ = z + random.nextFloat();

			world.spawnParticle("reddust", ptX, ptY, ptZ, 0.01D, 0.01D, 0.01D);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon(getTextureName());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getItemIconName()
	{
		return "thevoid:void_core";
	}

	private class Size
	{
		private final World worldObj;
		private final int portalMetadata;
		private final boolean portalDiffer;
		private final int field_150863_d;
		private final int field_150866_c;

		private ChunkCoordinates portalCoord;
		private int portalWidth;
		private int portalHeight;

		private int portalBlockCount = 0;

		public Size(World world, int x, int y, int z, int metadata)
		{
			this.worldObj = world;
			this.portalMetadata = metadata;
			this.portalDiffer = metadata % 2 == 0;
			int i = portalDiffer ? 2 : 1;
			this.field_150863_d = BlockPortal.field_150001_a[i][0];
			this.field_150866_c = BlockPortal.field_150001_a[i][1];

			i = y;

			while (y > i - 21 && y > 0 && isReplaceablePortal(world.getBlock(x, y - 1, z)))
			{
				--y;
			}

			i = getPortalWidth(x, y, z, field_150863_d) - 1;

			if (i >= 0)
			{
				this.portalCoord = new ChunkCoordinates(x + i * Direction.offsetX[field_150863_d], y, z + i * Direction.offsetZ[field_150863_d]);
				this.portalWidth = getPortalWidth(portalCoord.posX, portalCoord.posY, portalCoord.posZ, field_150866_c);

				if (portalWidth < 2 || portalWidth > 21)
				{
					this.portalCoord = null;
					this.portalWidth = 0;
				}
			}

			if (portalCoord != null)
			{
				this.portalHeight = getPortalHeight();
			}
		}

		protected int getPortalWidth(int x, int y, int z, int par4)
		{
			int var1 = Direction.offsetX[par4];
			int var2 = Direction.offsetZ[par4];
			int i;

			for (i = 0; i < 22; ++i)
			{
				if (!isReplaceablePortal(worldObj.getBlock(x + var1 * i, y, z + var2 * i)))
				{
					break;
				}

				if (worldObj.getBlock(x + var1 * i, y - 1, z + var2 * i) != Blocks.end_stone)
				{
					break;
				}
			}

			return worldObj.getBlock(x + var1 * i, y, z + var2 * i) == Blocks.end_stone ? i : 0;
		}

		protected int getPortalHeight()
		{
			int i, x, y, z;

			outside: for (portalHeight = 0; portalHeight < 21; ++portalHeight)
			{
				y = portalCoord.posY + portalHeight;

				for (i = 0; i < portalWidth; ++i)
				{
					x = portalCoord.posX + i * Direction.offsetX[field_150866_c];
					z = portalCoord.posZ + i * Direction.offsetZ[field_150866_c];
					Block block = worldObj.getBlock(x, y, z);

					if (!isReplaceablePortal(block))
					{
						break outside;
					}

					if (block == BlockVoidPortal.this)
					{
						++portalBlockCount;
					}

					if (i == 0)
					{
						block = worldObj.getBlock(x + Direction.offsetX[field_150863_d], y, z + Direction.offsetZ[field_150863_d]);

						if (block != Blocks.end_stone)
						{
							break outside;
						}
					}
					else if (i == portalWidth - 1)
					{
						block = worldObj.getBlock(x + Direction.offsetX[field_150866_c], y, z + Direction.offsetZ[field_150866_c]);

						if (block != Blocks.end_stone)
						{
							break outside;
						}
					}
				}
			}

			for (y = 0; y < portalWidth; ++y)
			{
				i = portalCoord.posX + y * Direction.offsetX[field_150866_c];
				x = portalCoord.posY + portalHeight;
				z = portalCoord.posZ + y * Direction.offsetZ[field_150866_c];

				if (worldObj.getBlock(i, x, z) != Blocks.end_stone)
				{
					portalHeight = 0;

					break;
				}
			}

			if (portalHeight <= 21 && portalHeight >= 3)
			{
				return portalHeight;
			}

			portalCoord = null;
			portalWidth = 0;
			portalHeight = 0;

			return 0;
		}

		protected boolean isReplaceablePortal(Block block)
		{
			return block.getMaterial() == Material.air || block == BlockVoidPortal.this;
		}

		public boolean canCreatePortal()
		{
			if (portalCoord != null && portalWidth >= 2 && portalWidth <= 21 && portalHeight >= 3 && portalHeight <= 21)
			{
				for (int i = 0; i < portalWidth; ++i)
				{
					int x = portalCoord.posX + Direction.offsetX[field_150866_c] * i;
					int z = portalCoord.posZ + Direction.offsetZ[field_150866_c] * i;

					for (int j = 0; j < portalHeight; ++j)
					{
						if (portalDiffer)
						{
							if (worldObj.getBlock(x + 1, portalCoord.posY + j, z) == BlockVoidPortal.this)
							{
								return false;
							}
							else if (worldObj.getBlock(x - 1, portalCoord.posY + j, z) == BlockVoidPortal.this)
							{
								return false;
							}
						}
						else
						{
							if (worldObj.getBlock(x, portalCoord.posY + j, z + 1) == BlockVoidPortal.this)
							{
								return false;
							}
							else if (worldObj.getBlock(x, portalCoord.posY + j, z - 1) == BlockVoidPortal.this)
							{
								return false;
							}
						}
					}
				}

				return true;
			}

			return false;
		}

		public void setPortalBlocks()
		{
			int x, z;

			for (int i = 0; i < portalWidth; ++i)
			{
				x = portalCoord.posX + Direction.offsetX[field_150866_c] * i;
				z = portalCoord.posZ + Direction.offsetZ[field_150866_c] * i;

				for (int j = 0; j < portalHeight; ++j)
				{
					worldObj.setBlock(x, portalCoord.posY + j, z, BlockVoidPortal.this, portalMetadata, 2);
				}
			}
		}
	}
}