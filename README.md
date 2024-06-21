<!--suppress HtmlDeprecatedAttribute -->
<p align="center"><a href="https://github.com/yanuarcy/Fruiteasy" target="_blank"><img src="https://drive.google.com/uc?export=view&id=1k-WxSjJMqLJiJ2A80J-Uso2yCUm63faP" width="400" alt="Fruiteasy Logo"></a></p>


## About Fruiteasy

The WHO recommends a daily fruit consumption of 400 grams per person for better health. 
However, in Indonesia, the average intake is only 81.14 grams per capita per day (BPS, 2021), falling short of the WHO threshold. 
Limited awareness of nutritional benefits leads to repetitive fruit choices and potential health issues. 
An Android app that classifies fruit images could address this challenge by providing interactive identification and 
detailed nutritional information, promoting healthier choices and reducing chemical consumption.

## Preview

Home Page

![preview](https://github.com/yanuarcy/SolarisTech/blob/c3fc9893ae66d77c3d1e5bbb4693bb294b087ea0/AdminSolarisTech.jpg)

## API

Here's the API we used into our Apps:

Post
- `Request Verify Email - POST` (https://fruiteasy-be-nrw674jbdq-et.a.run.app/UserAuth/request-verify-email).
- `Login - POST` (https://fruiteasy-be-nrw674jbdq-et.a.run.app/UserAuth/login).
- `Edit Profile - POST` (https://fruiteasy-be-nrw674jbdq-et.a.run.app/UserAuth/edit-profile).
- `Reset Password - POST` (https://fruiteasy-be-nrw674jbdq-et.a.run.app/UserAuth/reset-password).
- `History - POST` (https://fruiteasy-be-nrw674jbdq-et.a.run.app/Predict/history).
- `Predict Image - POST` (https://fruiteasy-be-nrw674jbdq-et.a.run.app/Predict/upload).

## Setup Project

Step 1:
Untuk Vendor nya

```
composer install
```

Step 2:
Untuk Node Modules nya

```
npm install
```

Step 3:
Untuk Database nya

```
cp .env.example .env
```

Step 4:
Untuk `APP_KEY` pada .env nya

```
php artisan key:generate
```

Step 5:
Setup Database anda, seperti di bawah ini

```
DB_DATABASE=laravel
DB_USERNAME=
DB_PASSWORD=
```

Step 6:
Jalankan `Migration` dan `Seeder` nya via Artisan

>Note: Tolong pastikan Konfigurasi database anda benar sebelum menjalankan perintah di bawah, seperti Nama,Username, dan Password.

```
php artisan migrate --seed
```

Final Step:
Run project anda via Artisan dan Npm:
>Note: Sebelum menjalankan project anda pada server, harap perhatikan Requirement Installation !

>Artisan
```
php artisan serve
```

>Npm
```
npm run dev
```

>Note: Tolong install ini pada terminal VS Code anda agar tidak terjadi error.

## Installation DataTables

Install via composer:

```
composer require yajra/laravel-datatables-oracle
```

Selanjutnya, anda install via NPM:

```
npm install datatables.net-bs5 datatables.net-buttons-bs5
```

Selanjutnya Registrasikan aset-aset `DataTables` pada file `/resources/js/app.js` seperti yang terlihat di bawah ini.
```javascript
import './bootstrap';
import 'datatables.net-bs5';
import 'datatables.net-buttons-bs5';
```

Selanjutnya Registrasikan aset-aset `DataTables` pada file `/resources/sass/app.scss` seperti yang terlihat di bawah ini.
```sass
// DataTables
@import "datatables.net-bs5/css/dataTables.bootstrap5.css";
@import "datatables.net-buttons-bs5/css/buttons.bootstrap5.css";
```

Selanjutnya Import `jQuery` pada file `/resources/js/bootstrap.js` seperti yang terlihat di bawah ini.
```javascript
import axios from 'axios';
window.axios = axios;

window.axios.defaults.headers.common['X-Requested-With'] = 'XMLHttpRequest';

import $ from 'jquery';
window.$ = $;
```

>Note: Tolong install ini pada terminal VS Code anda agar tidak terjadi error.

## Installation SweetAlert

Install via composer:

```
composer require realrashid/sweet-alert
```

Publish aset dari SweetAlert via Artisan:

```
php artisan sweetalert:publish
```

>Note: Tolong install ini pada terminal VS Code anda agar tidak terjadi error.

## Installation Laravel Excel

Install via composer:

```
composer require maatwebsite/excel
```

Publish file konfigurasi dengan menjalankan command script seperti di bawah ini:

```
php artisan vendor:publish --provider="Maatwebsite\Excel\ExcelServiceProvider" --tag=config
```

>Note: Tolong install ini pada terminal VS Code anda agar tidak terjadi error.

## Installation Laravel PDF

Install via composer:

```
composer require barryvdh/laravel-dompdf
```

Publish file konfigurasi dengan menjalankan command script seperti di bawah ini:

```
php artisan vendor:publish --provider="Barryvdh\DomPDF\ServiceProvider"
```