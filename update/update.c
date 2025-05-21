#include <windows.h>
#include <stdio.h>
#include <shlobj.h>
#include <urlmon.h> // URLDownloadToFileA
#pragma comment(lib, "urlmon.lib")

char* getInstallPathFromRegistry(char* buf, DWORD bufSize) {
    HKEY hKey;
    if (RegOpenKeyExA(HKEY_CURRENT_USER, "Software\\CampusSeat", 0, KEY_READ, &hKey) == ERROR_SUCCESS) {
        DWORD type = REG_SZ;
        if (RegQueryValueExA(hKey, "InstallPath", NULL, &type, (LPBYTE)buf, &bufSize) == ERROR_SUCCESS) {
            RegCloseKey(hKey);
            return buf;
        }
        RegCloseKey(hKey);
    }
    strcpy(buf, "C:\\Program Files\\CampusSeat");
    return buf;
}

int main(int argc, char* argv[]) {
    if (argc < 2) {
        MessageBoxA(NULL, "다운로드 URL이 전달되지 않았습니다.", "업데이트 오류", MB_OK | MB_ICONERROR);
        return 1;
    }
    char* downloadUrl = argv[1];

    char installPath[MAX_PATH];
    char exePath[MAX_PATH];
    char tmpPath[MAX_PATH];

    // 레지스트리에서 설치 경로 가져오기
    getInstallPathFromRegistry(installPath, sizeof(installPath));
    sprintf(exePath, "%s\\CampusSeat.exe", installPath);

    // 임시 파일 경로 (업데이트 폴더에 다운로드)
    char updateDir[MAX_PATH];
    SHGetFolderPathA(NULL, CSIDL_LOCAL_APPDATA, NULL, 0, updateDir);
    strcat(updateDir, "\\CampusSeat\\update");
    sprintf(tmpPath, "%s\\CampusSeat.new", updateDir);

    // 1. 서버에서 최신 CampusSeat.exe 다운로드
    HRESULT hr = URLDownloadToFileA(NULL, downloadUrl, tmpPath, 0, NULL);
    if (hr != S_OK) {
        MessageBoxA(NULL, "최신 파일 다운로드 실패!", "업데이트 오류", MB_OK | MB_ICONERROR);
        return 1;
    }

    // 2. 기존 CampusSeat.exe 삭제
    DeleteFileA(exePath);

    // 3. CampusSeat.new → CampusSeat.exe로 이동
    if (!MoveFileA(tmpPath, exePath)) {
        MessageBoxA(NULL, "파일 교체 실패!", "업데이트 오류", MB_OK | MB_ICONERROR);
        return 1;
    }

    // 4. CampusSeat.exe 실행
    ShellExecuteA(NULL, "open", exePath, NULL, installPath, SW_SHOWNORMAL);

    // CampusSeat_update.exe 숨김처리
    char updaterPath[MAX_PATH];
    sprintf(updaterPath, "%s\\CampusSeat_update.exe", updateDir);
    SetFileAttributesA(updaterPath, FILE_ATTRIBUTE_HIDDEN);

    return 0;
}