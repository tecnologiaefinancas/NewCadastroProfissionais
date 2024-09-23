package com.tecnologiaefinancas.newcadastroprofissionais;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.tecnologiaefinancas.newcadastroprofissionais.model.Doctor;
import com.tecnologiaefinancas.newcadastroprofissionais.model.PaymentType;
import com.tecnologiaefinancas.newcadastroprofissionais.persistence.DoctorDatabase;
import com.tecnologiaefinancas.newcadastroprofissionais.utils.UtilsGUI;

public class DoctorActivity extends AppCompatActivity {

    public static final String MODO = "MODO";

    public static final String ID = "ID";

    public static final int NOVO = 1;

    public static final int EDITAR = 2;

    private EditText editTextNome;

    private Spinner spinnerTipo;

    private CheckBox checkBoxBolsista;

    private RadioGroup radioGroupMaoUsada;

    private int modo;

    private Doctor originalDoctor;

    public static final String SUGERIR_TIPO = "SUGERIR_TPO";

    public static final String ULTIMO_TIPO = "ULTIMO_TIPO";

    private boolean sugerirTipo = false;

    private int ultimoTipo = 0;

    public static void newDoctor(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher){

        Intent intent = new Intent(activity, DoctorActivity.class);

        intent.putExtra(MODO, NOVO);

        launcher.launch(intent);
    }

    public static void doctorEdit(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher, Doctor pessoa){

        Intent intent = new Intent(activity, DoctorActivity.class);

        intent.putExtra(MODO, EDITAR);
        intent.putExtra(ID, pessoa.getId());

        launcher.launch(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextNome       = findViewById(R.id.editTextTextNome);
        spinnerTipo        = findViewById(R.id.spinnerTipo);
        checkBoxBolsista   = findViewById(R.id.checkBoxBolsista);
        radioGroupMaoUsada = findViewById(R.id.radioGroupMaoUsada);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        lerSugerirTipo();
        lerUltimoTipo();

        if (bundle != null) {

            modo = bundle.getInt(MODO, NOVO);

            if (modo == NOVO) {
                setTitle(getString(R.string.new_professional));

                if (sugerirTipo){
                    spinnerTipo.setSelection(ultimoTipo);
                }

            } else if (modo == EDITAR) {
                setTitle(getString(R.string.edit_professional));

                long id = bundle.getLong(ID);

                DoctorDatabase database = DoctorDatabase.getDatabase(this);

                originalDoctor = database.getDoctorDao().queryForId(id);

                editTextNome.setText(originalDoctor.getNome());
                editTextNome.setSelection(editTextNome.getText().length());

                spinnerTipo.setSelection(originalDoctor.getTipo());

                checkBoxBolsista.setChecked(originalDoctor.isIndicado());

                PaymentType originalPaymentType = originalDoctor.getPaymentType();

                RadioButton button = null;

                if (originalPaymentType == PaymentType.PIX){
                    button = findViewById(R.id.radioButtonDireita);
                }else
                    if (originalPaymentType == PaymentType.Boleto){
                        button = findViewById(R.id.radioButtonEsquerda);
                    }else
                        if (originalPaymentType == PaymentType.Cartao){
                            button = findViewById(R.id.radioButtonAmbas);
                        }

                if (button != null){
                    button.setChecked(true);
                }
            }
        }
    }

    public void salvar(){

        String nome = editTextNome.getText().toString();

        if (nome == null || nome.trim().isEmpty()){
            UtilsGUI.alert(this, R.string.name_cant_be_empty);
            editTextNome.requestFocus();
            return;
        }

        int tipo = spinnerTipo.getSelectedItemPosition();
        if (tipo < 0){
            UtilsGUI.alert(this, R.string.type_cant_be_empty);
            return;
        }

        boolean bolsista = checkBoxBolsista.isChecked();

        int radioButtonId = radioGroupMaoUsada.getCheckedRadioButtonId();

        PaymentType paymentType;

        if (radioButtonId == R.id.radioButtonDireita){
            paymentType = PaymentType.PIX;
        }else
            if (radioButtonId == R.id.radioButtonEsquerda){
                paymentType = PaymentType.Boleto;
            }else
                if (radioButtonId == R.id.radioButtonAmbas){
                    paymentType = PaymentType.Cartao;
                }else{
                    UtilsGUI.alert(this, R.string.payment_type_cant_be_empty);
                    return;
                }

        if (modo == EDITAR &&
            nome.equals(originalDoctor.getNome())    &&
            tipo     == originalDoctor.getTipo()     &&
            paymentType == originalDoctor.getPaymentType() &&
            bolsista == originalDoctor.isIndicado()){
            cancelar();
            return;
        }

        salvarUltimoTipo(tipo);

        Intent intent = new Intent();

        DoctorDatabase database = DoctorDatabase.getDatabase(this);

        if (modo == NOVO){

            Doctor pessoa = new Doctor(nome, tipo, bolsista, paymentType);

            long novoId = database.getDoctorDao().insert(pessoa);

            if (novoId <= 0){
                UtilsGUI.alert(this, R.string.erro_ao_tentar_inserir);
                return;
            }

            pessoa.setId(novoId);

            intent.putExtra(ID, pessoa.getId());

        }else{

            Doctor pessoaAlterada = new Doctor(nome, tipo, bolsista, paymentType);

            pessoaAlterada.setId(originalDoctor.getId());

            int quantidadeAlterada = database.getDoctorDao().update(pessoaAlterada);

            if (quantidadeAlterada == 0){
                UtilsGUI.alert(this, R.string.erro_ao_tentar_alterar);
                return;
            }

            intent.putExtra(ID, pessoaAlterada.getId());
        }

        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void limpar(){
        editTextNome.setText(null);

        spinnerTipo.setSelection(0);

        checkBoxBolsista.setChecked(false);

        radioGroupMaoUsada.clearCheck();

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
        getMenuInflater().inflate(R.menu.doctor_options, menu);
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
                        spinnerTipo.setSelection(ultimoTipo);
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

        ultimoTipo = shared.getInt(ULTIMO_TIPO, ultimoTipo);
    }

    private void salvarUltimoTipo(int novoValor){

        SharedPreferences shared = getSharedPreferences(MainActivity.ARQUIVO, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putInt(ULTIMO_TIPO, novoValor);

        editor.commit();

        ultimoTipo = novoValor;
    }
}