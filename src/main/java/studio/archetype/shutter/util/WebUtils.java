package studio.archetype.shutter.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.commons.io.Charsets;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import studio.archetype.shutter.client.ui.Messaging;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebUtils {

    private static final ExecutorService EXECUTORS = Executors.newFixedThreadPool(3);
    private static final String PASTEBIN_URL = "https://pastebin.com/api/api_post.php";
    private static final String API_KEY = "0b44c61a5421b90a72317de87bdb623a";

    public static void createPaste(String content) {
        EXECUTORS.execute(() -> {
            try {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpPost post = new HttpPost(PASTEBIN_URL);
                post.setHeader("Content-type", "application/json");
                JsonObject json = new JsonObject();
                json.addProperty("api_dev_key", API_KEY);
                json.addProperty("api_option", "paste");
                json.addProperty("api_paste_private", 1);
                json.addProperty("api_paste_format", "json");
                json.addProperty("api_paste_code", content);
                post.setEntity(new StringEntity(json.toString(), StandardCharsets.UTF_8));
                ResponseHandler<String> handler = r -> {
                    int status = r.getStatusLine().getStatusCode();
                    if ((status >= 200 && status <= 300) && r.getEntity() != null) {
                        JsonObject obj = new JsonParser().parse(EntityUtils.toString(r.getEntity())).getAsJsonObject();
                        return obj.toString();
                    } else
                        throw new HttpResponseException(status, EntityUtils.toString(r.getEntity()));
                };

                String s = client.execute(post, handler);
                MinecraftClient c = MinecraftClient.getInstance();

                c.execute(() -> {
                    Messaging.sendMessage(
                            new TranslatableText("msg.shutter.headline.cmd.success"),
                            new TranslatableText("msg.shutter.ok.upload", s),
                            Messaging.MessageType.POSITIVE);
                    c.player.sendMessage(new TranslatableText("msg.shutter.ok.pastebin", s).setStyle(Style.EMPTY.withClickEvent(
                            new ClickEvent(ClickEvent.Action.OPEN_URL, s)
                    )), false);
                });
            } catch(HttpResponseException e) {
                MinecraftClient c = MinecraftClient.getInstance();
                c.execute(() -> {
                    Messaging.sendMessage(
                            new TranslatableText("msg.shutter.headline.cmd.failed"),
                            new TranslatableText("msg.shutter.error.upload"),
                            Messaging.MessageType.NEGATIVE);
                    c.player.sendMessage(new TranslatableText("msg.shutter.error.pastebin", e.getMessage() + " | " + e.getStatusCode()).formatted(Formatting.RED), false);
                });
            } catch(IOException e) {
                MinecraftClient c = MinecraftClient.getInstance();
                c.execute(() -> {
                    Messaging.sendMessage(
                            new TranslatableText("msg.shutter.headline.cmd.failed"),
                            new TranslatableText("msg.shutter.error.upload"),
                            Messaging.MessageType.NEGATIVE);
                    c.player.sendMessage(new TranslatableText("msg.shutter.error.pastebin", e.getMessage()).formatted(Formatting.RED), false);
                });
                e.printStackTrace();
            }
        });
    }
}
