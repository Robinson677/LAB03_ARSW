# Assignment — Lab_Inmortals-Sync_Java21 (ARSW Lab #3)

**Objetivo:** Practicar sincronización mínima, evitar deadlocks y suspensión cooperativa en un sistema de hilos tipo “inmortales”.

## Pasos sugeridos
1) Correr con `-Dfight=naive` y registrar problemas (salud negativa, inconsistencias).
2) Corregir con **orden total** (o `tryLock(timeout)`) y comparar resultados.
3) Implementar **Pausa/Reanudar** estable con `PauseController`.
4) Implementar **Stop** ordenado (interrupciones + cierre de ejecutor).
5) Hacer **thread-safe** el `ScoreBoard` (contadores/estadísticas).
6) (Libre) Dining Philosophers: deadlock + solución.

## Entregables
- Código Java 21, `RESPUESTAS.txt` con análisis y evidencia (thread dumps/capturas).

## Lectura
Goetz et al., *Java Concurrency in Practice*, págs. **1–4** y **15–21**.
