package adyen.com.adyenpaysdk.services;

import android.content.Context;

/**
 * Created by andrei on 11/10/15.
 */
public interface PaymentService {

    void fetchPublicKey(String hppUrl, final PaymentServiceImpl.VolleyCallback callback, Context context);

}
