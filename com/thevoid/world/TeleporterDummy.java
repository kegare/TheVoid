/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.thevoid.world;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterDummy extends Teleporter
{
	public TeleporterDummy(WorldServer world)
	{
		super(world);
	}

	@Override
	public void placeInPortal(Entity entity, double posX, double posY, double posZ, float rotationYaw) {}

	@Override
	public boolean placeInExistingPortal(Entity entity, double posX, double posY, double posZ, float rotationYaw)
	{
		return true;
	}

	@Override
	public boolean makePortal(Entity entity)
	{
		return true;
	}

	@Override
	public void removeStalePortalLocations(long time) {}
}