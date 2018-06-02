package org.bapcraft.discordagent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.bapcraft.discordagent.api.DiscordAgentService;
import org.bapcraft.discordagent.cmd.AnnounceExecutor;
import org.bapcraft.discordagent.cmd.LinkExecutor;
import org.bapcraft.discordagent.cmd.MsgExecutor;
import org.bapcraft.discordagent.devent.MessageHandler;
import org.bapcraft.discordagent.storage.AgentStorage;
import org.bapcraft.discordagent.storage.FileAgentStorage;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.asset.AssetManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import net.dv8tion.jda.bot.entities.ApplicationInfo;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

@Plugin(id = "discordagent", name = "DiscordAgent", version = "0.1")
public class DiscordAgentPlugin {

	@Inject
	private Logger logger;

	// @Inject
	// @DefaultConfig(sharedRoot = true)
	private Path configPath;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;

	private DAConfig config;
	private AgentStorage storage;
	private LinkManager linkManager;

	private DiscordAgentServiceImpl agentService;

	@Inject
	public DiscordAgentPlugin(@DefaultConfig(sharedRoot = true) final Path configPath) {
		this.configPath = configPath;
	}

	@Listener
	public void onInit(GameInitializationEvent event) throws Exception {

		// Load config.
		AssetManager assets = Sponge.getAssetManager();
		Asset cfgAsset = assets.getAsset(this, "default.conf").get();
		try {

			File configFile = this.configPath.toFile();
			if (!configFile.exists()) {
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

		// Prepare storage backend, just always using plain files for now.
		File storageDir = new File("discordagent");
		if (!(storageDir.exists() && storageDir.isDirectory())) {
			storageDir.mkdirs();
		}

		this.storage = new FileAgentStorage(storageDir, this.logger);

		// Prepare out link manager.
		this.linkManager = new LinkManager(this.logger, this.storage);

		// Set up the connection to the Discord server.
		this.logger.info("Using Discord auth token: " + this.config.botAuthToken.substring(0, 8) + "... (snipped)");
		JDA jda = new JDABuilder(AccountType.BOT)
				.setToken(this.config.botAuthToken)
				.addEventListener(new MessageHandler(this.logger, this.linkManager))
				.buildAsync();

		ApplicationInfo ai = jda.asBot().getApplicationInfo().complete();
		String inviteUrl = ai.getInviteUrl(net.dv8tion.jda.core.Permission.ALL_GUILD_PERMISSIONS);
		this.logger.info("Discord bot needs to be invited to server if not: " + inviteUrl);

		this.agentService = new DiscordAgentServiceImpl(this.logger, this.config, this.storage, jda);

		CommandSpec daLinkCmd = CommandSpec.builder()
				.description(Text.of("Link your Minecraft account to your Discord account using the token."))
				.permission("discordagent.cmd.link")
				.arguments(GenericArguments.string(Text.of("token")))
				.executor(new LinkExecutor(this.linkManager))
				.build();

		CommandSpec daMsgCmd = CommandSpec.builder()
				.description(Text.of("Send a Discord message to a player by their Minecraft username."))
				.permission("discordagent.cmd.msg")
				.arguments(
						GenericArguments.user(Text.of("user")),
						GenericArguments.remainingJoinedStrings(Text.of("msg")))
				.executor(new MsgExecutor(this.agentService))
				.build();

		CommandSpec daAnnCmd = CommandSpec.builder()
				.description(Text.of("Make an announcement to the annoucements channel(s) on Discord."))
				.permission("discordagent.cmd.announce")
				.arguments(GenericArguments.remainingJoinedStrings(Text.of("msg")))
				.executor(new AnnounceExecutor(this.agentService))
				.build();
		
		CommandSpec daCmd = CommandSpec.builder()
				.child(daLinkCmd, "link")
				.child(daMsgCmd, "msg")
				.child(daAnnCmd, "announce")
				.build();

		CommandMapping s = Sponge.getCommandManager().register(this, daCmd, "discordagent", "dagent", "da").get();
		this.logger.info("Registered command: " + s.getPrimaryAlias());

	}

	@Listener
	public void onPostInit(GamePostInitializationEvent event) {
		if (this.agentService != null) {
			Sponge.getServiceManager().setProvider(this, DiscordAgentService.class, this.agentService);
		} else {
			this.logger.warn("Agent was not initialized, was there a problem configuring the bot?");
		}
	}

}
