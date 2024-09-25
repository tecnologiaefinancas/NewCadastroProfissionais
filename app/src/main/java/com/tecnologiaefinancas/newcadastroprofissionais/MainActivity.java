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

import com.tecnologiaefinancas.newcadastroprofissionais.model.Professional;
import com.tecnologiaefinancas.newcadastroprofissionais.persistence.ProfessionalDatabase;
import com.tecnologiaefinancas.newcadastroprofissionais.utils.Dialogs;

public class MainActivity extends AppCompatActivity {

    private ListView listViewProfessionals;

    private ProfessionalAdapter listAdapter;

    private List<Professional> professionalList;

    private ActionMode actionMode;

    private View viewSelected;

    private int positionSelected = -1;

    public static final String FILE = "com.tecnologiaefinancas.newcadastroprofissionais.PREFERENCIAIS";

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
                professionalEdit();
                mode.finish();
                return true;
            }else
                if (idMenuItem == R.id.idemMenuDelete){
                    professionalDelete(mode);
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

            listViewProfessionals.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.app_name));

        listViewProfessionals = findViewById(R.id.listViewProfessionals);

        listViewProfessionals.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listViewProfessionals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                positionSelected = position;
                professionalEdit();
            }
        });

        listViewProfessionals.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
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

                listViewProfessionals.setEnabled(false);

                actionMode = startSupportActionMode(mActionModeCallback);

                return false;
            }
        });

        lerPreferenciaOrdenacaoAscendente();

        popularLista();
    }

    private void popularLista(){

        ProfessionalDatabase database = ProfessionalDatabase.getDatabase(this);

        if (ascendingOrder){
            professionalList = database.getProfessionalDao().queryAllAscending();
        }else{
            professionalList = database.getProfessionalDao().queryAllDownward();
        }

        listAdapter = new ProfessionalAdapter(this, professionalList);

        listViewProfessionals.setAdapter(listAdapter);
    }

    private void professionalDelete(final ActionMode mode){

        final Professional professional = professionalList.get(positionSelected);

        String message = getString(R.string.really_wanna_delete) + "\n" + "\"" + professional.getName() + "\"";

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch(which){

                    case DialogInterface.BUTTON_POSITIVE:

                        ProfessionalDatabase database = ProfessionalDatabase.getDatabase(MainActivity.this);

                        int quantidadeAlterada = database.getProfessionalDao().delete(professional);

                        if (quantidadeAlterada > 0){
                            professionalList.remove(positionSelected);
                            listAdapter.notifyDataSetChanged();
                            mode.finish();
                        }else{
                            Dialogs.alert(MainActivity.this, R.string.error_trying_delete);
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            }
        };

        Dialogs.actionConfirmation(this, message, listener);
    }

    ActivityResultLauncher<Intent> launcherProfessionalEdit = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK){

                        Intent intent = result.getData();

                        Bundle bundle = intent.getExtras();

                        if (bundle != null){

                            long id = bundle.getLong(ProfessionalActivity.ID);

                            ProfessionalDatabase database = ProfessionalDatabase.getDatabase(MainActivity.this);

                            Professional professionalAdded = database.getProfessionalDao().queryForId(id);

                            professionalList.set(positionSelected, professionalAdded);

                            positionSelected = -1;

                            sortList();
                        }
                    }
                }
            });

    private void professionalEdit(){

        Professional professional = professionalList.get(positionSelected);

        ProfessionalActivity.professionalEdit(this, launcherProfessionalEdit, professional);
    }

    ActivityResultLauncher<Intent> launcherNewDoctor = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),

            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if (result.getResultCode() == Activity.RESULT_OK){

                        Intent intent = result.getData();


                        Bundle bundle = intent.getExtras();

                        if (bundle != null){

                            long id = bundle.getLong(ProfessionalActivity.ID);

                            ProfessionalDatabase database = ProfessionalDatabase.getDatabase(MainActivity.this);

                            Professional professionalAdded = database.getProfessionalDao().queryForId(id);

                            professionalList.add(professionalAdded);

                            sortList();
                        }}
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
            ProfessionalActivity.newProfessional(this, launcherNewDoctor);
            return true;
        }else
            if (idMenuItem == R.id.menuItemOrder){
                salvarPreferenciaOrdenacaoAscendente(!ascendingOrder);
                atualizarIconeOrdenacao(item);
                sortList();
                return true;
            }else
                if (idMenuItem == R.id.menuItemAbout){
                    AboutActivity.newActivity(this);
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
            Collections.sort(professionalList, Professional.ordenacaoCrescente);
        }else{
            Collections.sort(professionalList, Professional.ordenacaoDecrescente);
        }

        listAdapter.notifyDataSetChanged();
    }

    private void lerPreferenciaOrdenacaoAscendente(){

        SharedPreferences shared = getSharedPreferences(FILE, Context.MODE_PRIVATE);

        ascendingOrder = shared.getBoolean(ORDENACAO_ASCENDENTE, ascendingOrder);
    }

    private void salvarPreferenciaOrdenacaoAscendente(boolean newValue){

        SharedPreferences shared = getSharedPreferences(FILE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = shared.edit();

        editor.putBoolean(ORDENACAO_ASCENDENTE, newValue);

        editor.commit();

        ascendingOrder = newValue;
    }
}