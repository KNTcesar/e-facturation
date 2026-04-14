package bf.gov.dgi.sfe.service;

import bf.gov.dgi.sfe.dto.ClientRequest;
import bf.gov.dgi.sfe.dto.ClientResponse;
import bf.gov.dgi.sfe.entity.Client;
import bf.gov.dgi.sfe.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void create_shouldMapRequestAndReturnResponse() {
        ClientRequest request = new ClientRequest(
                "Client Test",
                "IFU-12345",
                "Ouagadougou",
                "+22670000000",
                "client@test.bf"
        );

        Client saved = new Client();
        UUID id = UUID.randomUUID();
        saved.setId(id);
        saved.setNom(request.nom());
        saved.setIfu(request.ifu());
        saved.setAdresse(request.adresse());
        saved.setTelephone(request.telephone());
        saved.setEmail(request.email());

        when(clientRepository.save(any(Client.class))).thenReturn(saved);

        ClientResponse result = clientService.create(request);

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientRepository).save(captor.capture());
        Client toPersist = captor.getValue();
        assertThat(toPersist.getNom()).isEqualTo("Client Test");
        assertThat(toPersist.getIfu()).isEqualTo("IFU-12345");
        assertThat(toPersist.getAdresse()).isEqualTo("Ouagadougou");
        assertThat(toPersist.getTelephone()).isEqualTo("+22670000000");
        assertThat(toPersist.getEmail()).isEqualTo("client@test.bf");

        assertThat(result.id()).isEqualTo(id);
        assertThat(result.nom()).isEqualTo("Client Test");
        assertThat(result.ifu()).isEqualTo("IFU-12345");
    }

    @Test
    void list_shouldReturnMappedResponses() {
        Client c1 = new Client();
        c1.setId(UUID.randomUUID());
        c1.setNom("Client A");
        c1.setIfu("IFU-A");
        c1.setAdresse("Adresse A");

        Client c2 = new Client();
        c2.setId(UUID.randomUUID());
        c2.setNom("Client B");
        c2.setIfu("IFU-B");
        c2.setAdresse("Adresse B");

        when(clientRepository.findAll()).thenReturn(List.of(c1, c2));

        List<ClientResponse> result = clientService.list();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).nom()).isEqualTo("Client A");
        assertThat(result.get(0).ifu()).isEqualTo("IFU-A");
        assertThat(result.get(1).nom()).isEqualTo("Client B");
        assertThat(result.get(1).ifu()).isEqualTo("IFU-B");
    }
}
