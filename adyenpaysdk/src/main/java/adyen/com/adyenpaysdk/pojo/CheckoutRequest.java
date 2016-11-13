package adyen.com.adyenpaysdk.pojo;

import adyen.com.adyenpaysdk.util.Currency;

/**
 * Created by andrei on 12/9/15.
 * Edited by Luca Nicoletti on 10/11/16.
 */
public class CheckoutRequest {

    private float checkoutAmount;
    private Currency currency;
    private int brandColor;
    private int brandLogo;
    private String token;
    private boolean testBackend;
    private boolean showOwnerName;
    private String title;
    private String bgColor;
    private String reference;


    public float getCheckoutAmount() {
        return checkoutAmount;
    }

    public void setCheckoutAmount(float checkoutAmount) {
        this.checkoutAmount = checkoutAmount;
    }

    public void setCheckoutAmount(Double checkoutAmount){
        this.checkoutAmount = checkoutAmount.floatValue();
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Deprecated
    public int getBrandColor() {
        return brandColor;
    }

    @Deprecated
    public void setBrandColor(int brandColor) {
        this.brandColor = brandColor;
    }

    public void setBackgroundColor(String hexString) {
        this.bgColor = hexString;
    }

    public String getBackgroundColor(){ return this.bgColor; }

    public int getBrandLogo() {
        return brandLogo;
    }

    public void setBrandLogo(int brandLogo) {
        this.brandLogo = brandLogo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isTestBackend() {
        return testBackend;
    }

    public void setTestBackend(boolean testBackend) {
        this.testBackend = testBackend;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return this.title;
    }

    public void setReference(String ref){ this.reference = ref; }

    public String getReference() { return this.reference; }

    public void setShowOwnerName(boolean toShow) { this.showOwnerName = toShow; }

    public boolean getShowOwnerName() { return this.showOwnerName; }
}
