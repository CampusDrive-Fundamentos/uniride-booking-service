Feature: Creacion de Viaje Grupal
 Como estudiante líder
 Quiero establecer mi punto de partida y destino
 Para crear un nuevo anuncio de viaje en la plataforma

Scenario: Creacion Exitosa
 Given que el líder llena los datos de su viaje y presiona "Crear Anuncio"
 When el sistema procesa la solicitud
 Then se confirma la creación y el viaje queda disponible para que otros se unan
 
Example:
|        Campo         |               Texto                       | 
| Estado del Viaje     | Creado (Buscando compañeros)              |
| Mensaje              | ¡Tu viaje ha sido publicado con éxito!    | 
 