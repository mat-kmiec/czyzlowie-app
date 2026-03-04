document.addEventListener('DOMContentLoaded', () => {
    lucide.createIcons();

    function getWindDirectionName(degrees) {
        if (degrees === null || degrees === undefined) return '--';
        if (degrees >= 337 || degrees < 22) return "N (Północny)";
        if (degrees >= 22 && degrees < 67) return "NE (Płn-Wsch)";
        if (degrees >= 67 && degrees < 112) return "E (Wschodni)";
        if (degrees >= 112 && degrees < 157) return "SE (Płd-Wsch)";
        if (degrees >= 157 && degrees < 202) return "S (Południowy)";
        if (degrees >= 202 && degrees < 247) return "SW (Płd-Zach)";
        if (degrees >= 247 && degrees < 292) return "W (Zachodni)";
        if (degrees >= 292 && degrees < 337) return "NW (Płn-Zach)";
        return "Zmienny";
    }

    const heroWindDirSpan = document.getElementById('hero-wind-dir');
    if (heroWindDirSpan) {
        const deg = parseInt(heroWindDirSpan.getAttribute('data-dir'), 10);
        heroWindDirSpan.innerText = `${deg}° (${getWindDirectionName(deg).split(' ')[0]})`;
    }

    const stealthWindSpan = document.getElementById('stealth-wind-dir');
    if (stealthWindSpan) {
        const deg = parseInt(stealthWindSpan.getAttribute('data-dir')) || 0;
        stealthWindSpan.innerText = getWindDirectionName(deg) + " (" + deg + "°)";
    }

    const gaugeBar = document.getElementById('bite-gauge-bar');
    const gaugeText = document.getElementById('bite-gauge-text');
    const scoreWrapper = document.querySelector('.gauge-container');

    if (scoreWrapper && gaugeBar && gaugeText) {
        const score = parseFloat(scoreWrapper.getAttribute('data-score')) || 0;
        const maxOffset = 565;
        const targetOffset = maxOffset - (maxOffset * score / 100);

        setTimeout(() => {
            gaugeBar.style.strokeDashoffset = targetOffset;
            let current = 0;
            const interval = setInterval(() => {
                current += Math.ceil(score / 30) || 1;
                if (current >= score) {
                    current = score;
                    clearInterval(interval);
                }
                gaugeText.innerText = current;
            }, 30);
        }, 300);
    }

    const synopData = window.synopData || [];

    if (synopData && synopData.length > 0) {
        const labels = synopData.map(snap => {
            const d = new Date(snap.timestamp);
            return d.toLocaleDateString('pl-PL', {day: '2-digit', month: '2-digit'}) + ' ' + d.toLocaleTimeString('pl-PL', {hour: '2-digit', minute:'2-digit'});
        });

        const datasetsMap = {
            'temperature': {
                data: synopData.map(s => s.temperature), label: 'Temperatura (°C)',
                borderColor: '#ef4444', backgroundColor: 'rgba(239, 68, 68, 0.1)',
                type: 'line', fill: true, showLine: true, pointRadius: 0
            },
            'pressure': {
                data: synopData.map(s => s.pressure), label: 'Ciśnienie (hPa)',
                borderColor: '#c084fc', backgroundColor: 'rgba(192, 132, 252, 0.1)',
                type: 'line', fill: true, showLine: true, pointRadius: 0
            },
            'windSpeed': {
                data: synopData.map(s => s.windSpeed), label: 'Prędkość Wiatru (km/h)',
                borderColor: '#10b981', backgroundColor: 'rgba(16, 185, 129, 0.1)',
                type: 'line', fill: true, showLine: true, pointRadius: 0
            },
            'windDirection': {
                data: synopData.map(s => s.windDirection), label: 'Kierunek Wiatru',
                borderColor: '#f59e0b', backgroundColor: '#f59e0b',
                type: 'line', fill: false, showLine: false,
                pointRadius: 4, pointHoverRadius: 6
            },
            'humidity': {
                data: synopData.map(s => s.humidity), label: 'Wilgotność (%)',
                borderColor: '#38bdf8', backgroundColor: 'rgba(56, 189, 248, 0.2)',
                type: 'line', fill: true, showLine: true, pointRadius: 0
            },
            'precipitation': {
                data: synopData.map(s => s.precipitation || 0), label: 'Opady (mm)',
                borderColor: '#6366f1', backgroundColor: 'rgba(99, 102, 241, 0.8)',
                type: 'bar', fill: false, showLine: true, pointRadius: 0
            }
        };

        const ctx = document.getElementById('dynamicChart').getContext('2d');
        Chart.defaults.color = '#94a3b8'; Chart.defaults.font.family = 'JetBrains Mono';

        let currentMetric = 'temperature';

        let currentChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: datasetsMap['temperature'].label,
                    data: datasetsMap['temperature'].data,
                    borderColor: datasetsMap['temperature'].borderColor,
                    backgroundColor: datasetsMap['temperature'].backgroundColor,
                    borderWidth: 2, tension: 0.4, fill: true, pointRadius: 0, pointHitRadius: 10
                }]
            },
            options: {
                responsive: true, maintainAspectRatio: false,
                interaction: { mode: 'index', intersect: false },
                plugins: {
                    legend: { display: false },
                    zoom: {
                        pan: {
                            enabled: true,
                            mode: 'x',
                            modifierKey: 'ctrl'
                        },
                        zoom: {
                            wheel: { enabled: true },
                            pinch: { enabled: true },
                            mode: 'x'
                        }
                    },
                    tooltip: {
                        backgroundColor: 'rgba(15, 23, 42, 0.95)', titleColor: '#fff', bodyColor: '#fff',
                        borderColor: 'rgba(255,255,255,0.1)', borderWidth: 1, padding: 12,
                        callbacks: {
                            label: function(context) {
                                let label = context.dataset.label || '';
                                if (label) { label += ': '; }
                                if (context.parsed.y !== null) {
                                    if (currentMetric === 'windDirection') {
                                        label += context.parsed.y + '° (' + getWindDirectionName(context.parsed.y) + ')';
                                    } else { label += context.parsed.y; }
                                }
                                return label;
                            }
                        }
                    }
                },
                scales: {
                    x: { grid: { display: false } },
                    y: {
                        grid: { color: 'rgba(255,255,255,0.05)' },
                        ticks: {
                            callback: function(value) {
                                if (currentMetric === 'windDirection') {
                                    if (value === 0 || value === 360) return 'N (0°)';
                                    if (value === 90) return 'E (90°)';
                                    if (value === 180) return 'S (180°)';
                                    if (value === 270) return 'W (270°)';
                                    return value;
                                }
                                return value;
                            }
                        }
                    }
                }
            }
        });

        const buttons = document.querySelectorAll('.chart-tab-btn');
        buttons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                buttons.forEach(b => { b.classList.remove('active'); const icon = b.querySelector('i'); if (icon) icon.style.color = ''; });
                btn.classList.add('active');

                currentMetric = btn.getAttribute('data-metric');
                const config = datasetsMap[currentMetric];

                currentChart.config.type = config.type === 'bar' ? 'bar' : 'line';
                currentChart.data.datasets[0].label = config.label;
                currentChart.data.datasets[0].data = config.data;
                currentChart.data.datasets[0].borderColor = config.borderColor;
                currentChart.data.datasets[0].backgroundColor = config.backgroundColor;
                currentChart.data.datasets[0].fill = config.fill;
                currentChart.data.datasets[0].showLine = config.showLine;
                currentChart.data.datasets[0].pointRadius = config.pointRadius;
                currentChart.data.datasets[0].pointBackgroundColor = config.backgroundColor;

                if (currentMetric === 'precipitation') {
                    currentChart.options.scales.y.beginAtZero = true; currentChart.options.scales.y.suggestedMax = 5;
                    currentChart.options.scales.y.max = undefined; currentChart.options.scales.y.stepSize = undefined;
                } else if (currentMetric === 'windDirection') {
                    currentChart.options.scales.y.beginAtZero = true; currentChart.options.scales.y.max = 360; currentChart.options.scales.y.stepSize = 90;
                } else if (currentMetric === 'humidity') {
                    currentChart.options.scales.y.beginAtZero = true; currentChart.options.scales.y.max = 100; currentChart.options.scales.y.stepSize = undefined;
                } else {
                    currentChart.options.scales.y.beginAtZero = false; currentChart.options.scales.y.max = undefined; currentChart.options.scales.y.stepSize = undefined;
                }

                currentChart.resetZoom();
                currentChart.update();
            });
        });
    }

    const hydroData = window.hydroData || [];

    if (hydroData && hydroData.length > 0) {
        const hLabels = hydroData.map(snap => {
            const d = new Date(snap.timestamp);
            return d.toLocaleDateString('pl-PL', {day: '2-digit', month: '2-digit'}) + ' ' + d.toLocaleTimeString('pl-PL', {hour: '2-digit', minute:'2-digit'});
        });

        const hydroDatasetsMap = {
            'waterLevel': {
                data: hydroData.map(h => h.waterLevel), label: 'Poziom Wody (cm)',
                borderColor: '#0ea5e9', backgroundColor: 'rgba(14, 165, 233, 0.1)',
            },
            'discharge': {
                data: hydroData.map(h => h.discharge), label: 'Przepływ Nurtu (m³/s)',
                borderColor: '#8b5cf6', backgroundColor: 'rgba(139, 92, 246, 0.1)',
            },
            'waterTemperature': {
                data: hydroData.map(h => h.waterTemperature), label: 'Temperatura Wody (°C)',
                borderColor: '#f43f5e', backgroundColor: 'rgba(244, 63, 94, 0.1)',
            }
        };

        const ctxHydro = document.getElementById('hydroChartCanvas');
        if(ctxHydro) {
            let currentHydroMetric = 'waterLevel';
            let hydroChart = new Chart(ctxHydro.getContext('2d'), {
                type: 'line',
                data: {
                    labels: hLabels,
                    datasets: [{
                        label: hydroDatasetsMap['waterLevel'].label,
                        data: hydroDatasetsMap['waterLevel'].data,
                        borderColor: hydroDatasetsMap['waterLevel'].borderColor,
                        backgroundColor: hydroDatasetsMap['waterLevel'].backgroundColor,
                        borderWidth: 2, tension: 0.4, fill: true, pointRadius: 0, pointHitRadius: 10
                    }]
                },
                options: {
                    responsive: true, maintainAspectRatio: false,
                    interaction: { mode: 'index', intersect: false },
                    plugins: {
                        legend: { display: false },
                        zoom: {
                            pan: { enabled: true, mode: 'x', modifierKey: 'ctrl' },
                            zoom: { wheel: { enabled: true }, pinch: { enabled: true }, mode: 'x' }
                        },
                        tooltip: {
                            backgroundColor: 'rgba(15, 23, 42, 0.95)', titleColor: '#fff', bodyColor: '#fff',
                            borderColor: 'rgba(255,255,255,0.1)', borderWidth: 1, padding: 12,
                            callbacks: {
                                label: function(context) {
                                    return context.dataset.label + ': ' + context.parsed.y;
                                }
                            }
                        }
                    },
                    scales: {
                        x: { grid: { display: false } },
                        y: { grid: { color: 'rgba(255,255,255,0.05)' } }
                    }
                }
            });

            const hBtns = document.querySelectorAll('.badge-btn');
            hBtns.forEach(btn => {
                btn.addEventListener('click', () => {
                    hBtns.forEach(b => b.classList.remove('active'));
                    btn.classList.add('active');

                    currentHydroMetric = btn.getAttribute('data-hydro-metric');
                    const config = hydroDatasetsMap[currentHydroMetric];

                    hydroChart.data.datasets[0].label = config.label;
                    hydroChart.data.datasets[0].data = config.data;
                    hydroChart.data.datasets[0].borderColor = config.borderColor;
                    hydroChart.data.datasets[0].backgroundColor = config.backgroundColor;

                    hydroChart.resetZoom();
                    hydroChart.update();
                });
            });

            const hydroTabs = document.querySelectorAll('.hydro-tab-btn');
            hydroTabs.forEach(tab => {
                tab.addEventListener('click', () => {
                    hydroTabs.forEach(t => t.classList.remove('active'));
                    tab.classList.add('active');

                    document.querySelectorAll('.hydro-pane').forEach(p => p.style.display = 'none');
                    const targetId = tab.getAttribute('data-target');
                    document.getElementById(targetId).style.display = 'block';

                    if(targetId === 'hydro-chart') {
                        hydroChart.update();
                    }
                });
            });
        }
    }
});