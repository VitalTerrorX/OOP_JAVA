package ru.nsu.spirin.logoworld.commands;

import org.apache.log4j.Logger;
import ru.nsu.spirin.logoworld.exceptions.CommandsWorkflowException;
import ru.nsu.spirin.logoworld.logic.Program;
import ru.nsu.spirin.logoworld.logic.World;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class CommandFactory {
    private static final Logger logger = Logger.getLogger(CommandFactory.class);

    private final World world;
    private final Program program;
    private final Map<String, String> commands;
    private final Map<String, Command> instances;

    public CommandFactory(Program program, World world) throws IOException {
        logger.debug("Command Factory started initialization.");
        InputStream stream = ClassLoader.getSystemResourceAsStream("commands.properties");
        if (stream == null) {
            logger.error("No commands.properties file found!");
            throw new IOException("Couldn't locate commands properties file");
        }

        Properties properties = new Properties();
        properties.load(stream);
        stream.close();

        commands = new HashMap<>();
        instances = new HashMap<>();

        for (var cmd : properties.stringPropertyNames()) {
            commands.put(cmd, properties.getProperty(cmd));
        }

        this.world = world;
        this.program = program;
        logger.debug("Command Factory finished initialization");
    }

    /**
     * Gets a command by name
     * @param command command name
     * @return {@code Command} subclass instance
     * @throws CommandsWorkflowException
     */
    public Command getCommand(String command) throws CommandsWorkflowException {
        if (!commands.containsKey(command)) {
            logger.debug("Invalid command request: " + command);
            return null;
        }
        if (instances.containsKey(command)) return instances.get(command);

        Command instance;
        try {
            logger.debug("Command request: " + command);
            logger.debug("Creating instance of class: " + commands.get(command));
            instance = (Command) Class.forName(commands.get(command)).getConstructor(Program.class, World.class).newInstance(program, world);
            instances.put(command, instance);
            logger.debug("Successfully created instance of class.");
        }
        catch (Exception e) {
            logger.error("Error encountered while creating new command instance:");
            logger.error(e.getLocalizedMessage());
            throw new CommandsWorkflowException(e.getLocalizedMessage());
        }
        return instance;
    }
}
