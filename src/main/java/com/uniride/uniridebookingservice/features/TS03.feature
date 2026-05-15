Feature: Visualización de estudiantes del grupo
 Como pasajero de un grupo
 Quiero ver la lista de mis compañeros
 Para saber con quiénes voy a compartir la ruta

Scenario: Consultar integrantes
 Given que el estudiante ingresa al detalle de su viaje actual
 When el líder presiona "Cancelar Viaje"
 Then el sistema disuelve el grupo y avisa a los demás integrantes
 
Example:
|  Integrante  |  Rol      | Estado de Pago|
| Alumno 1     | Líder     | Pendiente     |
| Alumno 2     | Pasajero  | Pagado        | 
 