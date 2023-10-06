package com.builtbroken.sbmgrowmeal;

import net.fabricmc.api.ModInitializer;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class GrowmealFabric implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    // public static final Logger LOGGER = LoggerFactory.getLogger("growmeal-fabric");
	
	public static final Item GROWMEAL = new GrowmealItem(new FabricItemSettings());

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		// LOGGER.info("Hello Fabric world!");
		
		Registry.register(Registries.ITEM, new Identifier("sbmgrowmeal", "growmeal"), GROWMEAL);
		
	}
}