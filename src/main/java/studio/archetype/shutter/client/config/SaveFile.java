package studio.archetype.shutter.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.util.SerializationUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SaveFile {

    public static final Path SHUTTER_DIR = FabricLoader.getInstance().getGameDir().resolve("shutter");
    public static final Path SHUTTER_REC_DIR = SHUTTER_DIR.resolve("recordings");

    private static final File PATH_FILE = SHUTTER_DIR.resolve("shutter_paths.nbt").toFile();

    private final Map<String, Map<Identifier, CameraPathManager>> remoteServerSaves;
    private final Map<String, Map<Identifier, CameraPathManager>> localWorldSaves;

    public static SaveFile getSaveFile() {
        try {
            if(!PATH_FILE.exists()) {
                SHUTTER_DIR.toFile().mkdirs();
                return new SaveFile();
            }

            NbtElement shutter = NbtIo.readCompressed(PATH_FILE).get("Shutter");
            return NbtOps.INSTANCE.withDecoder(CODEC).apply(shutter).result().orElse(new Pair<>(new SaveFile(), new NbtCompound())).getFirst();
        } catch(IOException e) {
            System.out.println("Shutter: Failed to read save data!");
            e.printStackTrace();
            return new SaveFile();
        }
    }

    private SaveFile() {
        this.remoteServerSaves = new HashMap<>();
        this.localWorldSaves = new HashMap<>();
    }

    private SaveFile(Map<String, Map<Identifier, CameraPathManager>> remote, Map<String, Map<Identifier, CameraPathManager>> local) {
        this.remoteServerSaves = new HashMap<>(remote);
        this.localWorldSaves = new HashMap<>(local);
    }

    public Map<Identifier, CameraPathManager> getLocalWorldSaves(String saveFile) {
        if(!localWorldSaves.containsKey(saveFile))
            localWorldSaves.put(saveFile, new HashMap<>());
        return localWorldSaves.get(saveFile);
    }

    public Map<Identifier, CameraPathManager> getRemoteServerSaves(String ip) {
        if(!remoteServerSaves.containsKey(ip))
            remoteServerSaves.put(ip, new HashMap<>());
        return remoteServerSaves.get(ip);
    }

    public boolean exportJson(String id, JsonElement json) {
        try(OutputStream stream = new FileOutputStream(SHUTTER_DIR.resolve(id + ".json").toFile())) {
            IOUtils.write(json.toString(), stream, Charset.defaultCharset());
            return true;
        } catch(IOException e) {
            System.out.println("Failed to export path!");
            e.printStackTrace();
            return false;
        }
    }

    public JsonElement importJson(String id) throws IOException {
        File f = SHUTTER_DIR.resolve(id + ".json").toFile();
        return new JsonParser().parse(IOUtils.toString(new FileInputStream(f), Charset.defaultCharset()));
    }

    public void save() {
        try {
            Optional<NbtElement> nbt = NbtOps.INSTANCE.withEncoder(CODEC).apply(this).resultOrPartial(System.out::println);
            if(nbt.isPresent()) {
                NbtCompound tag = new NbtCompound();
                tag.put("Shutter", nbt.get());
                NbtIo.writeCompressed(tag, PATH_FILE);
            } else {
                System.out.println("Shutter: Failed to encode data!");
            }
        } catch(IOException e) {
            System.out.println("Shutter: Failed to save data!");
            e.printStackTrace();
        }
    }

    private final static Codec<Map<Identifier, CameraPathManager>> CODEC_MANAGER = Codec
            .unboundedMap(SerializationUtils.CODEC_IDENTIFIER, CameraPathManager.CODEC)
            .fieldOf("WorldData")
            .codec()
            .xmap(HashMap::new, map -> map);

    private final static Codec<SaveFile> CODEC = RecordCodecBuilder.create(i ->
            i.group(
                    Codec.unboundedMap(Codec.STRING, CODEC_MANAGER).fieldOf("Server").forGetter((SaveFile o) -> o.remoteServerSaves),
                    Codec.unboundedMap(Codec.STRING, CODEC_MANAGER).fieldOf("Local").forGetter((SaveFile o) -> o.localWorldSaves)
            ).apply(i, SaveFile::new));
}
