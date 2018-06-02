package org.bapcraft.discordagent.cmd;

import java.util.Optional;

import org.bapcraft.discordagent.api.DiscordAgentService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;

public class MsgExecutor implements CommandExecutor {

	private DiscordAgentService serv;

	public MsgExecutor(DiscordAgentService serv) {
		this.serv = serv;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		User user = (User) args.getOne("user").get();
		String msg = (String) args.getOne("msg").get();
		StringBuilder sb = new StringBuilder();

		// Set up the header of the message.
		sb.append("**From ");
		sb.append(src.getName());
		
		// Now check to see if the player has registered with us, and include their mention in the message.
		if (src instanceof Player) {
			Player p = (Player) src;
			Optional<String> mentionOpt = this.serv.getMentionOf(p.getUniqueId());
			if (mentionOpt.isPresent()) {
				sb.append(" (" + mentionOpt.get() + ")");
			}
		}

		// Now close off the header and add the message.
		sb.append(":** ");
		sb.append(msg);

		this.serv.sendMessage(user.getUniqueId(), sb.toString());

		return CommandResult.success();

	}

}
