package dungeoncreator.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.StructureBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * User: The Grey Ghost
 * Date: 24/12/2014
 *
 * The Startup classes for this example are called during startup, in the following order:
 * onBlocksRegistration then onItemsRegistration
 *  See MinecraftByExample class for more information
 */
public class StartupCommon
{
    public static DoorBlock doorBlock;  // this holds the unique instance of your block
    public static BlockItem itemBlockSimple;  // this holds the unique instance of the ItemBlock corresponding to your block


    @SubscribeEvent
    public static void onBlocksRegistration(final RegistryEvent.Register<Block> blockRegisterEvent) {
        doorBlock = (DoorBlock)(new DoorBlock().setRegistryName("dungeoncreator", "door_block_registry_name"));
        blockRegisterEvent.getRegistry().register(doorBlock);
    }

    @SubscribeEvent
    public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
        // We need to create a BlockItem so the player can carry this block in their hand and it can appear in the inventory
        final int MAXIMUM_STACK_SIZE = 20;  // player can only hold 20 of this block in their hand at once

        Item.Properties itemSimpleProperties = new Item.Properties()
                .maxStackSize(MAXIMUM_STACK_SIZE)
                .group(ItemGroup.REDSTONE);  // which inventory tab?
        itemBlockSimple = new BlockItem(doorBlock, itemSimpleProperties);
        itemBlockSimple.setRegistryName(doorBlock.getRegistryName());
        itemRegisterEvent.getRegistry().register(itemBlockSimple);
    }

}