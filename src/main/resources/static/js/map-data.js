// Plik: js/map-data.js

export const MAP_CONFIG = {
    initialCoords: [52.23, 21.01],
    initialZoom: 6
};

export const CATEGORIES = {
    lake: { name: 'Jezioro', icon: 'line-squiggle', color: '#3b82f6' },
    river: { name: 'Rzeka', icon: 'waves', color: '#0ea5e9' },
    synop: { name: 'Stacja Synop', icon: 'thermometer', color: '#f59e0b' },
    meteo: { name: 'Stacja Meteo', icon: 'cloud-sun-rain', color: '#8b5cf6' },
    hydro: { name: 'Stacja Hydro', icon: 'droplets', color: '#06b6d4' },
    oxbow: { name: 'Starorzecze', icon: 'line-squiggle', color: '#309129' },
    launch: { name: 'Miejsce wodowania', icon: 'anchor', color: '#ff5900' },
    fishery: { name: 'Łowisko', icon: 'fish', color: '#b568f4' },
    commercial: { name: 'Łowisko komercyjne', icon: 'dollar-sign', color: '#ec4899' },
    me: { name: 'Moja lokalizacja', icon: 'circle-user', color: '#22c55e' }
};
