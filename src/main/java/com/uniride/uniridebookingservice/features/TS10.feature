Feature: Cancelación de Unión a Grupo
 Como estudiante seguidor
 Quiero poder salir de un grupo al que me uní
 Para buscar otra opción si esta ya no me conviene.

Scenario: Salida voluntaria
 Given que el pasajero presiona "Abandonar grupo" antes de que inicie el recorrido
 When el sistema procesa la salida
 Then se libera su asiento y el viaje vuelve a buscar un integrante
 
Example:
|        Campo          |              Texto                      | 
| Asientos Disponibles  |       2                                 |
| Mensaje               | Has abandonado el grupo exitosamente    | 
 