package studio.archetype.shutter.client.extensions;

public interface CameraExt {

    void setRoll(float roll);
    void addRoll(float roll);
    float getRoll(float tickDelta);

    void setPreviousRoll(float roll);
}
