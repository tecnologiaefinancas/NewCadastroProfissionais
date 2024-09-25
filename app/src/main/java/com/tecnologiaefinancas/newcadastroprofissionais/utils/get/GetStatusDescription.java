package com.tecnologiaefinancas.newcadastroprofissionais.utils.get;

import android.content.Context;

import com.tecnologiaefinancas.newcadastroprofissionais.model.ServiceStatus;

import java.util.ArrayList;
import java.util.List;

public class GetStatusDescription {

    public static List<String> getStatusDescriptions(Context context) {
        List<String> descriptions = new ArrayList<>();
        for (ServiceStatus status : ServiceStatus.values()) {
            descriptions.add(status.getDescricao(context));
        }
        return descriptions;
    }

    public static ServiceStatus getStatusFromDescription(Context context, String description) {
        for (ServiceStatus status : ServiceStatus.values()) {
            if (status.getDescricao(context).equals(description)) {
                return status;
            }
        }
        return ServiceStatus.SELECT_STATUS;
    }
}

