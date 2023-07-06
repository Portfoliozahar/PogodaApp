import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

public class Main {
    private Connection connection;
    public Main() throws SQLException {


        String username = "root";
        String password = "12345";
        String url = "jdbc:mysql://localhost:3306/app";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int regLatLong(float latitude, float longitude) {
        int id = -1;

        try {

            String insertQuery = "INSERT INTO coordinates (latitude, longitude) VALUES (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setFloat(1, latitude);
            preparedStatement.setFloat(2, longitude);
            preparedStatement.executeUpdate();


            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                id = generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public List<LocalDateTime> getAvailableForecastAsDateTimeList(int id) {
        List<LocalDateTime> dateTimeList = new ArrayList<>();

        try {

            String selectQuery = "SELECT DISTINCT date_time FROM forecast WHERE coordinate_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                LocalDateTime dateTime = resultSet.getTimestamp("date_time").toLocalDateTime();
                dateTimeList.add(dateTime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dateTimeList;
    }

    public boolean updateForecast(int id, float latitude, float longitude) {
        try {

            String updateQuery = "UPDATE coordinates SET latitude = ?, longitude = ? WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setFloat(1, latitude);
            preparedStatement.setFloat(2, longitude);
            preparedStatement.setInt(3, id);
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ForRes getForecastByDateTime(int id, LocalDateTime dateTime) {
        ForRes forecastResponse = null;

        try {

            String selectQuery = "SELECT temperature, precipitation FROM forecast WHERE coordinate_id = ? AND date_time = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setInt(1, id);
            preparedStatement.setTimestamp(2, Timestamp.valueOf(dateTime));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                float temperature = resultSet.getFloat("temperature");
                float precipitation = resultSet.getFloat("precipitation");
                forecastResponse = new ForRes(temperature, precipitation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return forecastResponse;
    }

    public List<LatTarget> getAllTargets() {
        List<LatTarget> targets = new ArrayList<>();

        try {

            String selectQuery = "SELECT id, latitude, longitude FROM coordinates";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(selectQuery);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                float latitude = resultSet.getFloat("latitude");
                float longitude = resultSet.getFloat("longitude");
                LatTarget target = new LatTarget(id, latitude, longitude);
                targets.add(target);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return targets;
    }

    public static void main(String[] args) throws SQLException {
        Main weatherApp = new Main();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Выберите команду:");
            System.out.println("1 - Введите новые координаты");
            System.out.println("2 - Получить список всех зарегистрированных координат");
            System.out.println("3 - Обновить данные");
            System.out.println("4 - Получить доступные даты-время прогноза");
            System.out.println("5 - Получить прогноз \n");

            int command = scanner.nextInt();

            switch (command) {
                case 1:
                    System.out.println("Введите широту:");
                    float latitude = scanner.nextFloat();
                    System.out.println("Введите долготу:");
                    float longitude = scanner.nextFloat();
                    int id = weatherApp.regLatLong(latitude, longitude);
                    System.out.println("Зарегистрированные координаты. ID " + id);
                    break;
                case 2:
                    List<LatTarget> targets = weatherApp.getAllTargets();
                    System.out.println("Зарегистрированные координаты:");
                    for (LatTarget target : targets) {
                        System.out.println("ID: " + target.getId() + ", Широта: " + target.getLatitude() + ", Долгота: " + target.getLongitude());
                    }
                    break;
                case 3:
                    System.out.println("Введите ID координат для обновления:");
                    int updateId = scanner.nextInt();
                    System.out.println("Введите новую широту:");
                    float newLatitude = scanner.nextFloat();
                    System.out.println("Введите новую долготу:");
                    float newLongitude = scanner.nextFloat();
                    boolean isUpdated = weatherApp.updateForecast(updateId, newLatitude, newLongitude);
                    if (isUpdated) {
                        System.out.println("Координаты успешно обновлены.");
                    } else {
                        System.out.println("Не удалось обновить координаты.");
                    }
                    break;
                case 4:
                    System.out.println("Введите ID координат, чтобы получить доступные даты:");
                    int availableId = scanner.nextInt();
                    List<LocalDateTime> dateTimeList = weatherApp.getAvailableForecastAsDateTimeList(availableId);
                    System.out.println("Доступные даты и время прогноза:");
                    for (LocalDateTime dateTime : dateTimeList) {
                        System.out.println(dateTime);
                    }
                    break;
                case 5:
                    System.out.println("Введите ID координат:");
                    int forecastId = scanner.nextInt();
                    System.out.println("Введите дату прогноза (гггг-ММ-дд):");
                    String forecastDateString = scanner.next();
                    System.out.println("Введите время прогноза (ЧЧ:мм):");
                    String forecastTimeString = scanner.next();
                    String forecastDateTimeString = forecastDateString + " " + forecastTimeString;
                    LocalDateTime forecastDateTime = LocalDateTime.parse(forecastDateTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    ForRes forecastResponse = weatherApp.getForecastByDateTime(forecastId, forecastDateTime);
                    if (forecastResponse != null) {
                        System.out.println("Температура: " + forecastResponse.getTemperature());
                        System.out.println("Осадки: " + forecastResponse.getPrecipitation());
                    } else {
                        System.out.println("Прогноз не найден.");
                    }
                    break;

                default:
                    System.out.println("Error.");
                    break;
            }

        }
    }
}
