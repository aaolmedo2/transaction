package com.banquito.core.loan.transaction.client;

import com.banquito.core.loan.transaction.DTO.external.TiposPrestamosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "tipos-prestamos-service", url = "${catalog.service.url}")
public interface TiposPrestamosClient {

    @GetMapping("/api/v1/tipos-prestamos")
    ResponseEntity<List<TiposPrestamosDTO>> findAll();

    @GetMapping("/api/v1/tipos-prestamos/{id}")
    ResponseEntity<TiposPrestamosDTO> findById(@PathVariable("id") String id);

    @PostMapping("/api/v1/tipos-prestamos")
    ResponseEntity<TiposPrestamosDTO> create(@RequestBody TiposPrestamosDTO tipoPrestamoDTO);
}
