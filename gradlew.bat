@ECHO OFF
WHERE gradle >NUL 2>NUL
IF %ERRORLEVEL% NEQ 0 (
  ECHO gradle is not installed in PATH
  EXIT /B 1
)
gradle %*
