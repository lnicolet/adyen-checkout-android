package adyen.com.adyenuisdk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;

import adyen.com.adyenpaysdk.Adyen;
import adyen.com.adyenpaysdk.exceptions.CheckoutRequestException;
import adyen.com.adyenpaysdk.exceptions.EncrypterException;
import adyen.com.adyenpaysdk.exceptions.NoPublicKeyExeption;
import adyen.com.adyenpaysdk.pojo.CardPaymentData;
import adyen.com.adyenpaysdk.pojo.CheckoutRequest;
import adyen.com.adyenpaysdk.pojo.CheckoutResponse;
import adyen.com.adyenpaysdk.util.Currency;
import adyen.com.adyenuisdk.customcomponents.AdyenEditText;
import adyen.com.adyenuisdk.listener.AdyenCheckoutListener;
import adyen.com.adyenuisdk.listener.EditTextImeBackListener;
import adyen.com.adyenuisdk.util.ColorUtil;

/**
 * Created by andrei on 11/5/15.
 * Edited by Luca Nicoletti on 10/11/16.
 */
public class PaymentActivity extends Activity {

    private static final String tag = PaymentActivity.class.getSimpleName();

    private TextView mPaymentAmount;
    private TextView mApplicationTitle;
    private TextView mPaymentReference;
    private RelativeLayout mPayButton;
    private LinearLayout mPaymentForm;
    private RelativeLayout mMerchantLogo;
    private RelativeLayout mOwnerNameContainer;
    private LinearLayout mMainLayout;
    private ImageView mMerchantLogoImage;

    private AdyenEditText mCreditCardNo;
    private AdyenEditText mCreditCardExpDate;
    private AdyenEditText mCreditCardOwnerName;
    private AdyenEditText mCreditCardCvc;

    ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;
    InputMethodManager inputMethodManager;

    private static AdyenCheckoutListener adyenCheckoutListener;

    private Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.payment_form);

        extras = getIntent().getExtras();

        mPaymentAmount = (TextView)findViewById(R.id.credit_card_pay);
        mPayButton = (RelativeLayout)findViewById(R.id.pay_button);
        mPaymentForm = (LinearLayout)findViewById(R.id.payment_form_layout);
        mMerchantLogo = (RelativeLayout)findViewById(R.id.application_layout);
        mOwnerNameContainer = (RelativeLayout)findViewById(R.id.credit_card_owner_container);

        mMainLayout = (LinearLayout)findViewById(R.id.main_layout);
        mPaymentReference = (TextView)findViewById(R.id.payment_reference_number);

        mApplicationTitle = (TextView)findViewById(R.id.application_title);
        if (extras.containsKey("applicationTitle")){
            mApplicationTitle.setText(extras.getString("applicationTitle"));
        }

        mCreditCardNo = (AdyenEditText)findViewById(R.id.credit_card_no);
        mCreditCardOwnerName = (AdyenEditText)findViewById(R.id.credit_card_owner);
        mCreditCardExpDate = (AdyenEditText)findViewById(R.id.credit_card_exp_date);
        mCreditCardCvc = (AdyenEditText)findViewById(R.id.credit_card_cvc);

        if (extras.containsKey("bgColor")){
            mMerchantLogo.setBackgroundColor(extras.getInt("bgColor"));
        }

        if (extras.containsKey("statusbarColor")){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(extras.getInt("statusbarColor")); //To make it a little darker
            }
        }

        if ( extras.containsKey("referenceNumber") )
            mPaymentReference.setText("Ref: " + extras.getString("referenceNumber")); //TODO fix this
        else
            mPaymentReference.setVisibility(View.GONE);

        if (extras.containsKey("showOwnerName"))
            mCreditCardOwnerName.requestFocus();
        else
            mOwnerNameContainer.setVisibility(View.GONE);

        mMerchantLogoImage = (ImageView)findViewById(R.id.application_logo);
        mMerchantLogoImage.setImageResource(extras.getInt("logo"));

        showInputKeyboard();
        initPaymentButtonText();
        initPaymentButton();
        initAdyenEditTextListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    private void showInputKeyboard() {
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        calculateKeyboardHeight();
    }

    public void calculateKeyboardHeight() {
        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect layoutRectangle = new Rect();
                mMainLayout.getWindowVisibleDisplayFrame(layoutRectangle);

                int screenHeight = mMainLayout.getRootView().getHeight();
                int heightDifference = screenHeight - (layoutRectangle.bottom - layoutRectangle.top);

                if(heightDifference > 500) {
                    Log.i(tag, "Logo height: " + (layoutRectangle.bottom - mPaymentForm.getHeight()));
                    setLogoLayoutHeight(layoutRectangle.bottom - mPaymentForm.getHeight());
                }

            }
        };

        mPaymentForm.getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    private void setLogoLayoutHeight(int logoHeight) {
        ViewGroup.LayoutParams layoutParams = mMerchantLogo.getLayoutParams();
        layoutParams.height = logoHeight;
        mMerchantLogo.setLayoutParams(layoutParams);
        mPaymentForm.getViewTreeObserver().removeOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    public static class PaymentActivityBuilder {
        Bundle arguments;
        CheckoutRequest checkoutRequest;

        public PaymentActivityBuilder(CheckoutRequest request) throws CheckoutRequestException {
            arguments = new Bundle();
            checkoutRequest = request;
            initPaymentFragment();
        }

        private void initPaymentFragment() throws CheckoutRequestException {
            if(checkoutRequest.getBrandLogo() != 0) {
                arguments.putInt("logo", checkoutRequest.getBrandLogo());
            } else {
                throw new CheckoutRequestException("Brand logo is not set! Please set the brand logo.");
            }

            if(checkoutRequest.getCheckoutAmount() > 0) {
                arguments.putFloat("amount", checkoutRequest.getCheckoutAmount());
            } else {
                throw new CheckoutRequestException("Amount is not set! Please set the amount.");
            }

            if(checkoutRequest.getCurrency() != null && !TextUtils.isEmpty(checkoutRequest.getCurrency().toString())) {
                arguments.putString("currency", checkoutRequest.getCurrency().toString());
            } else {
                throw new CheckoutRequestException("Currency is not set! Please set the currency.");
            }

            if(!TextUtils.isEmpty(checkoutRequest.getToken())) {
                arguments.putString("token", checkoutRequest.getToken());
            } else {
                throw new CheckoutRequestException("Token is not set! Please set the token.");
            }

            if (checkoutRequest.getTitle() != null ){
                arguments.putString("applicationTitle", checkoutRequest.getTitle());
            }

            //To follow Android guidelines, main color & status bar color have to be "the same" with the
            //statusbar color a little darker. That's why I'm doing this. I removed the Brand color. Not necessary anymore.
            if (checkoutRequest.getBackgroundColor() != null){
                try {
                    arguments.putInt("bgColor", Color.parseColor(checkoutRequest.getBackgroundColor()));
                    arguments.putInt("statusbarColor", getDarkerColor(checkoutRequest.getBackgroundColor()));

                } catch (IllegalArgumentException e){
                    throw new CheckoutRequestException("Unknown color, see Color.parseColor(String)");
                }
            } else {
                throw new CheckoutRequestException("Brand color is not set! Please set the brand color.");
            }

            if ( checkoutRequest.getReference() != null ){
                arguments.putString("referenceNumber", checkoutRequest.getReference());
            }

            if ( checkoutRequest.getShowOwnerName() )
                arguments.putBoolean( "showOwnerName", checkoutRequest.getShowOwnerName() );

            arguments.putBoolean("useTestBackend", checkoutRequest.isTestBackend());
        }

        public Intent build(AdyenCheckoutListener listener, Context context) {
            adyenCheckoutListener = listener;
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtras(arguments);
            return intent;
        }
    }

    public void initPaymentButtonText() {
        String currencyCode = extras.getString("currency");
        String currencySign = getCurrencySign(currencyCode);
        if(currencyCode.equals(Currency.USD.toString())) {
            mPaymentAmount.setText(mPaymentAmount.getText()
                    + " " + currencySign + " " + String.valueOf(String.format("%.02f", extras.getFloat("amount"))));
        } else {
            mPaymentAmount.setText(mPaymentAmount.getText()
                    + " " + String.valueOf(String.format("%.02f", extras.getFloat("amount"))) + " " + currencySign);
        }
    }

    private String getCurrencySign(String currencyCode) {
        return Currency.valueOf(currencyCode).getCurrencySign();
    }

    public void initPaymentButton() {
        mPayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CreditCardForm.isValid()) {
                    final CardPaymentData cardPaymentData = buildCardData();
                    Adyen.getInstance().setToken(extras.getString("token"));
                    Adyen.getInstance().setUseTestBackend(extras.getBoolean("useTestBackend"));
                    Adyen.getInstance().fetchPublicKey(new Adyen.CompletionCallback() {
                        @Override
                        public void onSuccess(String result) {
                            try {
                                CheckoutResponse checkoutResponse = new CheckoutResponse();
                                checkoutResponse.setPaymentData(cardPaymentData.serialize());
                                checkoutResponse.setAmount(extras.getFloat("amount"));
                                checkoutResponse.setCurrency(Currency.valueOf(extras.getString("currency")));
                                adyenCheckoutListener.checkoutAuthorizedPayment(checkoutResponse);
                            } catch (EncrypterException e) {
                                e.printStackTrace();
                            } catch (NoPublicKeyExeption noPublicKeyExeption) {
                                noPublicKeyExeption.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            adyenCheckoutListener.checkoutFailedWithError(error);
                        }
                    });
                }
            }
        });
    }

    private CardPaymentData buildCardData() {
        CardPaymentData cardPaymentData = new CardPaymentData();

        cardPaymentData.setCardHolderName("test");
        cardPaymentData.setCvc(mCreditCardCvc.getText().toString());
        cardPaymentData.setExpiryMonth(mCreditCardExpDate.getText().toString().split("/")[0]);
        cardPaymentData.setExpiryYear("20" + mCreditCardExpDate.getText().toString().split("/")[1]);
        cardPaymentData.setGenerationTime(new Date());
        cardPaymentData.setNumber(mCreditCardNo.getText().toString());

        return cardPaymentData;
    }

    private void initAdyenEditTextListeners() {
        mCreditCardNo.setOnEditTextImeBackListener(new EditTextImeBackListener() {
            @Override
            public void onImeBack(AdyenEditText ctrl, String text) {
                finish();
            }
        });

        mCreditCardExpDate.setOnEditTextImeBackListener(new EditTextImeBackListener() {
            @Override
            public void onImeBack(AdyenEditText ctrl, String text) {
                finish();
            }
        });

        mCreditCardCvc.setOnEditTextImeBackListener(new EditTextImeBackListener() {
            @Override
            public void onImeBack(AdyenEditText ctrl, String text) {
                finish();
            }
        });
    }

    /*
     * This is used to let the dev know if the user quit the form by pressing back button.
     * There wasn't any callback, so I added it.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            adyenCheckoutListener.checkoutFailedWithError("User pressed back button.");


        }
        return super.onKeyUp(keyCode, event);
    }

    /*
     * Function to make a color a little bit darker.
     * I use this to darker the color passed by the CheckoutRequest and set it as the
     * StatusBar color.
     */
    public static int getDarkerColor(String toDarker){
        float[] hsv = new float[3];
        int color = Color.parseColor(toDarker);
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        color = Color.HSVToColor(hsv);
        return color;
    }

    /*
     * Used for unit testing
     */
    public LinearLayout getmPaymentForm() {
        return mPaymentForm;
    }
}
