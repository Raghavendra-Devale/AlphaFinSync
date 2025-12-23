import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StockService {

  private BASE_URL = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getBySymbol(symbol: string): Observable<any> {
    return this.http.get(
      `${this.BASE_URL}/stocks/getBySymbol?symbol=${symbol}`
    );
  }

  getAllStocks(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.BASE_URL}/stocks/getAllStocks`
    );
  }

  getStockCount(): Observable<number> {
    return this.http.get<number>(
      `${this.BASE_URL}/stocks/count`
    );
  }

  getStocksBySector(sector: string): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.BASE_URL}/sector/getStocksBySector?sector=${sector}`
    );
  }

  getLatestPrice(symbol: string): Observable<any[]>{
  return this.http.get<any[]>(`${this.BASE_URL}/stock/${symbol}`);
}

}
