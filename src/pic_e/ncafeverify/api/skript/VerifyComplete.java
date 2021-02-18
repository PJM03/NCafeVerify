package pic_e.ncafeverify.api.skript;

import org.bukkit.event.Event;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;

public class VerifyComplete extends SkriptEvent{
	public boolean init(final Literal<?>[] args, final int matchedPattern, final SkriptParser.ParseResult parseResult) {
        return true;
    }
    
    public boolean check(final Event e) {
        return true;
    }
    
    public String toString(final Event e, final boolean debug) {
        return "";
    }
}
