public class ForecastResponse {
    private float temperature;
    private float precipitation;

    public ForecastResponse(float temperature, float precipitation) {
        this.temperature = temperature;
        this.precipitation = precipitation;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getPrecipitation() {
        return precipitation;
    }
}
