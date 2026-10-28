# Script test nhanh API Student OJT (can chay server truoc: .\mvnw.cmd spring-boot:run)
# Cach chay: powershell -ExecutionPolicy Bypass -File .\test-api.ps1

$base = "http://localhost:8080/api/v1/students"
$suffix = Get-Random -Minimum 100000 -Maximum 999999   # tranh trung du lieu giua cac lan chay

function Call-Api($title, $method, $url, $data, $expected) {
    Write-Host ""
    Write-Host "=== $title (mong doi: $expected) ===" -ForegroundColor Cyan
    $params = @{ Method = $method; Uri = $url; UseBasicParsing = $true }
    if ($data) {
        $params.Body = [Text.Encoding]::UTF8.GetBytes(($data | ConvertTo-Json))
        $params.ContentType = "application/json; charset=utf-8"
    }
    try {
        $res = Invoke-WebRequest @params
        $code = [int]$res.StatusCode
        $content = [Text.Encoding]::UTF8.GetString($res.RawContentStream.ToArray())
    } catch {
        # Doc body cua response loi (400/404/409) duoi dang UTF-8
        $response = $_.Exception.Response
        $code = [int]$response.StatusCode
        $content = $_.ErrorDetails.Message
        if (-not $content) {
            try {
                $stream = $response.GetResponseStream()
                if ($stream.CanSeek) { $stream.Position = 0 }
                $content = (New-Object IO.StreamReader($stream, [Text.Encoding]::UTF8)).ReadToEnd()
            } catch { $content = "(khong doc duoc noi dung loi)" }
        }
    }
    $color = if ($code -eq $expected) { "Green" } else { "Red" }
    Write-Host "HTTP $code" -ForegroundColor $color
    Write-Host $content
    return $content
}

try {
    Invoke-WebRequest $base -UseBasicParsing -TimeoutSec 5 | Out-Null
} catch {
    Write-Host "Server chua chay! Hay chay: .\mvnw.cmd spring-boot:run" -ForegroundColor Red
    exit 1
}

$student = @{
    fullName   = "Nguyen Van A"
    email      = "sv$suffix@r2s.com"
    phone      = "0901234567"
    nationalId = "079$suffix" + "123"
    university = "Dai hoc Bach Khoa"
    major      = "Cong nghe Phan mem"
}

# 1. Dang ky moi -> 201, SDT va CCCD bi che
$created = Call-Api "1. Dang ky sinh vien" Post "$base/register" $student 201
$id = ($created | ConvertFrom-Json).id

# 2. Lay theo id -> 200
Call-Api "2. Lay sinh vien theo id" Get "$base/$id" $null 200 | Out-Null

# 3. Lay danh sach -> 200
Call-Api "3. Lay danh sach" Get $base $null 200 | Out-Null

# 4. Trung email -> 409
$dupEmail = $student.Clone(); $dupEmail.nationalId = "079$suffix" + "999"
Call-Api "4. Trung email" Post "$base/register" $dupEmail 409 | Out-Null

# 5. Trung CCCD -> 409
$dupId = $student.Clone(); $dupId.email = "khac$suffix@r2s.com"
Call-Api "5. Trung CCCD" Post "$base/register" $dupId 409 | Out-Null

# 6. Du lieu sai -> 400
$invalid = @{ fullName = ""; email = "sai-email"; phone = "123"; nationalId = "abc" }
Call-Api "6. Du lieu khong hop le" Post "$base/register" $invalid 400 | Out-Null

# 7. Id khong ton tai -> 404
Call-Api "7. Id khong ton tai" Get "$base/00000000-0000-0000-0000-000000000000" $null 404 | Out-Null

Write-Host ""
Write-Host "Xong! Dong mau xanh = dung, mau do = sai." -ForegroundColor Yellow
