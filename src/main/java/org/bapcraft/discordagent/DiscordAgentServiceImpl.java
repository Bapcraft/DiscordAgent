package org.bapcraft.discordagent;

import java.util.Optional;
import java.util.UUID;

import org.bapcraft.discordagent.api.DiscordAgentService;
import org.bapcraft.discordagent.storage.AgentStorage;
import org.bapcraft.discordagent.storage.UserProfile;
import org.slf4j.Logger;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class DiscordAgentServiceImpl implements DiscordAgentService {

	private Logger logger;

	private DAConfig conf;
	private AgentStorage storage;
	private JDA jda;

	public DiscordAgentServiceImpl(Logger logger, DAConfig conf, AgentStorage storage, JDA jda) {
		this.logger = logger;
		this.conf = conf;
		this.storage = storage;
		this.jda = jda;
	}

	@Override
	public void broadcast(String message) {

		this.logger.info("To Discord (announcement): " + message);
		String chan = this.conf.announcementChannel;

		for (TextChannel tc : this.jda.getTextChannelsByName(chan, true)) {
			if (!tc.canTalk()) {
				continue;
			}

			tc.sendMessage(message).queue(m -> {
				// nothing?
			}, err -> {
				this.logger.error("Couldn't send message to channel " + tc.getName() + " in guild " + tc.getGuild().getName(), err);
			});
		}

	}

	@Override
	public void sendMessage(UUID uuid, String message) {

		this.logger.info("To user " + uuid + " on Discord: " + message);

		UserProfile prof = this.storage.getDiscordUser(uuid);
		if (prof == null) {
			this.logger.warn("Wanted to send a message to user that hasn't registered yet: " + uuid);
			return;
		}

		User du = this.jda.getUserById(prof.snowflakeId);
		du.openPrivateChannel().queue(ch -> {
			ch.sendMessage(message).queue();
		}, err -> {
			this.logger.error("Error sending message!", err);
		});

	}

	@Override
	public Optional<String> getMentionOf(UUID uuid) {
		UserProfile prof = this.storage.getDiscordUser(uuid);
		return Optional.ofNullable(prof).map(p -> p.discordMention);
	}

}
