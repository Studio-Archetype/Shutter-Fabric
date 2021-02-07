package studio.archetype.shutter.client.config;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import studio.archetype.shutter.pathing.CameraPathManager;
import studio.archetype.shutter.util.SerializationUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SaveFile {

    private static final File PATH_FILE = FabricLoader.getInstance().getGameDir().resolve("shutter_paths.nbt").toFile();

    private final Map<String, Map<Identifier, CameraPathManager>> remoteServerSaves;
    private final Map<String, Map<Identifier, CameraPathManager>> localWorldSaves;

    public static SaveFile getSaveFile() {
        try {
            if(!PATH_FILE.exists())
                return new SaveFile();
            Tag shutter = NbtIo.readCompressed(PATH_FILE).get("Shutter");
            return NbtOps.INSTANCE.withDecoder(CODEC).apply(shutter).result().orElse(new Pair<>(new SaveFile(), new CompoundTag())).getFirst();
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

    public void save() {
        try {
            Optional<Tag> nbt = NbtOps.INSTANCE.withEncoder(CODEC).apply(this).resultOrPartial(System.out::println);
            if(nbt.isPresent()) {
                CompoundTag tag = new CompoundTag();
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
