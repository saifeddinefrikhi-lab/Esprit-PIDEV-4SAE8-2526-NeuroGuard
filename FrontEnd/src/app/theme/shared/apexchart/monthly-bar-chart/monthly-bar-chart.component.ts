// angular import
import { Component, OnInit, viewChild, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';

// project import

// third party
import { ChartComponent, ApexOptions } from 'ng-apexcharts';

@Component({
  selector: 'app-monthly-bar-chart',
  standalone: true,
  imports: [CommonModule, ChartComponent],
  templateUrl: './monthly-bar-chart.component.html',
  styleUrl: './monthly-bar-chart.component.scss'
})
export class MonthlyBarChartComponent implements OnInit, OnChanges {
  // public props
  @Input() chartData: number[] = [0, 86, 28, 115, 48, 210, 136];
  @Input() chartCategories: string[] = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
  @Input() seriesName: string = 'Consultations';

  chart = viewChild.required<ChartComponent>('chart');
  chartOptions!: Partial<ApexOptions>;

  // life cycle hook
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
        height: 450,
        type: 'area',
        toolbar: {
          show: false
        },
        background: 'transparent'
      },
      dataLabels: {
        enabled: false
      },
      colors: ['#4f46e5', '#10b981'],
      series: [
        {
          name: this.seriesName,
          data: this.chartData
        }
      ],
      stroke: {
        curve: 'smooth',
        width: 3
      },
      fill: {
        type: 'gradient',
        gradient: {
          shadeIntensity: 1,
          opacityFrom: 0.4,
          opacityTo: 0.1,
          stops: [0, 90, 100]
        }
      },
      xaxis: {
        categories: this.chartCategories,
        labels: {
          style: {
            colors: this.chartCategories.map(() => '#94a3b8'),
            fontSize: '12px'
          }
        },
        axisBorder: {
          show: false
        },
        axisTicks: {
          show: false
        }
      },
      yaxis: {
        labels: {
          style: {
            colors: ['#94a3b8'],
            fontSize: '12px'
          }
        }
      },
      grid: {
        borderColor: '#f1f5f9',
        strokeDashArray: 4
      },
      tooltip: {
        theme: 'light',
        x: {
          show: true
        }
      }
    };
  }

  // public method
  toggleActive(value: string) {
    this.chartOptions.series = [
      {
        name: 'Page Views',
        data: value === 'month' ? [76, 85, 101, 98, 87, 105, 91, 114, 94, 86, 115, 35] : [31, 40, 28, 51, 42, 109, 100]
      },
      {
        name: 'Sessions',
        data: value === 'month' ? [110, 60, 150, 35, 60, 36, 26, 45, 65, 52, 53, 41] : [11, 32, 45, 32, 34, 52, 41]
      }
    ];
    const xaxis = { ...this.chartOptions.xaxis };
    xaxis.categories =
      value === 'month'
        ? ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
        : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    xaxis.tickAmount = value === 'month' ? 11 : 7;
    this.chartOptions = { ...this.chartOptions, xaxis };
    if (value === 'month') {
      document.querySelector('.chart-income.month')?.classList.add('active');
      document.querySelector('.chart-income.week')?.classList.remove('active');
    } else {
      document.querySelector('.chart-income.week')?.classList.add('active');
      document.querySelector('.chart-income.month')?.classList.remove('active');
    }
  }
}
