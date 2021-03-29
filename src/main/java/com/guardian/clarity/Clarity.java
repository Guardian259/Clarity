package com.guardian.clarity;

import com.guardian.clarity.registry.VisibilityRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.ArmorMaterial;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Clarity implements ModInitializer {
	public static final String MODID = "clarity";
	public static final Map<ArmorMaterial, int[]> ARMORVISIBILITY = new HashMap<>();
	public static final Logger LOG = LogManager.getLogger(Clarity.MODID);

	@Override
	public void onInitialize() {
		System.out.println("Clarity API Initializing");
		ServerLifecycleEvents.SERVER_STARTING.register((server) -> VisibilityRegistry.init());
		System.out.println("Clarity API Initializing complete, Success!");
	}
}
