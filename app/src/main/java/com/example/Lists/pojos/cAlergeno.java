package com.example.Lists.pojos;

public class cAlergeno {
    Alergeno alergeno;
    Boolean checked;

    public cAlergeno(Alergeno alergeno, Boolean checked) {
        this.alergeno = alergeno;
        this.checked = checked;
    }

    public Alergeno getAlergeno() {
        return alergeno;
    }

    public void setAlergeno(Alergeno alergeno) {
        this.alergeno = alergeno;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }
}