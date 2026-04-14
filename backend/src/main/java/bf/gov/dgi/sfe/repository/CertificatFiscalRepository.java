package bf.gov.dgi.sfe.repository;

import bf.gov.dgi.sfe.entity.CertificatFiscal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

// Acces lecture/ecriture des certificats fiscaux.
public interface CertificatFiscalRepository extends JpaRepository<CertificatFiscal, UUID> {
}
