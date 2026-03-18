/**
 * PR09 - Backend Apps Script API
 * Dataset: FIFA World Cup (Kaggle)
 */

// 1. OBTENER LA API KEY DESDE LAS PROPIEDADES DEL PROYECTO (Configuración > Propiedades)
const API_KEY_VAL = PropertiesService.getScriptProperties().getProperty("API_KEY");

function normalizeHeader(h) {
  // Convierte "Runner-up" / "Runners-Up" / "Runner up" -> "runnerup"
  return h
    .toString()
    .toLowerCase()
    .replace(/[^a-z0-9]/g, "");
}

function createJsonResponse(data) {
  return ContentService.createTextOutput(JSON.stringify(data))
    .setMimeType(ContentService.MimeType.JSON);
}

function doGet(e) {
  try {
    const key = e.parameter.key;
    const action = e.parameter.action;

    // Validación de seguridad
    if (key !== API_KEY_VAL) {
      return createJsonResponse({ error: "Unauthorized: API KEY incorrecta" });
    }

    // OJO: usando sheet activa. Si queréis hacerlo 100% fijo, cambiamos por getSheetByName("...").
    const sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();
    const data = sheet.getDataRange().getValues();
    const headers = data[0];
    const rows = data.slice(1);

    // ENDPOINT 1: Listar todos los mundiales
    if (action === "listAll") {
      const result = rows.map((row) => {
        let obj = {};
        headers.forEach((h, i) => obj[normalizeHeader(h)] = row[i]);
        return obj;
      });
      return createJsonResponse(result);
    }

    // ENDPOINT 2: Buscar por País Anfitrión (Filtro)
    if (action === "findByCountry") {
      const countrySearch = (e.parameter.country || "").toString();
      const filtered = rows
        .filter((row) =>
          row[1].toString().toLowerCase().includes(countrySearch.toLowerCase())
        )
        .map((row) => {
          let obj = {};
          headers.forEach((h, i) => obj[normalizeHeader(h)] = row[i]);
          return obj;
        });
      return createJsonResponse(filtered);
    }

    return createJsonResponse({ error: "Acción no válida" });
  } catch (error) {
    return createJsonResponse({ error: error.toString() });
  }
}

function doPost(e) {
  try {
    const postData = JSON.parse(e.postData.contents);
    const key = postData.key;

    // Validación de seguridad
    if (key !== API_KEY_VAL) {
      return createJsonResponse({ error: "Unauthorized" });
    }

    const sheet = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();

    // ENDPOINT 3: Insertar nuevo registro al final
    // Inserta SIEMPRE las 10 columnas del header:
    // [year, country, winner, runnerup, third, fourth, goals, qualified, matches, attendance]
    sheet.appendRow([
      postData.year ?? "",
      postData.country ?? "",
      postData.winner ?? "",
      postData.runnerup ?? "",
      postData.third ?? "",
      postData.fourth ?? "",
      postData.goals ?? 0,
      postData.qualified ?? 0,
      postData.matches ?? 0,
      postData.attendance ?? "",
    ]);

    return createJsonResponse({
      status: "Success",
      message: "Registro añadido correctamente",
    });
  } catch (error) {
    return createJsonResponse({ error: error.toString() });
  }
}

