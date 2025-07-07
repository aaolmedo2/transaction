package com.banquito.core.loan.transaction.client;

import com.banquito.core.loan.transaction.DTO.external.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clientes-service", url = "${clientes.service.url}")
public interface ClientesClient {

    @GetMapping("/api/v1/clientes/clientes/{idCliente}")
    ResponseEntity<ClienteDTO> findById(@PathVariable("idCliente") String idCliente);
}
