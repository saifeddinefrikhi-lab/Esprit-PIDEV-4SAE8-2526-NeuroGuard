// angular import
import { Component, viewChild, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';

// project import

// third party
import { NgApexchartsModule, ChartComponent, ApexOptions } from 'ng-apexcharts';

@Component({
  selector: 'app-analytics-chart',
  imports: [NgApexchartsModule],
  templateUrl: './analytics-chart.component.html',
  styleUrl: './analytics-chart.component.scss'
})
export class AnalyticsChartComponent implements OnInit, OnChanges {
  // public props
  @Input() chartData: number[] = [];
  @Input() chartCategories: string[] = [];
  @Input() seriesName: string = 'Consultations';

  chart = viewChild.required<ChartComponent>('chart');
  chartOptions!: Partial<ApexOptions>;

  ngOnInit(): void {
    this.updateOptions();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if ((changes['chartData'] || changes['chartCategories']) && this.chartOptions) {
      this.updateOptions();
    }
  }

  updateOptions(): void {
    this.chartOptions = {
      chart: {
        type: 'line',
        height: 340,
        toolbar: {
          show: false
        },
        background: 'transparent'
      },
      plotOptions: {
        bar: {
          columnWidth: '45%',
          borderRadius: 4
        }
      },
      colors: ['#4f46e5'],
      stroke: {
        curve: 'smooth',
        width: 3
      },
      grid: {
        strokeDashArray: 4,
        borderColor: '#f1f5f9'
      },
      series: [
        {
          name: this.seriesName,
          data: this.chartData.length > 0 ? this.chartData : [58, 90, 38, 83, 63, 75, 35, 55]
        }
      ],
      xaxis: {
        type: 'category',
        categories: this.chartCategories.length > 0 ? this.chartCategories : ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        labels: {
          style: {
            colors: (this.chartCategories.length > 0 ? this.chartCategories : [1,2,3,4,5,6,7,8,9,10,11,12]).map(() => '#94a3b8'),
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
        show: true,
        labels: {
          style: {
            colors: ['#94a3b8'],
            fontSize: '12px'
          }
        }
      },
      tooltip: {
        theme: 'light'
      }
    };
  }
}
