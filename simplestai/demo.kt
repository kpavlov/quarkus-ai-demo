///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.quarkus:quarkus-bom:${quarkus.version:3.20.0}@pom

/// mcp-server-shared provides shared cli entrypoint for mcp server
/// will eventually be available in quarkus-mcp.
//DEPS io.quarkiverse.mcp.servers:mcp-server-shared:1.0.0.CR3
package demo;

import io.quarkiverse.mcp.server.*

class demo {

@Tool(description = "Say hello")
fun hello(@ToolArg(description = "Who you want to be greeted") who: String): String {
    return "Hello $who"
}

@Prompt(description = "Make a greeting") 
fun makeGreeting(@PromptArg(description = "Who you want to be greeted") who: String): PromptMessage { 
    return PromptMessage.withUserRole(TextContent(
        """
        The assistants goal is to greet the user $who
        """
    ))
}
}