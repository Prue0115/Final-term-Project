#include <windows.h>
#include <stdio.h>

int main() {
    // 설치 폴더 경로 (실제 설치 경로에 맞게 수정)
    const char *installPath = "C:\\Program Files\\CampusSeat";
    char newExePath[MAX_PATH];
    char exePath[MAX_PATH];

    // 현재 실행 파일 위치에서 CampusSeat.new 찾기 (AppData\CampusSeat\update\CampusSeat.new)
    GetModuleFileNameA(NULL, newExePath, MAX_PATH);
    char *lastSlash = strrchr(newExePath, '\\');
    if (lastSlash) *(lastSlash + 1) = '\0';
    strcat(newExePath, "CampusSeat.new");

    // 설치 폴더의 CampusSeat.exe 경로
    sprintf(exePath, "%s\\CampusSeat.exe", installPath);

    // 1. 기존 CampusSeat.exe 삭제
    DeleteFileA(exePath);

    // 2. CampusSeat.new → CampusSeat.exe로 이동
    if (!MoveFileA(newExePath, exePath)) {
        MessageBoxA(NULL, "파일 교체 실패!", "업데이트 오류", MB_OK | MB_ICONERROR);
        return 1;
    }

    // 3. CampusSeat.exe 실행
    ShellExecuteA(NULL, "open", exePath, NULL, installPath, SW_SHOWNORMAL);

    return 0;
}