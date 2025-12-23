import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MarketService {

  private BASE_URL = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  getCurrentPrice(symbol: string): Observable<any> {
    return this.http.get(
      `${this.BASE_URL}/stock/${symbol}`
    );
  }

  getInsiderTransactions(symbol: string): Observable<any> {
    return this.http.get(
      `${this.BASE_URL}/stock/insidertransaction/${symbol}`
    );
  }
}
