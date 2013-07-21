package pl.bnsit.aa.part3.post;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import pl.bnsit.aa.R;

/**
 * Created with IntelliJ IDEA.
 * User: made
 * Date: 7/14/13
 * Time: 9:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuotesActivity extends Activity implements View.OnClickListener {

    private View buttonPicture;
    private View buttonCreateUser;
    private View buttonLogin;
    private EditText firstName;
    private EditText lastName;
    private EditText password;
    private EditText userName;
    private QuotesService quotesService = new QuotesService();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quotes);

        buttonCreateUser = findViewById(R.id.buttonCreateUser);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonPicture = findViewById(R.id.buttonPicture);

        firstName = (EditText) findViewById(R.id.editTextFirstName);
        lastName = (EditText)findViewById(R.id.editTextLastName);
        password = (EditText)findViewById(R.id.editTextPassword);
        userName = (EditText)findViewById(R.id.editTextUserName);


        addOnClickListener(this, buttonLogin, buttonCreateUser, buttonPicture);
    }

    private void addOnClickListener(View.OnClickListener listener, View... views) {
        if(views.length > 0)
            for(View view: views) {
                view.setOnClickListener(listener);
            }
    }

    @Override
    public void onClick(View view) {
        if(view.equals(buttonLogin)) {

        } else if (view.equals(buttonCreateUser)) {
            new Thread() {
                @Override
                public void run() {
                    quotesService.createUserHttpUrlConnection(userName.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), password.getText().toString());
                }
            }.start();

        } else if (view.equals(buttonLogin)) {

        }
    }
}