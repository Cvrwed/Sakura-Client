package cc.unknown.module.impl.movement.noweb;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.player.BlockWebEvent;
import cc.unknown.module.impl.movement.NoWeb;
import cc.unknown.value.Mode;

public class IgnoreNoWeb extends Mode<NoWeb> {

	public IgnoreNoWeb(String name, NoWeb parent) {
		super(name, parent);
	}

    @EventLink
    public final Listener<BlockWebEvent> onBlockWeb = event -> {
    	event.setCancelled();
    };
}
