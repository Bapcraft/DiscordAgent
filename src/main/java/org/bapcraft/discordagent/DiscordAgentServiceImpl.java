package org.bapcraft.discordagent;

import java.util.UUID;

import org.bapcraft.discordagent.api.DiscordAgentService;
import org.bapcraft.discordagent.storage.AgentStorage;
import org.bapcraft.discordagent.storage.UserProfile;
import org.slf4j.Logger;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;

public class DiscordAgentServiceImpl implements DiscordAgentService {

	private Logger logger;

	private AgentStorage storage;
	private JDA jda;

	public DiscordAgentServiceImpl(Logger logger, AgentStorage storage, JDA jda) {
		this.logger = logger;
		this.storage = storage;
		this.jda = jda;
	}

	@Override
	public void broadcast(String message) {
		this.logger.info("To Discord: " + message);
		this.logger.warn("NOT YET IMPLEMENTED");
	}

	@Override
	public void sendMessage(UUID uuid, String message) {

		this.logger.info("To user " + uuid + " on Discord: " + message);

		UserProfile prof = this.storage.getDiscordUser(uuid);
		User du = this.jda.getUserById(prof.snowflakeId);
		du.openPrivateChannel().queue(ch -> {
			ch.sendMessage(message).queue();
		}, err -> {
			this.logger.error("Error sending message!", err);
		});

	}

}
