
# DiscordAgent

This isn't a Discord chat bridge.  Instead, it sets up infrastructure to let
plugins send messages to players Discord accounts, by their Minecraft accounts.
All interaction on the Discord side is done through a bot account.  The bot key
is stored in the config file.

If you want to interface with this plugin from your own plugin, you can use the
`DiscordAgentService` interface.  It's not currently in its own module, but I'm
planning on moving it into its own soon enough.

But if you don't then there's a simpler way.  You can invoke the below commands
as the Server from code and they'll still work as-is.

## Minecraft Commands

All of the commands are subcommands of `/discordagent`, which has the aliases `/dagent`, `/da`.  All permissions should be suffixed onto `discordagent.cmd`.

| Subcommand | Perm | Description |
|------------|------|-------------|
| `link <token>` | `.link` | Links your Minecraft account to a token (see below). |
| `msg <user> <msg>` | `.msg` | Sends a message to a Discord account by their Minecraft username. |
| `announce <msg>` | `.announce` | Sends a message to the announcement channel(s). |
| `msgraw <uuid> <msg>` | `.msgraw` | Sends a message to a Discord account by their Minecraft UUID.  Useful for automation and from other plugins. |

## Discord Commands

These can be sent from any user in the same channel as the bot user.  Since
this plugin was written for Bapcraft, the prefix is `!bap`.  I'm going to make
this configurable later.

| Subcommand | Description |
|------------|-------------|
| `!bap help` | Print help, like this. |
| `!bap link` | Get a token to pass to `/discordagent link`.  It sends it to you as a PM, so it's kept private. |
| `!bap online` | Print players online. |
