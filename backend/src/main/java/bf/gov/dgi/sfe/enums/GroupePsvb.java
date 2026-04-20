package bf.gov.dgi.sfe.enums;

import java.math.BigDecimal;

// Groupes PSVB et taux associes.
public enum GroupePsvb {
    A(new BigDecimal("2.0")),
    B(new BigDecimal("1.0")),
    C(new BigDecimal("0.2")),
    D(BigDecimal.ZERO);

    private final BigDecimal taux;

    GroupePsvb(BigDecimal taux) {
        this.taux = taux;
    }

    public BigDecimal taux() {
        return taux;
    }
}
