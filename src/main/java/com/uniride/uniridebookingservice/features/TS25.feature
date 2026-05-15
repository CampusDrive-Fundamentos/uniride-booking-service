Feature: Confirmación de ingreso de parada
 Como estudiante que busca unirse
 Quiero ingresar el punto exacto donde me bajaré
 Para que la ruta se ajuste e incluya mi destino.

Scenario: Registro de nueva parada
 Given que un estudiante encuentra un grupo y escribe su dirección de destino
 When confirma su solicitud de unión
 Then el sistema registra su bajada y recalcula la ruta del vehículo
 
Example:
|      Campo     |             Valor Mostrado al Usuario           | 
| Tipo de Parada | Bajada de pasajero                              |
| Mensaje        | Tu destino ha sido agregado a la ruta del grupo |