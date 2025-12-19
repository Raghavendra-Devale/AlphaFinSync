# AlphaFinSync  

**AlphaFinSync** is a backend microservice that **fetches, processes, and stores stock financial data** from external APIs (e.g., *Alpha Vantage*) into a PostgreSQL database.  
It provides REST APIs to manage and query financial metrics such as **EPS, revenue, and other quarterly/yearly data**.  

---

## ✨ Features  
- 📡 **Data Integration** – Fetches stock data from Alpha Vantage API  
- 🔄 **Upsert Mechanism** – Inserts or updates financials without duplicates  
- 💾 **Database Persistence** – Stores stock financials in PostgreSQL  
- ⚡ **REST APIs** – Exposes endpoints for syncing and retrieving financial data  
- 🧪 **Tested with Postman** – Simple and reliable API testing  

---

## 🛠 Tech Stack  
- **Java 21**  
- **Spring Boot 3 (Web, JDBC)**  
- **PostgreSQL** with HikariCP  
- **Maven** for dependency management  
- **Postman** for API testing  

---

## 📊 Example Use Cases  
- Sync quarterly financial data for a stock symbol (e.g., `AAPL`).  
- Retrieve EPS history for analysis.  
- Maintain clean and consistent stock financial records.  

---

## 🚀 How to Run  

### 1️⃣ Clone the repository  
```bash
git clone https://github.com/your-username/AlphaFinSync.git
cd AlphaFinSync
>>>>>>> 84c4bdafc30b5e5af9296866a051a9c6bd06dd1e
