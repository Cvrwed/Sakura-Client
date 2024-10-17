package cc.unknown.ui.menu.main.impl;

import java.awt.Color;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import cc.unknown.util.font.impl.minecraft.FontRenderer;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

@Getter
@Setter
public class TextField extends Gui {
    private final int id;
    private final FontRenderer fontRenderer;
    public int xPosition;
    public int yPosition;
    private final int width;
    private final int height;
    private String text = "";
    private int maxStringLength = 32;
    private int cursorCounter;
    private boolean enableBackgroundDrawing = true;
    private boolean canLoseFocus = true;
    private boolean isFocused;
    private boolean isEnabled = true;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    private int enabledColor = 14737632;
    private int disabledColor = 7368816;
    private boolean visible = true;
    private GuiPageButtonList.GuiResponder guiResponder;
    private Predicate<String> validator = Predicates.alwaysTrue();

    public TextField(final int componentId, final FontRenderer fontrendererObj, final int x, final int y, final int par5Width, final int par6Height) {
        this.id = componentId;
        this.fontRenderer = fontrendererObj;
        this.xPosition = x;
        this.yPosition = y;
        this.width = par5Width;
        this.height = par6Height;
    }

    /**
     * Increments the cursor counter
     */
    public void updateCursorCounter() {
        ++this.cursorCounter;
    }

    /**
     * Sets the text of the textbox
     */
    public void setText(final String p_146180_1_) {
        if (this.validator.apply(p_146180_1_)) {
            if (p_146180_1_.length() > this.maxStringLength) {
                this.text = p_146180_1_.substring(0, this.maxStringLength);
            } else {
                this.text = p_146180_1_;
            }

            this.setCursorPositionEnd();
        }
    }

    public String getSelectedText() {
        final int i = Math.min(this.cursorPosition, this.selectionEnd);
        final int j = Math.max(this.cursorPosition, this.selectionEnd);
        return this.text.substring(i, j);
    }

    public void writeText(final String p_146191_1_) {
        String s = "";
        final String s1 = ChatAllowedCharacters.filterAllowedCharacters(p_146191_1_);
        final int i = Math.min(this.cursorPosition, this.selectionEnd);
        final int j = Math.max(this.cursorPosition, this.selectionEnd);
        final int k = this.maxStringLength - this.text.length() - (i - j);
        int l = 0;

        if (this.text.length() > 0) {
            s = s + this.text.substring(0, i);
        }

        if (k < s1.length()) {
            s = s + s1.substring(0, k);
            l = k;
        } else {
            s = s + s1;
            l = s1.length();
        }

        if (this.text.length() > 0 && j < this.text.length()) {
            s = s + this.text.substring(j);
        }

        if (this.validator.apply(s)) {
            this.text = s;
            this.moveCursorBy(i - this.selectionEnd + l);

            if (this.guiResponder != null) {
                this.guiResponder.setEntryValue(this.id, this.text);
            }
        }
    }

    /**
     * Deletes the specified number of words starting at the cursor position. Negative numbers will delete words left of
     * the cursor.
     */
    public void deleteWords(final int p_146177_1_) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(p_146177_1_) - this.cursorPosition);
            }
        }
    }

    /**
     * delete the selected text, otherwsie deletes characters from either side of the cursor. params: delete num
     */
    public void deleteFromCursor(final int p_146175_1_) {
        if (this.text.length() != 0) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                final boolean flag = p_146175_1_ < 0;
                final int i = flag ? this.cursorPosition + p_146175_1_ : this.cursorPosition;
                final int j = flag ? this.cursorPosition : this.cursorPosition + p_146175_1_;
                String s = "";

                if (i >= 0) {
                    s = this.text.substring(0, i);
                }

                if (j < this.text.length()) {
                    s = s + this.text.substring(j);
                }

                if (this.validator.apply(s)) {
                    this.text = s;

                    if (flag) {
                        this.moveCursorBy(p_146175_1_);
                    }

                    if (this.guiResponder != null) {
                        this.guiResponder.setEntryValue(this.id, this.text);
                    }
                }
            }
        }
    }

    public int getNthWordFromCursor(final int p_146187_1_) {
        return this.getNthWordFromPos(p_146187_1_, this.getCursorPosition());
    }

    public int getNthWordFromPos(final int p_146183_1_, final int p_146183_2_) {
        return this.getNthWordFromPosWS(p_146183_1_, p_146183_2_, true);
    }

    public int getNthWordFromPosWS(final int p_146197_1_, final int p_146197_2_, final boolean p_146197_3_) {
        int i = p_146197_2_;
        final boolean flag = p_146197_1_ < 0;
        final int j = Math.abs(p_146197_1_);

        for (int k = 0; k < j; ++k) {
            if (!flag) {
                final int l = this.text.length();
                i = this.text.indexOf(32, i);

                if (i == -1) {
                    i = l;
                } else {
                    while (p_146197_3_ && i < l && this.text.charAt(i) == 32) {
                        ++i;
                    }
                }
            } else {
                while (p_146197_3_ && i > 0 && this.text.charAt(i - 1) == 32) {
                    --i;
                }

                while (i > 0 && this.text.charAt(i - 1) != 32) {
                    --i;
                }
            }
        }

        return i;
    }

    /**
     * Moves the text cursor by a specified number of characters and clears the selection
     */
    public void moveCursorBy(final int p_146182_1_) {
        this.setCursorPosition(this.selectionEnd + p_146182_1_);
    }

    /**
     * sets the position of the cursor to the provided index
     */
    public void setCursorPosition(final int p_146190_1_) {
        this.cursorPosition = p_146190_1_;
        final int i = this.text.length();
        this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    /**
     * sets the cursors position to the beginning
     */
    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    /**
     * sets the cursors position to after the text
     */
    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }

    /**
     * Call this method from your GuiScreen to process the keys into the textbox
     */
    public boolean textboxKeyTyped(final char typedChar, final int keyCode) {
        if (!this.isFocused) {
            return false;
        }

        if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
        }
        
        if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
        }
        
        if (GuiScreen.isKeyComboCtrlV(keyCode) && this.isEnabled) {
            this.writeText(GuiScreen.getClipboardString());
            return true;
        }
        
        if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(this.getSelectedText());
            if (this.isEnabled) {
                this.writeText("");
            }
            return true;
        }

        switch (keyCode) {
            case 14:
                if (this.isEnabled) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(-1);
                    } else {
                        this.deleteFromCursor(-1);
                    }
                }
                return true;

            case 199:
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(0);
                } else {
                    this.setCursorPositionZero();
                }
                return true;

            case 203:
                handleArrowKey(-1);
                return true;

            case 205:
                handleArrowKey(1);
                return true;

            case 207:
                if (GuiScreen.isShiftKeyDown()) {
                    this.setSelectionPos(this.text.length());
                } else {
                    this.setCursorPositionEnd();
                }
                return true;

            case 211:
                if (this.isEnabled) {
                    if (GuiScreen.isCtrlKeyDown()) {
                        this.deleteWords(1);
                    } else {
                        this.deleteFromCursor(1);
                    }
                }
                return true;

            default:
                if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.isEnabled) {
                    this.writeText(Character.toString(typedChar));
                    return true;
                }
                return false;
        }
    }
    
    private void handleArrowKey(int direction) {
        if (GuiScreen.isShiftKeyDown()) {
            if (GuiScreen.isCtrlKeyDown()) {
                this.setSelectionPos(this.getNthWordFromPos(direction, this.getSelectionEnd()));
            } else {
                this.setSelectionPos(this.getSelectionEnd() + direction);
            }
        } else {
            if (GuiScreen.isCtrlKeyDown()) {
                this.setCursorPosition(this.getNthWordFromCursor(direction));
            } else {
                this.moveCursorBy(direction);
            }
        }
    }

    /**
     * Args: x, y, buttonClicked
     */
    public void mouseClicked(final int p_146192_1_, final int p_146192_2_, final int p_146192_3_) {
        final boolean flag = p_146192_1_ >= this.xPosition && p_146192_1_ < this.xPosition + this.width && p_146192_2_ >= this.yPosition && p_146192_2_ < this.yPosition + this.height;

        if (this.canLoseFocus) {
            this.setFocused(flag);
        }

        if (this.isFocused && flag && p_146192_3_ == 0) {
            int i = p_146192_1_ - this.xPosition;

            if (this.enableBackgroundDrawing) {
                i -= 4;
            }

            final String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.fontRenderer.trimStringToWidth(s, i).length() + this.lineScrollOffset);
        }
    }

    /**
     * Draws the textbox
     */
    public void drawTextBox() {
        if (this.isVisible()) {
            if (this.isEnableBackgroundDrawing()) {
                drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, new Color(0, 0, 0, 150).getRGB());
            }

            int i = this.isEnabled ? this.enabledColor : this.disabledColor;
            int j = this.cursorPosition - this.lineScrollOffset;
            int k = this.selectionEnd - this.lineScrollOffset;
            String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            boolean flag = j >= 0 && j <= s.length();
            boolean flag1 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;

            int textWidth = this.fontRenderer.width(s);
            int xPos = this.xPosition + (this.width - textWidth) / 2;
            int yPos = this.yPosition + (this.height - 8) / 2;

            int j1 = xPos;

            if (k > s.length()) {
                k = s.length();
            }

            if (s.length() > 0) {
                String s1 = flag ? s.substring(0, j) : s;
                j1 = this.fontRenderer.drawWithShadow(s1, (float) xPos, (float) yPos + 1, i);
            }

            boolean flag2 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
            int k1 = j1;

            if (!flag) {
                k1 = j > 0 ? xPos + this.width : xPos;
            } else if (flag2) {
                k1 = j1 - 1;
                --j1;
            }

            if (s.length() > 0 && flag && j < s.length()) {
                j1 = this.fontRenderer.drawWithShadow(s.substring(j), (float) j1, (float) yPos + 1, i);
            }

            if (flag1) {
                if (flag2) {
                    Gui.drawRect(k1, yPos - 1, k1 + 1, yPos + 1 + FontRenderer.FONT_HEIGHT, -3092272);
                } else {
                    Gui.drawRect(k1, yPos - 1, k1 + 1, yPos + FontRenderer.FONT_HEIGHT, -3092272);
                }
            }

            if (k != j) {
                int l1 = xPos + this.fontRenderer.width(s.substring(0, k));
                this.drawCursorVertical(k1, yPos - 1, l1 - 1, yPos + 1 + FontRenderer.FONT_HEIGHT);
            }
        }
    }

    /**
     * draws the vertical line cursor in the textbox
     */
    private void drawCursorVertical(int startX, int startY, int endX, int endY) {
    	int temp;
    	if (startX > endX) {
    	    temp = startX;
    	    startX = endX;
    	    endX = temp;
    	}

    	if (startY > endY) {
    	    temp = startY;
    	    startY = endY;
    	    endY = temp;
    	}

    	startX = Math.max(startX, this.xPosition);
    	endX = Math.min(endX, this.xPosition + this.width);
    	startY = Math.max(startY, this.yPosition);
    	endY = Math.min(endY, this.yPosition + this.height);

        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        
        GlStateManager.color(0.0F, 0.0F, 0.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(startX, endY, 0.0D).endVertex();
        worldrenderer.pos(endX, endY, 0.0D).endVertex();
        worldrenderer.pos(endX, startY, 0.0D).endVertex();
        worldrenderer.pos(startX, startY, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(final int p_146203_1_) {
        this.maxStringLength = p_146203_1_;

        if (this.text.length() > p_146203_1_) {
            this.text = this.text.substring(0, p_146203_1_);
        }
    }

    /**
     * returns the width of the textbox depending on if background drawing is enabled
     */
    public int getWidth() {
        return this.isEnableBackgroundDrawing() ? this.width - 8 : this.width;
    }

    /**
     * Sets the position of the selection anchor (i.e. position the selection was started at)
     */
    public void setSelectionPos(int p_146199_1_) {
        final int i = this.text.length();

        if (p_146199_1_ > i) {
            p_146199_1_ = i;
        }

        if (p_146199_1_ < 0) {
            p_146199_1_ = 0;
        }

        this.selectionEnd = p_146199_1_;

        if (this.fontRenderer != null) {
            if (this.lineScrollOffset > i) {
                this.lineScrollOffset = i;
            }

            final int j = this.getWidth();
            final String s = this.fontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
            final int k = s.length() + this.lineScrollOffset;

            if (p_146199_1_ == this.lineScrollOffset) {
                this.lineScrollOffset -= this.fontRenderer.trimStringToWidth(this.text, j, true).length();
            }

            if (p_146199_1_ > k) {
                this.lineScrollOffset += p_146199_1_ - k;
            } else if (p_146199_1_ <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - p_146199_1_;
            }

            this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, i);
        }
    }
}
