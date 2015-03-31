/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package thevoid.world;

import thevoid.client.renderer.EmptyRenderer;
import thevoid.core.Config;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderVoid extends WorldProvider
{
	@Override
	public void registerWorldChunkManager()
	{
		worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.ocean, 0.0F);
		hasNoSky = true;
		dimensionId = Config.dimensionTheVoid;
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderVoid(worldObj);
	}

	@Override
	public boolean canCoordinateBeSpawn(int chunkX, int chunkZ)
	{
		return true;
	}

	@Override
	public float calculateCelestialAngle(long time, float ticks)
	{
		return 0.0F;
	}

	@Override
	public int getMoonPhase(long time)
	{
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float[] calcSunriseSunsetColors(float angle, float ticks)
	{
		return null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 getFogColor(float angle, float ticks)
	{
		return new Vec3(0.001D, 0.001D, 0.001D);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getCloudHeight()
	{
		return 0.0F;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean isSkyColored()
	{
		return false;
	}

	@Override
	public int getAverageGroundLevel()
	{
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public double getVoidFogYFactor()
	{
		return 0.0D;
	}

	@Override
	public String getDimensionName()
	{
		return "The Void";
	}

	@Override
	public String getInternalNameSuffix()
	{
		return "_void";
	}

	@Override
	public String getWelcomeMessage()
	{
		return "Entering the Void";
	}

	@Override
	public String getDepartMessage()
	{
		return "Leaving the Void";
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getSkyRenderer()
	{
		if (super.getSkyRenderer() == null)
		{
			setSkyRenderer(EmptyRenderer.instance);
		}

		return super.getSkyRenderer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getCloudRenderer()
	{
		if (super.getCloudRenderer() == null)
		{
			setCloudRenderer(EmptyRenderer.instance);
		}

		return super.getCloudRenderer();
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IRenderHandler getWeatherRenderer()
	{
		if (super.getWeatherRenderer() == null)
		{
			setWeatherRenderer(EmptyRenderer.instance);
		}

		return super.getWeatherRenderer();
	}

	@Override
	public boolean shouldMapSpin(String entity, double x, double y, double z)
	{
		return false;
	}

	@Override
	public boolean isDaytime()
	{
		return false;
	}

	@Override
	public float getSunBrightnessFactor(float ticks)
	{
		return 0.0F;
	}

	@Override
	public float getCurrentMoonPhaseFactor()
	{
		return 0.0F;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 getSkyColor(Entity entity, float ticks)
	{
		return getFogColor(0.0F, ticks);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public Vec3 drawClouds(float ticks)
	{
		return getFogColor(0.0F, ticks);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getSunBrightness(float angle)
	{
		return 0.0F;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public float getStarBrightness(float angle)
	{
		return 0.0F;
	}

	@Override
	public void setAllowedSpawnTypes(boolean allowHostile, boolean allowPeaceful)
	{
		super.setAllowedSpawnTypes(false, false);
	}

	@Override
	public void calculateInitialWeather()
	{
		updateWeather();
	}

	@Override
	public void updateWeather()
	{
		worldObj.prevRainingStrength = 0.0F;
		worldObj.rainingStrength = 0.0F;
		worldObj.prevThunderingStrength = 0.0F;
		worldObj.thunderingStrength = 0.0F;
	}

	@Override
	public boolean canBlockFreeze(BlockPos pos, boolean byWater)
	{
		return false;
	}

	@Override
	public boolean canSnowAt(BlockPos pos, boolean checkLight)
	{
		return false;
	}

	@Override
	public BlockPos getSpawnPoint()
	{
		return BlockPos.ORIGIN;
	}

	@Override
	public BlockPos getRandomizedSpawnPoint()
	{
		return getSpawnPoint();
	}

	@Override
	public boolean isBlockHighHumidity(BlockPos pos)
	{
		return false;
	}

	@Override
	public int getActualHeight()
	{
		return 256;
	}

	@Override
	public double getHorizon()
	{
		return 0.0D;
	}

	@Override
	public boolean canDoLightning(Chunk chunk)
	{
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(Chunk chunk)
	{
		return false;
	}
}