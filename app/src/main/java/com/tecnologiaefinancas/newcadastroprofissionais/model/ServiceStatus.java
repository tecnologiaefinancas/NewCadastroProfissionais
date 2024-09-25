package com.tecnologiaefinancas.newcadastroprofissionais.model;

import android.content.Context;

import com.tecnologiaefinancas.newcadastroprofissionais.R;

public enum ServiceStatus {
    SELECT_STATUS(R.string.select_option),
    REFERRED_PROFESSIONAL(R.string.referred_professional),
    SCHEDULED(R.string.scheduled),
    COMPLETED(R.string.completed);

    private int description;

    ServiceStatus(int description) {
        this.description = description;
    }

    public String getDescricao(Context context) {
        return context.getString(description);
    }
}
