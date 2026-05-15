Feature: Visualización del estado del grupo
 Como estudiante
 Quiero ver claramente en qué etapa está mi viaje
 Para saber si debo ir al punto de encuentro o seguir esperando

Scenario: Seguimiento del estado
 Given que el estudiante revisa la pestaña de viajes activos.
 When visualiza la tarjeta de su viaje
 Then ve un indicador claro de la fase actual del servicio.
 
Example:
|     Campo      |   Valor Mostrado al Usuario      | 
| Estado Actual  | Buscando conductor...            |
| Ocupación      | 4 de 4 estudiantes (Lleno)       | 