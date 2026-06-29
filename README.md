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

### Problemas resueltos

Antes de la refactorización, cualquier cambio en la validación, la persistencia o las notificaciones implicaba modificar la misma clase, aumentando el riesgo de introducir errores en funcionalidades no relacionadas. Tras la separación, cada componente tiene una única razón para cambiar, lo que mejora la mantenibilidad y facilita las pruebas unitarias al poder evaluar cada clase de forma independiente.

Esta refactorización permitió comprobar que el SRP no consiste en reducir una clase a un único método, sino en asignarle una única responsabilidad. `UserManager` mantiene su función como coordinador, mientras que el resto de clases encapsulan tareas específicas. Asimismo, fue importante evitar una fragmentación excesiva del diseño, ya que separar responsabilidades sin un criterio claro puede aumentar innecesariamente la complejidad.

Como resultado, el sistema quedó mejor preparado para futuros cambios. Por ejemplo, reemplazar el servicio de correo o cambiar el motor de base de datos solo requiere modificar la clase correspondiente, reduciendo el acoplamiento y el riesgo de regresiones.

<img width="1920" height="1020" alt="Captura de pantalla 2026-06-28 201117" src="https://github.com/user-attachments/assets/8f8addb1-7f32-4bae-8f86-e76a52e1d802" />

## Principio 2: Open/Closed Principle (OCP)

### Aplicación del principio

El código base presentaba una clase `NotificationService` con un método `sendNotification` que utilizaba una cadena de condicionales para determinar qué tipo de notificación enviar según un parámetro de tipo `String`. Esta estructura funciona mientras el sistema maneja pocos casos, pero se vuelve problemática en cuanto los requisitos cambian: agregar un nuevo canal de notificación implica abrir la clase, modificar el método existente e introducir una nueva rama al condicional. Cada una de esas modificaciones es una oportunidad para romper el comportamiento ya probado de los tipos anteriores.

Antes de realizar la refactorización se consideraron dos alternativas. La primera consistía en utilizar una clase abstracta como base para los distintos tipos de notificación. Aunque esta opción permitía compartir comportamiento, en este sistema cada canal funciona de forma independiente y no existe lógica común que justifique una relación de herencia. La segunda alternativa fue crear una interfaz `Notification` con un único método `send`, la cual resultó más adecuada porque define un contrato común sin obligar a las clases a heredar de una misma base. Además, permite que en el futuro una clase pueda implementar varias interfaces si el diseño del sistema lo requiere.

La refactorización reemplazó el condicional por polimorfismo. Se definió la interfaz `Notification` y se crearon tres implementaciones independientes: `EmailNotification`, `SMSNotification` y `PushNotification`. La clase `NotificationService` dejó de conocer los detalles de cada canal y pasó a recibir cualquier objeto que cumpla el contrato de la interfaz. Esto significa que está cerrada para modificación, ya que no necesita cambiar cuando se agrega un nuevo tipo, pero abierta para extensión, ya que cualquier nueva clase que implemente `Notification` puede integrarse al sistema sin alterar el código existente.

El caso del tipo `"Fax"` presente en el código original, que antes producía el mensaje "Invalid notification type!", ilustra bien el cambio de paradigma. En el diseño original era necesario modificar `NotificationService` para soportar Fax. En el diseño refactorizado basta con crear una clase `FaxNotification` que implemente la interfaz, sin tocar ninguna clase existente. Este es precisamente el criterio para evaluar si el OCP se está aplicando correctamente: que la extensión no requiera modificación.

### Problemas resueltos

El problema central era la fragilidad del método `sendNotification` ante el crecimiento del sistema. Al concentrar la lógica de todos los canales en un único bloque condicional, cada nueva funcionalidad aumentaba la complejidad del método y elevaba el riesgo de introducir errores en canales ya funcionando. Con la refactorización, cada tipo de notificación queda encapsulado en su propia clase, que puede probarse de forma aislada y, una vez validada, no necesita volver a modificarse. Cualquier error introducido en el futuro estará contenido en la clase nueva, no en las ya existentes, lo que reduce significativamente el riesgo de regresión y hace al sistema más predecible y mantenible a largo plazo.

<img width="1920" height="1020" alt="Captura de pantalla 2026-06-28 202647" src="https://github.com/user-attachments/assets/913061a4-0586-4d98-a134-fd3cd724401f" />


## Principio 3: Liskov Substitution Principle (LSP)

### Aplicación del principio

El código base definía una clase `Animal` con dos métodos: `makeSound()` y `walk()`. La clase `Dog` heredaba ambos sin problema, pero `Fish` se veía obligada a sobreescribir `walk()` lanzando una excepción `UnsupportedOperationException`, ya que los peces no caminan. Esta es una violación directa del LSP: si cualquier parte del programa recibe un `Animal` y llama a `walk()`, el comportamiento depende de qué subclase concreta sea, y en el caso de `Fish` el programa falla en tiempo de ejecución. La clase hija no es sustituible por la clase base sin alterar el comportamiento del programa.

Antes de refactorizar se evaluaron dos enfoques. El primero consistía en crear subclases intermedias como `WalkingAnimal` y `NonWalkingAnimal` dentro de la jerarquía de `Animal`. Esta opción introduce rigidez: si en el futuro aparece un animal que nada y también camina, como una tortuga, la jerarquía no lo acomoda sin reestructurarse. El segundo enfoque, extraer la capacidad de caminar a una interfaz `Walkable`, es más adecuado porque es composable: cualquier clase puede implementarla independientemente de su posición en la jerarquía, sin comprometer el contrato de `Animal`.

La refactorización convirtió `Animal` en una clase abstracta que únicamente define el método `makeSound()`, ya que este representa un comportamiento válido para cualquier animal. La capacidad de caminar se trasladó a la interfaz `Walkable`, la cual es implementada por `Dog`, pero no por `Fish`. Gracias a este cambio, cualquier objeto de tipo `Animal` puede utilizar `makeSound()` sin problemas, mientras que solo los animales que realmente pueden caminar implementan `Walkable`.

Además, en `Main` se utilizó el tipo `Dog` en lugar de `Animal`, ya que el programa necesita acceder al método `walk()`. Esta decisión evita realizar conversiones innecesarias (`cast`) desde `Animal` a `Walkable`, las cuales podrían volver a introducir errores similares a los del diseño original. En otras palabras, el principio de sustitución de Liskov no impide utilizar tipos concretos cuando es necesario; lo importante es que las subclases respeten el comportamiento esperado de su clase base.

### Problemas resueltos

Antes de la refactorización, el programa podía fallar en tiempo de ejecución si se trataba un `Fish` como un `Animal` y se llamaba al método `walk()`. Este tipo de errores es difícil de detectar porque el compilador no los identifica y solo aparecen cuando el programa se ejecuta. Con la nueva estructura, el compilador garantiza que `walk()` solo pueda invocarse en objetos que implementan `Walkable`, evitando este problema desde el diseño.

Adicionalmente, el diseño resultante es más honesto: la jerarquía de clases refleja con precisión las capacidades reales de cada animal, lo que hace el sistema más fácil de entender, extender y mantener sin sorpresas.

<img width="1920" height="1020" alt="Captura de pantalla 2026-06-28 205101" src="https://github.com/user-attachments/assets/ea31eb24-bb39-4828-93f7-1734d7934517" />


## Principio 4: Interface Segregation Principle (ISP)

### Aplicación del principio

El código base definía una interfaz `Device` con tres métodos: `turnOn()`, `turnOff()` y `charge()`. Esta interfaz asumía que todo dispositivo es recargable, lo cual no es cierto para todos los casos del dominio. Como consecuencia, `DisposableCamera` se veía forzada a implementar `charge()` lanzando una excepción `UnsupportedOperationException`, ya que no tiene capacidad de recarga. Esto es una violación directa del ISP: la clase depende de un método que no puede cumplir, y cualquier código que invoque `charge()` sobre un `Device` genérico puede fallar en tiempo de ejecución sin advertencia del compilador.

Al plantear la refactorización, se analizó cómo reorganizar la interfaz sin dividirla más de lo necesario. Una alternativa era separar cada método en una interfaz distinta, pero esto habría complicado el diseño sin aportar beneficios reales. En este caso, encender y apagar forman parte del comportamiento básico de cualquier dispositivo, por lo que tiene sentido mantener ambos métodos en la interfaz `Device`. En cambio, la capacidad de recarga no está presente en todos los dispositivos, por lo que se trasladó a una interfaz independiente, `Chargeable`. Así, `Phone` implementa ambas interfaces, mientras que `DisposableCamera` solo implementa `Device`, evitando que una clase tenga que definir métodos que no le corresponden. Además, este diseño facilita la incorporación de nuevos dispositivos con distintas capacidades sin modificar las interfaces existentes.

Con esta estructura, `Phone` implementa tanto `Device` como `Chargeable` porque cumple ambos contratos, mientras que `DisposableCamera` solo implementa `Device`. Ninguna clase está obligada a declarar comportamiento que no puede sostener. Además, si en el futuro se agrega un dispositivo como una batería externa que se carga pero no enciende ni apaga de forma convencional, el diseño lo acomoda sin modificar las interfaces existentes, solo implementando `Chargeable`.

### Problemas resueltos

El principal problema del diseño original era que `DisposableCamera` debía implementar `charge()` aunque no pudiera recargarse, lo que provocaba una excepción si ese método era llamado. Con la separación de interfaces, este problema desaparece porque solo los dispositivos recargables implementan `Chargeable`. Además, el código queda menos acoplado: si en el futuro se agregan o modifican métodos relacionados con la recarga, únicamente deberán actualizarse las clases que implementan esa interfaz. Esto hace que el sistema sea más fácil de mantener y evita implementar métodos vacíos o con excepciones que pueden generar errores durante la ejecución.

<img width="1920" height="1020" alt="Captura de pantalla 2026-06-28 211622" src="https://github.com/user-attachments/assets/0156a408-b6aa-4d35-b5b2-6bd5b92dbf66" />

## Principio 5: Dependency Inversion Principle (DIP)

### Aplicación del principio

El código base presentaba una clase `PaymentProcessor` que instanciaba directamente un objeto `CreditCardPayment` dentro de su constructor. Esta dependencia concreta significaba que `PaymentProcessor`, un módulo de alto nivel encargado de orquestar el proceso de pago, conocía y dependía de los detalles internos de un módulo de bajo nivel específico. Cualquier intento de usar un método de pago distinto requería abrir y modificar `PaymentProcessor`, violando tanto el DIP como el OCP de forma simultánea.

Durante la refactorización se evaluó si era más conveniente utilizar una interfaz o una clase abstracta para representar los métodos de pago. Como cada forma de pago funciona de manera independiente y no comparte lógica con las demás, una clase abstracta no ofrecía ventajas reales. Por ello, se optó por crear la interfaz `PaymentMethod` con el método `processPayment`, ya que define un contrato común sin obligar a las implementaciones a formar parte de una jerarquía de herencia.

También se analizó cómo inyectar la dependencia en `PaymentProcessor`. La inyección mediante un setter permitía cambiar el método de pago durante la ejecución, pero esa flexibilidad no era necesaria y hacía que el objeto fuera mutable. Otra posibilidad era utilizar un *factory*, aunque para este proyecto habría añadido complejidad sin aportar un beneficio claro. Finalmente, se eligió la inyección por constructor porque deja la dependencia definida desde la creación del objeto, evita modificaciones posteriores y facilita las pruebas unitarias al permitir pasar cualquier implementación de `PaymentMethod` sin cambiar el código de `PaymentProcessor`.

Con esta estructura, `PaymentProcessor` depende únicamente de la abstracción `PaymentMethod` y no tiene conocimiento de si el pago se realiza con tarjeta, PayPal o criptomoneda. Agregar un nuevo método de pago como transferencia bancaria implica únicamente crear una nueva clase que implemente `PaymentMethod` e inyectarla al construir el procesador, sin tocar ninguna clase existente.

### Problemas resueltos

El problema central era el acoplamiento rígido entre `PaymentProcessor` y `CreditCardPayment`, que hacía imposible extender el sistema sin modificar código ya funcionando. En términos prácticos, esto significaba que cada nuevo método de pago representaba un riesgo de regresión sobre la funcionalidad de tarjeta de crédito ya probada. Con la refactorización, cada implementación de `PaymentMethod` es completamente independiente y puede probarse de forma aislada. `PaymentProcessor` puede validarse una sola vez contra la abstracción y funciona correctamente con cualquier implementación presente o futura, lo que elimina la necesidad de retesting ante cada extensión del sistema y reduce significativamente el costo de mantenimiento a medida que el catálogo de métodos de pago crece.

<img width="1920" height="1020" alt="Captura de pantalla 2026-06-28 214843" src="https://github.com/user-attachments/assets/88009ac0-baf3-4da2-9be6-bbf017725d12" />
