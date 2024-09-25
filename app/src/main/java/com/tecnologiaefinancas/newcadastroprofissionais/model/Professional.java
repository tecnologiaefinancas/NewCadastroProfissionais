package com.tecnologiaefinancas.newcadastroprofissionais.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;

@Entity
public class Professional {

    public static Comparator ordenacaoCrescente = new Comparator<Professional>() {
        @Override
        public int compare(Professional pessoa1, Professional pessoa2) {
            return pessoa1.getNome().compareToIgnoreCase(pessoa2.getNome());
        }
    };

    public static Comparator ordenacaoDecrescente = new Comparator<Professional>() {
        @Override
        public int compare(Professional professionalA, Professional professionalB) {
            return -1 * professionalA.getNome().compareToIgnoreCase(professionalB.getNome());
        }
    };

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String nome;

    private int tipo;

    private boolean indicado;

    private PaymentType paymentType;

    public Professional(String nome, int tipo, boolean indicado, PaymentType paymentType) {
        this.nome     = nome;
        this.tipo     = tipo;
        this.indicado = indicado;
        this.paymentType = paymentType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public boolean isIndicado() {
        return indicado;
    }

    public void setIndicado(boolean indicado) {
        this.indicado = indicado;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        return nome + " - " + tipo + " - " + indicado + " - " + paymentType;
    }
}
