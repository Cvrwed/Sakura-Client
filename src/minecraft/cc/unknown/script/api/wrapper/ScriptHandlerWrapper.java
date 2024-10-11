package cc.unknown.script.api.wrapper;

import jdk.nashorn.api.scripting.JSObject;

import cc.unknown.script.util.ScriptHandler;

public abstract class ScriptHandlerWrapper<T> extends ScriptWrapper<T> {

    private final ScriptHandler handler = new ScriptHandler();

    public ScriptHandlerWrapper(final T wrapped) {
        super(wrapped);
    }

    public void handle(final String functionName, final JSObject function) {
        this.handler.handle(functionName, function);
    }

    public void unhandle(final String functionName) {
        this.handler.unhandle(functionName);
    }

    public void call(final String functionName, final Object... parameters) {
        this.handler.call(functionName, parameters);
    }
}
