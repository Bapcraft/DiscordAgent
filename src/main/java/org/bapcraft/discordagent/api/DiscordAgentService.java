package org.bapcraft.discordagent.api;

import java.util.UUID;

public interface DiscordAgentService {

	void broadcast(String message);

	void sendMessage(UUID uuid, String message);

}
