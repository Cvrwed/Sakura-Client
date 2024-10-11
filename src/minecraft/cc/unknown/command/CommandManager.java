package cc.unknown.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Sakura;
import cc.unknown.command.impl.*;
import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.input.ChatInputEvent;
import cc.unknown.util.chat.ChatUtil;
import lombok.Getter;

@Getter
public final class CommandManager {
    private final List<Command> commandList = new ArrayList<>();
    private final String prefix = ".";

    public void init() {
        this.add(new Bind());
        this.add(new Clip());
        this.add(new Config());
        this.add(new Transaction());
        this.add(new Friend());
        this.add(new Help());
        this.add(new Name());
        this.add(new Join());
        this.add(new Script());
        this.add(new Toggle());
        this.add(new Target());

        Sakura.instance.getEventBus().register(this);
    }

    public void add(Command command) {
        this.commandList.add(command);
    }

    public <T extends Command> T get(final String name) {
        return (T) this.commandList.stream()
                .filter(command -> Arrays.stream(command.getExpressions())
                        .anyMatch(expression -> expression.equalsIgnoreCase(name))
                ).findAny().orElse(null);
    }

    @EventLink
    public final Listener<ChatInputEvent> onChatInput = event -> {
        String message = event.getMessage();

        if (!message.startsWith(prefix)) return;

        message = message.substring(1);
        final String[] args = message.split(" ");

        final AtomicBoolean commandFound = new AtomicBoolean(false);

        try {
            this.commandList.stream()
                    .filter(command ->
                            Arrays.stream(command.getExpressions())
                                    .anyMatch(expression ->
                                            expression.equalsIgnoreCase(args[0])))
                    .forEach(command -> {
                        commandFound.set(true);
                        command.execute(args);
                    });
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        if (!commandFound.get()) {
            ChatUtil.display("Unknown command! Try .help if you're lost");
        }

        event.setCancelled();
    };
}