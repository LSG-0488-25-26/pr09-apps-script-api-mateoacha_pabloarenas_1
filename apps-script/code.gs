/**
 * PR09 - Backend Apps Script API
 * Dataset: FIFA World Cup (Kaggle)
 * 
 * @author Mateo & Pablo
 * @version 1.0
 * 
 * Endpoints:
 * - GET ?type=worldcups → Listar todos los mundiales
 * - GET ?type=worldcups&country=PAIS → Buscar por país anfitrión
 * - POST con body {year, country, winner, ...} → Insertar new record
 */

// ==================== CONFIGURACIÓN GLOBAL ====================
// API_KEY se obtiene desde Configuración > Propiedades del script
const API_KEY = PropertiesService.getScriptProperties().getProperty("API_KEY");
const DEBUG = false; // Cambiar a true para debug

// Configuración de la hoja de datos
const SHEET_NAMES = ["PR09-Dataset", "Hoja 1", "FIFA"];
const COLUMN_COUNT = 10;

// ==================== FUNCIONES UTILITARIAS ====================

/**
 * Normaliza headers: "Runner-up" → "runnerup", "Goals for" → "goalsfor"
 */
function normalizeHeader(h) {
  return h
    .toString()
    .toLowerCase()
    .replace(/[^a-z0-9]/g, "");
}

/**
 * Retorna un JSON response con formato estándar
 */
function jsonResponse(data) {
  return ContentService.createTextOutput(JSON.stringify(data))
    .setMimeType(ContentService.MimeType.JSON);
}

/**
 * Obtiene la hoja activa del documento
 */
function getActiveDataSheet() {
  const ss = SpreadsheetApp.getActiveSpreadsheet();
  for (let sheetName of SHEET_NAMES) {
    const sheet = ss.getSheetByName(sheetName);
    if (sheet) return sheet;
  }
  return ss.getActiveSheet();
}

/**
 * Obtiene todas las filas como objetos JSON
 */
function getAllRows() {
  const sheet = getActiveDataSheet();
  const data = sheet.getDataRange().getValues();
  
  if (data.length < 1) {
    return [];
  }
  
  const headers = data[0];
  const rows = data.slice(1);
  
  return rows.map((row) => {
    let obj = {};
    headers.forEach((h, i) => {
      obj[normalizeHeader(h)] = row[i];
    });
    return obj;
  });
}

/**
 * Valida la API_KEY
 */
function validateApiKey(apiKey) {
  if (!apiKey || apiKey.trim() !== API_KEY) {
    throw new Error("Unauthorized: API KEY incorrecta");
  }
}

// ==================== ENDPOINT: doGet (GET requests) ====================

/**
 * Maneja las peticiones GET
 * Parámetros:
 *   - apiKey: API_KEY para validación
 *   - type: "worldcups" (default), "worldcups" → lista todos
 *   - country: STRING (opcional) → filtra por país si type="worldcups"
 */
function doGet(e) {
  try {
    const apiKey = (e.parameter.apiKey || e.parameter.key || "").toString().trim();
    const type = (e.parameter.type || "worldcups").toString().trim().toLowerCase();
    
    // Validar API key
    validateApiKey(apiKey);
    
    const action = (e.parameter.action || "").toString().trim();
    const countryFilter = (e.parameter.country || "").toString().trim().toLowerCase();
    let allRows = getAllRows();

    // Compatibilidad: si se usa ?action=listAll / ?action=findByCountry (navegador/antiguo)
    if (action) {
      if (action === "listAll") {
        return jsonResponse({
          status: "ok",
          type: "worldcups",
          data: allRows,
          count: allRows.length,
        });
      }

      if (action === "findByCountry") {
        if (countryFilter) {
          allRows = allRows.filter((row) =>
            (row.country || "").toString().toLowerCase().includes(countryFilter)
          );
        } else {
          allRows = [];
        }
        return jsonResponse({
          status: "ok",
          type: "worldcups",
          data: allRows,
          count: allRows.length,
        });
      }
    }

    // ENDPOINT 1/2: Listar o filtrar con type=worldcups
    if (type === "worldcups") {
      // Filtrar por país si se proporciona
      if (countryFilter) {
        allRows = allRows.filter((row) =>
          (row.country || "").toString().toLowerCase().includes(countryFilter)
        );
      }

      return jsonResponse({
        status: "ok",
        type: "worldcups",
        data: allRows,
        count: allRows.length,
      });
    }
    
    // Tipo no reconocido
    return jsonResponse({
      status: "error",
      error: "Tipo de consulta inválido. Usa type=worldcups"
    });
    
  } catch (error) {
    if (DEBUG) Logger.log("doGet Error: " + error.toString());
    return jsonResponse({
      status: "error",
      error: error.toString()
    });
  }
}

// ==================== ENDPOINT: doPost (POST requests) ====================

/**
 * Maneja las peticiones POST
 * Body esperado:
 * {
 *   "apiKey": "...",
 *   "year": 2022,
 *   "country": "Qatar",
 *   "winner": "Argentina",
 *   "runnerup": "France",
 *   "third": "Morocco",
 *   "fourth": "Croatia",
 *   "goals": 172,
 *   "qualified": 32,
 *   "matches": 64,
 *   "attendance": "3000000"
 * }
 */
function doPost(e) {
  try {
    const postData = JSON.parse(e.postData.contents || "{}");
    const apiKey = (postData.apiKey || postData.key || "").toString().trim();
    
    // Validar API key
    validateApiKey(apiKey);
    
    // Validar campos requeridos
    if (!postData.year || !postData.country || !postData.winner) {
      throw new Error("Campos requeridos faltantes: year, country, winner");
    }
    
    if (isNaN(postData.year)) {
      throw new Error("El campo 'year' debe ser un número");
    }
    
    // ENDPOINT 3: Insertar nuevo registro
    const sheet = getActiveDataSheet();
    
    const newRow = [
      postData.year || "",
      postData.country || "",
      postData.winner || "",
      postData.runnerup || "",
      postData.third || "",
      postData.fourth || "",
      postData.goals || 0,
      postData.qualified || 0,
      postData.matches || 0,
      postData.attendance || ""
    ];
    
    // Validar que tenemos 10 columnas
    if (newRow.length !== COLUMN_COUNT) {
      throw new Error(`Se esperaban ${COLUMN_COUNT} campos, se recibieron ${newRow.length}`);
    }
    
    sheet.appendRow(newRow);
    
    if (DEBUG) Logger.log("Registro insertado: " + JSON.stringify(newRow));
    
    return jsonResponse({
      status: "ok",
      message: "Registro añadido correctamente",
      data: { inserted: true, year: postData.year, country: postData.country }
    });
    
  } catch (error) {
    if (DEBUG) Logger.log("doPost Error: " + error.toString());
    return jsonResponse({
      status: "error",
      error: error.toString()
    });
  }
}

