package com.tecnologiaefinancas.newcadastroprofissionais;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tecnologiaefinancas.newcadastroprofissionais.model.Professional;
import com.tecnologiaefinancas.newcadastroprofissionais.model.PaymentType;
import com.tecnologiaefinancas.newcadastroprofissionais.persistence.ProfessionalDatabase;
import com.tecnologiaefinancas.newcadastroprofissionais.utils.get.GetStatusDescription;
import com.tecnologiaefinancas.newcadastroprofissionais.utils.Dialogs;

public class ProfessionalActivity extends AppCompatActivity {

    public static final String MODE = "MODE";

    public static final String ID = "ID";

    public static final int NEW_PROFESSIONAL = 1;

    public static final int EDIT = 2;

    private TextView textViewCreateProfessional;

    private EditText editTextName;

    private Spinner spinnerType;

    private CheckBox checkBoxReferredProfessional;

    private RadioGroup radioGroupTypeOfPayment;

    private TextView textViewComments;

    private EditText editTextMultiLineComments;

    private Spinner spinnerStatus;

    private int mode;

    private Professional professionalAddedToEdit;

    public static final String SUGGEST_TYPE = "SUGGEST_TYPE";

    public static final String LAST_TYPE = "LAST_TYPE";

    private boolean suggestType = false;

    private int lastType = 0;

    public static void newProfessional(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher){

        Intent intent = new Intent(activity, ProfessionalActivity.class);

        intent.putExtra(MODE, NEW_PROFESSIONAL);

        launcher.launch(intent);
    }

    public static void professionalEdit(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher, Professional pessoa){

        Intent intent = new Intent(activity, ProfessionalActivity.class);

        intent.putExtra(MODE, EDIT);
        intent.putExtra(ID, pessoa.getId());

        launcher.launch(intent);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        textViewCreateProfessional = findViewById(R.id.textViewCreateProfessional);
        editTextName = findViewById(R.id.editTextNameProfessional);
        spinnerType = findViewById(R.id.spinnerTipo);
        checkBoxReferredProfessional = findViewById(R.id.checkBoxReferredProfessional);
        radioGroupTypeOfPayment = findViewById(R.id.radioGroupTypeOfPayment);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        textViewComments = findViewById(R.id.textViewComments);
        editTextMultiLineComments = findViewById(R.id.editTextMultiLineComments);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        readSuggestedType();
        readLastType();

        //Criacao de adapter para uso no Spinner
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GetStatusDescription.getStatusDescriptions(this));
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapterStatus);

        if (bundle != null) {

            mode = bundle.getInt(MODE, NEW_PROFESSIONAL);

            if (mode == NEW_PROFESSIONAL) {
                setTitle(getString(R.string.new_professional));

                if (suggestType){
                    spinnerType.setSelection(lastType);
                }

            } else if (mode == EDIT) {
                long id = bundle.getLong(ID);
                editProfessional(id);
            }
        }
    }

    private void editProfessional(long id) {
        setTitle(getString(R.string.edit_professional));

        ProfessionalDatabase database = ProfessionalDatabase.getDatabase(this);

        professionalAddedToEdit = database.getProfessionalDao().queryForId(id);

        editTextName.setText(professionalAddedToEdit.getName());
        editTextName.setSelection(editTextName.getText().length());

        spinnerType.setSelection(professionalAddedToEdit.getTipo());
        spinnerStatus.setSelection(0);

        checkBoxReferredProfessional.setChecked(professionalAddedToEdit.isReferred());

        PaymentType originalPaymentType = professionalAddedToEdit.getPaymentType();

        editTextMultiLineComments.setText(professionalAddedToEdit.getComments());

        RadioButton button = null;

        if (originalPaymentType == PaymentType.PIX){
            button = findViewById(R.id.radioButtonPix);
        }else
        if (originalPaymentType == PaymentType.Boleto){
            button = findViewById(R.id.radioButtonBill);
        }else
        if (originalPaymentType == PaymentType.Cartao){
            button = findViewById(R.id.radioButtonCreditCard);
        }

        if (button != null){
            button.setChecked(true);
        }
    }

    public void save(){

        String name = editTextName.getText().toString();
        String comments = "";



        if (name == null || name.trim().isEmpty()){
            Dialogs.alert(this, R.string.name_cant_be_empty);
            editTextName.requestFocus();
            return;
        }

        if (editTextMultiLineComments != null){
            comments = editTextMultiLineComments.getText().toString();
        }

        int tipo = spinnerType.getSelectedItemPosition();
        if (tipo < 0){
            Dialogs.alert(this, R.string.type_cant_be_empty);
            return;
        }

        boolean isReferred = checkBoxReferredProfessional.isChecked();

        int radioButtonId = radioGroupTypeOfPayment.getCheckedRadioButtonId();

        PaymentType paymentType;

        if (radioButtonId == R.id.radioButtonPix){
            paymentType = PaymentType.PIX;
        }else
            if (radioButtonId == R.id.radioButtonBill){
                paymentType = PaymentType.Boleto;
            }else
                if (radioButtonId == R.id.radioButtonCreditCard){
                    paymentType = PaymentType.Cartao;
                }else{
                    Dialogs.alert(this, R.string.payment_type_cant_be_empty);
                    return;
                }

        if (mode == EDIT &&
            name.equals(professionalAddedToEdit.getName())    &&
            tipo     == professionalAddedToEdit.getTipo()     &&
            paymentType == professionalAddedToEdit.getPaymentType() &&
            isReferred == professionalAddedToEdit.isReferred() &&
            comments == professionalAddedToEdit.getComments()){
            cancel();
            return;
        }

        saveLastType(tipo);

        Intent intent = new Intent();

        ProfessionalDatabase database = ProfessionalDatabase.getDatabase(this);

        if (mode == NEW_PROFESSIONAL){

            Professional professional = new Professional(name, tipo, isReferred, paymentType, comments);

            long newID = database.getProfessionalDao().insert(professional);

            if (newID <= 0){
                Dialogs.alert(this, R.string.error_not_inserted);
                return;
            }

            professional.setId(newID);

            intent.putExtra(ID, professional.getId());

        }else{

            Professional professionalEdited = new Professional(name, tipo, isReferred, paymentType, comments);

            professionalEdited.setId(professionalAddedToEdit.getId());

            int quantityEdited = database.getProfessionalDao().update(professionalEdited);

            if (quantityEdited == 0){
                Dialogs.alert(this, R.string.error_trying_edit);
                return;
            }

            intent.putExtra(ID, professionalEdited.getId());
        }

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void clear(){
        editTextName.setText(null);

        spinnerStatus.setSelection(0);

        spinnerType.setSelection(0);

        checkBoxReferredProfessional.setChecked(false);

        radioGroupTypeOfPayment.clearCheck();

        editTextMultiLineComments.setText(" ");

        Toast.makeText(this,
                       R.string.entires_were_deleted,
                       Toast.LENGTH_SHORT).show();
    }

    public void cancel(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.professional_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem item = menu.findItem(R.id.menuItemLastAdded);

        item.setChecked(suggestType);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemSave){
            save();
            return true;
        }else
            if (idMenuItem == R.id.menuItemClear){
                clear();
                return true;
            }else
                if (idMenuItem == R.id.menuItemLastAdded){

                    System.out.println("A concluir");


                    return true;
                }else
                    if (idMenuItem == android.R.id.home){
                        cancel();
                        return true;
                    }else{
                        return super.onOptionsItemSelected(item);
                    }
    }

    private void readSuggestedType(){

        SharedPreferences shared = getSharedPreferences(MainActivity.FILE, Context.MODE_PRIVATE);

        suggestType = shared.getBoolean(SUGGEST_TYPE, suggestType);
    }

    private void saveSuggestedType(boolean newValue){

        SharedPreferences shared = getSharedPreferences(MainActivity.FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean(SUGGEST_TYPE, newValue);

        editor.commit();

        suggestType = newValue;
    }

    private void readLastType(){

        SharedPreferences shared = getSharedPreferences(MainActivity.FILE, Context.MODE_PRIVATE);

        lastType = shared.getInt(LAST_TYPE, lastType);
    }

    private void saveLastType(int newValue){

        SharedPreferences shared = getSharedPreferences(MainActivity.FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putInt(LAST_TYPE, newValue);

        editor.commit();

        lastType = newValue;
    }

}