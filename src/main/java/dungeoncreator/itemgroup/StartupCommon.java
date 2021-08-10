package dungeoncreator.itemgroup;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StartupCommon
{
  public static AllMbeItemsItemGroup allMbeItemsItemGroup;

  @SubscribeEvent
  public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
    allMbeItemsItemGroup = new AllMbeItemsItemGroup("mcdc_itemgroup");
  }
}
