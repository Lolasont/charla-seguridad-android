package com.example.model

val OPCIONES_EQUIPO_APOYO = listOf(
    "Red eléctrica trifásica",
    "Red eléctrica monofásica",
    "Fuentes de poder bajo y alto voltaje",
    "Cable de Alimentación Eléctrica",
    "Bornes de Conexión",
    "Dispositivos de maniobra eléctrica",
    "Tablero o Gabinete eléctrico",
    "Cable Banana/Caimán",
    "Transformador Eléctrico",
    "Variador de Frecuencia",
    "Partidor Suave",
    "Temporizador",
    "Motor eléctrico",
    "Instrumento de medida",
    "Herramientas mecánicas",
    "Máquinas de presión neumática",
    "Máquinas de presión hidráulica",
    "Herramientas manuales de golpe",
    "Taladro eléctrico",
    "Pistola de Calor Industrial",
    "Morsa de Banco Giratoria Con Yunque",
    "Sierra Manual",
    "Estación de Soldadura",
    "Esmeril angular"
)

val OPCIONES_NIVEL_TECNICO = listOf(
    "Inicial",
    "Intermedio",
    "Avanzado"
)

val OPCIONES_EPP = listOf(
    "Guante dieléctrico",
    "Guante de cabritilla",
    "Guante Hy-Flex",
    "Guante de nitrilo",
    "Zapatos de Seguridad dieléctrico",
    "Zapatos de Seguridad con puntera",
    "Delantal Blanco",
    "Antiparra de Seguridad",
    "Pantalla facial",
    "Casco dieléctrico",
    "Casco de seguridad",
    "Chaleco Reflectante",
    "Cubre Calzado",
    "Protector Solar",
    "Ropa de PVC",
    "Overol de trabajo",
    "Arnés de Seguridad con dos Colas",
    "Absorbedor de caídas",
    "Gorro legionario para casco",
    "Protector Auditivo tipo fono",
    "Tapones auditivos",
    "Lápiz detector de voltaje",
    "Máscara con filtro para gases/vapores"
)

val OPCIONES_COMANDO_VOZ = listOf(
    "Alto",
    "Atención",
    "Precaución"
)

data class ParCondicionAmbiental(
    val id: String,
    val opcion1: String,
    val opcion2: String
)

val CONDICIONES_AMBIENTALES_PARES = listOf(
    ParCondicionAmbiental("luz", "Falta de Luz", "Exceso de Luz"),
    ParCondicionAmbiental("temp", "Calor", "Frío extremo"),
    ParCondicionAmbiental("rad", "Radiación UV", "Humedad alta"),
    ParCondicionAmbiental("quim", "Materiales inflamables", "Gases / Vapores"),
    ParCondicionAmbiental("acust", "Ruido", "Vibraciones"),
    ParCondicionAmbiental("espacio", "Superficies resbaladizas", "Sin Ventilación")
)

val OPCIONES_CONDICIONES_AMBIENTALES = listOf(
    "Falta de Luz",
    "Calor",
    "Radiación UV",
    "Materiales inflamables",
    "Ruido",
    "Gases",
    "Vibraciones",
    "Superficies resbaladizas",
    "Humedad",
    "Polvo/Humo",
    "Sin Ventilación"
)

val MEDIDAS_CONTROL_PROPUESTAS = listOf(
    "Inspección previa de herramientas y equipos",
    "Uso obligatorio del EPP especificado",
    "Delimitación y señalización del área de trabajo",
    "Verificación de ausencia de tensión (5 reglas de oro)",
    "Orden y aseo en el puesto de trabajo",
    "Ventilación adecuada del recinto",
    "Procedimiento de trabajo seguro conocido por ejecutantes",
    "Disponibilidad y acceso a extintor de incendios"
)

val OPCIONES_RIESGOS = listOf(
    "Choque eléctrico",
    "Arco eléctrico",
    "Incendio",
    "Ruido",
    "Explosión",
    "Corte",
    "Tropiezos",
    "Contacto con partes activas",
    "Quemaduras",
    "Fallas en aislamiento",
    "Equipos defectuosos",
    "Sobrecargas",
    "Exposición al polvo",
    "Desniveles",
    "Caída de materiales",
    "Trabajos en altura",
    "Intoxicación",
    "Radiación electromagnética",
    "Caída igual o distinto nivel",
    "Proyección de partículas",
    "Espacio confinado",
    "Golpeado por/contra/con",
    "Sustancias tóxicas",
    "Ergonómico",
    "Radiación láser",
    "Otros"
)
