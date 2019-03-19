package tfar.classicbar.proxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod.EventBusSubscriber
public class CommonProxy {
    public void postInit(FMLPostInitializationEvent event) {
    }

    public void refreshResources() {
        Minecraft.getMinecraft().scheduleResourcesRefresh();

    }
}

