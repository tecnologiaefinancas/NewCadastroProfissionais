    package com.tecnologiaefinancas.newcadastroprofissionais;

    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.TextView;

    import java.util.List;

    import com.tecnologiaefinancas.newcadastroprofissionais.model.Doctor;
    import com.tecnologiaefinancas.newcadastroprofissionais.model.PaymentType;

    public class DoctorAdapter extends BaseAdapter {

        private Context context;

        private List<Doctor> doctorsList;

        private String[] tipos;

        private static class DoctorHolder {
            public TextView textViewValorNome;
            public TextView textViewValorTipo;
            public TextView textViewValorBolsista;
            public TextView textViewPaymentTypeValue;
        }

        public DoctorAdapter(Context context, List<Doctor> doctor){
            this.context = context;
            this.doctorsList = doctor;

            tipos = context.getResources().getStringArray(R.array.types);
        }

        @Override
        public int getCount() {
            return doctorsList.size();
        }

        @Override
        public Object getItem(int position) {
            return doctorsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            DoctorHolder holder;

            if (convertView == null){

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_list_doctor, parent, false);

                holder = new DoctorHolder();

                holder.textViewValorNome     = convertView.findViewById(R.id.textViewValorNome);
                holder.textViewValorTipo     = convertView.findViewById(R.id.textViewValorTipo);
                holder.textViewValorBolsista = convertView.findViewById(R.id.textViewValorBolsista);
                holder.textViewPaymentTypeValue = convertView.findViewById(R.id.textViewTipoPagamento);

                convertView.setTag(holder);

            }else{

                holder = (DoctorHolder) convertView.getTag();

            }

            Doctor doctor = doctorsList.get(position);

            holder.textViewValorNome.setText(doctor.getNome());

            holder.textViewValorTipo.setText(tipos[doctor.getTipo()]);

            if (doctor.isIndicado()){
                holder.textViewValorBolsista.setText(R.string.referred_professional);
            }else{
                holder.textViewValorBolsista.setText(R.string.not_referred);
            }

            if (doctor.getPaymentType() == PaymentType.PIX){
                holder.textViewPaymentTypeValue.setText(R.string.pix);
            }else
                if (doctor.getPaymentType() == PaymentType.Boleto){
                    holder.textViewPaymentTypeValue.setText(R.string.boleto);
                }else
                    if (doctor.getPaymentType() == PaymentType.Cartao){
                        holder.textViewPaymentTypeValue.setText(R.string.credit_card);
                    }

            return convertView;
        }
    }
