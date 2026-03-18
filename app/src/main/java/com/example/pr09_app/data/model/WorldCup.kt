package com.example.pr09_app.data.model

/**
 * Data class para representar un Mundial de Fútbol
 * 
 * Campos alineados con el dataset FIFA World Cup de Kaggle
 * @property year Año del mundial
 * @property country País anfitrión
 * @property winner Equipo ganador
 * @property runnerup Subcampeón
 * @property third Tercer lugar
 * @property fourth Cuarto lugar
 * @property goals Goles totales del torneo
 * @property qualified Equipos que participaron
 * @property matches Cantidad de partidos jugados
 * @property attendance Asistencia total
 */
data class WorldCup(
    val year: Int,
    val country: String,
    val winner: String,
    val runnerup: String,
    val third: String,
    val fourth: String,
    val goals: Int,
    val qualified: Int,
    val matches: Int,
    val attendance: String
)

/**
 * Data class genérico para capturar respuestas GET de la API
 * 
 * Patrón estándar de respuesta JSON:
 * {
 *   "status": "ok" | "error",
 *   "type": "worldcups",
 *   "data": [...],
 *   "error": null | "mensaje de error"
 * }
 * 
 * Es parametrizable con genéricos para adaptarse a diferentes tipos de datos
 * Ejemplo: GetResponse<List<WorldCup>>
 */
data class GetResponse<T>(
    val status: String,
    val type: String? = null,
    val data: T? = null,
    val error: String? = null
)

