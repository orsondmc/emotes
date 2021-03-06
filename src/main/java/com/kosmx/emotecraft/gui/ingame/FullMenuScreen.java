package com.kosmx.emotecraft.gui.ingame;

import com.kosmx.emotecraft.config.EmoteHolder;
import com.kosmx.emotecraft.gui.EmoteMenu;
import com.kosmx.emotecraft.gui.widget.AbstractEmoteListWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.List;

public class FullMenuScreen extends Screen {

    private TextFieldWidget searchBox;
    private EmoteList emoteList;

    public FullMenuScreen(Text title){
        super(title);
    }

    @Override
    public void init(){
        int x = (int) Math.min(this.width * 0.8, this.height - 60);
        this.searchBox = new TextFieldWidget(this.textRenderer, (this.width - x) / 2, 12, x, 20, this.searchBox, new TranslatableText("emotecraft.search"));
        this.searchBox.setChangedListener((string)->this.emoteList.filter(string::toLowerCase));
        this.emoteList = new EmoteList(this.client, x, height, width, this);
        this.emoteList.setLeftPos((this.width - x) / 2);
        emoteList.setEmotes(EmoteHolder.list);
        this.children.add(searchBox);
        this.children.add(emoteList);
        this.setInitialFocus(this.searchBox);
        this.buttons.add(new ButtonWidget(this.width - 120, this.height - 30, 96, 20, ScreenTexts.CANCEL, (button->this.client.openScreen(null))));
        this.buttons.add(new ButtonWidget(this.width - 120, this.height - 60, 96, 20, new TranslatableText("emotecraft.config"), (button->this.client.openScreen(new EmoteMenu(this)))));
        this.children.addAll(this.buttons);
    }

    @Override
    public boolean isPauseScreen(){
        return false;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
        this.renderBackgroundTexture(0);
        this.emoteList.render(matrices, mouseX, mouseY, delta);
        this.searchBox.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);
    }

    private class EmoteList extends AbstractEmoteListWidget<EmoteList.EmoteEntry> {

        public EmoteList(MinecraftClient client, int boxSize, int height, int width, Screen screen){
            super(client, boxSize, height, (height - boxSize) / 2 + 10, width > (width + boxSize)/2 + 120 ? (height + boxSize) / 2 + 10 : height - 80,36, screen);
        }

        @Override
        public void setEmotes(List<EmoteHolder> list){
            for(EmoteHolder emote : list){
                this.emotes.add(new EmoteEntry(this.client, emote));
            }
            filter(()->"");
        }

        private class EmoteEntry extends AbstractEmoteListWidget.AbstractEmoteEntry<EmoteEntry> {

            public EmoteEntry(MinecraftClient client, EmoteHolder emote){
                super(client, emote);
            }

            @Override
            protected void onPressed(){
                if(MinecraftClient.getInstance().getCameraEntity() instanceof ClientPlayerEntity){
                    this.emote.playEmote(MinecraftClient.getInstance().player);
                    this.client.openScreen(null);
                }
            }
        }
    }
}
