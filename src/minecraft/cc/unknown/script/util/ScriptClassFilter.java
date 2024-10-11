package cc.unknown.script.util;

import jdk.nashorn.api.scripting.ClassFilter;

public final class ScriptClassFilter implements ClassFilter {

    @Override
    public boolean exposeToScripts(final String className) {
        return className.startsWith("cc.unknown.script.api");
    }
}
