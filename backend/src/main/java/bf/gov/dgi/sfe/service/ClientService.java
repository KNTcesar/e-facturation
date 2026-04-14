package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.ClientRequest;
import bf.gov.dgi.sfe.dto.ClientResponse;
import bf.gov.dgi.sfe.entity.Client;
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
    public ClientResponse create(ClientRequest request) {
        Client c = new Client();
        c.setNom(request.nom());
        c.setIfu(request.ifu());
        c.setAdresse(request.adresse());
        c.setTelephone(request.telephone());
        c.setEmail(request.email());
        c = clientRepository.save(c);
        return new ClientResponse(c.getId(), c.getNom(), c.getIfu(), c.getAdresse(), c.getTelephone(), c.getEmail());
    }

    @Transactional(readOnly = true)
    public List<ClientResponse> list() {
        return clientRepository.findAll().stream()
                .map(c -> new ClientResponse(c.getId(), c.getNom(), c.getIfu(), c.getAdresse(), c.getTelephone(), c.getEmail()))
                .toList();
    }
}
