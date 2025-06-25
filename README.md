# 🖥️ Yapay Zeka Destekli Hasta Randevu Sistemi – Backend

Bu proje, hastaların doktorlardan randevu almasını sağlayan yapay zeka destekli bir sistemin Java Spring Boot ile geliştirilmiş backend uygulamasıdır. Sistem; hasta, doktor ve admin olmak üzere üç farklı kullanıcı türü sunar. Randevu oluşturma, şikayet bildirme, kullanıcı yönetimi gibi modülleri içeren bu sistem, doğal dil işleme (NLP) kullanarak şikayetlere uygun poliklinik önerilerinde de bulunur.


## ⚙️ Sistem Özellikleri ve Roller

Bu sistem; **Hasta**, **Doktor** ve **Admin** olmak üzere üç farklı kullanıcı rolüne sahiptir. Kullanıcılar kendilerine özel paneller aracılığıyla sisteme erişir. Yapay zeka destekli poliklinik önerisi, nöbetçi eczane sorgulama ve rol bazlı işlem yetkileri ile sistem fonksiyonel, modern ve güvenli bir yapıya sahiptir.

---

### 👤 Hasta

- Kayıt olabilir ve giriş yapabilir.
- İki yöntemle randevu alabilir:
  - **Geleneksel Yöntem:** Poliklinik ve doktor seçerek manuel randevu.
  - **Yapay Zeka Destekli:** Şikayet metni girerek sistemin önerdiği polikliniğe yönlendirilir.
- Randevularını listeleyebilir, güncelleyebilir ve iptal edebilir.
- Şikayet ve dilek metinleri iletebilir, durum güncellemelerini takip edebilir.
- **Nöbetçi Eczane Bilgisi** sorgulayabilir (CollectAPI üzerinden).
- **Reçetelerini** görüntüleyebilir.
- **Hasta Geçmişi** kayıtlarını listeleyebilir.
- **Doktor tarafından oluşturulan raporları** görebilir.
- Kendi profil bilgilerini güncelleyebilir.

---

### 🩺 Doktor

- Kendisine atanmış hastaların listesini ve detaylı bilgilerini görüntüleyebilir.
- Kendi randevularını ve randevu detaylarını listeleyebilir.
- Hastaların geçmiş verilerine erişebilir:
  - **Reçeteler**
  - **Hasta Geçmişi**
  - **Test Sonuçları**
  - **Raporlar**
- Yeni **Reçete**, **Hasta Geçmişi**, **Test Sonucu** ve **Hasta Raporu** oluşturabilir.
- Kendi profil bilgilerini güncelleyebilir.

> ⚠️ Doktorlar istatistiksel grafiklere erişemez. Bu yetki yalnızca admin kullanıcılara özeldir.

---

### 🛡️ Admin

- Tüm kullanıcıları (hasta/doktor) listeleyebilir, arayabilir, yeni kullanıcı oluşturabilir veya mevcut kullanıcıyı güncelleyebilir.
- Sistemdeki tüm randevuları listeleyebilir, detaylarını görüntüleyebilir, gerektiğinde iptal edebilir.
- Tüm kullanıcıların gönderdiği şikayetleri listeleyebilir, çözüm sürecini yönetebilir.
- **İstatistiksel Grafikler** ile sistem genel verilerini analiz edebilir.
- **Yapay Zeka ile Grafik Yorumu:** Grafiklerdeki veriler için yapay zekadan özet, yorum ve çıkarım alabilir.
- **Yapay Zeka ile Sistem Analizi:** Genel sistem durumu hakkında öneri ve analizler alabilir (örneğin yoğun poliklinikler, hasta yükü, sistem iyileştirme önerileri).
- Kendi profil bilgilerini güncelleyebilir.

---

### 🤖 Yapay Zeka Destekli Randevu Sistemi

- Hasta, metin olarak şikayetini yazar.
- Sistem, yapay zeka (OpenAI GPT, HuggingFace vb.) aracılığıyla şikayeti analiz eder.
- Uygun poliklinik ve tedavi önerileri sunar.
- Kullanıcı, bu öneriler üzerinden doğrudan randevu oluşturabilir.

---

### 🧪 Nöbetçi Eczane Modülü

- Hasta kullanıcılar, günlük nöbetçi eczane bilgilerine erişebilir.
- Eczane verileri CollectAPI üzerinden alınır.

## 🧰 Kullanılan Teknolojiler

<p align="left">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" width="40" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" width="40" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/mysql/mysql-original.svg" width="40" />
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/github/github-original.svg" width="40" />
</p>

### 🔙 Backend
- **Java 17**
- **Spring Boot** – RESTful API geliştirme
- **Spring Data JPA** – Veritabanı işlemleri
- **Spring Security** – Kimlik doğrulama ve yetkilendirme
- **Hibernate** – ORM yönetimi
- **MySQL** – İlişkisel veritabanı
- **JWT (JSON Web Token)** – Oturum yönetimi
- **BCrypt** – Şifre güvenliği
- **Lombok** – Boilerplate kodları azaltma
- **Maven** – Proje ve bağımlılık yönetimi
- **OpenAI API / Hugging Face / SpaCy** – NLP (doğal dil işleme) ile şikayet analizi
- **CollectAPI** – Nöbetçi eczane bilgilerini almak için dış servis

### 🧪 Test Araçları
- **JUnit 5** – Servis ve repository testleri
- **Mockito** – Mock nesneler ile iş mantığı testi
- **RestAssured** – REST API entegrasyon testleri

  ## 📦 Kurulum ve Çalıştırma

Aşağıdaki adımları izleyerek projeyi kendi bilgisayarınızda kolayca çalıştırabilirsiniz.

### 1️⃣ Gereksinimler

- Java 17+
- Maven
- MySQL 8+
- Bir IDE (IntelliJ IDEA, VS Code vb.)

---

### 2️⃣ Projeyi Klonlayın

git clone https://github.com/ihalilbesli/hastaneRandevu.git
cd hastaneRandevu

---

### 3️⃣ Veritabanı Oluşturun

CREATE DATABASE hastarandevu;

---

### 4️⃣ `application.properties` Dosyasını Ayarlayın

src/main/resources/application.properties içine aşağıdakileri yazın:

spring.application.name=hastarandevu
spring.config.import=classpath:application-secret.properties
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
logging.level.root=INFO
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
server.error.include-message=always

---

### 5️⃣ `application-secret.properties` Dosyasını Ekleyin

Bu dosya gizli bilgileri içerdiği için projeye dahil edilmez. `src/main/resources/` klasörüne kendiniz ekleyin:

# MySQL erişimi
spring.datasource.url=jdbc:mysql://localhost:3306/hastarandevu
spring.datasource.username=KENDI_KULLANICI_ADINIZI_YAZIN
spring.datasource.password=KENDI_SIFRENIZI_YAZIN
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Admin kullanıcı varsayılan bilgisi
spring.security.user.name=admin
spring.security.user.password=admin


# Yapay Zeka API
ai.api.url=https://api.openai.com/v1/completions
ai.api.key=YOUR_OPENAI_API_KEY

> NOT: `application-secret.properties` dosyasını `.gitignore` içine eklemeyi unutmayın.

---

### 6️⃣ Bağımlılıkları Yükleyin

mvn clean install

---

### 7️⃣ Uygulamayı Başlatın

mvn spring-boot:run

Alternatif: IDE üzerinden `HastaRandevuApplication.java` dosyasını sağ tıklayıp çalıştırabilirsiniz.

---

### 🔐 Örnek Kullanıcı Girişi

POST /api/auth/login

{
  "email": "admin@example.com",
  "password": "1234"
}

---

✔️ Uygulama varsayılan olarak şu adreste çalışır:  
http://localhost:8080

## 📁 Proje Dosya Yapısı

Aşağıda backend projenin klasör yapısı ve her klasörün ne işe yaradığını açıklamalı şekilde görebilirsiniz:
```
src
└── main
    ├── java
    │   └── com.hastarandevu.app
    │       ├── aspect         → Loglama, hata yönetimi gibi kesitsel (AOP) işlemler
    │       ├── config         → Güvenlik ayarları, CORS, JWT, uygulama yapılandırmaları
    │       ├── controller     → REST API endpoint'lerini barındıran sınıflar
    │       ├── dto            → Veri transferi için kullanılan DTO sınıfları
    │       ├── filter         → Giriş/çıkış isteklerini filtreleyen güvenlik filtreleri (örn. JWTFilter)
    │       ├── model          → Veritabanı tablolarını temsil eden Entity sınıfları
    │       ├── repository     → Veritabanı işlemleri için JPA arayüzleri
    │       ├── service        → İş mantığını tanımlayan servis arayüzleri
    │       ├── util           → Yardımcı sınıflar (örn. SecurityUtil, TokenUtil)
    │       └── HastarandevuApplication.java → Spring Boot uygulamasının başlangıç sınıfı (main class)

    └── resources
        ├── application.properties           → Genel yapılandırma dosyası
        └── application-secret.properties    → Gizli bilgiler (DB şifreleri, API anahtarları vb.)
```



✅ Bu yapı, projeni anlamak isteyen herkes için anlaşılır ve düzenli bir rehber sunar.  

## 📌 API Endpoint Özeti

Aşağıda sistemin REST API uç noktalarının özeti verilmiştir. Geliştiriciler bu endpoint’leri kullanarak frontend veya mobil uygulamalarla entegrasyon sağlayabilir.

---

### 🔐 Kimlik Doğrulama (Auth)

- `POST /hastarandevu/auth/login` → Kullanıcı girişi (JWT alır)
- `POST /hastarandevu/auth/register` → Yeni kullanıcı kaydı (sadece hasta rolü)
- `POST /hastarandevu/auth/reset-password` → Şifre sıfırlama

---

### 👤 Kullanıcı (User)

- `GET /hastarandevu/users/{id}` → Belirli kullanıcıyı getir
- `PUT /hastarandevu/users/{id}` → Kullanıcı bilgilerini güncelle
- `DELETE /hastarandevu/users/{id}` → Kullanıcıyı sil
- `GET /hastarandevu/profile/me` → Giriş yapan kişinin bilgileri
- `PUT /hastarandevu/profile/me` → Giriş yapan kişi bilgilerini günceller
- `POST /hastarandevu/users/profile/change-password` → Şifre değiştir
- `GET /hastarandevu/users/role/{role}` → Role göre kullanıcıları getir
- `GET /hastarandevu/users/specialization/{specialization}` → Uzmanlığa göre getir (doktorlar)
- Filtreleme: `/users/phone/{phone}`, `/users/name/{name}`, `/users/email/{email}`, vb.

---

### 📅 Randevu (Appointment)

- `GET /hastarandevu/appointments` → Tüm randevuları getir
- `GET /hastarandevu/appointments/patient/{id}` → Hastaya ait randevular
- `GET /hastarandevu/appointments/doctor/{id}` → Doktora ait randevular
- `GET /hastarandevu/appointments/doctor/{id}/date` → Doktorun belli tarihteki randevuları
- `GET /hastarandevu/appointments/available` → Uygun saat dilimlerini getir
- `POST /hastarandevu/appointments` → Randevu oluştur
- `PUT /hastarandevu/appointments/{id}/cancel` → Randevuyu iptal et
- `PUT /hastarandevu/appointments/{id}/status` → Randevu durumunu güncelle
- `DELETE /hastarandevu/appointments/{id}` → Randevuyu sil

---

### 🧪 Test Sonuçları

- `GET /hastarandevu/test-result` → Tüm test sonuçlarını getir
- `GET /hastarandevu/test-result/patient/{id}` → Hastanın sonuçları
- `GET /hastarandevu/test-result/doctor/{id}/filter` → Doktora göre filtreli sonuçlar
- `POST /hastarandevu/test-result` → Yeni test sonucu ekle
- `PUT /hastarandevu/test-result/{id}` → Güncelle
- `DELETE /hastarandevu/test-result/{id}` → Sil

---

### 💊 Reçete (Prescription)

- `GET /hastarandevu/prescriptions` → Tüm reçeteler
- `GET /hastarandevu/prescriptions/patient/{id}` → Hastaya ait reçeteler
- `GET /hastarandevu/prescriptions/doctor/{id}` → Doktora ait reçeteler
- `POST /hastarandevu/prescriptions` → Yeni reçete oluştur
- `PUT /hastarandevu/prescriptions/{id}` → Güncelle
- `DELETE /hastarandevu/prescriptions/{id}` → Sil

---

### 📝 Hasta Raporu (Patient Report)

- `GET /hastarandevu/patient-report` → Tüm raporlar
- `GET /hastarandevu/patient-report/patient/{id}` → Hastaya ait raporlar
- `GET /hastarandevu/patient-report/doctor/{id}` → Doktora ait raporlar
- `POST /hastarandevu/patient-report` → Yeni rapor oluştur
- `PUT /hastarandevu/patient-report/{id}` → Güncelle
- `DELETE /hastarandevu/patient-report/{id}` → Sil

---

### 🩺 Hasta Geçmişi (Patient History)

- `GET /hastarandevu/patient-history` → Tüm geçmiş kayıtları
- `GET /hastarandevu/patient-history/patient/{id}` → Hastaya ait geçmiş
- `GET /hastarandevu/patient-history/doctor/{id}` → Doktora göre filtreli
- `POST /hastarandevu/patient-history` → Yeni kayıt oluştur
- `PUT /hastarandevu/patient-history/{id}` → Güncelle
- `DELETE /hastarandevu/patient-history/{id}` → Sil

---

### 📢 Şikayet (Complaint)

- `GET /hastarandevu/complaints` → Tüm şikayetleri getir
- `GET /hastarandevu/complaints/status` → Duruma göre filtrele
- `GET /hastarandevu/complaints/filter` → Gelişmiş filtre
- `POST /hastarandevu/complaints` → Yeni şikayet oluştur
- `PUT /hastarandevu/complaints/{id}` → Güncelle
- `DELETE /hastarandevu/complaints/{id}` → Sil

---

### 🏥 Klinik (Clinic)

- `GET /hastarandevu/clinics` → Aktif klinikleri getir
- `POST /hastarandevu/clinics` → Yeni klinik ekle
- `PUT /hastarandevu/clinics/{id}/activate|passive` → Klinik durumunu değiştir
- `GET /hastarandevu/clinics/{id}/doctors` → Klinik içindeki doktorları getir

---

### 🧠 Yapay Zeka (AI)

- `POST /hastarandevu/ai/analyze` → Hastanın şikayetini analiz eder, öneri verir
- `POST /hastarandevu/ai/analyze-graph` → Grafik yorumlama
- `GET /hastarandevu/ai/admin/risk-alerts` → Yüksek riskli durumlar
- `GET /hastarandevu/ai/admin/analyze-complaints` → Şikayet analizi
- `GET /hastarandevu/ai/admin/analyze-user-behavior` → Kullanıcı davranış analizi

---

### 📊 İstatistikler (Analytics)

- Kullanıcılar: `/analytics/users/roles`, `/genders`, `/monthly`
- Şikayetler: `/analytics/complaints/status`, `/subject`, `/clinic`
- Randevular: `/analytics/appointments/clinic`, `/doctor`, `/date`, `/status`, `/time-slot`
- Klinik: `/analytics/clinics/doctor-count`

---

### 📁 Dışa Aktarma (Export)

- `/export/users`, `/export/patient-reports`, `/export/test-results`, `/export/prescriptions`, `/export/appointments` vb.

---

### 🩹 Nöbetçi Eczane

- `GET /hastarandevu/eczaneler` → CollectAPI ile entegre nöbetçi eczaneleri getirir

---

### 🩺 Doktorun Hastaları

- `GET /doctorPatients/my-patients` → Doktorun geçmişte işlem yaptığı hastalar
- `GET /doctorPatients/my-patients/search-by-name` → Ada göre filtre
- `GET /doctorPatients/my-patients/search-by-email` → E-posta ile filtre
- `GET /doctorPatients/my-patients-today` → Bugünkü hastalar
- `GET /doctorPatients/my-patients-today-full` → Bugünkü tüm detaylı veriler

---

### 🛡️ Erişim Logları

- `GET /acces-log` → Tüm erişim geçmişi
- `GET /acces-log/status/{status}`, `/role/{role}`, `/email/{email}` → Filtreleme


---

## 👨‍💻 Geliştirici Bilgisi

Bu proje, İstanbul Gelişim Üniversitesi Bilgisayar Mühendisliği öğrencisi  
**İbrahim Halil Beşli** tarafından 2025 yılı bahar döneminde bitirme projesi olarak geliştirilmiştir.

Proje kapsamında backend Spring Boot, frontend ise Angular kullanılarak geliştirilmiş; hasta, doktor ve admin panelleri içeren çok rollü bir randevu sistemi oluşturulmuştur.  
Yapay zeka desteğiyle şikayet analizleri yapılabilmekte, sistemdeki veriler grafiklerle görselleştirilip yapay zekaya yorumlatılabilmektedir.

Her türlü soru ve geri bildirim için iletişime geçebilirsiniz.  
📧 **E-posta:** ihalilbesli@gmail.com
🔗 **LinkedIn:** [linkedin.com/in/ibrahim-halil-beşli-3079ab223](https://www.linkedin.com/in/ibrahim-halil-be%C5%9Fli-3079ab223/)




