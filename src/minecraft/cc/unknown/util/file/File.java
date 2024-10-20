package cc.unknown.util.file;

import cc.unknown.util.Accessor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class File implements Accessor {

    private final java.io.File file;
    private final FileType fileType;

    public abstract boolean read();

    public abstract boolean write();
}