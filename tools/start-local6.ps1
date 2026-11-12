param([switch]$NoBrowser)

$ErrorActionPreference = 'Stop'
Set-Location (Split-Path $PSScriptRoot -Parent)
$projectDir = (Get-Location).Path
$runtimeDir = Join-Path $projectDir 'target/local-runtime'
$dataDir = Join-Path $runtimeDir 'mysql-lite-data'
$tomcatDir = Join-Path $runtimeDir 'apache-tomcat-9.0.98'
$loginUrl = 'http://localhost:8081/restman6/login'

function Test-LocalPort([int]$Port) {
    $client = New-Object System.Net.Sockets.TcpClient
    try { return $client.ConnectAsync('127.0.0.1', $Port).Wait(500) -and $client.Connected }
    catch { return $false }
    finally { $client.Dispose() }
}

function Wait-Ready([scriptblock]$Check, [string]$Message) {
    $deadline = [DateTime]::UtcNow.AddSeconds(45)
    do {
        if (& $Check) { return }
        Start-Sleep -Milliseconds 500
    } while ([DateTime]::UtcNow -lt $deadline)
    throw $Message
}

function Test-Web {
    try {
        $response = Invoke-WebRequest -Uri $loginUrl -UseBasicParsing -TimeoutSec 2
        return $response.StatusCode -eq 200 -and $response.Content.Contains('RestMan6')
    } catch { return $false }
}

try {
    if (!(Test-Path "$dataDir/mysql") -or !(Test-Path "$tomcatDir/bin/bootstrap.jar")) {
        throw 'Thieu runtime demo trong target/local-runtime. Khong chay mvn clean vi se xoa runtime va du lieu demo.'
    }

    if (!(Test-LocalPort 3307)) {
        $mysqlHome = Join-Path $env:ProgramFiles 'MySQL/MySQL Server 8.0'
        $mysqlExe = Join-Path $mysqlHome 'bin/mysqld.exe'
        if (!(Test-Path $mysqlExe)) { throw "Khong tim thay MySQL: $mysqlExe" }
        Write-Host 'Dang mo database demo...'
        $mysqlArgs = @('--no-defaults', "--basedir=`"$mysqlHome`"", "--datadir=`"$dataDir`"",
            '--port=3307', '--bind-address=127.0.0.1', '--mysqlx=OFF',
            '--innodb-buffer-pool-size=32M', '--performance-schema=OFF', '--max-connections=15',
            '--character-set-server=utf8mb4', '--collation-server=utf8mb4_0900_as_ci', '--console')
        Start-Process -FilePath $mysqlExe -ArgumentList $mysqlArgs -WindowStyle Hidden `
            -RedirectStandardOutput "$runtimeDir/mysql-stdout.log" `
            -RedirectStandardError "$runtimeDir/mysql-stderr.log" | Out-Null
        Wait-Ready { Test-LocalPort 3307 } "MySQL chua san sang. Xem $runtimeDir/mysql-stderr.log"
    }

    if (!(Test-Web)) {
        if (Test-LocalPort 8081) { throw 'Cong 8081 dang duoc ung dung khac su dung.' }
        $javaExe = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME 'bin/java.exe' } else { '' }
        if (!$javaExe -or !(Test-Path $javaExe)) { throw 'Can dat JAVA_HOME toi JDK 17 tro len.' }
        $javaVersion = (& $javaExe --version | Out-String)
        if ($javaVersion -notmatch '(?:openjdk|java) (\d+)' -or [int]$Matches[1] -lt 17) {
            throw 'JAVA_HOME phai tro toi JDK 17 tro len.'
        }
        $env:RESTMAN_DB_URL = 'jdbc:mysql://127.0.0.1:3307/restman6?useUnicode=true&characterEncoding=UTF-8&connectionTimeZone=Asia/Bangkok&serverTimezone=Asia/Bangkok&allowPublicKeyRetrieval=true&useSSL=false'
        $env:RESTMAN_DB_USER = 'restman6'
        $env:RESTMAN_DB_PASSWORD = 'RestmanDemo6!'
        Write-Host 'Dang mo RestMan6...'
        $javaArgs = @('-Xms32m', '-Xmx128m', '-XX:+UseSerialGC', '-XX:ReservedCodeCacheSize=32m',
            '-Dfile.encoding=UTF-8', '-Duser.timezone=Asia/Bangkok',
            "-Dcatalina.home=`"$tomcatDir`"", "-Dcatalina.base=`"$tomcatDir`"", '-cp',
            "`"$tomcatDir/bin/bootstrap.jar;$tomcatDir/bin/tomcat-juli.jar`"",
            'org.apache.catalina.startup.Bootstrap', 'start')
        Start-Process -FilePath $javaExe -ArgumentList $javaArgs -WindowStyle Hidden `
            -RedirectStandardOutput "$runtimeDir/tomcat-stdout.log" `
            -RedirectStandardError "$runtimeDir/tomcat-stderr.log" | Out-Null
        Wait-Ready { Test-Web } "Web chua san sang. Xem $runtimeDir/tomcat-stderr.log"
    }

    Write-Host "Da san sang: $loginUrl"
    if (!$NoBrowser) { Start-Process $loginUrl }
} catch {
    Write-Host "Khong mo duoc RestMan6: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
