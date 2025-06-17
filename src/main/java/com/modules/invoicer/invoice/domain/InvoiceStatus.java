package com.modules.invoicer.invoice.domain;

public enum InvoiceStatus {
    DRAFT("Borrador"),
    PENDING("Pendiente"),
    SENT("Enviada"),
    PAID("Pagada"),
    CANCELLED("Cancelada"),
    PENDING_VERIFACTU("Pendiente Verifactu"),
    SENT_VERIFACTU("Enviada Verifactu");

    private final String displayName;

    InvoiceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
