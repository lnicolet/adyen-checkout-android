package adyen.com.pay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
            // checkoutRequest.setBrandColor(R.color.colorAccent); //that's deprecated now. It's going to change any color anymore.
            checkoutRequest.setReference("12131231123"); // set the reference of the checkout request
            checkoutRequest.setBackgroundColor("#F5943F"); //Can set background from Hex String
            checkoutRequest.setTitle("Unieuro"); //Used to set the title in the layout
            String imageString = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAKoAAACqCAMAAAAKqCSwAAABblBMVEX/mTP/mTT/mjX/mzb/mzf/mzj/nDn/nj3/oEH/oUP/okX/o0b/o0f/o0j/pEn/pUr/pUv/pUz/pk3/p07/p1D/qFH/qVP/qVT/qlX/q1j/rFn/rVr/rl3/r17/r1//sWP/sWT/smX/s2b/s2f/s2j/tGn/tWr/tWv/tm3/t27/t2//uHH/uXP/uXT/unX/u3b/u3f/u3j/vHn/vn3/wIH/wYP/woX/x4//yJH/yZL/yZP/yZT/ypX/y5f/zJn/zZz/z57/z5//z6D/0KH/0aL/06b/06f/1Kn/1av/1q3/167/2LH/2bL/2bP/3Ln/3bz/3r3/4cL/4sX/5Mn/5cz/5s3/587/58//59D/6NH/6dL/6dP/6dT/6tX/7Nn/7dv/7dz/7t3/797/79//7+D/8OH/8uX/8+j/9On/9+7/9+//9/D/+PH/+fL/+fP/+fT/+vX/+/b/+/f//Pn//fr//fv//fz//v3///7///8E/X0hAAACSklEQVR42u3Z/1dMQRjH8Wn7shUVfW8lLUqltohEiCJs+qKE9IU2EYUl2r3/vWPn7m32ucdvt87jnPfz23x+ep05M3eemWu8/6YMVKhQoUKFChUqVKhQoUKFChUqVKhQoUKFChUqVKhQoUKFChUqVKhQoUKFChUqVKhQof6rsrODbXX1idGFA+3UpXbjV9eqburzChNU/IUi6mZ68tHSdyd4W2Wcqt7WQt0ZKv8LapkLksMLpqSGlVB3Gouih8XoVanUVH1QQc31BKLYGz+7L6hmVgV1xRGN+NmgpN5WQb3riE5/tVlSUkdVUIcdUcWWzToldUAF9ZIjiq3Z5dshqRdVUC+71PVC9KtVUpMqqClHVPmuEP1oltR+FdQxRxT/VIj26nVuq0lH1PSzEO1WS+q4Cup8eEluV0rqMxXUzdiRaMxGr6XUrKigZs+Gzs95Ka35rKNdGTn6AGRsck9Se5R0VnOB6HzOJn2SOq2E+qW2KJqxwV6t7AEzWlrrB76oed+OF+Wk9qq5sPy2p0DLhj++KqkLiu5WL8ev30j7c+qtlQtp4kAR1a3D7mOZ1OOg3pLSK3md1PwdKY2/1/m6kukPnamPdb5ZzYRaKnMtr5I6EYKarm+eRupqWNqx66mkToWkyeik0VKfSOnNrKeU+rF0U7UvRroRot1Wy02Bs6zzadZTTPX206lEw6kz51JT67moDxd+W0CFChUqVKhQoUKFChUqVKhQoUKFChUqVKhQoUKFChUqVKhQoUKFChUqVKhQoUKFCvXE6g+SJ+YqRihWswAAAABJRU5ErkJggg==";
            imageString = imageString.substring(imageString.indexOf(',') + 1);
            checkoutRequest.setBrandLogo(imageString);
            checkoutRequest.setShowOwnerName(true);
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
        Toast.makeText(context, checkoutResponse.getPaymentData(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void checkoutFailedWithError(String errorMessage) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
    }
}
