package thebetweenlands.world.storage;

import net.minecraft.world.World;
import thebetweenlands.herblore.aspects.AspectManager;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.world.events.EnvironmentEvent;
import thebetweenlands.world.events.EnvironmentEventRegistry;

public class BetweenlandsWorldData extends WorldDataBase {
	private EnvironmentEventRegistry environmentEventRegistry = new EnvironmentEventRegistry();
	private AspectManager aspectManager = new AspectManager();

	public BetweenlandsWorldData() {
		super(ModInfo.ID + ":worldData");
	}

	public BetweenlandsWorldData(String name) {
		super(name);
	}

	public EnvironmentEventRegistry getEnvironmentEventRegistry() {
		return this.environmentEventRegistry;
	}
	
	public AspectManager getAspectManager() {
		return this.aspectManager;
	}

	/**
	 * Called before loading data and setting defaults
	 */
	@Override
	protected void init() {
		this.environmentEventRegistry.init();
	}

	/**
	 * Called when this data is created for the first time for this world
	 */
	@Override
	protected void setDefaults() {
		for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
			event.setDefaults();
			event.setLoaded();
		}
		if(!this.getWorld().isRemote) this.aspectManager.loadAndPopulateStaticAspects(null, AspectManager.getAspectsSeed(this.getWorld().getWorldInfo().getSeed()));
	}

	/**
	 * Load data here
	 */
	@Override
	protected void load() {
		for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
			event.readFromNBT(this.getData());
		}
		this.environmentEventRegistry.setDisabled(this.getData().getBoolean("eventsDisabled"));
		if(!this.getWorld().isRemote) this.aspectManager.loadAndPopulateStaticAspects(this.getData(), AspectManager.getAspectsSeed(this.getWorld().getWorldInfo().getSeed()));
	}

	/**
	 * Save data here
	 */
	@Override
	protected void save() {
		for(EnvironmentEvent event : this.environmentEventRegistry.getEvents().values()) {
			event.writeToNBT(this.getData());
		}
		this.getData().setBoolean("eventsDisabled", this.environmentEventRegistry.isDisabled());
		if(!this.getWorld().isRemote) this.aspectManager.saveStaticAspects(this.getData());
	}

	public static BetweenlandsWorldData forWorld(World world) {
		return WorldDataBase.forWorld(world, BetweenlandsWorldData.class);
	}
}
