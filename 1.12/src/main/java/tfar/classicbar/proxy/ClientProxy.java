package tfar.classicbar.proxy;

import net.minecraftforge.fml.common.Loader;
import tfar.classicbar.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import static tfar.classicbar.ModConfig.displayToughnessBar;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    private static HealthBarRenderer healthBarRenderer;

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
        //Register Renderers for events

        MinecraftForge.EVENT_BUS.register(new HealthBarRenderer());
        MinecraftForge.EVENT_BUS.register(new ArmorBarRenderer());
        if (displayToughnessBar)MinecraftForge.EVENT_BUS.register(new ArmorToughnessBarRenderer());
        MinecraftForge.EVENT_BUS.register(new OxygenBarRenderer());
        MinecraftForge.EVENT_BUS.register(new HungerBarRenderer());

        //mod renderers
        if (Loader.isModLoaded("randomthings"))
            MinecraftForge.EVENT_BUS.register(new LavaCharmRenderer());


        //Register event for configuration change
        EventConfigChanged eventConfigChanged = new EventConfigChanged();
        MinecraftForge.EVENT_BUS.register(eventConfigChanged);
    }

    @Override
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        ConfigManager.sync(ClassicBar.MODID, Config.Type.INSTANCE);

        //Ensure changes are applied by forcing recalculation.
        healthBarRenderer.forceUpdate();
    }
}
