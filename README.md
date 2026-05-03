# Bonsai Tree Collection Manager

Full-stack project consisting of two separate repositories:

- **API** (`BonsaiTreeApp/API/`) — ASP.NET Core 10 Web API + EF Core + SQL Server, deployed to Azure App Service.
- **Android** (`BonsaiTreeApp/Android/BonsaiApp/`) — Android Java app that consumes the API.

The two folders are independent Git repos with their own GitHub Actions workflows.

---

## Part 1 — Test the API Locally

### Prerequisites
- .NET 10 SDK — https://dotnet.microsoft.com/download
- SQL Server LocalDB (installs with Visual Studio, or via the SQL Server Express installer)
- (One-time) EF Core CLI:
  ```powershell
  dotnet tool install --global dotnet-ef
  ```

### Steps

1. Open a terminal in the API folder:
   ```powershell
   cd BonsaiTreeApp\API\BonsaiAPI
   ```

2. Restore packages and build:
   ```powershell
   dotnet restore
   dotnet build
   ```

3. Run the API:
   ```powershell
   dotnet run
   ```
   The first request triggers `Database.Migrate()` which creates `BonsaiDB` in LocalDB and inserts seed data (5 species + 3 trees).

4. Open Swagger UI in a browser:
   ```
   http://localhost:5220/swagger
   ```

5. Quickly test endpoints from `BonsaiAPI/bonsai.http` in VS Code (REST Client extension), or with curl:
   ```powershell
   curl http://localhost:5220/api/species
   curl http://localhost:5220/api/trees
   curl "http://localhost:5220/api/trees/search?name=Maple"
   ```

### Available Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET | /api/species | All species |
| GET | /api/species/{id} | Single species |
| GET | /api/trees | All trees (with species) |
| GET | /api/trees/{id} | Single tree |
| GET | /api/trees/search?name= | Search trees by nickname |
| POST | /api/trees | Add a tree |
| PUT | /api/trees/{id} | Update a tree |
| DELETE | /api/trees/{id} | Delete a tree |

---

## Part 2 — Test the Android App Locally

### Prerequisites
- Android Studio Hedgehog (or later)
- JDK 11
- Android emulator (API level 26+) **or** physical device with USB debugging enabled

### Open in Android Studio

1. Launch Android Studio → **File → Open** → select:
   ```
   BonsaiTreeApp\Android\BonsaiApp
   ```
2. Wait for Gradle sync to finish.

### Point the App at Your Local API

The Android emulator cannot reach `localhost` directly — it routes through `10.0.2.2`.

Open `app/src/main/java/org/tudublin/bonsaiapp/api/RetrofitClient.java` and set:

| Target | BASE_URL |
|--------|----------|
| Emulator (local API on your PC) | `http://10.0.2.2:5220` |
| Physical device on same Wi-Fi | `http://<your-PC-LAN-IP>:5220` |
| Production (cloud) | `https://bonsaiapi.azurewebsites.net` |

For LAN testing, find your IP with `ipconfig` (look for IPv4 of your Wi-Fi adapter) and allow inbound TCP port 5220 in Windows Firewall. To make Kestrel listen on all interfaces, run:
```powershell
dotnet run --urls "http://0.0.0.0:5220"
```

### Run

- **Emulator:** in Android Studio, pick a device in the toolbar and click **Run**.
- **Physical device:** enable Developer Options → USB Debugging, plug it in, accept the trust prompt, then click **Run**.

### Run Espresso End-to-End Tests

With an emulator running or a device connected:
```powershell
cd BonsaiTreeApp\Android\BonsaiApp
.\gradlew connectedAndroidTest
```
Reports: `app/build/reports/androidTests/connected/index.html`

---

## Part 3 — Host the API on Azure

### One-Time Azure Setup (Azure CLI)

```powershell
# Sign in
az login

# Variables
$RG = "BonsaiRG"
$LOC = "westeurope"
$SQLSERVER = "bonsai-sql-$(Get-Random -Maximum 9999)"   # must be globally unique
$SQLPWD = "Strong!Password123"
$DBNAME = "BonsaiDB"
$PLAN = "BonsaiPlan"
$APP = "bonsaiapi"                                       # must be globally unique

# Resource group
az group create --name $RG --location $LOC

# Azure SQL Server + Database (Basic tier)
az sql server create --name $SQLSERVER --resource-group $RG `
    --location $LOC --admin-user sqladmin --admin-password $SQLPWD
az sql db create --resource-group $RG --server $SQLSERVER `
    --name $DBNAME --service-objective Basic

# Allow Azure services to reach the SQL server
az sql server firewall-rule create --resource-group $RG --server $SQLSERVER `
    --name AllowAzure --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0

# App Service plan (Linux, B1) + Web App running .NET 10
az appservice plan create --name $PLAN --resource-group $RG --sku B1 --is-linux
az webapp create --resource-group $RG --plan $PLAN `
    --name $APP --runtime "DOTNET|10.0"

# Connection string for the app
$CONN = "Server=tcp:$SQLSERVER.database.windows.net,1433;Initial Catalog=$DBNAME;User ID=sqladmin;Password=$SQLPWD;Encrypt=True;TrustServerCertificate=False;"
az webapp config connection-string set --resource-group $RG --name $APP `
    --connection-string-type SQLAzure --settings BonsaiContext="$CONN"
```

On first request the app calls `Database.Migrate()` automatically, which creates the tables and seeds initial data in Azure SQL.

### Manual Deploy (one-off)

```powershell
cd BonsaiTreeApp\API\BonsaiAPI
dotnet publish -c Release -o ..\publish
Compress-Archive -Path ..\publish\* -DestinationPath ..\publish.zip -Force
az webapp deploy --resource-group BonsaiRG --name bonsaiapi `
    --src-path ..\publish.zip --type zip
```

After deploy, browse:
```
https://bonsaiapi.azurewebsites.net/swagger
https://bonsaiapi.azurewebsites.net/api/species
```

### Automated Deploy via GitHub Actions

The API repo already includes `.github/workflows/deploy.yml`. To enable it:

1. In Azure Portal → App Service `bonsaiapi` → **Get publish profile** → download the `.PublishSettings` XML file.
2. In your GitHub API repo → **Settings → Secrets and variables → Actions → New repository secret**:
   - **Name:** `AZURE_WEBAPP_PUBLISH_PROFILE`
   - **Value:** paste the entire XML content
3. Push to `main`. The workflow restores, builds, publishes, and deploys to App Service.

---

## Part 4 — Push the Android App to GitHub

The Android repo includes `.github/workflows/build.yml` which runs on every push and builds a debug APK as a downloadable artifact.

```powershell
cd BonsaiTreeApp\Android\BonsaiApp
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin <your-android-github-url>
git push -u origin main
```

Same procedure for the API repo from `BonsaiTreeApp\API`.

---

## Part 5 — Switch the Android App to the Cloud API

After deploying the API to Azure:

1. Edit `app/src/main/java/org/tudublin/bonsaiapp/api/RetrofitClient.java`:
   ```java
   private static final String BASE_URL = "https://bonsaiapi.azurewebsites.net";
   ```
2. Rebuild and run. The app now reads all data from the cloud — no local API needed.

---

## Internationalisation

The app is provided in English (`values/strings.xml`) and Ukrainian (`values-uk/strings.xml`). To test:
1. On the device: **Settings → System → Languages → Add Українська** and move it to the top.
2. Re-launch the app — all UI text switches to Ukrainian.

---

## Tech Stack

| Layer | Tech |
|-------|------|
| API | ASP.NET Core 10, EF Core 10 (code-first), Swashbuckle (OpenAPI), CORS |
| DB | SQL Server LocalDB (dev) / Azure SQL (prod) |
| Android | Java 11, Retrofit 2, Gson, Glide, RecyclerView, Material Components, ViewBinding |
| Tests | Espresso (Android UI E2E) |
| CI/CD | GitHub Actions (separate workflows per repo) |
