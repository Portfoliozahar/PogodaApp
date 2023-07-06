# Сервис погоды

для работы надо создать в MySQL 


-- Создание Базы
```sql
CREATE DATABASE app;
```

```sql
-- Создание таблицы Coordinates
CREATE TABLE Coordinates (
  id INT AUTO_INCREMENT PRIMARY KEY,
  latitude FLOAT NOT NULL,
  longitude FLOAT NOT NULL
);

-- Создание таблицы Forecast
```sql
CREATE TABLE Forecast (
  id INT AUTO_INCREMENT PRIMARY KEY,
  coordinate_id INT NOT NULL,
  date_time DATETIME NOT NULL,
  temperature FLOAT NOT NULL,
  precipitation FLOAT NOT NULL,
  FOREIGN KEY (coordinate_id) REFERENCES Coordinates(id)
);
```

-- Тесты
```sql
INSERT INTO Coordinates (latitude, longitude) VALUES
  (52.52, 13.41),
  (51.51, -0.13),
  (40.71, -74.01);
```

-- Тесты
```sql
INSERT INTO Forecast (coordinate_id, date_time, temperature, precipitation) VALUES
  (1, '2023-07-06 12:00:00', 25.5, 0.2),
  (1, '2023-07-06 15:00:00', 27.8, 0.0),
  (2, '2023-07-06 12:00:00', 20.1, 0.0),
  (2, '2023-07-06 15:00:00', 22.3, 0.0),
  (3, '2023-07-06 12:00:00', 30.0, 0.5),
  (3, '2023-07-06 15:00:00', 32.2, 0.0);
```

##Подключение

String username = "root";

String password = "12345";

String url = "jdbc:mysql://localhost:3306/app";

Class.forName("com.mysql.cj.jdbc.Driver");
