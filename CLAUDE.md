# Project: [Your Project Name]

This is a Clojure project with an active nREPL connection available via MCP.

## Development Workflow

**IMPORTANT**: Before writing or modifying any Clojure code, you should:

1. Use the `eval_clojure` tool to explore the existing code and test ideas
2. Check what namespaces are loaded: `(all-ns)`
3. Inspect functions and their behavior interactively
4. Verify assumptions by evaluating expressions in the REPL
5. Test small code snippets before suggesting full implementations

The REPL shares the same state as the user's editor, so all their loaded namespaces and definitions are available.

## Common REPL Exploration Patterns

- Check what's in a namespace: `(dir namespace.name)`
- Look up documentation: `(doc function-name)`
- Find source: `(source function-name)`
- Test a function: `(function-name test-args)`
- Check loaded namespaces: `(map ns-name (all-ns))`

Always prefer exploring via the REPL over making assumptions about the codebase.
