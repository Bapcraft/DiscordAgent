package org.bapcraft.discordagent;

import org.slf4j.Logger;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.hooks.EventListener;

public class DiscordEventListener implements EventListener {

	private Logger logger;

	public DiscordEventListener(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void onEvent(Event event) {
		this.logger.info("got event " + event);
	}

}
