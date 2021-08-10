package dungeoncreator.itemgroup;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;

// This creative tab is very similar to the basic CreativeTab, but overrides displayAllReleventItems to
//  customise the list of displayed item - filters through all the item looking for ones whose namespace is
//  "minecraftbyexample"

public class AllMbeItemsItemGroup extends ItemGroup {

  public AllMbeItemsItemGroup(String label) {
    super(label);
  }

  @Override
  public ItemStack createIcon() {
    return new ItemStack(Items.BOOK);
  }

  // The code below is not necessary for your own ItemGroup, if you specify the ItemGroup within your item
  //  eg
  // public class ItemSimple extends Item {
  //  public ItemSimple() {
  //    super(new Item.Properties().group(StartupCommon.allMbeItemsItemGroup)
  //    );
  //  }
  //
  @Override
  public void fill(NonNullList<ItemStack> itemsToShowOnTab)
  {
    HashMap<String, Boolean> blocks = BlockMap.getBlockMap();

    System.out.println("AllMbeItemsItemGroup");
    for (Item item : ForgeRegistries.ITEMS) {
      if (item != null) {
        if(blocks.getOrDefault(item.getRegistryName().getNamespace()+":"+item.getRegistryName().getPath(), false)) {
          item.fillItemGroup(ItemGroup.SEARCH, itemsToShowOnTab);
        }
      }
    }
  }

}
