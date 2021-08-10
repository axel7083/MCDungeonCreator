package dungeoncreator;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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
    registerCommonEvents(modEventBus);
  }


  /**
   * Register common events for both dedicated servers and clients. This method is safe to call directly.
   */
  public void registerCommonEvents(IEventBus eventBus) {
    eventBus.register(dungeoncreator.itemgroup.StartupCommon.class);
    eventBus.register(dungeoncreator.commands.StartupCommon.class);
    eventBus.register(dungeoncreator.commands.StartupCommon.class);
  }

}
