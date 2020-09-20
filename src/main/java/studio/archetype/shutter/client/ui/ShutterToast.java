package studio.archetype.shutter.client.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.client.entityrenderer.CameraPointEntityRenderer;

public class ShutterToast implements Toast {

    private static final int HEIGHT = 48;
    private static final int WIDTH = 160;

    private ToastBackgrounds bg;

    private String title, sub1, sub2;

    public ShutterToast(ToastBackgrounds bg, String title, String sub1, String sub2) {
        this.bg = bg;
        this.title = title;
        this.sub1 = sub1;
        this.sub2 = sub2;
    }

    @Override
    public Visibility draw(MatrixStack matrices, ToastManager manager, long startTime) {
        bg.draw(matrices, 0, 0, manager);
        drawHead(manager.getGame(), 8, 8);
        manager.getGame().textRenderer.draw(matrices, title, 30.0F, 7.0F, 16776960 | -16777216);
        manager.getGame().textRenderer.draw(matrices, sub1, 30.0F, 20.0F, -1);
        manager.getGame().textRenderer.draw(matrices, sub2, 30.0F, 31.0F, -1);
        return startTime >= 5000L ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    private void drawHead(MinecraftClient c, int x, int y) {
        ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
        CompoundTag ownerTag = new CompoundTag();
        ownerTag.putIntArray("Id", new int[]{-1113502024, 843466295, -1803561648, -487382137});

        CompoundTag textureVal = new CompoundTag();
        textureVal.putString("Value", CameraPointEntityRenderer.CAMERA_TEX);
        ListTag textures = new ListTag();
        textures.add(textureVal);
        CompoundTag properties = new CompoundTag();
        properties.put("textures", textures);
        ownerTag.put("Properties", properties);

        stack.setTag((CompoundTag) new CompoundTag().put("SkullOwner", ownerTag));

        c.getItemRenderer().renderInGui(stack, x, y);
    }

    public enum ToastBackgrounds {
        NEUTRAL(0),
        POSITIVE(1),
        NEGATIVE(2);

        private final int y;

        ToastBackgrounds(int y) {
            this.y = y;
        }

        public void draw(MatrixStack matrix, int x, int y, ToastManager man) {
            man.getGame().getTextureManager().bindTexture(new Identifier("shutter", "textures/ui/toasts.png"));
            man.drawTexture(matrix, x, y, 0, this.y * HEIGHT, WIDTH, HEIGHT);
        }
    }
}
