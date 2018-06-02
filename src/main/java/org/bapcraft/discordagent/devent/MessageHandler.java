package org.bapcraft.discordagent.devent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bapcraft.discordagent.LinkManager;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class MessageHandler implements EventListener {

	private Logger logger;
	private LinkManager links;

	public MessageHandler(Logger logger, LinkManager links) {
		this.logger = logger;
		this.links = links;
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

		String cmd = expr[1].toLowerCase();
		String arg = null;
		if (expr.length == 3) {
			arg = expr[2];
		}

		if (cmd.equals("online")) {

			List<Player> players = Sponge.getServer()
					.getOnlinePlayers()
					.stream()
					.sorted((a, b) -> a.getName().compareTo(b.getName()))
					.collect(Collectors.toList());

			StringBuilder sb = new StringBuilder(String.format("**Players online (%s):** ", players.size()));
			if (players.size() == 0) {
				sb.append("*none*");
			} else {

				sb.append(players.get(0).getName());
				for (int i = 1; i < players.size(); i++) {
					sb.append(", " + players.get(i).getName());
				}

			}

			mre.getChannel().sendMessage(sb.toString()).queue();

		} else if (cmd.equals("link")) {

			User u = mre.getAuthor();

			this.logger.info("Initializing token for user " + u.getAsMention() + " (" + u.getName() + ")");

			u.openPrivateChannel().queue((PrivateChannel ch) -> {

				// Get a new token.
				String token = this.links.createNewToken(u.getIdLong(), u.getName(), u.getDiscriminator(), u.getAsMention());

				// Send the PM.
				ch.sendMessage(
						String.format("Run this command in-game to link your accounts! `/dagent link %s`", token))
						.queue();

				// Send the player a message in the channel.
				mre.getChannel()
						.sendMessage(u.getAsMention() + " Just sent you a private message, check your PMs!")
						.queue();

			}, err -> {
				this.logger.error("Couldn't open private message channel with " + u.getAsMention() + "!", err);
			});

		}

	}

	private void printHelp(MessageChannel c) {

		List<String> lines = Arrays.asList(
				"Hi, welcome to Bapcraft!  Here's a list of commands I know!:",
				"**!bap help** - prints this error message",
				"**!bap link** - get a token to link your account to your Minecraft account in-game with `/dagent link`",
				"**!bap online** - list players in-game");

		StringBuilder sb = new StringBuilder();
		for (String s : lines) {
			sb.append(s + "\n");
		}

		c.sendMessage(sb.toString().trim()).queue();

	}

}
