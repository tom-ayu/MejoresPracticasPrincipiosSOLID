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


## Principio 2: Open/Closed Principle (OCP)

### Aplicación del principio

El código base presentaba una clase `NotificationService` con un método `sendNotification`
que utilizaba una cadena de condicionales para determinar qué tipo de notificación enviar
según un parámetro de tipo `String`. Esta estructura funciona mientras el sistema maneja
pocos casos, pero se vuelve problemática en cuanto los requisitos cambian: agregar un nuevo
canal de notificación implica abrir la clase, modificar el método existente e introducir
una nueva rama al condicional. Cada una de esas modificaciones es una oportunidad para
romper el comportamiento ya probado de los tipos anteriores.

Antes de realizar la refactorización se consideraron dos alternativas. La primera consistía en utilizar una clase abstracta como base para los distintos tipos de notificación. Aunque esta opción permitía compartir comportamiento, en este sistema cada canal funciona de forma independiente y no existe lógica común que justifique una relación de herencia. La segunda alternativa fue crear una interfaz `Notification` con un único método `send`, la cual resultó más adecuada porque define un contrato común sin obligar a las clases a heredar de una misma base. Además, permite que en el futuro una clase pueda implementar varias interfaces si el diseño del sistema lo requiere.

La refactorización reemplazó el condicional por polimorfismo. Se definió la interfaz
`Notification` y se crearon tres implementaciones independientes: `EmailNotification`,
`SMSNotification` y `PushNotification`. La clase `NotificationService` dejó de conocer los
detalles de cada canal y pasó a recibir cualquier objeto que cumpla el contrato de la
interfaz. Esto significa que está cerrada para modificación, ya que no necesita cambiar
cuando se agrega un nuevo tipo, pero abierta para extensión, ya que cualquier nueva clase
que implemente `Notification` puede integrarse al sistema sin alterar el código existente.

El caso del tipo `"Fax"` presente en el código original, que antes producía el mensaje
"Invalid notification type!", ilustra bien el cambio de paradigma. En el diseño original
era necesario modificar `NotificationService` para soportar Fax. En el diseño refactorizado
basta con crear una clase `FaxNotification` que implemente la interfaz, sin tocar ninguna
clase existente. Este es precisamente el criterio para evaluar si el OCP se está aplicando
correctamente: que la extensión no requiera modificación.

### Problemas que resolvió

El problema central era la fragilidad del método `sendNotification` ante el crecimiento del
sistema. Al concentrar la lógica de todos los canales en un único bloque condicional, cada
nueva funcionalidad aumentaba la complejidad del método y elevaba el riesgo de introducir
errores en canales ya funcionando. Con la refactorización, cada tipo de notificación queda
encapsulado en su propia clase, que puede probarse de forma aislada y, una vez validada, no
necesita volver a modificarse. Cualquier error introducido en el futuro estará contenido en
la clase nueva, no en las ya existentes, lo que reduce significativamente el riesgo de
regresión y hace al sistema más predecible y mantenible a largo plazo.


## Principio 3: Liskov Substitution Principle (LSP)

### Aplicación del principio

El código base definía una clase `Animal` con dos métodos: `makeSound()` y `walk()`. La
clase `Dog` heredaba ambos sin problema, pero `Fish` se veía obligada a sobreescribir
`walk()` lanzando una excepción `UnsupportedOperationException`, ya que los peces no
caminan. Esta es una violación directa del LSP: si cualquier parte del programa recibe un
`Animal` y llama a `walk()`, el comportamiento depende de qué subclase concreta sea, y en
el caso de `Fish` el programa falla en tiempo de ejecución. La clase hija no es sustituible
por la clase base sin alterar el comportamiento del programa.

Antes de refactorizar se evaluaron dos enfoques. El primero consistía en crear subclases
intermedias como `WalkingAnimal` y `NonWalkingAnimal` dentro de la jerarquía de `Animal`.
Esta opción introduce rigidez: si en el futuro aparece un animal que nada y también camina,
como una tortuga, la jerarquía no lo acomoda sin reestructurarse. El segundo enfoque,
extraer la capacidad de caminar a una interfaz `Walkable`, es más adecuado porque es
composable: cualquier clase puede implementarla independientemente de su posición en la
jerarquía, sin comprometer el contrato de `Animal`.

La refactorización convirtió `Animal` en una clase abstracta que únicamente define el método `makeSound()`, ya que este representa un comportamiento válido para cualquier animal. La capacidad de caminar se trasladó a la interfaz `Walkable`, la cual es implementada por `Dog`, pero no por `Fish`. Gracias a este cambio, cualquier objeto de tipo `Animal` puede utilizar `makeSound()` sin problemas, mientras que solo los animales que realmente pueden caminar implementan `Walkable`.

Además, en `Main` se utilizó el tipo `Dog` en lugar de `Animal`, ya que el programa necesita acceder al método `walk()`. Esta decisión evita realizar conversiones innecesarias (`cast`) desde `Animal` a `Walkable`, las cuales podrían volver a introducir errores similares a los del diseño original. En otras palabras, el principio de sustitución de Liskov no impide utilizar tipos concretos cuando es necesario; lo importante es que las subclases respeten el comportamiento esperado de su clase base.

### Problemas que resolvió

La refactorización convirtió `Animal` en una clase abstracta que únicamente define el método `makeSound()`, ya que este representa un comportamiento válido para cualquier animal. La capacidad de caminar se trasladó a la interfaz `Walkable`, la cual es implementada por `Dog`, pero no por `Fish`. Gracias a este cambio, cualquier objeto de tipo `Animal` puede utilizar `makeSound()` sin problemas, mientras que solo los animales que realmente pueden caminar implementan `Walkable`.

Además, en `Main` se utilizó el tipo `Dog` en lugar de `Animal`, ya que el programa necesita acceder al método `walk()`. Esta decisión evita realizar conversiones innecesarias (`cast`) desde `Animal` a `Walkable`, las cuales podrían volver a introducir errores similares a los del diseño original. En otras palabras, el principio de sustitución de Liskov no impide utilizar tipos concretos cuando es necesario; lo importante es que las subclases respeten el comportamiento esperado de su clase base.

### Problemas que resolvió

Antes de la refactorización, el programa podía fallar en tiempo de ejecución si se trataba un `Fish` como un `Animal` y se llamaba al método `walk()`. Este tipo de errores es difícil de detectar porque el compilador no los identifica y solo aparecen cuando el programa se ejecuta. Con la nueva estructura, el compilador garantiza que `walk()` solo pueda invocarse en objetos que implementan `Walkable`, evitando este problema desde el diseño.

Adicionalmente, el diseño resultante es más honesto: la jerarquía de clases refleja con precisión las capacidades reales de cada animal, lo que hace el sistema más fácil de entender, extender y mantener sin sorpresas.