package studio.archetype.shutter.client.ui;

import net.minecraft.text.Text;

public class ShutterWaitingToast extends ShutterMessageToast {

    private boolean isDone;

    public ShutterWaitingToast(Type type, Text title, Text subtitle, Text subsubTitle) {
        super(type, title, subtitle, subsubTitle);
        this.isDone = false;
    }

    public void done() {
        this.isDone = true;
    }

    @Override
    protected Visibility getVisibility(long startTime) {
        return isDone ? Visibility.HIDE : Visibility.SHOW;
    }
}
