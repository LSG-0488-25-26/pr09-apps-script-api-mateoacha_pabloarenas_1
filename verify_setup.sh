#!/bin/bash
# =============================================================================
# PR09 - Quick Setup & Verification Script
# =============================================================================
# Este script verifica que todo esté listo antes de compilar/ejecutar

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║  PR09 - Verificación Rápida de Setup                          ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""

# Estados
CHECKS_PASSED=0
CHECKS_FAILED=0

# Función para verificar
check_file() {
    echo -n "Verificando $1... "
    if [ -f "$1" ]; then
        echo "✓ OK"
        ((CHECKS_PASSED++))
        return 0
    else
        echo "✗ FALTA"
        ((CHECKS_FAILED++))
        return 1
    fi
}

check_dir() {
    echo -n "Verificando carpeta $1... "
    if [ -d "$1" ]; then
        echo "✓ OK"
        ((CHECKS_PASSED++))
        return 0
    else
        echo "✗ FALTA"
        ((CHECKS_FAILED++))
        return 1
    fi
}

check_contains() {
    echo -n "Buscando '$2' en $1... "
    if grep -q "$2" "$1" 2>/dev/null; then
        echo "✓ ENCONTRADO"
        ((CHECKS_PASSED++))
        return 0
    else
        echo "✗ NO ENCONTRADO"
        ((CHECKS_FAILED++))
        return 1
    fi
}

# =============================================================================
# VERIFICACIONES
# =============================================================================

echo ""
echo "📁 ESTRUCTURA DEL PROYECTO"
echo "─────────────────────────────────────────────────────────────────"
check_file "apps-script/code.gs"
check_file "app/build.gradle.kts"
check_file "app/secrets.defaults.properties"
check_dir "app/src/main/java/com/example/pr09_app"
check_file "README.md"

echo ""
echo "🔧 ARCHIVOS ANDROID CRÍTICOS"
echo "─────────────────────────────────────────────────────────────────"
check_file "app/src/main/java/com/example/pr09_app/MainActivity.kt"
check_file "app/src/main/java/com/example/pr09_app/data/model/WorldCup.kt"
check_file "app/src/main/java/com/example/pr09_app/data/network/ApiService.kt"
check_file "app/src/main/java/com/example/pr09_app/data/network/ApiClient.kt"
check_file "app/src/main/java/com/example/pr09_app/ui/dataset/DatasetListScreen.kt"
check_file "app/src/main/java/com/example/pr09_app/ui/dataset/DatasetViewModel.kt"

echo ""
echo "📝 VERIFICACIÓN DE CONTENIDO"
echo "─────────────────────────────────────────────────────────────────"
check_contains "apps-script/code.gs" "const API_KEY = PropertiesService"
check_contains "apps-script/code.gs" "function doGet"
check_contains "apps-script/code.gs" "function doPost"
check_contains "app/build.gradle.kts" "buildConfigField"
check_contains "README.md" "3 Endpoints"
check_contains ".gitignore" "secrets.properties"

echo ""
echo "⚙️ CONFIGURACIÓN"
echo "─────────────────────────────────────────────────────────────────"
if [ -f "secrets.properties" ]; then
    echo -n "secrets.properties DETECTADO (archivo real)... "
    echo "⚠️  AVISO: Asegúrate de NO commitear este archivo"
    ((CHECKS_PASSED++))
else
    echo -n "secrets.properties (archivo real) NO encontrado... "
    echo "ℹ️  OK (será creado en próximo paso)"
    ((CHECKS_PASSED++))
fi

echo ""
echo "🔐 SEGURIDAD"
echo "─────────────────────────────────────────────────────────────────"
if grep -r "SK_" app/src --include="*.kt" 2>/dev/null | grep -q "."; then
    echo "✗ ALERTA: API_KEY encontrada en código Kotlin"
    ((CHECKS_FAILED++))
else
    echo "✓ OK: API_KEY no está hardcoded en Kotlin"
    ((CHECKS_PASSED++))
fi

if grep -q "secrets.properties" ".gitignore" 2>/dev/null; then
    echo "✓ OK: .gitignore excluye secrets.properties"
    ((CHECKS_PASSED++))
else
    echo "⚠️  ADVERTENCIA: .gitignore no excluye secrets.properties"
    ((CHECKS_FAILED++))
fi

# =============================================================================
# RESUMEN
# =============================================================================

echo ""
echo "═════════════════════════════════════════════════════════════════"
echo "📊 RESUMEN DE VERIFICACIÓN"
echo "═════════════════════════════════════════════════════════════════"
echo "✓ Verificaciones pasadas: $CHECKS_PASSED"
echo "✗ Verificaciones fallidas: $CHECKS_FAILED"
echo ""

if [ $CHECKS_FAILED -eq 0 ]; then
    echo "✅ PROYECTO LISTO PARA SETUP"
    echo ""
    echo "PRÓXIMOS PASOS:"
    echo "1. Copiar apps-script/code.gs al Apps Script de Google"
    echo "2. Crear secrets.properties en la raíz del proyecto con:"
    echo "   BASE_URL=https://script.google.com/macros/d/{YOUR_ID}/usercontent"
    echo "   API_KEY=tu_api_key_aqui"
    echo "3. Ejecutar: ./gradlew clean build"
    echo "4. Ejecutar app en Android Studio"
else
    echo "⚠️  PROBLEMAS DETECTADOS"
    echo ""
    echo "Verifica los archivos faltantes o incompletos"
    echo "Consulta README.md para más detalles"
fi

echo ""
echo "═════════════════════════════════════════════════════════════════"
