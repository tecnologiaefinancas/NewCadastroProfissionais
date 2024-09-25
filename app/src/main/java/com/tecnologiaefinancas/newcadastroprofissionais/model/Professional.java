package com.tecnologiaefinancas.newcadastroprofissionais.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;

@Entity
public class Professional {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private int tipo;

    private boolean isReferred;

    private PaymentType paymentType;

    private String comments;

    public Professional(String name, int tipo, boolean isReferred, PaymentType paymentType, String comments) {
        this.name = name;
        this.tipo     = tipo;
        this.isReferred = isReferred;
        this.paymentType = paymentType;
        this.comments = comments;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public boolean isReferred() {
        return isReferred;
    }

    public void setReferred(boolean referred) {
        this.isReferred = referred;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "Professional{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tipo=" + tipo +
                ", isReferred=" + isReferred +
                ", paymentType=" + paymentType +
                ", comments='" + comments + '\'' +
                '}';
    }

    public static Comparator ordenacaoCrescente = new Comparator<Professional>() {
        @Override
        public int compare(Professional pessoa1, Professional pessoa2) {
            return pessoa1.getName().compareToIgnoreCase(pessoa2.getName());
        }
    };

    public static Comparator ordenacaoDecrescente = new Comparator<Professional>() {
        @Override
        public int compare(Professional professionalA, Professional professionalB) {
            return -1 * professionalA.getName().compareToIgnoreCase(professionalB.getName());
        }
    };
}
