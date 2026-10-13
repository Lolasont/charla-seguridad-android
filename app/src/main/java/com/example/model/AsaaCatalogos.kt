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
    "Zapatos de Seguridad dieléctrico",
    "Delantal Blanco (bien abrochado)",
    "Antiparra de Seguridad",
    "Casco dieléctrico",
    "Chaleco Reflectante",
    "Arnés de Seguridad con dos Colas",
    "Absorbedor de caídas",
    "Gorro legionario para casco",
    "Protector Auditivo",
    "Lápiz detector de voltaje",
    "Cubre Calzado",
    "Protector Solar",
    "Ropa de PVC"
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
    "Ruido",
    "Humedad",
    "Calor",
    "Gases",
    "Polvo/Humo",
    "Radiación UV",
    "Vibraciones",
    "Sin Ventilación",
    "Materiales inflamables",
    "Superficies resbaladizas"
)

val MEDIDAS_CONTROL_PROPUESTAS = listOf(
    "1. Verificación de ausencia de tensión",
    "2. Equipo de protección personal dieléctrico",
    "3. Inspección de herramientas y equipos",
    "4. Delimitación y señalización del área de trabajo",
    "5. Ventilación y control de atmósfera de trabajo",
    "6. Protección auditiva y visual según la tarea",
    "7. Manejo manual de cargas y postura de trabajo",
    "8. Otro"
)

val OPCIONES_RIESGOS = listOf(
    "1 Choque eléctrico",
    "2 Ex. Arco eléctrico",
    "3 Incendio",
    "4 Ex. Ruido",
    "5 Explosión",
    "6 CC partes activas",
    "7 Ex. Polvo",
    "8 Ergonómico",
    "9 CC superficies calientes",
    "10 Atrapamiento por partes móviles",
    "11 Ex. gases o vapores tóxicos",
    "12 Ex. sustancias tóxicas",
    "13 Descarga electrostática (ESD)",
    "14 Ex. gases o vapores tóxicos",
    "15 Ex. sustancias tóxicas",
    "16 Caída igual o distinto nivel",
    "17 Proyección de partículas",
    "18 Ex. atmósferas peligrosas",
    "19 Golpeado por/Contra/Con",
    "20 CC Elementos cortopunzantes",
    "21 Ex. Radiación electromagnética",
    "22 Ex. Radiación láser",
    "23 Otros",
    "27 Otros"
)
