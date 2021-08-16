package dungeoncreator.items;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class StartupCommon {
    public static WalkableItem walkableItem;  // this holds the unique instance of your block
    public static ExcludeItem excludeItem;  // this holds the unique instance of your block

    @SubscribeEvent
    public static void onItemsRegistration(final RegistryEvent.Register<Item> itemRegisterEvent) {
        walkableItem = new WalkableItem();
        excludeItem = new ExcludeItem();
        walkableItem.setRegistryName("walkable_item");
        excludeItem.setRegistryName("exclude_item");
        itemRegisterEvent.getRegistry().register(walkableItem);
        itemRegisterEvent.getRegistry().register(excludeItem);
    }
}
