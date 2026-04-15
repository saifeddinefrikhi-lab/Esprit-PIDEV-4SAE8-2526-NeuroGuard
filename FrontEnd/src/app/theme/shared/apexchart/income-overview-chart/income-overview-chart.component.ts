// angular import
import { Component, OnInit, viewChild, Input, OnChanges, SimpleChanges } from '@angular/core';

// project import

// third party
import { NgApexchartsModule, ChartComponent, ApexOptions } from 'ng-apexcharts';
import { CardComponent } from 'src/app/theme/shared/components/card/card.component';

@Component({
  selector: 'app-income-overview-chart',
  imports: [CardComponent, NgApexchartsModule],
  templateUrl: './income-overview-chart.component.html',
  styleUrl: './income-overview-chart.component.scss'
})
export class IncomeOverviewChartComponent implements OnInit, OnChanges {
  // public props
  @Input() chartData: number[] = [80, 95, 70, 42, 65, 55, 78];
  @Input() chartCategories: string[] = ['Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa', 'Su'];
  @Input() seriesName: string = 'Revenue';

  chart = viewChild.required<ChartComponent>('chart');
  chartOptions!: Partial<ApexOptions>;

  ngOnInit() {
    this.updateOptions();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['chartData'] || changes['chartCategories']) && this.chartOptions) {
      this.updateOptions();
    }
  }

  private updateOptions(): void {
    this.chartOptions = {
      chart: {
        type: 'bar',
        height: 365,
        toolbar: {
          show: false
        },
        background: 'transparent'
      },
      plotOptions: {
        bar: {
          columnWidth: '40%',
          borderRadius: 6,
          distributed: true
        }
      },
      dataLabels: {
        enabled: false
      },
      series: [
        {
          name: this.seriesName,
          data: this.chartData
        }
      ],
      stroke: {
        show: true,
        width: 2,
        colors: ['transparent']
      },
      xaxis: {
        categories: this.chartCategories,
        axisBorder: {
          show: false
        },
        axisTicks: {
          show: false
        },
        labels: {
          style: {
            colors: this.chartCategories.map(() => '#94a3b8'),
            fontSize: '11px'
          }
        }
      },
      yaxis: {
        show: true,
        labels: {
          style: {
            colors: ['#94a3b8'],
            fontSize: '11px'
          }
        }
      },
      colors: ['#0ea5e9', '#6366f1', '#10b981', '#f59e0b', '#f43f5e', '#8b5cf6', '#ec4899'],
      grid: {
        show: true,
        borderColor: '#f1f5f9',
        strokeDashArray: 4
      },
      tooltip: {
        theme: 'light'
      }
    };
  }
}
