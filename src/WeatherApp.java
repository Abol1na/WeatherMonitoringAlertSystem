import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

// Strategy Pattern: WeatherDataCollectionStrategy
interface WeatherDataCollectionStrategy {
    void collectData(WeatherData weatherData);
}

class WeatherAPI implements WeatherDataCollectionStrategy {
    public void collectData(WeatherData weatherData) {
        // Simulate fetching weather data from an API with random temperature
        double temperatureCelsius = Math.random() * 50 - 10;  // Random temperature between -10°C and 40°C
        double humidity = Math.random() * 50 + 50;  // Random percentage humidity
        double pressure = Math.random() * 10 + 1013;  // Random hPa pressure
        weatherData.setMeasurements(temperatureCelsius, humidity, pressure);
    }
}

class WeatherSensor implements WeatherDataCollectionStrategy {
    public void collectData(WeatherData weatherData) {
        // Simulate collecting weather data from sensors with random temperature
        double temperatureCelsius = Math.random() * 50 - 10;  // Рандомная температура от -10 до 40
        double humidity = Math.random() * 50 + 50;
        double pressure = Math.random() * 10 + 1013;
        weatherData.setMeasurements(temperatureCelsius, humidity, pressure);
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
            double temperature = weatherData.getTemperatureCelsius(); // Use getTemperatureCelsius()

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

class TemperatureDecorator extends WeatherReportDecorator {
    private String temperatureScale;

    public TemperatureDecorator(WeatherData weatherData, String temperatureScale) {
        super(weatherData);
        this.temperatureScale = temperatureScale;
    }

    @Override
    public void display() {
        String scaleLabel = "";
        double temperature = 0.0;

        if (temperatureScale.equalsIgnoreCase("Celsius")) {
            scaleLabel = "°C";
            temperature = weatherData.getTemperatureCelsius();
        } else if (temperatureScale.equalsIgnoreCase("Fahrenheit")) {
            scaleLabel = "°F";
            temperature = weatherData.getTemperatureFahrenheit();
        } else if (temperatureScale.equalsIgnoreCase("Kelvin")) {
            scaleLabel = "K";
            temperature = weatherData.getTemperatureKelvin();
        } else {
            System.out.println("Неверная шкала температуры. Используется шкала по умолчанию (Celsius).");
            scaleLabel = "°C";
            temperature = weatherData.getTemperatureCelsius();
        }

        System.out.println("Температура (" + temperatureScale + "): " + temperature + scaleLabel);
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

// Add more weather states
class CloudyState implements WeatherConditionState {
    public void display() {
        System.out.println("Погода: Облачно");
    }
}

class SnowyState implements WeatherConditionState {
    public void display() {
        System.out.println("Погода: Снег");
    }
}


// Context for State Pattern: WeatherData
class WeatherData extends Observable {
    private double temperatureCelsius;  // Celsius
    private double humidity;  // Percentage
    private double pressure;  // hPa (hectopascals)
    private WeatherConditionState conditionState;

    public void setMeasurements(double temperatureCelsius, double humidity, double pressure) {
        this.temperatureCelsius = temperatureCelsius;
        this.humidity = humidity;
        this.pressure = pressure;

        if (temperatureCelsius > 25.0) {
            conditionState = new SunnyState();
        } else if (temperatureCelsius > 15.0) {
            conditionState = new CloudyState();
        } else if (temperatureCelsius > 0.0) {
            conditionState = new RainyState();
        } else {
            conditionState = new SnowyState();
        }

        measurementsChanged();
    }

    public void measurementsChanged() {
        setChanged();
        notifyObservers();
    }

    public double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public double getTemperatureFahrenheit() {
        return (temperatureCelsius * 9/5) + 32;
    }

    public double getTemperatureKelvin() {
        return temperatureCelsius + 273.15;
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
        String temperatureScale = "Celsius"; // По умолчанию выбрана шкала Цельсия

        System.out.println("Система мониторинга и оповещения о погоде");
        System.out.println("Введите 'q' для выхода.");

        while (true) {
            System.out.println("<=======================Добро пожаловать=======================>");
            System.out.println("Меню:");
            System.out.println("1. Получить данные о погоде");
            System.out.println("2. Выбрать шкалу температуры (Celsius/Fahrenheit/Kelvin)");
            System.out.println("3. Установить порог температуры для оповещения");
            System.out.println("4. Сменить источник данных (API/Датчик)");
            System.out.println("5. Отобразить данные о погоде");
            System.out.println("6. Отобразить состояние погоды");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine();

            if ("q".equalsIgnoreCase(choice)) {
                break;
            } else if ("1".equals(choice)) {
                // Collect weather data
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
            } else if ("3".equals(choice)) {
                // Set temperature threshold for alerts
                System.out.print("Введите новый порог температуры (" + temperatureScale + "): ");
                double threshold = convertTemperature(Double.parseDouble(scanner.nextLine()), temperatureScale, "Celsius");
                WeatherObserver temperatureObserver = new WeatherObserver(weatherData, threshold);
            } else if ("4".equals(choice)) {
                // Change data source
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
            } else if ("5".equals(choice)) {
                // Display weather data
                System.out.println("<==============================================================>");
                System.out.println("Последние данные о погоде:");
                WeatherReportDecorator temperatureDecorator = new TemperatureDecorator(weatherData, temperatureScale);
                temperatureDecorator.display();
                System.out.println("Влажность: " + weatherData.getHumidity() + "%");
                System.out.println("Давление: " + weatherData.getPressure() + " гПа");
                System.out.println("<==============================================================>");
            } else if ("6".equals(choice)) {
                // Display weather condition
                weatherData.displayWeatherCondition();
            } else if ("2".equals(choice)) {
                // Choose temperature scale
                System.out.print("Выберите шкалу температуры (Celsius/Fahrenheit/Kelvin): ");
                temperatureScale = scanner.nextLine();
            } else {
                System.out.println("Неверный выбор. Пожалуйста, выберите правильный пункт.");
            }
        }
    }

    private static double convertTemperature(double value, String fromScale, String toScale) {
        if (fromScale.equals(toScale)) {
            return value;
        }
        if (fromScale.equals("Celsius")) {
            if (toScale.equals("Fahrenheit")) {
                return (value * 9/5) + 32;
            } else if (toScale.equals("Kelvin")) {
                return value + 273.15;
            }
        } else if (fromScale.equals("Fahrenheit")) {
            if (toScale.equals("Celsius")) {
                return (value - 32) * 5/9;
            } else if (toScale.equals("Kelvin")) {
                return (value + 459.67) * 5/9;
            }
        } else if (fromScale.equals("Kelvin")) {
            if (toScale.equals("Celsius")) {
                return value - 273.15;
            } else if (toScale.equals("Fahrenheit")) {
                return (value * 9/5) - 459.67;
            }
        }
        return value; // Return the value as is if no conversion is done.
    }
}