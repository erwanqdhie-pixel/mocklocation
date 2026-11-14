# Simple Mock Location (Android Kotlin)

Aplikasi Mock GPS super ringan tanpa peta berat (~2 MB setelah di-build). Sudah dilengkapi konfigurasi **GitHub Actions** untuk build APK otomatis di cloud tanpa perlu instal Android Studio.

## Cara Build Otomatis via GitHub Actions (Tanpa Android Studio)
1. Ekstrak file ZIP ini.
2. Buat repository baru di **GitHub** (github.com).
3. Upload semua isi folder `SimpleMockLocation` (termasuk folder `.github`) ke repository tersebut.
   *Catatan: Pastikan folder `.github` ikut ter-upload (pada Windows/Mac, aktifkan "Show Hidden Files" jika folder berawalan titik tidak terlihat).*
4. Buka tab **Actions** di repository GitHub kamu, lalu pilih workflow **Build Android APK** (akan jalan otomatis setelah upload, atau klik **Run workflow**).
5. Tunggu sekitar 2 menit hingga centang hijau muncul, lalu download file **SimpleMockLocation-APK** di bagian **Artifacts** paling bawah.

## Cara Pakai di HP Android
1. Ekstrak hasil download dari GitHub dan instal `app-debug.apk` ke HP Android.
2. Buka **Settings > Developer Options > Select mock location app**, lalu pilih **Simple Mock GPS**.
3. Buka aplikasi **Simple Mock GPS**, isi Latitude dan Longitude, lalu tekan **Mulai Mock Location**.
