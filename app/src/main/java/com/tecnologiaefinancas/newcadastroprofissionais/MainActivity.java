package com.tecnologiaefinancas.newcadastroprofissionais;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;

import java.util.Collections;
import java.util.List;

import com.tecnologiaefinancas.newcadastroprofissionais.model.Doctor;
import com.tecnologiaefinancas.newcadastroprofissionais.persistence.DoctorDatabase;
import com.tecnologiaefinancas.newcadastroprofissionais.utils.UtilsGUI;

public class MainActivity extends AppCompatActivity {

    private ListView listViewDoctors;

    private DoctorAdapter listAdapter;

    private List<Doctor> doctorsList;

    private ActionMode actionMode;

    private View viewSelected;

    private int positionSelected = -1;

    public static final String ARQUIVO = "com.tecnologiaefinancas.newcadastroprofissionais.PREFERENCIAIS";

    public static final String ORDENACAO_ASCENDENTE = "ORDENACAO_ASCENDENTE";

    private boolean ascendingOrder = true;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflate = mode.getMenuInflater();
            inflate.inflate(R.menu.main_selected_item, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            int idMenuItem = item.getItemId();

            if (idMenuItem == R.id.itemMenuEdit){
                doctorEdit();
                mode.finish();
                return true;
            }else
                if (idMenuItem == R.id.idemMenuDelete){
                    doctorDelete(mode);
                    return true;
                }else{
                    return false;
                }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

            if (viewSelected != null){
                viewSelected.setBackgroundColor(Color.TRANSPARENT);
            }

            actionMode = null;
            viewSelected = null;

            listViewDoctors.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.app_name));

        listViewDoctors = findViewById(R.id.listViewDoctors);

        listViewDoctors.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionSelected = position;
                doctorEdit();
            }
        });

        listViewDoctors.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent,
                                           View view,
                                           int position,
                                           long id) {

                if (actionMode != null){
                    return false;
                }

                positionSelected = position;

                view.setBackgroundColor(Color.LTGRAY);

                viewSelected = view;

                listViewDoctors.setEnabled(false);

                actionMode = startSupportActionMode(mActionModeCallback);

                return false;
            }
        });

        lerPreferenciaOrdenacaoAscendente();

        popularLista();
    }

    private void popularLista(){

        DoctorDatabase database = DoctorDatabase.getDatabase(this);

        if (ascendingOrder){
            doctorsList = database.getDoctorDao().queryAllAscending();
        }else{
            doctorsList = database.getDoctorDao().queryAllDownward();
        }

        listAdapter = new DoctorAdapter(this, doctorsList);

        listViewDoctors.setAdapter(listAdapter);
    }

    private void doctorDelete(final ActionMode mode){

        final Doctor doctor = doctorsList.get(positionSelected);

        String mensagem = getString(R.string.deseja_realmente_apagar) + "\n" + "\"" + doctor.getNome() + "\"";

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch(which){

                    case DialogInterface.BUTTON_POSITIVE:

                        DoctorDatabase database = DoctorDatabase.getDatabase(MainActivity.this);

                        int quantidadeAlterada = database.getDoctorDao().delete(doctor);

                        if (quantidadeAlterada > 0){
                            doctorsList.remove(positionSelected);
                            listAdapter.notifyDataSetChanged();
                            mode.finish();
                        }else{
                            UtilsGUI.alert(MainActivity.this, R.string.erro_ao_tentar_apagar);
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        UtilsGUI.actionConfirmation(this, mensagem, listener);
    }

    ActivityResultLauncher<Intent> launcherDoctorEdit = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK){

                        Intent intent = result.getData();

                        Bundle bundle = intent.getExtras();

                        if (bundle != null){

                            long id = bundle.getLong(DoctorActivity.ID);

                            DoctorDatabase database = DoctorDatabase.getDatabase(MainActivity.this);

                            Doctor doctorAdded = database.getDoctorDao().queryForId(id);

                            doctorsList.set(positionSelected, doctorAdded);

                            positionSelected = -1;

                            sortList();
                        }
                    }
                }
            });

    private void doctorEdit(){

        Doctor doctor = doctorsList.get(positionSelected);

        DoctorActivity.doctorEdit(this, launcherDoctorEdit, doctor);
    }

    ActivityResultLauncher<Intent> launcherNewDoctor = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK){

                        Intent intent = result.getData();

                        Bundle bundle = intent.getExtras();

                        if (bundle != null){

                            long id = bundle.getLong(DoctorActivity.ID);

                            DoctorDatabase database = DoctorDatabase.getDatabase(MainActivity.this);

                            Doctor doctorAdded = database.getDoctorDao().queryForId(id);

                            doctorsList.add(doctorAdded);

                            sortList();
                        }
                    }
                }
            });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_options, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem menuItemOrdenacao = menu.findItem(R.id.menuItemOrder);

        atualizarIconeOrdenacao(menuItemOrdenacao);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int idMenuItem = item.getItemId();

        if (idMenuItem == R.id.menuItemAdd){
            DoctorActivity.newDoctor(this, launcherNewDoctor);
            return true;
        }else
            if (idMenuItem == R.id.menuItemOrder){
                salvarPreferenciaOrdenacaoAscendente(!ascendingOrder);
                atualizarIconeOrdenacao(item);
                sortList();
                return true;
            }else
                if (idMenuItem == R.id.menuItemAbout){
                    AboutActivity.nova(this);
                    return true;
                }else{
                    return super.onOptionsItemSelected(item);
                }
    }

    private void atualizarIconeOrdenacao(MenuItem menuItemOrdenacao){

        if (ascendingOrder){
            menuItemOrdenacao.setIcon(R.drawable.ic_action_ordenacao_ascendente);
        }else{
            menuItemOrdenacao.setIcon(R.drawable.ic_action_ordenacao_descendente);
        }
    }

    private void sortList(){

        if (ascendingOrder){
            Collections.sort(doctorsList, Doctor.ordenacaoCrescente);
        }else{
            Collections.sort(doctorsList, Doctor.ordenacaoDecrescente);
        }

        listAdapter.notifyDataSetChanged();
    }

    private void lerPreferenciaOrdenacaoAscendente(){

        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);

        ascendingOrder = shared.getBoolean(ORDENACAO_ASCENDENTE, ascendingOrder);
    }

    private void salvarPreferenciaOrdenacaoAscendente(boolean newValue){

        SharedPreferences shared = getSharedPreferences(ARQUIVO, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean(ORDENACAO_ASCENDENTE, newValue);

        editor.commit();

        ascendingOrder = newValue;
    }
}