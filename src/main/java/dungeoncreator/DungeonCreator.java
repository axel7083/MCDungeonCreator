package dungeoncreator;

import dungeoncreator.utils.Cache;
import dungeoncreator.utils.OrthoViewHandler;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DungeonCreator.MODID)
public class DungeonCreator {
  // you also need to update the modid in two other places as well:
  //  build.gradle file (the version, group, and archivesBaseName parameters)
  //  resources/META-INF/mods.toml (the name, description, and version parameters)
  public static final String MODID = "dungeoncreator";

  public DungeonCreator() {


    // Get an instance of the mod event bus
    final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
    modEventBus.addListener(this::clientSetup);
    registerCommonEvents(modEventBus);

    MinecraftForge.EVENT_BUS.addListener(this::onWorldLoaded);
    MinecraftForge.EVENT_BUS.addListener(this::onWorldUnload);
  }


  public void onWorldLoaded(WorldEvent.Load event)
  {
    if (!event.getWorld().isRemote() && event.getWorld() instanceof ServerWorld)
    {
      System.out.println("[onWorldLoaded]");
      Cache cache = Cache.getInstance();
      cache.currentWorld = event.getWorld();

      System.out.println("[onWorldLoaded] "  + ((ServerWorld) event.getWorld()).getServer().getWorldIconFile().getParentFile().getAbsolutePath());
      // Fetching the WorldIconFile allows us to get the Save Directory easily.
      cache.groupObject = GroupObject.getInstance(((ServerWorld) event.getWorld()).getServer().getWorldIconFile().getParentFile());

      System.out.println("Found " + cache.groupObject.objects.size() + " tiles.");
    }
  }

  public void onWorldUnload(WorldEvent.Unload event)
  {
    if (!event.getWorld().isRemote() && event.getWorld() instanceof ServerWorld)
    {
      System.out.println("[onWorldUnload]");
      Cache cache = Cache.getInstance();

      // If the world is being save AND the current world has not been set ot null yet
      if(cache.currentWorld != null) {

        // Saving the cached groupObject
        try {
          cache.groupObject.save();
        } catch (IOException e) {
          e.printStackTrace();
        }

        // We destroy the cache
        cache.destroy();
      }
    }
  }

  /**
   * Register common events for both dedicated servers and clients. This method is safe to call directly.
   */
  public void registerCommonEvents(IEventBus eventBus) {
    eventBus.register(dungeoncreator.itemgroup.StartupCommon.class);
    eventBus.register(dungeoncreator.commands.StartupCommon.class);
    eventBus.register(dungeoncreator.items.StartupCommon.class);
  }

  private void clientSetup(final FMLClientSetupEvent event) {
    OrthoViewHandler ovh = OrthoViewHandler.getInstance();
    MinecraftForge.EVENT_BUS.register(ovh);

  }

}
