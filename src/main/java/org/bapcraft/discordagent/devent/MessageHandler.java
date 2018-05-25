package org.bapcraft.discordagent.devent;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class MessageHandler implements EventListener {

	private Logger logger;

	public MessageHandler(Logger logger) {
		this.logger = logger;
	}

	@Override
	public void onEvent(Event event) {

		// We only want to deal with message received events.
		if (!(event instanceof MessageReceivedEvent)) {
			return;
		}

		MessageReceivedEvent mre = (MessageReceivedEvent) event;
		Message msg = mre.getMessage();
		String contents = msg.getContentStripped();

		if (!contents.startsWith("!bap") || mre.getAuthor().isBot()) {
			// It's not a command, or it's from a bot, so forget about it.
			return;
		}

		String[] expr = contents.split(" ");
		if (expr.length < 2 || expr[1].equals("help")) {
			this.printHelp(msg.getChannel());
			return;
		}

		String cmd = expr[1];
		String arg = null;
		if (expr.length == 3) {
			arg = expr[2];
		}

		// TODO

	}

	private void printHelp(MessageChannel c) {

		List<String> lines = Arrays.asList(
				"Hi, welcome to Bapcraft!  Here's a list of commands I know!:",
				"**!bap help** - prints this error message",
				"**!bap link** - get a token to link your account to your Minecraft account in-game with `/dagent link`");

		StringBuilder sb = new StringBuilder();
		for (String s : lines) {
			sb.append(s + "\n");
		}

		c.sendMessage(sb.toString().trim()).queue();

	}

}
