package cc.unknown.ui.menu.account.impl;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.SwingUtilities;

import com.google.gson.Gson;

import cc.unknown.ui.menu.account.AccountManagerScreen;
import cc.unknown.ui.menu.main.impl.Button;
import cc.unknown.ui.menu.main.impl.TextField;
import cc.unknown.util.Accessor;
import cc.unknown.util.account.auth.MicrosoftLogin;
import cc.unknown.util.account.impl.MicrosoftAccount;
import cc.unknown.util.render.BackgroundUtil;
import cc.unknown.util.vector.Vector2d;
import cc.unknown.util.web.Browser;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class CookieScreen extends GuiScreen implements Accessor {
    private static TextField usernameBox;
    private static GuiScreen reference;
    private static String text_to_render = "Select file";
    private static String[] cookie_string;
    private static final Gson gson = new Gson();

    public CookieScreen() {
        reference = this;
    }
    
    @Override
    public void initGui() {
    	buttonList.clear();
        int boxWidth = 200;
        int boxHeight = 24;
        int padding = 4;
        float buttonWidth = (boxWidth - padding * 2) / 3.0F;

        Vector2d position = new Vector2d(this.width / 2 - boxWidth / 2, this.height / 2 - 24);
        usernameBox = new TextField(0, this.fontRendererObj, (int) position.x, (int) position.y, (int) boxWidth, (int) boxHeight);
    	this.buttonList.add(new Button(1, (int) position.x, (int) position.y + boxHeight + padding, (int) boxWidth, (int) boxHeight, "Login & Add"));
    	this.buttonList.add(new Button(2, (int) position.x, (int) position.y + (boxHeight + padding) * 2, (int) buttonWidth, (int) boxHeight, "Select File"));
    	this.buttonList.add(new Button(3, (int) ((int) position.x + buttonWidth + padding), (int) position.y + (boxHeight + padding) * 2, (int) buttonWidth, (int) boxHeight, "Microsoft"));
    	this.buttonList.add(new Button(4, (int) ((int) position.x + (buttonWidth + padding) * 2), (int) position.y + (boxHeight + padding) * 2, (int) buttonWidth, (int) boxHeight, "Back"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        BackgroundUtil.renderBackground(this);

        usernameBox.drawTextBox();
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
            this.actionPerformed(this.buttonList.get(2));
        }
    }
    
    @Override
    public void actionPerformed(final GuiButton button) {
    	switch (button.id) {
        case 1: 
            new Thread(() -> {
                try {
                    if (cookie_string.length != 0) {
                        StringBuilder cookies = new StringBuilder();
                        ArrayList<String> cooki = new ArrayList<>();
                        for (String cookie : cookie_string) {
                            if (cookie.split("\t")[0].endsWith("login.live.com") && !cooki.contains(cookie.split("\t")[5])) {
                                cookies.append(cookie.split("\t")[5]).append("=").append(cookie.split("\t")[6]).append("; ");
                                cooki.add(cookie.split("\t")[5]);
                            }
                        }
                        cookies = new StringBuilder(cookies.substring(0, cookies.length() - 2));
                        HttpsURLConnection connection = (HttpsURLConnection) new URL("https://sisu.xboxlive.com/connect/XboxLive/?state=login&cobrandId=8058f65d-ce06-4c30-9559-473c9275a65d&tid=896928775&ru=https%3A%2F%2Fwww.minecraft.net%2Fen-us%2Flogin&aid=1142970254").openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
                        connection.setRequestProperty("Accept-Encoding", "niggas");
                        connection.setRequestProperty("Accept-Language", "en-US;q=0.8");
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
                        connection.setInstanceFollowRedirects(false);
                        connection.connect();

                        String location = connection.getHeaderField("Location").replaceAll(" ", "%20");
                        connection = (HttpsURLConnection) new URL(location).openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
                        connection.setRequestProperty("Accept-Encoding", "niggas");
                        connection.setRequestProperty("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
                        connection.setRequestProperty("Cookie", cookies.toString());
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
                        connection.setInstanceFollowRedirects(false);
                        connection.connect();

                        String location2 = connection.getHeaderField("Location");

                        connection = (HttpsURLConnection) new URL(location2).openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
                        connection.setRequestProperty("Accept-Encoding", "niggas");
                        connection.setRequestProperty("Accept-Language", "fr-FR,fr;q=0.9,en-US;q=0.8,en;q=0.7");
                        connection.setRequestProperty("Cookie", cookies.toString());
                        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36");
                        connection.setInstanceFollowRedirects(false);
                        connection.connect();

                        String location3 = connection.getHeaderField("Location");
                        String accessToken = location3.split("accessToken=")[1];

                        String decoded = new String(Base64.getDecoder().decode(accessToken), StandardCharsets.UTF_8).split("\"rp://api.minecraftservices.com/\",")[1];
                        String token = decoded.split("\"Token\":\"")[1].split("\"")[0];
                        String uhs = decoded.split(Pattern.quote("{\"DisplayClaims\":{\"xui\":[{\"uhs\":\""))[1].split("\"")[0];

                        String xbl = "XBL3.0 x=" + uhs + ";" + token;

                        final MicrosoftLogin.McResponse mcRes = gson.fromJson(
                                Browser.postExternal("https://api.minecraftservices.com/authentication/login_with_xbox",
                                        "{\"identityToken\":\"" + xbl + "\",\"ensureLegacyEnabled\":true}", true),
                                MicrosoftLogin.McResponse.class);

                        if (mcRes == null) {
                            text_to_render = "Invalid Account";
                            return;
                        }

                        final MicrosoftLogin.ProfileResponse profileRes = gson.fromJson(
                                Browser.getBearerResponse("https://api.minecraftservices.com/minecraft/profile", mcRes.access_token),
                                MicrosoftLogin.ProfileResponse.class);

                        if (profileRes == null) {
                            text_to_render = "Invalid Account";
                            return;
                        }
                        
                        MicrosoftAccount microsoftAccount = new MicrosoftAccount(profileRes.name, profileRes.id, mcRes.access_token, "");
                        AccountManagerScreen.addAccount(microsoftAccount);
                        microsoftAccount.login();
                        mc.displayGuiScreen(new AccountScreen());
                    }
                } catch (Exception e) {
                    text_to_render = "Invalid Account";
                }

            }, "Login To Cookie").start();
        	break;
        case 2:
            new Thread(() -> {
                FileDialog dialog = new FileDialog((Frame) null, "Select Cookie File");
                dialog.setMode(FileDialog.LOAD);
                dialog.setVisible(true);

                String fileName = dialog.getFile();
                String directory = dialog.getDirectory();
                dialog.dispose();

                if (fileName != null) {
                    String path = new File(directory, fileName).getAbsolutePath();
                    try (Scanner scanner = new Scanner(new FileReader(path))) {
                        StringBuilder content = new StringBuilder();
                        while (scanner.hasNextLine()) {
                            content.append(scanner.nextLine()).append("\n");
                        }
                        SwingUtilities.invokeLater(() -> {
                            usernameBox.setText(fileName);
                            text_to_render = "Selected file!";
                            cookie_string = content.toString().split("\n");
                        });
                    } catch (IOException e) {
                        SwingUtilities.invokeLater(() -> {
                            text_to_render = "Error (read)";
                        });
                    }
                } else {
                    SwingUtilities.invokeLater(() -> {
                        text_to_render = "No file selected.";
                    });
                }
            }, "Select Cookie File").start();
            break;
        case 3:
        	mc.displayGuiScreen(new MicrosoftScreen());
        	break;
        case 4:
        	mc.displayGuiScreen(new AccountScreen());
        	break;

        }
    }
}
