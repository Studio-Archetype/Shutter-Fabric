package studio.archetype.shutter.client.ui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import studio.archetype.shutter.client.config.ClientConfig;
import studio.archetype.shutter.client.ui.widgets.EnumButtonWidget;
import studio.archetype.shutter.client.ui.widgets.EnumSliderWidget;

public class RecordingScreen extends Screen {

    private final ClientConfig config;

    public RecordingScreen(ClientConfig config) {
        super(new LiteralText("shutter-recording"));
        this.config = config;
    }

    @Override
    protected void init() {
        addButton(new EnumButtonWidget<>(new LiteralText("Render Mode"), width / 2 + 25, height / 2 - 49, 150, 20, config.recSettings.renderMode, (v) -> config.recSettings.renderMode = v));
        addButton(new EnumButtonWidget<>(new LiteralText("Codec"), width / 2 - 175, height / 2 - 49, 150, 20, config.recSettings.codec, (v) -> config.recSettings.codec = v));

        addButton(new EnumSliderWidget<>(new LiteralText("Framerate"), width / 2 - 75, height / 2 - 10, 150, 20, config.recSettings.framerate, (v) -> config.recSettings.framerate = v));

        addButton(new ButtonWidget(width / 2 - 75, height / 2 + 29, 150, 20, new LiteralText("Start!").formatted(Formatting.GREEN, Formatting.BOLD), (b) -> {
            //TODO the thing
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
