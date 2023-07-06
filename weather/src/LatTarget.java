public class LatTarget {
    private int id;
    private float latitude;
    private float longitude;

    public LatTarget(int id, float latitude, float longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }
}
