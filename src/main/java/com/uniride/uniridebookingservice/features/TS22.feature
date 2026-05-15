Feature: Confirmación de Pago al Líder
 Como estudiante líder
 Quiero marcar en la app quién ya me transfirió su parte
 Para no confundirme con las cuentas del taxi

Scenario: Marcado de pago recibido
 Given que un pasajero le hace el Yape al líder.
 When el líder toca el botón "Marcar como pagado" al lado del nombre del pasajero
 Then la tarjeta de ese compañero se actualiza visualmente
 
Example:
|  Pasajero |       Estado Financiero       | 
|     2     | Pagado (Confirmado por ti)    |
|     3     | Por pagar (Confirmado por ti) |
