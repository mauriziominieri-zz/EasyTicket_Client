package com.amazonaws.rds.eu_central_1.cxjgpctcq5m4.dbingsoftware.appminieri.Fase1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.amazonaws.rds.eu_central_1.cxjgpctcq5m4.dbingsoftware.appminieri.InputValidation;
import com.amazonaws.rds.eu_central_1.cxjgpctcq5m4.dbingsoftware.appminieri.MainActivity;
import com.amazonaws.rds.eu_central_1.cxjgpctcq5m4.dbingsoftware.appminieri.R;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Accesso extends AppCompatActivity {

    Toast my_Toast;
    TextView v;
    View view;
    public static OkHttpClient client;


    private final AppCompatActivity activity = Accesso.this;
    private InputValidation inputValidation;
    private TextInputLayout textInputLayoutPassword;
    private TextInputEditText textInputEditTextUsername;
    private TextInputEditText textInputEditTextPassword;
    private CheckBox saveLoginCheckBox;

    final String serverRequest="http://ingsoftw.eu-central-1.elasticbeanstalk.com/login";



    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    public static String username,password;
    private Button buttonAccedi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accesso);


        buttonAccedi=(Button)findViewById(R.id.buttonAccedi);
        buttonAccedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 CheckData();
            }
        });

        saveLoginCheckBox = (CheckBox)findViewById(R.id.checkBox);
        textInputEditTextUsername =  findViewById(R.id.textInputEditTextUsername);
        textInputEditTextPassword =  findViewById(R.id.textInputEditTextPassword);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);



        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();
        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            textInputEditTextUsername.setText(loginPreferences.getString("username", ""));
            textInputEditTextPassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }

    }


    public void CheckData(){
        Log.i("Messaggi","Entrato in CheckData");
        inputValidation = new InputValidation(activity);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(  textInputEditTextUsername.getWindowToken(), 0);

        username =   textInputEditTextUsername.getText().toString();
        password =  textInputEditTextPassword.getText().toString();

        if (saveLoginCheckBox.isChecked()) {
            loginPrefsEditor.putBoolean("saveLogin", true);
            loginPrefsEditor.putString("username", username);
            loginPrefsEditor.putString("password", password);
            loginPrefsEditor.commit();
        } else {
            loginPrefsEditor.clear();
            loginPrefsEditor.commit();
        }


       if (username.isEmpty()) {
            textInputEditTextUsername.setError(getString(R.string.error_message_empty));
            return;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword, textInputLayoutPassword, getString(R.string.error_message_password))) {
            return;
        }

        //POST REQUEST

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)          //Fondamentale, mette l'& tra le informazioni, ed è necessario
                .addFormDataPart("username", username)
                .addFormDataPart("password",  password)
                .build();


       Request request = new Request.Builder().url(serverRequest).post(requestBody).build();


// init cookie manager
        CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(this));

        client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.i("messaggi","response ERRORE");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("messaggi","Login con : "+username+" & password : "+password);
                if(response.isSuccessful()) {
                     Log.i("messaggi", "response ACCETTATO : " + response.toString());
                     if(response.code()==200)
                     {
                          MainActivity.profiloEntered=true;
                          GoToMainActivity();
                     }
                }
                else {
                    Log.i("messaggi", "response FALLITO : " + response.toString());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            my_Toast = Toast.makeText(Accesso.this, "Utente non trovato", Toast.LENGTH_LONG);
                            v = (TextView) my_Toast.getView().findViewById(android.R.id.message);
                            view = my_Toast.getView();
                            view.getBackground().setColorFilter(getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
                            v.setTextColor(Color.WHITE);
                            my_Toast.show();
                        }
                    });
                }
            }
        });

    }


    public void GoToMainActivity() {
        Log.i("Messaggi","Entrato in MainActivity");
        Intent i=new Intent(Accesso.this,MainActivity.class);
        startActivity(i);
    }


    public void GoToRegistrazione(View v) {
        Log.i("Messaggi","Entrato in Registrazione");
        Intent i=new Intent(Accesso.this,Registrazione.class);
        startActivity(i);
    }

}
