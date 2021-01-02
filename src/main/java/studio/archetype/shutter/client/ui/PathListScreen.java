package studio.archetype.shutter.client.ui;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.ScrollingContainer;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.options.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;
import studio.archetype.shutter.client.ShutterClient;
import studio.archetype.shutter.pathing.CameraPath;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.pathing.exceptions.PathEmptyException;

import java.util.ArrayList;
import java.util.List;

public class PathListScreen extends Screen {

    private final CameraPathManager paths;

    private ScrollingContainer pathList;

    public PathListScreen(World w) {
        super(new TranslatableText("screen.shutter.title"));
        this.paths = ShutterClient.INSTANCE.getPathManager(w);
    }

    @Override
    public void init() {
        pathList = new ScrollingContainer() {
            public Rectangle getBounds() { return new Rectangle(width, height); }
            public int getMaxScrollHeight() { return 0; }
        };

        if(!paths.getPaths().isEmpty()) {
            for (int i = 0; i < paths.getPaths().size(); i++) {
                CameraPath path = paths.getPaths().get(i);
                List<AbstractConfigListEntry> entries = new ArrayList<>();
                entries.add(ConfigEntryBuilder.create().startTextField(new TranslatableText("ui.shutter.pathList.entryName"), path.id.toString()).build());
                SubCategoryListEntry pathEntry = new SubCategoryListEntry(
                        new TranslatableText("ui.shutter.pathList.entry", i),
                        entries,
                        i == 0
                );
            }
        }

        addButton(new ButtonWidget(this.width / 2 - 100, this.height - 50, 200, 20, ScreenTexts.DONE, (button) -> this.client.openScreen(null) ));
        addButton(new ButtonWidget(this.width / 2 - 100, this.height - 80, 200, 20, new LiteralText("Clear Path"), (button) -> {
            try {
                ShutterClient.INSTANCE.getPathManager(MinecraftClient.getInstance().world).clearPath(CameraPathManager.DEFAULT_PATH);
            } catch(PathEmptyException ignored) { }
        }));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        if(isEmpty()) {
            drawCenteredText(matrices,
                    this.textRenderer,
                    new TranslatableText("ui.shutter.pathList.none").setStyle(Style.EMPTY.withItalic(true)),
                    this.width / 2, this.height / 2 - textRenderer.fontHeight / 2, 16777215);
        } else {
            pathList.updatePosition(delta * 3);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public void tick() {
        if(!isEmpty()) {

        }
    }

    private boolean isEmpty() {
        return paths.getPaths().size() == 1 && paths.getPaths().get(0).getNodes().isEmpty();
    }
}
