// Plik: js/map-data.js

export const MAP_CONFIG = {
    initialCoords: [52.23, 21.01],
    initialZoom: 6
};

export const CATEGORIES = {
    lake: { name: 'Jezioro', icon: 'droplet', color: '#3b82f6' },
    reservoir: { name: 'Zbiornik zaporowy', icon: 'hexagon', color: '#0284c7' },
    river: { name: 'Rzeka', icon: 'waves', color: '#0ea5e9' },
    synop: { name: 'Stacja Synop', icon: 'thermometer', color: '#f59e0b' },
    meteo: { name: 'Stacja Meteo', icon: 'cloud-sun-rain', color: '#8b5cf6' },
    hydro: { name: 'Stacja Hydro', icon: 'droplets', color: '#06b6d4' },
    oxbow: { name: 'Starorzecze', icon: 'worm', color: '#309129' },
    slip: { name: 'Slip (Wodowanie)', icon: 'anchor', color: '#ff5900' },
    specific_spot: { name: 'Miejscówka', icon: 'fish', color: '#b568f4' },
    // fishery: { name: 'Łowisko', icon: 'fish', color: '#b568f4' },
    commercial: { name: 'Łowisko komercyjne', icon: 'coins', color: '#ec4899' },
    restriction: { name: 'Zakazy i ograniczenia', icon: 'shield-off', color: '#ef4444' },
    partial_limit: { name: 'Ograniczenie', icon: 'shield-off', color: 'rgba(255,89,0,0.94)' },
    me: { name: 'Moja lokalizacja', icon: 'circle-user', color: '#22c55e' }
};
