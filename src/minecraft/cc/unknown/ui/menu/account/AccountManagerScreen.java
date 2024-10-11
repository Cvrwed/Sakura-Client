package cc.unknown.ui.menu.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cc.unknown.Sakura;
import cc.unknown.ui.menu.account.display.AccountViewModel;
import cc.unknown.ui.menu.account.impl.AccountScreen;
import cc.unknown.ui.menu.main.MainMenu;
import cc.unknown.ui.menu.main.impl.Button;
import cc.unknown.util.Accessor;
import cc.unknown.util.account.Account;
import cc.unknown.util.file.alt.AltManager;
import cc.unknown.util.gui.ScrollUtil;
import cc.unknown.util.render.BackgroundUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class AccountManagerScreen extends GuiScreen implements Accessor {
    private static final AltManager ALT_MANAGER = Sakura.instance.getAltManager();
    private static final List<AccountViewModel<?>> ACCOUNT_DISPLAY_LIST = new ArrayList<>();
    private static GuiScreen prevScreen;

    private boolean updateMarker;

    private static int screenWidth;
    private static int screenHeight;
    ScrollUtil scrollUtil = new ScrollUtil();
    private static int accountsInRow;

    public AccountManagerScreen(GuiScreen gui) {
        this.prevScreen = gui;
    }
    
    @Override
    public void initGui() {
    	this.buttonList.clear();
	    screenWidth = width;
	    screenHeight = height;
	    
	    int buttonLenght = 2;
	    int buttonWidth = 100;
	    int buttonHeight = 24;
	    int buttonPadding = 5;
	
	    int buttonX = width / 2 - ((buttonLenght & 1) == 0 ?
	            (buttonWidth + buttonPadding) * (int) (2 / 2) - buttonPadding / 2 :
	            (buttonWidth + buttonPadding) * (int) (2 / 2) + buttonWidth / 2);
	
	    this.buttonList.add(new Button(0, buttonX, height - buttonHeight - buttonPadding * 2, buttonWidth, buttonHeight, "Add Account"));	
	    this.buttonList.add(new Button(1, buttonX += buttonWidth + buttonPadding, height - buttonHeight - buttonPadding * 2, buttonWidth, buttonHeight, "Back"));
	
        if (prevScreen instanceof AccountManagerScreen) {
            reorderViewModels();
            return;
        }

        ACCOUNT_DISPLAY_LIST.clear();
        ALT_MANAGER.load();
        List<Account> accounts = ALT_MANAGER.getAccounts();
        for (Account account : accounts) {
            addDisplay(account);
        }
        
        super.initGui();
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	BackgroundUtil.renderBackground(this);
        // Draw the accounts.
        scrollUtil.onRender();
        scrollUtil.setMax(ACCOUNT_DISPLAY_LIST.isEmpty() ? 0 : (-((ACCOUNT_DISPLAY_LIST.size() % accountsInRow) + 1) *
                ACCOUNT_DISPLAY_LIST.get(0).getHeight() - 10));

        for (int i = 0; i < ACCOUNT_DISPLAY_LIST.size(); i++) {
            AccountViewModel<?> model = ACCOUNT_DISPLAY_LIST.get(i);
            model.setScroll(scrollUtil.getScroll());

            model.draw();
        }

        GlStateManager.pushMatrix();
        this.buttonList.forEach(button -> button.drawButton(mc, mouseX, mouseY));
        GlStateManager.popMatrix();
    }

    @Override
    public void updateScreen() {
        if (updateMarker) {
            updateMarker = false;

            // Handle the account removal.
            List<AccountViewModel<?>> removables = new ArrayList<>();
            for (AccountViewModel<?> model : ACCOUNT_DISPLAY_LIST) {
                if (model.isRemovable()) {
                    ALT_MANAGER.getAccounts().remove(model.getAccount());
                    removables.add(model);
                }
            }

            ACCOUNT_DISPLAY_LIST.removeAll(removables);
            ALT_MANAGER.update();
            reorderViewModels();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (AccountViewModel<?> model : ACCOUNT_DISPLAY_LIST) {
            if (model.mouseClicked(mouseX, mouseY, mouseButton)) {
                updateMarker = true;
                return;
            }
        }
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public void actionPerformed(final GuiButton button) {
    	switch (button.id) {
    	case 0:
    		mc.displayGuiScreen(new AccountScreen());
    		break;
    	case 1:
    		mc.displayGuiScreen(new MainMenu());
    		break;
    	}
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }

    public static void addAccount(Account account) {
        addDisplay(account);
        ALT_MANAGER.getAccounts().add(account);
        ALT_MANAGER.update();
    }

    private static void addDisplay(Account account) {
        int accountWidth = 172;
        int accountHeight = 40;
        int accountPadding = 5;
        accountsInRow = Math.max(1, Math.min(3, (screenWidth - accountWidth / 3) / (accountWidth + accountPadding)));

        // Calculate the account x and y position.
        int accountY = 16 + (accountHeight + accountPadding) * (ACCOUNT_DISPLAY_LIST.size() / accountsInRow);
        int accountX = screenWidth / 2 - ((accountsInRow & 1) == 0 ?
                (accountWidth + accountPadding) * (int) (accountsInRow / 2) - accountPadding / 2 :
                (accountWidth + accountPadding) * (int) (accountsInRow / 2) + accountWidth / 2);

        accountX += (accountWidth + accountPadding) * (ACCOUNT_DISPLAY_LIST.size() % accountsInRow);

        // Add the account view model.
        AccountViewModel<?> viewModel = new AccountViewModel<>(account, accountX, accountY, accountWidth, accountHeight);
        viewModel.setScreenHeight(screenHeight);
        ACCOUNT_DISPLAY_LIST.add(viewModel);
    }

    private void reorderViewModels() {
        List<AccountViewModel<?>> accountViewModels = new ArrayList<>(ACCOUNT_DISPLAY_LIST);
        ACCOUNT_DISPLAY_LIST.clear();
        for (AccountViewModel<?> model : accountViewModels) {
            addDisplay(model.getAccount());
        }
    }

    @Override
    public void onResize(Minecraft mcIn, int p_175273_2_, int p_175273_3_) {
        prevScreen = this;
        super.onResize(mcIn, p_175273_2_, p_175273_3_);
    }

    @Override
    public void onGuiClosed() {
        ACCOUNT_DISPLAY_LIST.clear();
    }
}
