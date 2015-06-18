package informatics.uk.ac.ed.esm;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;


public class ResearcherSetup extends AppCompatActivity {

    private TextView txtEmail;
    private TextInputLayout txtEmail_inpLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_researcher_setup);

        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtEmail_inpLyt = (TextInputLayout) findViewById(R.id.txtEmail_InpLyt);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_researcher_setup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnNext_onClick(View view){
        boolean hasErrors = false;

        String emailAddress = Utils.getTrimmedTextFromTextView(this.txtEmail);
        if (emailAddress.isEmpty()) {
            txtEmail_inpLyt.setError("Enter an email address.");
            hasErrors = true;
        }

        if (!hasErrors) {
            Intent intent = new Intent(this, StudyConfiguration.class);
            startActivity(intent);
        }
    }
}
