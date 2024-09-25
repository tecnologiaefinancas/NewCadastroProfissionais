package com.tecnologiaefinancas.newcadastroprofissionais.utils.get;

import com.tecnologiaefinancas.newcadastroprofissionais.model.StatusProfessional;

import java.util.ArrayList;
import java.util.List;

public class GetStatusDescription {

    public static List<String> getStatusDescriptions() {
        List<String> descriptions = new ArrayList<>();
        for (StatusProfessional status : StatusProfessional.values()) {
            descriptions.add(status.getDescricao());
        }
        return descriptions;
    }

    public static StatusProfessional getStatusFromDescription(String description) {
        for (StatusProfessional status : StatusProfessional.values()) {
            if (status.getDescricao().equals(description)) {
                return status;
            }
        }
        return StatusProfessional.SELECIONAR;
    }
}
