# Senior Assist - Asistente Virtual (Frontend Android)

Este es el repositorio para el frontend de la aplicación Android "Senior Assist", un asistente virtual diseñado para ayudar a los adultos mayores en su día a día. La aplicación está construida de forma nativa para Android utilizando tecnologías modernas.

## 📝 Descripción General

Senior Assist tiene como objetivo proporcionar una interfaz simple y accesible para que los adultos mayores puedan gestionar su salud, recibir recordatorios importantes y comunicarse con un asistente virtual para obtener ayuda.

## ✨ Características Principales

*   **Asistente Virtual:** Un chatbot interactivo (usando Lottie para las animaciones) para responder preguntas y asistir al usuario.
*   **Gestión de Perfil de Salud:** Permite al usuario registrar y consultar su información de salud.
*   **Recordatorios de Medicamentos:** Sistema de notificaciones y alarmas para asegurar la toma de medicamentos a tiempo.
*   **Registro de Usuarios:** Sistema para crear y gestionar cuentas de usuario de forma segura.

## 🛠️ Tecnologías Utilizadas

El proyecto está desarrollado completamente en **Kotlin** y sigue las guías de arquitectura recomendadas por Google.

*   **Interfaz de Usuario (UI):**
    *   **Jetpack Compose:** Framework de UI declarativo y moderno para Android.
    *   **Material 3:** Para componentes de diseño visual actualizados.
    *   **Navigation Compose:** Para la navegación entre pantallas de forma fluida.
    *   **Lottie Compose:** Para la implementación de animaciones complejas y atractivas.
*   **Arquitectura:**
    *   **MVVM (Model-View-ViewModel):** Patrón de diseño para separar la lógica de la UI y mejorar la mantenibilidad.
    *   **Lifecycle/ViewModel Compose:** Para gestionar el estado de la UI de forma consciente del ciclo de vida de los componentes.
*   **Red (Networking):**
    *   **Retrofit & Gson:** Para realizar peticiones a la API REST del backend y procesar las respuestas JSON.
    *   **OkHttp Logging Interceptor:** Para depurar y monitorizar las llamadas a la red durante el desarrollo.
*   **Asincronía:**
    *   **Coroutines de Kotlin:** Para manejar operaciones en segundo plano (como llamadas a la red) de forma eficiente y sin bloquear la UI.

## 🚀 Instalación y Ejecución

1.  **Clonar el repositorio:**
    ```bash
    git clone https://github.com/Skinnyrafff/SeniorAssist-Frontend.git
    ```
2.  **Abrir en Android Studio:**
    *   Abre la versión más reciente de Android Studio.
    *   Selecciona "Open" y navega hasta el directorio donde clonaste el repositorio.
3.  **Sincronizar Gradle:**
    *   Android Studio sincronizará el proyecto con los archivos de Gradle automáticamente.
4.  **Ejecutar la aplicación:**
    *   Selecciona un emulador o conecta un dispositivo físico.
    *   Presiona el botón "Run 'app'" (▶️) para compilar y ejecutar la aplicación.
