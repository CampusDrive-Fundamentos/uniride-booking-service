Feature: Validación de Abordaje mediante Código
 Como conductor
 Quiero ingresar en mi aplicación el código que me dicta el líder
 Para asegurar que son los pasajeros correctos y empezar el viaje

Scenario: Inicio de viaje seguro
 Given que los estudiantes suben al vehículo y le dictan su código al conductor
 When el conductor ingresa el código correcto en su pantalla
 Then el viaje pasa a estar oficialmente en curso.
 
Example:
|       Campo       |   Valor Mostrado al Usuario    | 
| Estado del viaje  | En curso (Navegando)           |
| Mensaje           | Código aceptado. ¡Buen viaje!  | 