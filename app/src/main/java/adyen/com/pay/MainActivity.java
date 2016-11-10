package adyen.com.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import adyen.com.adyenpaysdk.exceptions.CheckoutRequestException;
import adyen.com.adyenpaysdk.pojo.CheckoutRequest;
import adyen.com.adyenpaysdk.pojo.CheckoutResponse;
import adyen.com.adyenpaysdk.util.Currency;
import adyen.com.adyenuisdk.PaymentActivity;
import adyen.com.adyenuisdk.listener.AdyenCheckoutListener;

public class MainActivity extends FragmentActivity implements AdyenCheckoutListener {

    private InitPaymentFragment mInitPaymentFragment;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        mInitPaymentFragment = new InitPaymentFragment();

        initView();
    }

    private void initView() {
        getSupportFragmentManager().
                beginTransaction().
                add(R.id.fragment_container, mInitPaymentFragment).
                addToBackStack(null).
                commit();
    }

    public void initPayment(View view) {
        ConfigLoader configLoader = new ConfigLoader();
        JSONObject configuration = configLoader.loadJsonConfiguration();
        CheckoutRequest checkoutRequest = new CheckoutRequest();
        try {
            checkoutRequest.setBrandColor(R.color.colorAccent); //that's deprecated now. It's going to change any color anymore.
            checkoutRequest.setBackgroundColor("#ffa904"); //Can set background from Hex String
            checkoutRequest.setTitle("Unieuro"); //Used to set the title in the layout
            checkoutRequest.setBrandLogo(R.mipmap.nespresso_logo);
            checkoutRequest.setCheckoutAmount(10f); //Can pass a Double if needed
            checkoutRequest.setCurrency(Currency.getCurrencyByString("EUR")); //Can parse eur, EUR, usd, USD & GBP, gbp
            checkoutRequest.setToken(configuration.getString("userToken"));
            checkoutRequest.setTestBackend(true);

            Intent intent = new PaymentActivity.PaymentActivityBuilder(checkoutRequest).build(this, context);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (CheckoutRequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void checkoutAuthorizedPayment(CheckoutResponse checkoutResponse) {
        Log.i("Response: ", checkoutResponse.toString());
    }

    @Override
    public void checkoutFailedWithError(String errorMessage) {

    }
}
