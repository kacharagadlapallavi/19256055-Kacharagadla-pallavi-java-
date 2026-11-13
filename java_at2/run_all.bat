@echo off
echo ================================================================================
echo    CSA09 PROGRAMMING IN JAVA - ASSIGNMENT 2 (CO2) AUTOMATED TEST SUITE
echo ================================================================================

echo.
echo [*] Compiling all task packages...
javac -d . Q1\*.java Q2\*.java Q3\*.java Q4\*.java Q5\*.java Q6\*.java Q7\*.java
if %ERRORLEVEL% NEQ 0 (
    echo [-] Compilation failed!
    exit /b %ERRORLEVEL%
)
echo [+] All classes compiled successfully.

echo.
echo ================================================================================
echo    RUNNING TASK 01: Railway e-Ticket Generator Benchmark
echo ================================================================================
java -cp . Q1.Q1Benchmark

echo.
echo ================================================================================
echo    RUNNING TASK 02: Music Playlist Editor Benchmark
echo ================================================================================
java -cp . Q2.Q2Benchmark

echo.
echo ================================================================================
echo    RUNNING TASK 03: Event Attendance De-duplication (Complete the Code & Contract)
echo ================================================================================
echo --- A. Incomplete Baseline (Before TODO 1):
java -cp . Q3.AttendanceIncomplete
echo --- B. Completed Implementation (After TODO 1):
java -cp . Q3.Attendance
echo --- C. Contract Investigation Suite:
java -cp . Q3.ContractInvestigation

echo.
echo ================================================================================
echo    RUNNING TASK 04: Hospital Pharmacy Inventory Map Benchmark
echo ================================================================================
java -cp . Q4.Q4Benchmark

echo.
echo ================================================================================
echo    RUNNING TASK 05: Inter-College Sports Leaderboard (Generic Ranking Component)
echo ================================================================================
echo --- A. Completed Leaderboard Demo:
java -cp . Q5.LeaderboardDemo
echo --- B. Generic Ranking Test Suite:
java -cp . Q5.GenericLeaderboardTest

echo.
echo ================================================================================
echo    RUNNING TASK 06: Hospital Emergency Triage (PriorityQueue & Fairness)
echo ================================================================================
echo --- A. Completed Triage Demo:
java -cp . Q6.Triage
echo --- B. Triage Investigation Suite:
java -cp . Q6.TriageInvestigation

echo.
echo ================================================================================
echo    RUNNING TASK 07: Day-4 Design Challenge (Diagnose, Redesign & Validate)
echo ================================================================================
java -cp . Q7.Q7Benchmark

echo.
echo ================================================================================
echo    ALL TASKS EXECUTED SUCCESSFULLY!
echo ================================================================================
pause

