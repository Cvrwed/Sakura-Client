package cc.unknown.script.api.wrapper;

import cc.unknown.script.api.API;
import lombok.AllArgsConstructor;

/**
 * @author Strikeless
 * @since 23.06.2022
 */
@AllArgsConstructor
public abstract class ScriptWrapper<T> extends API {
    protected T wrapped;
}
