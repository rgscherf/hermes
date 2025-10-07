# hermes: the fast Clojure MCP server

```
But come now, tell me this, resourceful son of Maia:
Has this marvellous thing been with you from your birth,
Or did some god or mortal man give it you--a noble gift--and teach you heavenly song?
For wonderful is this new-uttered sound I hear,
The like of which I vow that no man nor god dwelling on Olympus ever yet has known but you,
O thievish son of Maia.

- Homeric Hymn to Hermes
```

Hermes connects to your running nREPL (using `.nrepl-port`) and exposes a single tool, `eval_clojure`, that gives your agent access to the very same REPL you're using to write Clojure code.

## Installation

Hermes requires the JVM installed on your system, and the `java` command on your path.

Clone this repository and place `target/hermes-standalone.jar` somewhere permanent--call the absolute path to that location `$HERMES_PATH`. Then configure your tool of choice with:

```json
{
	"mcpServers": {
		"clojure-repl": {
			"command": "java",
			"args": ["-jar", "$HERMES_PATH", "."]
		}
	}
}
```

If you're a Claude Code user, you can do this from the command line:

```bash
claude mcp add-json clojure-repl --scope user '{ "type": "stdio", "command": "java", "args": [ "-jar", "$HERMES_PATH", "."] }'
```

`eval_clojure`'s description implores the agent to use the REPL as frequently as possible. It may be helpful to reinforce those instructions in your `CLAUDE.md` or other context file; an example CLAUDE.md is included in this repository.

## License

Copyright © 2025 Robert Scherf

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
