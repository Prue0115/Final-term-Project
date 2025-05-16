[Setup]
AppName=CampusSeat
AppVersion=1.0.0
DefaultDirName={pf}\CampusSeat
DefaultGroupName=CampusSeat
OutputBaseFilename=CampusSeatSetup
Compression=lzma
SolidCompression=yes

[Files]
Source: "CampusSeat.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "CampusSeat.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "images\*"; DestDir: "{app}\images"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\CampusSeat"; Filename: "{app}\CampusSeat.exe"
Name: "{desktop}\CampusSeat"; Filename: "{app}\CampusSeat.exe"