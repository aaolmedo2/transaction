package com.banquito.core.loan.transaction.client;

import com.banquito.core.loan.transaction.DTO.external.PrestamosDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "prestamos-service", url = "${catalog.service.url}")
public interface PrestamosClient {

    @GetMapping("/api/v1/prestamos")
    ResponseEntity<List<PrestamosDTO>> findAll();

    @GetMapping("/api/v1/prestamos/{id}")
    ResponseEntity<PrestamosDTO> findById(@PathVariable("id") String id);

    @PostMapping("/api/v1/prestamos")
    ResponseEntity<PrestamosDTO> create(@RequestBody PrestamosDTO prestamoDTO);
}
