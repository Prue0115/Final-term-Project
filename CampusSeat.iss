[Setup]
AppName=CampusSeat
AppVersion=1.0.0
DefaultDirName={pf}\CampusSeat
DefaultGroupName=CampusSeat
OutputDir=.
OutputBaseFilename=CampusSeat_Setup
SetupIconFile=images\campusseat.ico
AllowUNCPath=yes
AllowRootDirectory=yes


[Files]
Source: "CampusSeat.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "CampusSeat_update.exe"; DestDir: "{commondesktop}\CampusSeat\update"; Flags: ignoreversion
Source: "CampusSeat_uninstall.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "images\*"; DestDir: "{app}\images"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{commondesktop}\CampusSeat"; Filename: "{app}\CampusSeat.exe"; IconFilename: "{app}\images\CampusSeat.ico"

[Registry]
Root: HKCU; Subkey: "Software\CampusSeat"; ValueType: string; ValueName: "InstallPath"; ValueData: "{app}"; Flags: uninsdeletevalue

[UninstallDelete]
Type: filesandordirs; Name: "{localappdata}\CampusSeat\update"
Type: filesandordirs; Name: "{app}\images"

[Run]
Filename: "{app}\CampusSeat.exe"; Description: "자리 지키미 실행"; Flags: nowait postinstall skipifsilent

[Languages]
Name: "korean"; MessagesFile: "compiler:Languages\Korean.isl"