package adyen.com.adyenpaysdk.util;

/**
 * Created by andrei on 12/21/15.
 * Edited by Luca Nicoletti on 10/11/16.
 */
public enum Currency {

    USD("$"),
    EUR("€"),
    GBP("£");

    private String currencySign;

    private Currency(String sign) {
        this.currencySign = sign;
    }

    public String getCurrencySign() {
        return this.currencySign;
    }

    public static Currency getCurrencyByString(String cur){
        switch (cur){
            case "EUR":
            case "eur":
                return Currency.EUR;
            case "USD":
            case "usd":
                return Currency.USD;
            case "GBP":
            case "gbp":
                return Currency.GBP;
            default:
                //Default behaviour
                return Currency.EUR;
        }
    }
}
