# MejoresPracticasPrincipiosSOLID
Sesión 31 - 32 - 33: Mejores prácticas en Desarrollo de Software

## Principio 1: Single Responsibility Principle (SRP)

### Aplicación del principio

El código base presentaba una clase `UserManager` que concentraba tres responsabilidades distintas en un mismo lugar: validar los datos del usuario, persistir la información en la base de datos y enviar una notificación de bienvenida por correo electrónico. Aunque el código funcionaba correctamente, su estructura representaba una violación directa del SRP, ya que existían al menos tres razones independientes por las que la clase podría necesitar modificarse: un cambio en las reglas de validación, un cambio en el mecanismo de almacenamiento o un cambio en el canal de notificación. Agrupar estas responsabilidades generaba un acoplamiento innecesario entre funcionalidades que deberían evolucionar de forma independiente.

La refactorización consistió en descomponer `UserManager` en cuatro clases con responsabilidades bien definidas:

* `UserValidator`: valida el correo electrónico y la contraseña.
* `UserRepository`: gestiona la persistencia de los datos.
* `NotificationService`: envía la notificación de bienvenida.
* `UserManager`: coordina el flujo delegando cada tarea a la clase correspondiente.

### Problemas que resolvió

Antes de la refactorización, cualquier cambio en la validación, la persistencia o las notificaciones implicaba modificar la misma clase, aumentando el riesgo de introducir errores en funcionalidades no relacionadas. Tras la separación, cada componente tiene una única razón para cambiar, lo que mejora la mantenibilidad y facilita las pruebas unitarias al poder evaluar cada clase de forma independiente.

Esta refactorización permitió comprobar que el SRP no consiste en reducir una clase a un único método, sino en asignarle una única responsabilidad. `UserManager` mantiene su función como coordinador, mientras que el resto de clases encapsulan tareas específicas. Asimismo, fue importante evitar una fragmentación excesiva del diseño, ya que separar responsabilidades sin un criterio claro puede aumentar innecesariamente la complejidad.

Como resultado, el sistema quedó mejor preparado para futuros cambios. Por ejemplo, reemplazar el servicio de correo o cambiar el motor de base de datos solo requiere modificar la clase correspondiente, reduciendo el acoplamiento y el riesgo de regresiones.
