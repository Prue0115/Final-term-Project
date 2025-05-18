#include <windows.h>
#include <stdio.h>
#include <shlobj.h>

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

int main() {
    char installPath[MAX_PATH];
    getInstallPathFromRegistry(installPath, sizeof(installPath));
    char exePath[MAX_PATH];
    char updateExePath[MAX_PATH];
    char updateDir[MAX_PATH];

    // CampusSeat.exe 삭제
    sprintf(exePath, "%s\\CampusSeat.exe", installPath);
    DeleteFileA(exePath);

    // 설치 폴더 내 모든 파일 삭제 (여기서는 폴더만 삭제)
    RemoveDirectoryA(installPath);

    // AppData\CampusSeat\update 폴더 및 CampusSeat_update.exe 삭제
    SHGetFolderPathA(NULL, CSIDL_LOCAL_APPDATA, NULL, 0, updateDir);
    strcat(updateDir, "\\CampusSeat\\update");
    sprintf(updateExePath, "%s\\CampusSeat_update.exe", updateDir);
    DeleteFileA(updateExePath);
    RemoveDirectoryA(updateDir);

    // 바탕화면 바로가기 삭제
    char desktop[MAX_PATH];
    SHGetFolderPathA(NULL, CSIDL_DESKTOPDIRECTORY, NULL, 0, desktop);
    strcat(desktop, "\\CampusSeat.lnk");
    DeleteFileA(desktop);

    MessageBoxA(NULL, "CampusSeat가 성공적으로 삭제되었습니다.", "언인스톨 완료", MB_OK | MB_ICONINFORMATION);
    return 0;
}