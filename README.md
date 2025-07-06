# Transaction loans

## Port
8084

## 🔐 Endpoints
### 🏦 PRESTAMOS CLIENTES

#### 📥 GET

```http
  GET http://localhost:8084/api/v1/prestamos-clientes
  GET http://localhost:8084/api/v1/garantias-tipos-prestamos-cliente
  GET http://localhost:8084/api/v1/seguros-prestamo-cliente
  GET http://localhost:8084/api/v1/comisiones-prestamo-cliente
  PATCH http://localhost:8084/api/v1/prestamos-clientes/7/estado?estado=APROBADO
  GET http://localhost:8084/api/v1/cronogramas-pagos/generar/7
  GET http://localhost:8084/api/v1/pagos-prestamos/registrar?idCuota=85&tipoPago=EFECTIVO&referencia=COMPROBANTE-2025-06-06
```

#### ➕ POST

```http
  POST http://localhost:8084/api/v1/prestamos-clientes
  POST http://localhost:8084/api/v1/garantias-tipos-prestamos-cliente
  POST http://localhost:8084/api/v1/seguros-prestamo-cliente
  POST http://localhost:8084/api/v1/comisiones-prestamo-cliente
  PATCH http://localhost:8084/api/v1/prestamos-clientes/7/estado?estado=APROBADO
  POST http://localhost:8084/api/v1/cronogramas-pagos/generar/7
  POST http://localhost:8084/api/v1/pagos-prestamos/registrar?idCuota=85&tipoPago=EFECTIVO&referencia=COMPROBANTE-2025-06-06
```

#### Request body
```javascript
{
    "idCliente": 1,
    "idPrestamo": "6869841af5b8faf03b4dc3ba",
    "montoSolicitado": 15000,
    "plazoMeses": 24,
    "tasaInteresAplicada": 15
}

{
    "idPrestamoCliente": 6,
    "idGarantiaTipoPrestamo": "68697fb3f5b8faf03b4dc3b9",
    "montoTasado": 100000, //validar que no sea mayor al que viene en el id
    "descripcion": "GARANTIA CARRO DE SEGUNDA MANO, MAL ESTADO.",
    "documentoReferencia": "DOC-RF5689"
}
{
    "idPrestamoCliente": 6,
    "idSeguroPrestamo": "6869841af5b8faf03b4dc3ba"  // ID del préstamo
}
{
    "idPrestamoCliente": 6,
    "idComisionPrestamo": "6869841af5b8faf03b4dc3ba"
}

```


## Constrain for catalog loans

| TABLE                         | ATTRIBUTE        | CONSTRAINT                                                                                       | ENUM                        |
|:------------------------------|:------------------|:------------------------------------------------------------------------------------------------------|:-----------------------------|
| comisiones_prestamo_cliente   | `estado`          | ▫️ 'PENDIENTE'<br>▫️ 'CANCELADA'<br>▫️ 'EXENTA'                                                   | EstadoComisionClienteEnum   |
| comisiones_prestamos          | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'                                                                      | EstadoGeneralEnum           |
| tipos_comisiones              | `tipo`            | ▫️ 'ORIGINACION'<br>▫️ 'PAGO ATRASADO'<br>▫️ 'PREPAGO'<br>▫️ 'MODIFICACION'<br>▫️ 'SERVICIO ADICIONAL' | TipoComisionEnum            |
| tipos_comisiones              | `tipo_calculo`    | ▫️ 'PORCENTAJE'<br>▫️ 'FIJO'                                                                      | TipoCalculoComisionEnum     |
| tipos_comisiones              | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'                                                                      | EstadoGeneralEnum           |
| cronogramas_pagos             | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'<br>▫️ 'PENDIENTE'<br>▫️ 'PAGADO'                                     | EstadoCronogramaEnum        |
| esquemas_amortizacion         | `nombre`          | ▫️ 'FRANCES'<br>▫️ 'AMERICANO'<br>▫️ 'ALEMAN'                                                     | EsquemaAmortizacionEnum     |
| esquemas_amortizacion         | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'                                                                      | EstadoGeneralEnum           |
| garantias                     | `tipo_garantia`   | ▫️ 'HIPOTECA'<br>▫️ 'PRENDARIA'<br>▫️ 'PERSONAL'                                                  | TipoGarantiaEnum            |
| garantias                     | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'                                                                      | EstadoGeneralEnum           |
| garantias_tipos_prestamos     | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'                                                                      | EstadoGeneralEnum           |
| garantias_tipos_prestamos_cliente | `estado`     | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'                                                                      | EstadoGeneralEnum           |
| pagos_prestamos               | `estado`          | ▫️ 'COMPLETADO'<br>▫️ 'REVERTIDO'                                                                 | EstadoPagoEnum              |
| tipos_prestamos               | `tipo_cliente`    | ▫️ 'PERSONA'<br>▫️ 'EMPRESA'<br>▫️ 'AMBOS'                                                        | TipoClienteEnum             |
| tipos_prestamos               | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'                                                                      | EstadoGeneralEnum           |
| prestamos                     | `base_calculo`    | ▫️ '30/360'<br>▫️ '31/365'                                                                        | BaseCalculoEnum             |
| prestamos                     | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'<br>▫️ 'SOLICITADO'                                                   | EstadoPrestamoEnum          |
| prestamos_clientes            | `estado`          | ▫️ 'SOLICITADO'<br>▫️ 'APROBADO'<br>▫️ 'DESEMBOLSADO'<br>▫️ 'VIGENTE'<br>▫️ 'EN_MORA'<br>▫️ 'REFINANCIADO'<br>▫️ 'PAGADO'<br>▫️ 'CASTIGADO' | EstadoPrestamoClienteEnum |
| seguros                       | `tipo_seguro`     | ▫️ 'VIDA'<br>▫️ 'DESEMPLEO'<br>▫️ 'PROTECCION_PAGOS'<br>▫️ 'DESGRAVAMEN'<br>▫️ 'INCENDIOS'         | TipoSeguroEnum              |
| seguros                       | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'VENCIDO'<br>▫️ 'CANCELADO'<br>▫️ 'INACTIVO'                                    | EstadoSeguroEnum            |
| seguros_prestamos             | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'                                                                      | EstadoGeneralEnum           |
| seguros_prestamo_cliente      | `estado`          | ▫️ 'ACTIVO'<br>▫️ 'INACTIVO'                                                                      | EstadoGeneralEnum           |
