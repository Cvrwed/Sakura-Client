package cc.unknown.module.impl.visual;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.impl.other.TickEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.api.Category;
import cc.unknown.module.api.ModuleInfo;
import cc.unknown.util.font.impl.minecraft.FontRenderer;
import cc.unknown.util.render.RenderUtil;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.util.vector.Vector2i;
import cc.unknown.value.impl.DragValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatFormatting;

@ModuleInfo(aliases = {"Scoreboard"}, description = "Allows you to customize the Minecraft scoreboard", category = Category.VISUALS, autoEnabled = true)
public final class ScoreBoard extends Module {

    private final DragValue position = new DragValue("Position", this, new Vector2d(200, 200));

    private Collection<Score> collection;
    private ScoreObjective scoreObjective;
    private int maxWidth;
    private final int padding = 3;
    private final int fontHeight = FontRenderer.FONT_HEIGHT;

    @EventLink()
    public final Listener<Render2DEvent> onRender2D = event -> {
        if (this.scoreObjective == null) return;

        final Vector2i position = new Vector2i((int) this.position.position.x, (int) this.position.position.y);

        final int size = collection.size();
        final int height = fontHeight * size + padding;
        this.renderScoreboard(position.x, position.y, new Color(0, 0, 0, 120), true, 10);

    };

    /**
     * Updates the scoreboard each tick.
     */
    @EventLink()
    public final Listener<TickEvent> onTick = event -> {
        this.scoreObjective = this.getScoreObjective();
        if (this.scoreObjective == null) return;

        final Collection<Score> collection = this.scoreObjective.getScoreboard().getSortedScores(this.scoreObjective);
        final List<Score> list = collection.stream()
                .filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#"))
                .collect(Collectors.toList());

        if (list.size() > 15) {
            this.collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
        } else {
            this.collection = list;
        }

        this.maxWidth = mc.fontRendererObj.width(scoreObjective.getDisplayName());

        for (final Score score : collection) {
            final ScorePlayerTeam scoreplayerteam = this.scoreObjective.getScoreboard().getPlayersTeam(score.getPlayerName());
            final String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName());
            this.maxWidth = Math.max(this.maxWidth, mc.fontRendererObj.width(s));
        }

        this.maxWidth += 2;
    };

    private ScoreObjective getScoreObjective() {
        final Scoreboard scoreboard = mc.world.getScoreboard();
        ScoreObjective scoreobjective = null;
        final ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.player.getName());

        if (scoreplayerteam != null) {
            final int colorIndex = scoreplayerteam.getChatFormat().getColorIndex();

            if (colorIndex > -1) {
                scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
            }
        }

        return scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
    }

    private void renderScoreboard(int x, int y, final Color backgroundColor, final boolean font, int round) {
        final FontRenderer fontRenderer = mc.fontRendererObj;
        final int size = collection.size();
        final int height = fontHeight * size + padding;

        Vector2d scale = new Vector2d(maxWidth + padding * 4, height + fontHeight + padding);
        this.position.setScale(scale);

        RenderUtil.roundedRectangle(x, y, maxWidth + padding * 4, height + fontHeight + padding, round, backgroundColor);

        if (!font) {
            return;
        }

        final int fontColor = 553648127;
        x += padding * 2;
        y += padding + 1.5;

        final String objective = scoreObjective.getDisplayName();
        fontRenderer.draw(objective, x + maxWidth / 2.0F - fontRenderer.width(objective) / 2.0F, y, fontColor);

        int currentY = y + fontHeight;
        for (int i = collection.size() - 1; i >= 0; i--) { 
            Score score1 = ((List<Score>) collection).get(i);
            ScorePlayerTeam scorePlayerTeam = this.scoreObjective.getScoreboard().getPlayersTeam(score1.getPlayerName());
            String s1 = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score1.getPlayerName());
            fontRenderer.draw(s1, x, currentY, fontColor);
            currentY += fontHeight;
        }
    }
}