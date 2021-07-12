package studio.archetype.shutter.client.cmd;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.Collection;

public class PathTimeArgumentType implements ArgumentType<Double> {

    private static final ImmutableList<String> EXAMPLES = ImmutableList.of("77.9", "22.2t", "5.25s", "3.75m", "1.0025h");

    private PathTimeArgumentType() {
    }

    public static PathTimeArgumentType pathTime() {
        return new PathTimeArgumentType();
    }

    public static double getTicks(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Double.class);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public Double parse(StringReader reader) throws CommandSyntaxException {
        double value = reader.readDouble();
        double multiplier;
        try {
            char type = reader.read();
            if(type != 's' && type != 'm' && type != 'h' && type != 't')
                throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol().createWithContext(reader, "t/s/m/h");

            switch(type) {
                case 's':
                    multiplier = 20;
                    break;
                case 'm':
                    multiplier = 60 * 20;
                    break;
                case 'h':
                    multiplier = 60 * 60 * 20;
                    break;
                default:
                    multiplier = 1;
                    break;
            }
        } catch(StringIndexOutOfBoundsException e) {
            multiplier = 1;
        }

        return value * multiplier;
    }
}
