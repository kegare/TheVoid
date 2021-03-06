/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package thevoid.block;

import java.util.Random;

import com.google.common.cache.LoadingCache;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thevoid.core.Config;
import thevoid.world.TeleporterVoid;

public class BlockVoidPortal extends BlockPortal
{
	public BlockVoidPortal()
	{
		super();
		this.setUnlocalizedName("voidPortal");
		this.setStepSound(soundTypeGlass);
		this.setTickRandomly(false);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {}

	@Override
	public boolean func_176548_d(World worldIn, BlockPos pos)
	{
		Size size = new Size(worldIn, pos, EnumFacing.Axis.X);

		if (size.func_150860_b() && size.field_150864_e == 0)
		{
			size.func_150859_c();

			return true;
		}
		else
		{
			Size size1 = new Size(worldIn, pos, EnumFacing.Axis.Z);

			if (size1.func_150860_b() && size1.field_150864_e == 0)
			{
				size1.func_150859_c();

				return true;
			}
			else return false;
		}
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
	{
		EnumFacing.Axis axis = state.getValue(AXIS);
		Size size;

		if (axis == EnumFacing.Axis.X)
		{
			size = new Size(worldIn, pos, EnumFacing.Axis.X);

			if (!size.func_150860_b() || size.field_150864_e < size.field_150868_h * size.field_150862_g)
			{
				worldIn.setBlockState(pos, Blocks.air.getDefaultState());
			}
		}
		else if (axis == EnumFacing.Axis.Z)
		{
			size = new Size(worldIn, pos, EnumFacing.Axis.Z);

			if (!size.func_150860_b() || size.field_150864_e < size.field_150868_h * size.field_150862_g)
			{
				worldIn.setBlockState(pos, Blocks.air.getDefaultState());
			}
		}
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
	{
		if (!worldIn.isRemote && entityIn.isEntityAlive())
		{
			if (entityIn.timeUntilPortal <= 0)
			{
				MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
				int dimOld = entityIn.dimension;
				int dimNew = dimOld == Config.dimensionTheVoid ? entityIn.getEntityData().getInteger("TheVoid:LastDim") : Config.dimensionTheVoid;
				WorldServer worldOld = server.worldServerForDimension(dimOld);
				WorldServer worldNew = server.worldServerForDimension(dimNew);

				if (worldOld == null || worldNew == null)
				{
					return;
				}

				Teleporter teleporter = new TeleporterVoid(worldNew);

				entityIn.worldObj.removeEntity(entityIn);
				entityIn.isDead = false;
				entityIn.timeUntilPortal = entityIn.getPortalCooldown();

				if (entityIn instanceof EntityPlayerMP)
				{
					EntityPlayerMP player = (EntityPlayerMP)entityIn;

					if (!player.isSneaking() && !player.isPotionActive(Potion.blindness))
					{
						worldOld.playSoundToNearExcept(player, "thevoid:void_portal", 0.5F, 1.0F);

						server.getConfigurationManager().transferPlayerToDimension(player, dimNew, teleporter);

						worldNew.playSoundAtEntity(player, "thevoid:void_portal", 0.75F, 1.0F);

						player.getEntityData().setInteger("TheVoid:LastDim", dimOld);
					}
				}
				else
				{
					entityIn.dimension = dimNew;

					server.getConfigurationManager().transferEntityToWorld(entityIn, dimOld, worldOld, worldNew, teleporter);

					Entity target = EntityList.createEntityByName(EntityList.getEntityString(entityIn), worldNew);

					if (target != null)
					{
						worldOld.playSoundEffect(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, "thevoid:void_portal", 0.25F, 1.15F);

						target.copyDataFromOld(entityIn);
						target.forceSpawn = true;

						worldNew.spawnEntityInWorld(target);
						worldNew.playSoundAtEntity(target, "thevoid:void_portal", 0.5F, 1.15F);

						target.forceSpawn = false;
						target.getEntityData().setInteger("TheVoid:LastDim", dimOld);
					}

					entityIn.setDead();

					worldOld.resetUpdateEntityTick();
					worldNew.resetUpdateEntityTick();
				}
			}
			else
			{
				entityIn.timeUntilPortal = entityIn.getPortalCooldown();
			}
		}
	}

	@Override
	public PatternHelper func_181089_f(World world, BlockPos pos)
	{
		EnumFacing.Axis axis = EnumFacing.Axis.Z;
		Size size = new Size(world, pos, EnumFacing.Axis.X);
		LoadingCache<BlockPos, BlockWorldState> cache = BlockPattern.func_181627_a(world, true);

		if (!size.func_150860_b())
		{
			axis = EnumFacing.Axis.X;
			size = new Size(world, pos, EnumFacing.Axis.Z);
		}

		if (!size.func_150860_b())
		{
			return new PatternHelper(pos, EnumFacing.NORTH, EnumFacing.UP, cache, 1, 1, 1);
		}
		else
		{
			int[] aint = new int[EnumFacing.AxisDirection.values().length];
			EnumFacing facing = size.field_150866_c.rotateYCCW();
			BlockPos blockpos = size.field_150861_f.up(size.func_181100_a() - 1);

			for (EnumFacing.AxisDirection direction : EnumFacing.AxisDirection.values())
			{
				PatternHelper pattern = new PatternHelper(facing.getAxisDirection() == direction ? blockpos : blockpos.offset(size.field_150866_c, size.func_181101_b() - 1), EnumFacing.func_181076_a(direction, axis), EnumFacing.UP, cache, size.func_181101_b(), size.func_181100_a(), 1);

				for (int i = 0; i < size.func_181101_b(); ++i)
				{
					for (int j = 0; j < size.func_181100_a(); ++j)
					{
						BlockWorldState blockworldstate = pattern.translateOffset(i, j, 1);

						if (blockworldstate.getBlockState() != null && blockworldstate.getBlockState().getBlock().getMaterial() != Material.air)
						{
							++aint[direction.ordinal()];
						}
					}
				}
			}

			EnumFacing.AxisDirection var1 = EnumFacing.AxisDirection.POSITIVE;

			for (EnumFacing.AxisDirection direction : EnumFacing.AxisDirection.values())
			{
				if (aint[direction.ordinal()] < aint[var1.ordinal()])
				{
					var1 = direction;
				}
			}

			return new PatternHelper(facing.getAxisDirection() == var1 ? blockpos : blockpos.offset(size.field_150866_c, size.func_181101_b() - 1), EnumFacing.func_181076_a(var1, axis), EnumFacing.UP, cache, size.func_181101_b(), size.func_181100_a(), 1);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
	{
		if (rand.nextInt(5) == 0)
		{
			double ptX = pos.getX() + rand.nextFloat();
			double ptY = pos.getY() + 0.5D;
			double ptZ = pos.getZ() + rand.nextFloat();

			worldIn.spawnParticle(EnumParticleTypes.REDSTONE, ptX, ptY, ptZ, 0.01D, 0.01D, 0.01D);
		}
	}

	public class Size
	{
		private final World world;
		private final EnumFacing.Axis axis;
		private final EnumFacing field_150866_c;
		private final EnumFacing field_150863_d;
		private int field_150864_e = 0;
		private BlockPos field_150861_f;
		private int field_150862_g;
		private int field_150868_h;

		public Size(World worldIn, BlockPos pos, EnumFacing.Axis axis)
		{
			this.world = worldIn;
			this.axis = axis;

			if (axis == EnumFacing.Axis.X)
			{
				this.field_150863_d = EnumFacing.EAST;
				this.field_150866_c = EnumFacing.WEST;
			}
			else
			{
				this.field_150863_d = EnumFacing.NORTH;
				this.field_150866_c = EnumFacing.SOUTH;
			}

			for (BlockPos blockpos1 = pos; pos.getY() > blockpos1.getY() - 21 && pos.getY() > 0 && func_150857_a(worldIn.getBlockState(pos.down()).getBlock()); pos = pos.down())
			{
				;
			}

			int i = func_180120_a(pos, field_150863_d) - 1;

			if (i >= 0)
			{
				this.field_150861_f = pos.offset(field_150863_d, i);
				this.field_150868_h = func_180120_a(field_150861_f, field_150866_c);

				if (field_150868_h < 2 || field_150868_h > 21)
				{
					this.field_150861_f = null;
					this.field_150868_h = 0;
				}
			}

			if (field_150861_f != null)
			{
				this.field_150862_g = func_150858_a();
			}
		}

		protected int func_180120_a(BlockPos pos, EnumFacing face)
		{
			int i;

			for (i = 0; i < 22; ++i)
			{
				BlockPos pos1 = pos.offset(face, i);

				if (!func_150857_a(world.getBlockState(pos1).getBlock()) || world.getBlockState(pos1.down()).getBlock() != Blocks.end_stone)
				{
					break;
				}
			}

			Block block = world.getBlockState(pos.offset(face, i)).getBlock();

			return block == Blocks.end_stone ? i : 0;
		}

		public int func_181100_a()
		{
			return field_150862_g;
		}

		public int func_181101_b()
		{
			return field_150868_h;
		}

		protected int func_150858_a()
		{
			int i;

			outside: for (field_150862_g = 0; field_150862_g < 21; ++field_150862_g)
			{
				for (i = 0; i < field_150868_h; ++i)
				{
					BlockPos pos = field_150861_f.offset(field_150866_c, i).up(field_150862_g);
					Block block = world.getBlockState(pos).getBlock();

					if (!func_150857_a(block))
					{
						break outside;
					}

					if (block == BlockVoidPortal.this)
					{
						++field_150864_e;
					}

					if (i == 0)
					{
						block = world.getBlockState(pos.offset(field_150863_d)).getBlock();

						if (block != Blocks.end_stone)
						{
							break outside;
						}
					}
					else if (i == field_150868_h - 1)
					{
						block = world.getBlockState(pos.offset(field_150866_c)).getBlock();

						if (block != Blocks.end_stone)
						{
							break outside;
						}
					}
				}
			}

			for (i = 0; i < field_150868_h; ++i)
			{
				if (world.getBlockState(field_150861_f.offset(field_150866_c, i).up(field_150862_g)).getBlock() != Blocks.end_stone)
				{
					field_150862_g = 0;
					break;
				}
			}

			if (field_150862_g <= 21 && field_150862_g >= 3)
			{
				return field_150862_g;
			}
			else
			{
				field_150861_f = null;
				field_150868_h = 0;
				field_150862_g = 0;

				return 0;
			}
		}

		protected boolean func_150857_a(Block block)
		{
			return block.getMaterial() == Material.air || block == BlockVoidPortal.this;
		}

		public boolean func_150860_b()
		{
			return field_150861_f != null && field_150868_h >= 2 && field_150868_h <= 21 && field_150862_g >= 3 && field_150862_g <= 21;
		}

		public void func_150859_c()
		{
			for (int i = 0; i < field_150868_h; ++i)
			{
				BlockPos pos = field_150861_f.offset(field_150866_c, i);

				for (int j = 0; j < field_150862_g; ++j)
				{
					world.setBlockState(pos.up(j), getDefaultState().withProperty(AXIS, axis), 2);
				}
			}
		}
	}
}