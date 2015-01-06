/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package com.thevoid.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.thevoid.core.TheVoid;

public class ItemVoidCore extends ItemBlock
{
	public ItemVoidCore(Block block)
	{
		super(block);
		this.setMaxStackSize(1);
	}

	@Override
	public String getUnlocalizedName()
	{
		return "item.voidCore";
	}

	@Override
	public String getUnlocalizedName(ItemStack itemstack)
	{
		return getUnlocalizedName();
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if (!world.isRemote)
		{
			switch (side)
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
				world.playSoundEffect(x + 0.5D, y + 0.5D, z + 0.5D, field_150939_a.stepSound.func_150496_b(), 1.0F, 2.0F);

				if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
				{
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		return false;
	}
}