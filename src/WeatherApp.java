import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

// Strategy Pattern: WeatherDataCollectionStrategy
interface WeatherDataCollectionStrategy {
    void collectData(WeatherData weatherData);
}

class WeatherAPI implements WeatherDataCollectionStrategy {
    public void collectData(WeatherData weatherData) {
        // Simulate fetching weather data from an API (replace with actual API calls)
        double temperature = Math.random() * 50 + 10;  // Градусы Цельсия
        double humidity = Math.random() * 50 + 50;  // Проценты
        double pressure = Math.random() * 10 + 1013;  // гПа (гектопаскали)
        weatherData.setMeasurements(temperature, humidity, pressure);
    }
}

class WeatherSensor implements WeatherDataCollectionStrategy {
    public void collectData(WeatherData weatherData) {
        // Simulate collecting weather data from sensors (replace with real sensor data)
        double temperature = 22.0;  // Градусы Цельсия
        double humidity = 60.0;  // Проценты
        double pressure = 1015.0;  // гПа (гектопаскали)
        weatherData.setMeasurements(temperature, humidity, pressure);
    }
}

// Observer Pattern: WeatherObserver
class WeatherObserver implements Observer {
    private WeatherData weatherData;
    private double temperatureThreshold;

    public WeatherObserver(WeatherData weatherData, double temperatureThreshold) {
        this.weatherData = weatherData;
        this.temperatureThreshold = temperatureThreshold;
        weatherData.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof WeatherData) {
            WeatherData weatherData = (WeatherData) o;
            double temperature = weatherData.getTemperature();

            if (temperature < temperatureThreshold) {
                System.out.println("Температура ниже " + temperatureThreshold + "°C. Предупреждение!");
            }
        }
    }
}
// Decorator Pattern: WeatherReportDecorator
abstract class WeatherReportDecorator {
    protected WeatherData weatherData;

    public WeatherReportDecorator(WeatherData weatherData) {
        this.weatherData = weatherData;
    }

    public abstract void display();
}

class CelsiusTemperatureDecorator extends WeatherReportDecorator {
    public CelsiusTemperatureDecorator(WeatherData weatherData) {
        super(weatherData);
    }

    @Override
    public void display() {
        System.out.println("Температура: " + weatherData.getTemperature() + "°C");
    }
}

// Command Pattern: WeatherAlertCommand
class WeatherAlertCommand {
    private WeatherData weatherData;
    private double temperatureThreshold;

    public WeatherAlertCommand(WeatherData weatherData, double temperatureThreshold) {
        this.weatherData = weatherData;
        this.temperatureThreshold = temperatureThreshold;
    }

    public void execute() {
        if (weatherData.getTemperature() < temperatureThreshold) {
            System.out.println("Команда: Температура ниже " + temperatureThreshold + "°C. Предупреждение!");
        }
    }
}

// State Pattern: WeatherConditionState
interface WeatherConditionState {
    void display();
}

class SunnyState implements WeatherConditionState {
    public void display() {
        System.out.println("Погода: Солнечно");
    }
}

class RainyState implements WeatherConditionState {
    public void display() {
        System.out.println("Погода: Дождливо");
    }
}

// Context for State Pattern: WeatherData
class WeatherData extends Observable {
    private double temperature;  // Градусы Цельсия
    private double humidity;  // Проценты
    private double pressure;  // гПа (гектопаскали)
    private WeatherConditionState conditionState;

    public void setMeasurements(double temperature, double humidity, double pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;

        // Обновление состояния погоды
        if (temperature > 20.0) {
            conditionState = new SunnyState();
        } else {
            conditionState = new RainyState();
        }

        measurementsChanged();
    }

    public void measurementsChanged() {
        setChanged();
        notifyObservers();
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void displayWeatherCondition() {
        conditionState.display();
    }
}

public class WeatherApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        WeatherData weatherData = new WeatherData();

        System.out.println("Система мониторинга и оповещения о погоде");
        System.out.println("Введите 'q' для выхода.");

        while (true) {
            System.out.println("<====================Добро пожаловать====================>");
            System.out.println("Меню:");
            System.out.println("1. Получить данные о погоде");
            System.out.println("2. Установить порог температуры для оповещения");
            System.out.println("3. Сменить источник данных (API/Датчик)");
            System.out.println("4. Отобразить данные о погоде");
            System.out.println("5. Отобразить состояние погоды");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine();

            if ("q".equalsIgnoreCase(choice)) {
                break;
            } else if ("1".equals(choice)) {
                // Сбор данных о погоде
                System.out.print("Выберите источник данных (API/Датчик): ");
                String input = scanner.nextLine();
                WeatherDataCollectionStrategy dataCollectionStrategy;

                if ("API".equalsIgnoreCase(input)) {
                    dataCollectionStrategy = new WeatherAPI();
                } else if ("Датчик".equalsIgnoreCase(input)) {
                    dataCollectionStrategy = new WeatherSensor();
                } else {
                    System.out.println("Неверный выбор. Пожалуйста, введите 'API' или 'Датчик'.");
                    continue;
                }

                dataCollectionStrategy.collectData(weatherData);
            } else if ("2".equals(choice)) {
                // Установка порога температуры для оповещения
                System.out.print("Введите новый порог температуры (°C): ");
                double threshold = Double.parseDouble(scanner.nextLine());
                WeatherObserver temperatureObserver = new WeatherObserver(weatherData, threshold);
            } else if ("3".equals(choice)) {
                // Смена источника данных
                System.out.print("Выберите источник данных (API/Датчик): ");
                String input = scanner.nextLine();
                WeatherDataCollectionStrategy dataCollectionStrategy;

                if ("API".equalsIgnoreCase(input)) {
                    dataCollectionStrategy = new WeatherAPI();
                } else if ("Датчик".equalsIgnoreCase(input)) {
                    dataCollectionStrategy = new WeatherSensor();
                } else {
                    System.out.println("Неверный выбор. Пожалуйста, введите 'API' или 'Датчик'.");
                    continue;
                }

                dataCollectionStrategy.collectData(weatherData);
            } else if ("4".equals(choice)) {
                // Отображение данных о погоде
                System.out.println("Последние данные о погоде:");
                System.out.println("Температура: " + weatherData.getTemperature() + "°C");
                System.out.println("Влажность: " + weatherData.getHumidity() + "%");
                System.out.println("Давление: " + weatherData.getPressure() + " гПа");
            } else if ("5".equals(choice)) {
                // Отображение состояния погоды
                weatherData.displayWeatherCondition();
            } else {
                System.out.println("Неверный выбор. Пожалуйста, выберите правильный пункт.");
            }
        }
    }
}
