package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.ClientRequest;
import bf.gov.dgi.sfe.dto.ClientResponse;
import bf.gov.dgi.sfe.entity.Client;
import bf.gov.dgi.sfe.enums.TypeClient;
import bf.gov.dgi.sfe.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
// Gestion metier des clients facturees.
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    // Cree un client en appliquant les regles de validation selon son type.
    public ClientResponse create(ClientRequest request) {
        validateByType(request);

        Client c = new Client();
        c.setTypeClient(request.typeClient());
        c.setNom(request.nom());
        c.setIfu(trimToNull(request.ifu()));
        c.setRccm(trimToNull(request.rccm()));
        c.setAdresse(request.adresse());
        c.setTelephone(trimToNull(request.telephone()));
        c.setEmail(trimToNull(request.email()));
        c = clientRepository.save(c);
        return new ClientResponse(c.getId(), c.getTypeClient(), c.getNom(), c.getIfu(), c.getRccm(), c.getAdresse(), c.getTelephone(), c.getEmail());
    }

    @Transactional(readOnly = true)
    // Liste les clients enregistres.
    public List<ClientResponse> list() {
        return clientRepository.findAll().stream()
                .map(c -> new ClientResponse(c.getId(), c.getTypeClient(), c.getNom(), c.getIfu(), c.getRccm(), c.getAdresse(), c.getTelephone(), c.getEmail()))
                .toList();
    }

    // Verifie les champs IFU/RCCM obligatoires selon le type de client.
    private void validateByType(ClientRequest request) {
        String ifu = trimToNull(request.ifu());
        String rccm = trimToNull(request.rccm());

        if (request.typeClient() == TypeClient.PM || request.typeClient() == TypeClient.PC) {
            if (ifu == null) {
                throw new IllegalArgumentException("L'IFU est obligatoire pour le type client " + request.typeClient());
            }
            if (rccm == null) {
                throw new IllegalArgumentException("Le RCCM est obligatoire pour le type client " + request.typeClient());
            }
        }

        if (request.typeClient() == TypeClient.PP && ifu == null) {
            throw new IllegalArgumentException("L'IFU est obligatoire pour le type client PP");
        }
    }

    // Nettoie une chaine et retourne null si elle est vide.
    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
