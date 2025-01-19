package com.snowbird.fixmydaycare;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(FixMyDayCare.MOD_ID)
@Mod.EventBusSubscriber(modid = FixMyDayCare.MOD_ID)
public class FixMyDayCare {

    public static final String MOD_ID = "fixmydaycare";

    public FixMyDayCare() {
    }
}
