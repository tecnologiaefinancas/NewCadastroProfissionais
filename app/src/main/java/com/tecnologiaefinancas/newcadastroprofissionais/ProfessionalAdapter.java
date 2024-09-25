    package com.tecnologiaefinancas.newcadastroprofissionais;

    import android.content.Context;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.BaseAdapter;
    import android.widget.TextView;

    import java.util.List;

    import com.tecnologiaefinancas.newcadastroprofissionais.model.Professional;
    import com.tecnologiaefinancas.newcadastroprofissionais.model.PaymentType;

    public class ProfessionalAdapter extends BaseAdapter {

        private Context context;

        private List<Professional> doctorsList;

        private String[] tipos;

        private static class DoctorHolder {
            public TextView textViewNameValue;
            public TextView textViewTypeValue;
            public TextView textViewReferredProfessionalValue;
            public TextView textViewPaymentTypeValue;
        }

        public ProfessionalAdapter(Context context, List<Professional> professional){
            this.context = context;
            this.doctorsList = professional;

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
                convertView = inflater.inflate(R.layout.row_list_professional, parent, false);

                holder = new DoctorHolder();

                holder.textViewNameValue = convertView.findViewById(R.id.textViewNameValue);
                holder.textViewTypeValue     = convertView.findViewById(R.id.textViewTypeValue);
                holder.textViewReferredProfessionalValue = convertView.findViewById(R.id.textViewReferredProfessionalValue);
                holder.textViewPaymentTypeValue = convertView.findViewById(R.id.textViewPaymentTypeValue);

                convertView.setTag(holder);

            }else{


                holder = (DoctorHolder) convertView.getTag();

            }

            Professional professional = doctorsList.get(position);

            holder.textViewNameValue.setText(professional.getName());

            holder.textViewTypeValue.setText(tipos[professional.getTipo()]);

            if (professional.isReferred()){
                holder.textViewReferredProfessionalValue.setText(R.string.referred_professional);
            }else{
                holder.textViewReferredProfessionalValue.setText(R.string.not_referred);
            }

            if (professional.getPaymentType() == PaymentType.PIX){
                holder.textViewPaymentTypeValue.setText(R.string.pix);
            }else
                if (professional.getPaymentType() == PaymentType.Boleto){
                    holder.textViewPaymentTypeValue.setText(R.string.boleto);
                }else
                    if (professional.getPaymentType() == PaymentType.Cartao){
                        holder.textViewPaymentTypeValue.setText(R.string.credit_card);
                    }

            return convertView;
        }
    }
