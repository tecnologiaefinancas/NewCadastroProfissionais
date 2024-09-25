package com.tecnologiaefinancas.newcadastroprofissionais;

import static android.text.TextUtils.isEmpty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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

    public static final String MODO = "MODO";

    public static final String ID = "ID";

    public static final int NEWPROFESSIONAL = 1;

    public static final int EDIT = 2;

    private TextView textViewCreateProfessional;

    private EditText editTextName;

    private Spinner spinnerType;

    private CheckBox checkBoxReferredProfessional;

    private RadioGroup radioGroupTypeOfPayment;

    private TextView textViewComments;

    private EditText editTextMultiLineComments;

    private Spinner spinnerStatus;

    private int modo;

    private Professional professionalAddedToEdit;

    public static final String SUGERIR_TIPO = "SUGERIR_TPO";

    public static final String LAST_TYPE = "ULTIMO_TIPO";

    private boolean suggestType = false;

    private int lastType = 0;

    public static void newDoctor(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher){

        Intent intent = new Intent(activity, ProfessionalActivity.class);

        intent.putExtra(MODO, NEWPROFESSIONAL);

        launcher.launch(intent);
    }

    public static void professionalEdit(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher, Professional pessoa){

        Intent intent = new Intent(activity, ProfessionalActivity.class);

        intent.putExtra(MODO, EDIT);
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

            modo = bundle.getInt(MODO, NEWPROFESSIONAL);

            if (modo == NEWPROFESSIONAL) {
                setTitle(getString(R.string.new_professional));

                if (suggestType){
                    spinnerType.setSelection(lastType);
                }

            } else if (modo == EDIT) {
                setTitle(getString(R.string.edit_professional));

                long id = bundle.getLong(ID);

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

        if (modo == EDIT &&
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

        if (modo == NEWPROFESSIONAL){

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

        MenuItem item = menu.findItem(R.id.menuItemSugerirTipo);

        item.setChecked(suggestType);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemSalvar){
            save();
            return true;
        }else
            if (idMenuItem == R.id.menuItemLimpar){
                clear();
                return true;
            }else
                if (idMenuItem == R.id.menuItemSugerirTipo){

                    boolean valor = !item.isChecked();

                    saveSuggestedType(valor);
                    item.setChecked(valor);

                    if (suggestType){
                        spinnerType.setSelection(lastType);
                    }

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

        suggestType = shared.getBoolean(SUGERIR_TIPO, suggestType);
    }

    private void saveSuggestedType(boolean newValue){

        SharedPreferences shared = getSharedPreferences(MainActivity.FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean(SUGERIR_TIPO, newValue);

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