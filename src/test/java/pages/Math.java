package pages;

public class Math {

    public double convertCurrencyByRate(double money, double rate) {
        double value = money / rate;
        return value;
    }

    public double roundToTwoDecimals(double number) {
        double roundedValue = java.lang.Math.round(number * 100.0) / 100.0;
        return roundedValue;
    }
}
