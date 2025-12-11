# Documentación del Frontend - Senior Assist

Esta documentación detalla la arquitectura y componentes del frontend de la aplicación Senior Assist.

## 1. Punto de Entrada: `MainActivity.kt`

La `MainActivity` es el punto de entrada de la aplicación. Su responsabilidad principal es:

-   **Solicitar permisos:** Pide los permisos necesarios para la aplicación, como `RECORD_AUDIO`, `POST_NOTIFICATIONS` y `SCHEDULE_EXACT_ALARM`.
-   **Gestionar el estado de registro:** Comprueba si el usuario está registrado utilizando `CredentialsManager`.
    -   Si el usuario está registrado, muestra la `MainScreen`.
    -   Si no, muestra la `RegistrationScreen`.

## 2. Pantalla Principal y Navegación: `MainScreen.kt`

La `MainScreen` es la pantalla principal de la aplicación después de que el usuario se ha registrado.

-   **Estructura:** Utiliza un `Scaffold` para organizar la pantalla en una barra superior (Top Bar), contenido principal, una barra de navegación inferior (Bottom Bar) y un botón de acción flotante (Floating Action Button).
-   **Navegación:** Emplea un `NavHost` con una `NavigationBar` para permitir al usuario cambiar entre las diferentes secciones de la aplicación:
    -   **Voz (`AssistantScreen`):** La pantalla inicial, que proporciona interacción por voz.
    -   **Chat (`ChatScreen`):** Una interfaz de chat de texto con el asistente.
    -   **Recordatorios (`RemindersScreen`):** Permite al usuario gestionar sus recordatorios.
    -   **Perfil (`HealthProfileScreen`):** Muestra el perfil de salud del usuario.
-   **Banner de Emergencia:** Muestra un banner en la parte superior de la pantalla si se activa una emergencia.
-   **Botón de Emergencia:** Un `FloatingActionButton` visible en la pantalla de Voz que permite al usuario activar manualmente una llamada de emergencia.

## 3. Pantallas de Funcionalidades

### 3.1. Asistente de Voz: `AssistantScreen.kt`

Esta pantalla es la interfaz principal para la interacción por voz.

-   **Estado de la Interfaz:** La interfaz cambia según el `VoiceUiState` para reflejar el estado actual:
    -   `RESPONDING`: El asistente está esperando un comando de voz.
    -   `LISTENING`: El asistente está escuchando al usuario.
    -   `THINKING`: El asistente está procesando la información.
-   **Visualización:**
    -   Muestra el texto transcrito o la respuesta del asistente.
    -   Utiliza una animación Lottie para indicar visualmente el estado del asistente.
-   **Interacción:** El usuario puede tocar la pantalla para iniciar el reconocimiento de voz cuando el estado es `RESPONDING`.

### 3.2. Chat de Texto: `ChatScreen.kt`

Proporciona una interfaz de chat de texto tradicional para interactuar con el asistente.

-   **Visualización de Mensajes:** Muestra una lista de mensajes entre el usuario y el asistente en `MessageBubble`s.
-   **Entrada de Texto:** Un `OutlinedTextField` en la parte inferior de la pantalla permite al usuario escribir y enviar mensajes.
-   **Reproducción de Mensajes:** Los mensajes del asistente tienen un botón para reproducir el texto en voz alta.

### 3.3. Recordatorios: `RemindersScreen.kt`

Permite a los usuarios gestionar sus recordatorios de medicamentos, citas, etc.

-   **Visualización de Recordatorios:** Los recordatorios se agrupan por fecha y se muestran en tarjetas (`ReminderCard`).
-   **Acciones:**
    -   **Crear:** Un `FloatingActionButton` abre un diálogo (`AddReminderDialog`) para crear nuevos recordatorios.
    -   **Actualizar:** Los usuarios pueden marcar los recordatorios como completados.
    -   **Eliminar:** Los usuarios pueden eliminar recordatorios.
-   **Diálogo de Añadir Recordatorio:** Un diálogo modal permite al usuario introducir un título, fecha y hora para un nuevo recordatorio.

### 3.4. Perfil de Salud: `HealthProfileScreen.kt`

Esta pantalla permite al usuario ver y gestionar su información de salud.

-   **Visualización:** La información de salud se muestra en grupos de "chips" (`HealthChipGroup`) para:
    -   Notas Médicas
    -   Condiciones de Salud
    -   Medicamentos Actuales
-   **Añadir y Eliminar Datos:**
    -   Un campo de texto en la parte superior permite añadir nueva información.
    -   Los "chips" de filtro permiten al usuario seleccionar la categoría del nuevo dato.
    -   Cada "chip" tiene un botón para eliminar el dato correspondiente.
-   **ViewModel:** La lógica para añadir, eliminar y mostrar los datos de salud se gestiona en el `HealthProfileViewModel`.
