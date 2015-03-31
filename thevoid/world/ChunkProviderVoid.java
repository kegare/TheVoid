/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package thevoid.world;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ChunkProviderVoid implements IChunkProvider
{
	private final World worldObj;

	public ChunkProviderVoid(World world)
	{
		this.worldObj = world;
	}

	@Override
	public Chunk provideChunk(BlockPos blockPosIn)
	{
		return null;
	}

	@Override
	public Chunk provideChunk(int x, int z)
	{
		Chunk chunk = new Chunk(worldObj, x, z);

		Arrays.fill(chunk.getBiomeArray(), (byte)0);

		return chunk;
	}

	@Override
	public boolean chunkExists(int chunkX, int chunkZ)
	{
		return true;
	}

	@Override
	public void populate(IChunkProvider chunkProvider, int chunkX, int chunkZ) {}

	@Override
	public boolean func_177460_a(IChunkProvider chunkProvider, Chunk chunk, int x, int z)
	{
		return false;
	}

	@Override
	public boolean saveChunks(boolean flag, IProgressUpdate progress)
	{
		return true;
	}

	@Override
	public void saveExtraData() {}

	@Override
	public boolean unloadQueuedChunks()
	{
		return false;
	}

	@Override
	public boolean canSave()
	{
		return true;
	}

	@Override
	public String makeString()
	{
		return "VoidLevelSource";
	}

	@Override
	public List func_177458_a(EnumCreatureType type, BlockPos pos)
	{
		return null;
	}

	@Override
	public BlockPos getStrongholdGen(World worldIn, String name, BlockPos pos)
	{
		return null;
	}

	@Override
	public int getLoadedChunkCount()
	{
		return 0;
	}

	@Override
	public void recreateStructures(Chunk chunk, int x, int z) {}
}