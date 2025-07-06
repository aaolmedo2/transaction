package com.banquito.core.loan.transaction.client;

import com.banquito.core.loan.transaction.DTO.external.ClienteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clientes-service", url = "${clientes.service.url:http://localhost:8083}")
public interface ClientesClient {

    @GetMapping("/api/v1/clientes/{idCliente}")
    ResponseEntity<ClienteDTO> findById(@PathVariable("idCliente") String idCliente);

    @GetMapping("/api/v1/clientes/{idCliente}/validar")
    ResponseEntity<Boolean> validarCliente(@PathVariable("idCliente") String idCliente);
}
