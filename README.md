# ASOftp

# Requisitos
  - Java JDK 11
  - OpenSUSE 15.4
  - Imagen ISO de OpenSUSE con los instaladores en la maquina virtual

# Descripción
  Aplicación de escritorio desarrollada en JAVA para configurar vsFTPd en Linux

# Funcionalidad
  - Instalar vsFTPd de manera online como manera local(iso openSUSE)
  - Verificar si vsFTPd está instalado
  - Verificar el estado del servicio (Corriendo, Detenido)
  - Cambiar el estado del servicio (Iniciar/Parar/Reiniciar)
  - Leer la configuración del archivo .conf (solamente variables no comentadas)
  - Cargar opciones YES/NO y campos de TEXTO para editar
  - Guardar los cambios de configuración del servicio

# Instrucciones para correr el archivo .JAR
  1. Ubicar el archivo FTP.jar
  2. Abrir terminal y escribir el siguiente comando:
     java -jar FTP.jar
     Este comando iniciará la aplicación

# Guía De Usuario
Al iniciar la aplicación podremos ver un total de 7 botones y 4 textos de estado
- Sección de Instalación
1. Botón "Instalar FTP desde CD": instala el servicio vsFTPd en la máquina virtual desde la imágen ISO, al hacer click en este se mostrará una ventana para que el usuario ingrese su contraseña de SuperUsuario para que se proceda con la instalación del servicio.
2. Botón "Instalar FTP Online": instala el servicio vsFTPd desde el repositorio princial, al hacer click en este se mostrará una ventana para que el usuario ingrese su contraseña de SuperUsuario para que se proceda con la instalación del servicio.
3. Texto de estado de la instalación:
   3.1. "Estado de la instalación:": La aplicación fue iniciada recientemente y no se interactuó con ninguno de los botones.
   3.2. "Estado: Vsftpd instalado correctamente desde el repositorio local.": Se instaló por medio del Botón "Instalar FTP desde CD"
   3.3. "Estado: Vsftpd instalado correctamente desde el repositorio principal.": Se instaló por medio del Botón "Instalar FTP Online"

- Sección de Estado
4. Botón "Mostrar Configuración": muestra el formulario de configuración en el cual están todas las ociones a configurar mediante selección de opciones o mediante ingreso de texto.
4.1. Botón "Guardar": guarda las configuraciones aplicadas en caso de realizar algun cambio o mantiene las que se tenian y reinicia el servicio.
4.2. Botón "Cancelar": Cierra la ventana de formulario sin cambios.
5. Botón: "Verificar Estado": verifica si vsFTPd está instalado en el sistema, en caso de estar instalado verifica el estado del mismo si está "corriendo" o está "detenido". Los estados se muestran en los siguientes campos.
6. Texto "Estado del servidor FTP:"
6.1. "Estado del servidor FTP:": La aplicación fue iniciada recientemente y no se interactuó con ninguno de los botones.
6.2. "Estado del servidor FTP: Vsftpd está instalado": El servicio se encuentra instalado.
6.3. "Estado del servidor FTP: Vsftpd no está instalado": El servicio no se encuentra instalado.
6.4. "Estado del servidor FTP: Vsftpd está corriendo": El servicio se encuentra instalado y está corriendo.
6.5. "Estado del servidor FTP: Vsftpd no está corriendo": El servicio se encuentra instalado pero está corriendo.

- Sección de cambio de estado
7. Botón "Iniciar FTP": Si el servicio vsFTPd está instalado lo inicia. Requiere contraseña de SuperUsuario.
8. Botón "Reiniciar FTP": Si el servicio vsFTPd está instalado lo reinicia. Requiere contraseña de SuperUsuario.
9. Botón "Detener FTP": Si el servicio vsFTPd está instalado lo detiene. Requiere contraseña de SuperUsuario.
10. Texto "En espera..."
10.1. "En espera...": La aplicación fue iniciada recientemente y no se interactuó con ninguno de los botones.
10.2. "Servicio iniciado con éxito": El botón "Iniciar FTP" ejecutó su tarea con éxito y el servicio está corriendo.
10.3. "Servicio reiniciado con éxito": El botón "Reiniciar FTP" ejecutó su tarea con éxito y el servicio fue detenido y fue iniciado nuevamente.
10.4 "Servicio detenido con éxito": El botón "Detener FTP" ejecutó su tarea con éxito y el servicio está detenido.
   
