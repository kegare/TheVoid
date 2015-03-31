/*
 * The Void
 *
 * Copyright (c) 2015 kegare
 * https://github.com/kegare
 *
 * This mod is distributed under the terms of the Minecraft Mod Public License Japanese Translation, or MMPL_J.
 */

package thevoid.item;

import thevoid.core.TheVoid;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemVoidCore extends Item
{
	public ItemVoidCore()
	{
		super();
		this.setUnlocalizedName("voidCore");
		this.setMaxStackSize(1);
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		BlockPos pos1 = pos.offset(side);

		if (TheVoid.void_portal.func_176548_d(world, pos1))
		{
			world.playSoundEffect(pos1.getX() + 0.5D, pos1.getY() + 0.5D, pos1.getZ() + 0.5D, TheVoid.void_portal.stepSound.getPlaceSound(), 1.0F, 2.0F);

			if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0)
			{
				player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}

			return true;
		}

		return false;
	}
}