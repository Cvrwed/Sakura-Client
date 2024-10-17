package cc.unknown.ui.menu.account.impl;

import java.io.IOException;

import cc.unknown.Sakura;
import cc.unknown.ui.menu.account.AccountManagerScreen;
import cc.unknown.ui.menu.account.display.AccountViewModel;
import cc.unknown.ui.menu.main.impl.Button;
import cc.unknown.ui.menu.main.impl.TextField;
import cc.unknown.util.Accessor;
import cc.unknown.util.account.name.UsernameGenerator;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.vector.Vector2d;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class RenameAccountScreen extends GuiScreen implements Accessor {
    private static GuiScreen reference;
    private static TextField usernameBox;
    private AccountViewModel<?> accountViewModel;

    public RenameAccountScreen(AccountViewModel<?> accountViewModel) {
        this.accountViewModel = accountViewModel;
        reference = this;
    }
    
    @Override
    public void initGui() {
    	this.buttonList.clear();
        int boxWidth = 200;
        int boxHeight = 24;
        int padding = 4;
        float buttonWidth = (boxWidth - padding) / 2.0F;

        Vector2d position = new Vector2d(width / 2 - boxWidth / 2, height / 2 - 32);
        accountViewModel = new AccountViewModel<>(accountViewModel.getAccount(), (float) position.x, (float) position.y, 200, 40);
        accountViewModel.setScreenHeight(height);

        position = new Vector2d(width / 2 - boxWidth / 2, height / 2 + 32);
        usernameBox = new TextField(0, this.fontRendererObj, (int) position.x, (int) position.y, (int) boxWidth, (int) boxHeight);
        usernameBox.setText(accountViewModel.getAccount().getName());
    	this.buttonList.add(new Button(1, (int) position.x, (int) position.y + boxHeight + padding, (int) buttonWidth, (int) boxHeight, "Update"));
    	this.buttonList.add(new Button(2, (int) ((int) position.x + buttonWidth + padding), (int) position.y + boxHeight + padding, (int) buttonWidth, (int) boxHeight, "Back"));    	
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        BackgroundUtil.renderBackground(this);
        usernameBox.drawTextBox();
        accountViewModel.draw();
        GlStateManager.pushMatrix();
        this.buttonList.forEach(button -> button.drawButton(mc, mouseX, mouseY));
        GlStateManager.popMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	usernameBox.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
    	usernameBox.textboxKeyTyped(typedChar, keyCode);
        if (typedChar == '\r') {
            this.actionPerformed(this.buttonList.get(0));
        }
    }
    
    @Override
    public void actionPerformed(final GuiButton button) {
        String username = usernameBox.getText();

    	switch (button.id) {
        case 1: 
            if (!UsernameGenerator.validate(username)) {
                return;
            }

            accountViewModel.getAccount().setName(username);
            accountViewModel.getAccount().login();
            Sakura.instance.getAltManager().update();
            mc.displayGuiScreen(new AccountManagerScreen(reference));
        	break;
        case 2:
            mc.displayGuiScreen(new AccountManagerScreen(reference));
        	break;
        }
    }
}
