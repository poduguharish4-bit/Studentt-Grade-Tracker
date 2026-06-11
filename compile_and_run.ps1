# Compilation and execution script for Student Grade Tracker (PowerShell)

# Create build directory if it doesn't exist
if (-not (Test-Path -Path "build")) {
    New-Item -ItemType Directory -Path "build" | Out-Null
}

Write-Host "Compiling Java source files..." -ForegroundColor Cyan

# Compile source files recursively using sourcepath and Main.java entry point
javac -d build -sourcepath src src/com/codealpha/gradetracker/Main.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "Compilation successful!" -ForegroundColor Green

# Determine arguments to pass to the Java program
$argsToPass = @()
if ($args.Count -gt 0) {
    $argsToPass = $args
}

Write-Host "Launching Student Grade Tracker..." -ForegroundColor Cyan
java -cp build com.codealpha.gradetracker.Main $argsToPass
