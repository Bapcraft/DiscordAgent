package org.bapcraft.discordagent;

import java.io.IOException;
import java.nio.file.Path;

import org.bapcraft.discordagent.api.DiscordAgentService;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ServiceManager;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

@Plugin(id = "discordagent", name = "DiscordAgent", version = "0.1")
public class DiscordAgentPlugin {

	@Inject
	private Game game;

	@Inject
	private ServiceManager serviceManager;

	@Inject
	private Logger logger;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private Path configPath;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;

	private DAConfig config;

	private DiscordAgentServiceImpl agentService;

	@Listener
	public void onInit(GameInitializationEvent event) throws Exception {

		// Load config.
		Asset cfgAsset = this.game.getAssetManager().getAsset(this, "default.conf").get();
		try {

			if (!this.configPath.toFile().exists()) {
				cfgAsset.copyToFile(this.configPath);
			}

			ConfigurationNode root = this.configLoader.load();
			this.config = root.getValue(TypeToken.of(DAConfig.class));

		} catch (IOException e) {
			this.logger.error("Unable to load config!");
			throw e;
		} catch (ObjectMappingException e) {
			this.logger.error("Unable to parse config!");
			throw e;
		}

		this.agentService = new DiscordAgentServiceImpl(this.logger);

	}

	@Listener
	public void onPostInit(GamePostInitializationEvent event) {
		this.serviceManager.setProvider(this, DiscordAgentService.class, this.agentService);
	}

}