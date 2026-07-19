# 🛒 ecommerce-backend

> Event-driven, production-kalitesinde bir e-ticaret backend'i. Sipariş akışı Kafka
> üzerinden yönetilir; ürün listeleri Redis ile cache'lenir; kimlik doğrulama JWT ile yapılır.

🚧 **Bu proje aktif geliştirme aşamasında.**

[![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-database-blue?logo=postgresql)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-cache-red?logo=redis)](https://redis.io/)
[![Kafka](https://img.shields.io/badge/Kafka-event--driven-black?logo=apachekafka)](https://kafka.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-compose-2496ED?logo=docker)](https://www.docker.com/)
[![CI](https://img.shields.io/badge/CI-GitHub%20Actions-blue?logo=githubactions)](https://github.com/features/actions)

---

## 📖 Ne Yapıyor?

`ecommerce-backend`, kullanıcı kaydı/girişi, ürün kataloğu, sipariş oluşturma ve
sipariş sonrası asenkron iş akışlarını (stok güncelleme, bildirim) event-driven
bir mimariyle yöneten bir REST API'dir. Sipariş oluşturulduğunda bir Kafka event'i
fırlatılır; stok ve bildirim servisleri bu event'i bağımsız olarak tüketir.

## 🏗️ Mimari

```
                     ┌──────────────┐
                     │    Client    │
                     └──────┬───────┘
                            │ REST (JWT)
                            ▼
                  ┌───────────────────┐
                  │   Spring Boot API │
                  │  (Auth/Product/   │
                  │      Order)       │
                  └───┬───────┬───────┘
                      │       │
           ┌──────────┘       └──────────┐
           ▼                             ▼
   ┌───────────────┐             ┌───────────────┐
   │  PostgreSQL    │             │     Redis      │
   │ (kalıcı veri)  │             │  (ürün cache)  │
   └───────────────┘             └───────────────┘
           │
           │ OrderCreatedEvent
           ▼
   ┌───────────────────┐
   │       Kafka        │
   └──────┬─────┬───────┘
          │     │
          ▼     ▼
   ┌──────────┐ ┌──────────────┐
   │  Stok     │ │  Bildirim     │
   │  Consumer │ │  Consumer     │
   └──────────┘ └──────────────┘
```

## 🧰 Teknolojiler

- **Java 17** + **Spring Boot 3** — uygulama çatısı
- **PostgreSQL** — ana veritabanı
- **Redis** — ürün listesi cache
- **Kafka** — event-driven sipariş akışı (producer/consumer)
- **Docker + Docker Compose** — yerel geliştirme ve deploy
- **GitHub Actions** — CI/CD pipeline
- **JWT** — authentication
- **Swagger/OpenAPI** — API dokümantasyonu
- **MapStruct + Lombok** — DTO mapping ve boilerplate azaltma
- **Flyway** — veritabanı migration yönetimi

## 🚀 Kurulum

> ⚠️ docker-compose.yml henüz eklenmedi. Bu bölüm ilerleyen bir adımda
> tamamlandığında güncellenecek.

```bash
git clone https://github.com/yusufguc/ecommerce-backend.git
cd ecommerce-backend
cp .env.example .env   # kendi değerlerinizi girin
docker-compose up -d
```

Uygulama ayağa kalktığında:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## 📡 API Endpoints

> Bu tablo geliştirme ilerledikçe doldurulacak.

| Method | Endpoint | Açıklama | Auth |
|--------|----------|----------|------|
| POST   | `/api/auth/register` | Yeni kullanıcı kaydı | ❌ |
| POST   | `/api/auth/login`    | Giriş, JWT üretimi   | ❌ |
| GET    | `/api/products`      | Ürün listesi (cache'li) | ❌ |
| POST   | `/api/orders`        | Sipariş oluşturma    | ✅ |

## 📸 Ekran Görüntüleri / Postman Collection

> İlerleyen bir adımda eklenecek.

## 📄 Lisans

MIT
