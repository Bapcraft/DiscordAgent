package org.bapcraft.discordagent;

import java.util.UUID;

import org.bapcraft.discordagent.api.DiscordAgentService;
import org.slf4j.Logger;

public class DiscordAgentServiceImpl implements DiscordAgentService {

	private Logger logger;

	public DiscordAgentServiceImpl(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void broadcast(String message) {
		this.logger.info("To Discord: " + message);
		this.logger.warn("NOT YET IMPLEMENTED");
	}

	@Override
	public void sendMessage(UUID uuid, String message) {
		this.logger.info("To user " + uuid + " on Discord: " + message);
		this.logger.warn("NOT YET IMPLEMENTED");
	}

}
