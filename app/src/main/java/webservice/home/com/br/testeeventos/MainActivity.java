package webservice.home.com.br.testeeventos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private TextView textView;
    private TextView textView2;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        textView = (TextView) findViewById(R.id.info);
        textView2 = (TextView) findViewById(R.id.textView2);

        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                accessToken = AccessToken.getCurrentAccessToken();
                textView.setText(loginResult.getAccessToken().getUserId());

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                                textView.setText(graphResponse.toString());
                            }
                        }
                );
                Bundle paramenters = new Bundle();
                paramenters.putString("fields","id, name, locale, birthday");
                paramenters.putString("edges","events");
                request.setParameters(paramenters);
                request.executeAsync();


                new GraphRequest(AccessToken.getCurrentAccessToken(),
                        "/" + loginResult.getAccessToken().getUserId() + "/events",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            @Override
                            public void onCompleted(GraphResponse graphResponse) {
                                textView2.setText(graphResponse.toString());
                            }
                        }).executeAsync();

            }

            @Override
            public void onCancel() {
                // App code
                textView.setText("cancelado");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                textView.setText(exception.toString());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
