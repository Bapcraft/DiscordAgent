package org.bapcraft.discordagent.cmd;

import java.util.Optional;
import java.util.UUID;

import org.bapcraft.discordagent.api.DiscordAgentService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;

public class RawMsgExecutor implements CommandExecutor {

	private DiscordAgentService serv;

	public RawMsgExecutor(DiscordAgentService serv) {
		this.serv = serv;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

		Optional<UUID> uuidOpt = args.getOne("uuid").map(o -> (UUID) o);
		Optional<User> userOpt = args.getOne("user").map(o -> (User) o);
		UUID uuid = uuidOpt.isPresent() ? uuidOpt.get() : userOpt.map(u -> u.getUniqueId()).get();
		
		String rawMsg = (String) args.getOne("msg").get();
		String fixedMsg = rawMsg.replaceAll("\\\\n", "\n");

		this.serv.sendMessage(uuid, fixedMsg);
		return CommandResult.success();

	}

}
