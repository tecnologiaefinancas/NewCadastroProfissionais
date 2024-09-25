package com.tecnologiaefinancas.newcadastroprofissionais;

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

    public static final int NOVO = 1;

    public static final int EDITAR = 2;

    private TextView textViewCreateProfessional;

    private EditText editTextName;

    private Spinner spinnerTipo;

    private CheckBox checkBoxReferredProfessional;

    private RadioGroup radioGroupTypeOfPayment;

    private TextView textViewComments;

    private EditText editTextMultiLineComments;

    private Spinner spinnerStatus;

    private int modo;

    private Professional originalProfessional;

    public static final String SUGERIR_TIPO = "SUGERIR_TPO";

    public static final String ULTIMO_TIPO = "ULTIMO_TIPO";

    private boolean sugerirTipo = false;

    private int lastType = 0;

    public static void newDoctor(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher){

        Intent intent = new Intent(activity, ProfessionalActivity.class);

        intent.putExtra(MODO, NOVO);

        launcher.launch(intent);
    }

    public static void professionalEdit(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher, Professional pessoa){

        Intent intent = new Intent(activity, ProfessionalActivity.class);

        intent.putExtra(MODO, EDITAR);
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
        editTextName = findViewById(R.id.editTextName);
        spinnerTipo        = findViewById(R.id.spinnerTipo);
        checkBoxReferredProfessional = findViewById(R.id.checkBoxReferredProfessional);
        radioGroupTypeOfPayment = findViewById(R.id.radioGroupTypeOfPayment);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        textViewComments = findViewById(R.id.textViewComments);
        editTextMultiLineComments = findViewById(R.id.editTextMultiLineComments);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        lerSugerirTipo();
        lerUltimoTipo();

        //Criacao de adapter para uso no Spinner
        ArrayAdapter<String> adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GetStatusDescription.getStatusDescriptions());
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapterStatus);

        if (bundle != null) {

            modo = bundle.getInt(MODO, NOVO);

            if (modo == NOVO) {
                setTitle(getString(R.string.new_professional));

                if (sugerirTipo){
                    spinnerTipo.setSelection(lastType);
                }

            } else if (modo == EDITAR) {
                setTitle(getString(R.string.edit_professional));

                long id = bundle.getLong(ID);

                ProfessionalDatabase database = ProfessionalDatabase.getDatabase(this);

                originalProfessional = database.getProfessionalDao().queryForId(id);

                editTextName.setText(originalProfessional.getNome());
                editTextName.setSelection(editTextName.getText().length());

                spinnerTipo.setSelection(originalProfessional.getTipo());

                checkBoxReferredProfessional.setChecked(originalProfessional.isIndicado());

                PaymentType originalPaymentType = originalProfessional.getPaymentType();

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

    public void salvar(){

        String nome = editTextName.getText().toString();

        if (nome == null || nome.trim().isEmpty()){
            Dialogs.alert(this, R.string.name_cant_be_empty);
            editTextName.requestFocus();
            return;
        }

        int tipo = spinnerTipo.getSelectedItemPosition();
        if (tipo < 0){
            Dialogs.alert(this, R.string.type_cant_be_empty);
            return;
        }

        boolean bolsista = checkBoxReferredProfessional.isChecked();

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

        if (modo == EDITAR &&
            nome.equals(originalProfessional.getNome())    &&
            tipo     == originalProfessional.getTipo()     &&
            paymentType == originalProfessional.getPaymentType() &&
            bolsista == originalProfessional.isIndicado()){
            cancelar();
            return;
        }

        salvarUltimoTipo(tipo);

        Intent intent = new Intent();

        ProfessionalDatabase database = ProfessionalDatabase.getDatabase(this);

        if (modo == NOVO){

            Professional pessoa = new Professional(nome, tipo, bolsista, paymentType);

            long novoId = database.getProfessionalDao().insert(pessoa);

            if (novoId <= 0){
                Dialogs.alert(this, R.string.erro_ao_tentar_inserir);
                return;
            }

            pessoa.setId(novoId);

            intent.putExtra(ID, pessoa.getId());

        }else{

            Professional pessoaAlterada = new Professional(nome, tipo, bolsista, paymentType);

            pessoaAlterada.setId(originalProfessional.getId());

            int quantidadeAlterada = database.getProfessionalDao().update(pessoaAlterada);

            if (quantidadeAlterada == 0){
                Dialogs.alert(this, R.string.erro_ao_tentar_alterar);
                return;
            }

            intent.putExtra(ID, pessoaAlterada.getId());
        }

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void limpar(){
        editTextName.setText(null);

        spinnerTipo.setSelection(0);

        checkBoxReferredProfessional.setChecked(false);

        radioGroupTypeOfPayment.clearCheck();

        Toast.makeText(this,
                       R.string.as_entradas_foram_apagadas,
                       Toast.LENGTH_SHORT).show();
    }

    public void cancelar(){
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

        item.setChecked(sugerirTipo);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemSalvar){
            salvar();
            return true;
        }else
            if (idMenuItem == R.id.menuItemLimpar){
                limpar();
                return true;
            }else
                if (idMenuItem == R.id.menuItemSugerirTipo){

                    boolean valor = !item.isChecked();

                    salvarSugerirTipo(valor);
                    item.setChecked(valor);

                    if (sugerirTipo){
                        spinnerTipo.setSelection(lastType);
                    }

                    return true;
                }else
                    if (idMenuItem == android.R.id.home){
                        cancelar();
                        return true;
                    }else{
                        return super.onOptionsItemSelected(item);
                    }
    }

    private void lerSugerirTipo(){

        SharedPreferences shared = getSharedPreferences(MainActivity.ARQUIVO, Context.MODE_PRIVATE);

        sugerirTipo = shared.getBoolean(SUGERIR_TIPO, sugerirTipo);
    }

    private void salvarSugerirTipo(boolean novoValor){

        SharedPreferences shared = getSharedPreferences(MainActivity.ARQUIVO, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean(SUGERIR_TIPO, novoValor);

        editor.commit();

        sugerirTipo = novoValor;
    }

    private void lerUltimoTipo(){

        SharedPreferences shared = getSharedPreferences(MainActivity.ARQUIVO, Context.MODE_PRIVATE);

        lastType = shared.getInt(ULTIMO_TIPO, lastType);
    }

    private void salvarUltimoTipo(int novoValor){

        SharedPreferences shared = getSharedPreferences(MainActivity.ARQUIVO, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putInt(ULTIMO_TIPO, novoValor);

        editor.commit();

        lastType = novoValor;
    }
}