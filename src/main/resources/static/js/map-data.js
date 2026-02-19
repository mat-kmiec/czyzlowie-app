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
    oxbow: { name: 'Starorzecze', icon: 'line-squiggle', color: '#10b981' },
    launch: { name: 'Miejsce wodowania', icon: 'anchor', color: '#ef4444' },
    fishery: { name: 'Łowisko', icon: 'fish', color: '#6366f1' },
    commercial: { name: 'Łowisko komercyjne', icon: 'dollar-sign', color: '#ec4899' },
    me: { name: 'Moja lokalizacja', icon: 'circle-user', color: '#22c55e' }
};

export const LOCATIONS = [
    { id: 1, name: 'Jezioro Śniardwy', cat: 'lake', lat: 53.75, lng: 21.73, url: '/lowisko/1' },
    { id: 2, name: 'Rzeka Bug (Włodawa)', cat: 'river', lat: 51.55, lng: 23.55, url: '/lowisko/2' },
    { id: 3, name: 'Łowisko Złota Rybka', cat: 'commercial', lat: 52.10, lng: 20.80, url: '/lowisko/3' },
    { id: 4, name: 'Stacja IMGW Warszawa', cat: 'synop', lat: 52.22, lng: 20.98, url: '/stacja/1' },
    { id: 6, name: 'Slip Zegrze1', cat: 'launch', lat: 52.46, lng: 22.03, url: '/slip/1' },
    { id: 7, name: 'Slip Zegrze2', cat: 'launch', lat: 52.46, lng: 23.03, url: '/slip/3' },
    { id: 8, name: 'Slip Zegrze3', cat: 'launch', lat: 52.46, lng: 24.03, url: '/slip/5' },
];