package studio.archetype.shutter.util;

import com.google.common.collect.Maps;
import org.lwjgl.BufferUtils;

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
Taken from ReplayMod
@author Johni0702
@link https://github.com/ReplayMod/ReplayMod/blob/develop/src/main/java/com/replaymod/render/utils/ByteBufferPool.java
 */

public class ByteBufferPool {

    private static final Map<Integer, List<SoftReference<ByteBuffer>>> bufferPool = Maps.newHashMap();

    public static synchronized ByteBuffer allocate(int size) {
        List<SoftReference<ByteBuffer>> available = bufferPool.get(size);
        if(available != null) {
            Iterator<SoftReference<ByteBuffer>> iter = available.iterator();
            try {
                while(iter.hasNext()) {
                    SoftReference<ByteBuffer> reference = iter.next();
                    ByteBuffer buffer = reference.get();
                    iter.remove();
                    if(buffer != null) {
                        return buffer;
                    }
                }
            } finally {
                if(!iter.hasNext()) {
                    bufferPool.remove(size);
                }
            }
        }
        return BufferUtils.createByteBuffer(size);
    }

    public static synchronized void release(ByteBuffer buffer) {
        buffer.clear();
        int size = buffer.capacity();
        List<SoftReference<ByteBuffer>> available = bufferPool.computeIfAbsent(size, k -> new LinkedList<>());
        available.add(new SoftReference<>(buffer));
    }
}