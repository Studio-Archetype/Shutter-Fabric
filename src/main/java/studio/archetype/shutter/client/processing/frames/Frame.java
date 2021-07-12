package studio.archetype.shutter.client.processing.frames;

import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;

public interface Frame {
    int getFrameId();

    ByteBuffer getData();

    default void printBuffer(boolean metaOnly, int rowSize) {
        ByteBuffer buffer = getData();
        if(buffer == null)
            return;

        StringBuilder builder = new StringBuilder();
        int size = buffer.remaining();
        builder.append("--------------------------------------------------------\n");
        builder.append("Frame ID: ").append(getFrameId()).append("\n");
        builder.append("Thread  : ").append(Thread.currentThread().getName()).append("\n");
        builder.append("Capacity: ").append(buffer.capacity()).append("\n");
        builder.append("Position: ").append(buffer.position()).append("/").append(size).append("\n");
        builder.append("--------------------------------------------------------\n");

        if(metaOnly) {
            System.out.println(builder);
            return;
        }

        for(int row = 0; row < size; row += rowSize) {
            int index = 0;
            while(index < rowSize && (row + index) < size) {
                if((row + index) < size)
                    builder.append(StringUtils.leftPad(Integer.toHexString(buffer.get(row + index)), 2, '0'));
                else
                    builder.append("  ");
                builder.append(' ');
                index++;
            }
            builder.append('\n');
        }

        builder.append("--------------------------------------------------------\n");
        System.out.println(builder);
    }


}
