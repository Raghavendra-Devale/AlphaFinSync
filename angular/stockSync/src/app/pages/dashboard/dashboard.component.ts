import { CommonModule } from '@angular/common';
import { Component, AfterViewInit, OnDestroy } from '@angular/core';
import * as echarts from 'echarts';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements AfterViewInit, OnDestroy {

  dashboardData = {
    symbol: 'AAPL',
    companyName: 'Apple Inc.',
    lastClosePrice: 270.97,
    dayChangePercent: '+1.12%',
    marketStatus: 'Closed',
    sector: 'Technology',
    sharesOutstanding: '200B',
    lastSynced: '22 Dec 2025'
  };

  private chartInstance!: echarts.ECharts;
  private pieChart!: echarts.ECharts;

  // ✅ Dummy API data 
  private dummyStockApiData = {
    "Meta Data": {
      "2. Symbol": "AAPL"
    },
    "Time Series (Daily)": {
      "2025-12-22": { "4. close": "270.97", "5. volume": "36571827" },
      "2025-12-19": { "4. close": "273.67", "5. volume": "144632048" },
      "2025-12-18": { "4. close": "232.19", "5. volume": "51630721" },
      "2025-12-17": { "4. close": "271.84", "5. volume": "50138743" },
      "2025-12-16": { "4. close": "274.61", "5. volume": "37648628" }
    }
  };

  ngAfterViewInit(): void {
    const timeSeries =
      this.dummyStockApiData['Time Series (Daily)'];

    const { dates, closePrices, volumes } =
      this.transformTimeSeriesData(timeSeries);

    this.initLineChart(dates, closePrices);
    this.initPieChart(volumes);
  }

  // ✅ Transform function 
  private transformTimeSeriesData(timeSeries: any) {
    const dates: string[] = [];
    const closePrices: number[] = [];
    const volumes: { name: string; value: number }[] = [];

    Object.keys(timeSeries)
      .sort()
      .forEach(date => {
        dates.push(date);
        closePrices.push(Number(timeSeries[date]['4. close']));
        volumes.push({
          name: date,
          value: Number(timeSeries[date]['5. volume'])
        });
      });

    return { dates, closePrices, volumes };
  }

  // ✅  signature (accepts data)
  private initLineChart(dates: string[], prices: number[]): void {
    const chartElement = document.getElementById('stockChart');
    if (!chartElement) return;

    this.chartInstance = echarts.init(chartElement);

    this.chartInstance.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: dates
      },
      yAxis: {
        type: 'value',
        boundaryGap: true
      },
      series: [
        {
          type: 'line',
          smooth: true,
          data: prices
        }
      ]
    });
  }

  // ✅  signature (accepts data)
  private initPieChart(volumes: { name: string; value: number }[]): void {
    const el = document.getElementById('sectorChart');
    if (!el) return;

    this.pieChart = echarts.init(el);

    this.pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [
        {
          name: 'AAPL Trading Volume',
          type: 'pie',
          radius: '60%',
          data: volumes
        }
      ]
    });
  }

  ngOnDestroy(): void {
    this.chartInstance?.dispose();
    this.pieChart?.dispose();
  }
}
