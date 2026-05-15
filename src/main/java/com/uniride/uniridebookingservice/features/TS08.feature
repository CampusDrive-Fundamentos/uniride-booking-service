Feature: Eliminación de anuncio por el líder
 Como estudiante líder
 Quiero tener la opción de cancelar mi anuncio
 Para deshacer el grupo si ocurre un imprevisto

Scenario: Consultar integrantes
 Given que el grupo aún no ha sido aceptado por ningún conductor
 When a pantalla carga la información del grupo
 Then se muestra la lista de las personas que ya están a bordo y qué rol cumplen
 
Example:
|        Campo         |                    Texto                              | 
| Estado del Viaje     | Cancelado                                             |
| Mensaje              | Has cancelado este viaje. El grupo ha sido cerrado    | 
 