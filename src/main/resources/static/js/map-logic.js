import { MAP_CONFIG, CATEGORIES } from './map-data.js';

class MapApplication {
    constructor() {
        this.map = null;
        this.categories = CATEGORIES;
        this.locations = [];
        this.markersCache = {};

        const defaultInactive = ['me', 'hydro', 'meteo'];
        this.activeCategories = new Set(Object.keys(this.categories).filter(k => !defaultInactive.includes(k)));

        this.baseLayers = {
            standard: L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenStreetMap',
                maxZoom: 19
            }),
            satellite: L.tileLayer('https://mt1.google.com/vt/lyrs=y&x={x}&y={y}&z={z}', {
                attribution: 'Map data &copy; Google',
                maxZoom: 20
            }),
            terrain: L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenTopoMap',
                maxZoom: 17
            })
        };
        this.currentBaseLayer = 'standard';

        this.init();
    }

    async init() {
        this.initMap();
        this.initCustomControls();
        this.setupClusterGroup();
        this.renderLegend();

        await this.fetchLocationsFromApi();

        this.loadMarkers();
        this.updateMapMarkers();
        this.renderList(this.locations);

        this.attachEventListeners();

        if (window.lucide) {
            lucide.createIcons();
        }
    }

    async fetchLocationsFromApi() {
        try {
            const response = await fetch('/api/map/markers');
            if (!response.ok) throw new Error("Błąd pobierania danych");

            const data = await response.json();

            this.locations = data.map(marker => {
                const typeLower = marker.type.toLowerCase();
                let baseUrl = '/synop';

                if (typeLower === 'hydro') baseUrl = '/hydro';
                else if (typeLower === 'meteo') baseUrl = '/meteo';
                else if (['lake', 'river', 'commercial', 'oxbow'].includes(typeLower)) baseUrl = '/lowisko';
                else if (typeLower === 'launch') baseUrl = '/slip';

                return {
                    id: marker.id,
                    name: marker.name,
                    cat: typeLower,
                    lat: marker.lat,
                    lng: marker.lng,
                    url: `${baseUrl}/${marker.slug}`
                };
            });
        } catch (error) {
            console.error('Błąd ładowania danych mapy:', error);
            this.locations = [];
        }
    }

    initMap() {
        this.map = L.map('map', {
            zoomControl: false,
            preferCanvas: true
        }).setView(MAP_CONFIG.initialCoords, MAP_CONFIG.initialZoom);

        this.baseLayers[this.currentBaseLayer].addTo(this.map);
        this.map.on('locationfound', (e) => this.handleLocationFound(e));
    }

    initCustomControls() {
        const container = L.DomUtil.create('div', 'map-controls-container');

        const zoomGroup = L.DomUtil.create('div', 'map-controls-group', container);
        this.createBtn(zoomGroup, 'plus', 'Przybliż', () => this.map.zoomIn());
        this.createBtn(zoomGroup, 'minus', 'Oddal', () => this.map.zoomOut());

        this.createBtn(container, 'navigation', 'Moja lokalizacja', () => this.map.locate({setView: true, maxZoom: 14}));
        this.createBtn(container, 'expand', 'Resetuj widok', () => this.map.setView(MAP_CONFIG.initialCoords, MAP_CONFIG.initialZoom));

        const customControl = L.Control.extend({
            options: { position: 'topright' },
            onAdd: () => container
        });
        this.map.addControl(new customControl());
    }

    createBtn(parent, icon, title, onClick) {
        const btn = L.DomUtil.create('button', 'map-custom-btn', parent);
        btn.innerHTML = `<i data-lucide="${icon}"></i>`;
        btn.title = title;
        L.DomEvent.disableClickPropagation(btn);
        btn.onclick = onClick;
        return btn;
    }

    setupClusterGroup() {
        this.mainClusterGroup = L.markerClusterGroup({
            disableClusteringAtZoom: 14,
            maxClusterRadius: 50
        });
        this.map.addLayer(this.mainClusterGroup);
        this.mainClusterGroup.on('animationend', () => {
            if (window.lucide) lucide.createIcons();
        });

        this.mainClusterGroup.on('spiderfied', () => {
            if (window.lucide) lucide.createIcons();
        });

        this.map.on('zoomend', () => {
            if (window.lucide) {
                setTimeout(() => lucide.createIcons(), 50);
            }
        });
    }

    renderLegend() {
        const container = document.getElementById('legendContainer');
        if (!container) return;

        container.innerHTML = '';

        Object.entries(this.categories).forEach(([key, data]) => {
            if (key === 'me') return;

            const btn = document.createElement('button');
            btn.className = `legend-btn ${this.activeCategories.has(key) ? 'active' : ''}`;
            btn.style.setProperty('--color-indicator', data.color);
            btn.innerHTML = `<i data-lucide="${data.icon}"></i> ${data.name}`;

            btn.onclick = () => this.toggleCategory(key, btn);
            container.appendChild(btn);
        });
    }

    toggleCategory(key, btnElement) {
        if (this.activeCategories.has(key)) {
            this.activeCategories.delete(key);
            btnElement.classList.remove('active');
        } else {
            this.activeCategories.add(key);
            btnElement.classList.add('active');
        }

        this.filterListBySearch();
    }

    loadMarkers() {
        this.locations.forEach(loc => {
            const catData = this.categories[loc.cat];
            if (!catData) return;

            const htmlIcon = L.divIcon({
                className: 'custom-div-icon',
                html: `<div class="custom-map-marker" style="background-color: ${catData.color}">
                           <i data-lucide="${catData.icon}"></i>
                       </div>`,
                iconSize: [32, 32],
                iconAnchor: [16, 16]
            });

            const marker = L.marker([loc.lat, loc.lng], { icon: htmlIcon });

            const popupContent = `
                <div class="popup-header">
                    <div class="popup-title">${loc.name}</div>
                    <div class="popup-badge" style="background: ${catData.color}25; color: ${catData.color}; border: 1px solid ${catData.color}40;">
                        <i data-lucide="${catData.icon}"></i> ${catData.name}
                    </div>
                </div>
                <div class="popup-body">
                    <a href="${loc.url}" class="popup-btn">Szczegóły <i data-lucide="arrow-right"></i></a>
                </div>
            `;

            marker.bindPopup(popupContent, {
                className: 'custom-popup',
                autoPanPadding: [50, 50]
            });

            marker.on('popupopen', () => {
                if (window.lucide) lucide.createIcons();
            });

            this.markersCache[loc.id] = marker;
        });
    }

    updateMapMarkers() {
        const query = document.getElementById('mapSearch')?.value.toLowerCase() || '';
        const markersToAdd = [];

        this.locations.forEach(loc => {
            const catData = this.categories[loc.cat];
            if (!catData) return;

            const matchesSearch = loc.name.toLowerCase().includes(query);
            const isCategoryActive = this.activeCategories.has(loc.cat);

            if (matchesSearch && isCategoryActive) {
                markersToAdd.push(this.markersCache[loc.id]);
            }
        });

        this.mainClusterGroup.clearLayers();
        this.mainClusterGroup.addLayers(markersToAdd);
    }

    renderList(itemsToRender) {
        const container = document.getElementById('locationsList');
        const countEl = document.getElementById('locCount');

        if (countEl) countEl.innerText = itemsToRender.length;
        if (!container) return;

        container.innerHTML = itemsToRender.map(loc => {
            const catData = this.categories[loc.cat];
            if (!catData) return '';

            return `
                <div class="location-item" data-id="${loc.id}">
                    <div class="custom-map-marker" style="background-color: ${catData.color}; width: 30px; height: 30px; position: static; flex-shrink: 0;">
                        <i data-lucide="${catData.icon}"></i>
                    </div>
                    <div class="loc-details">
                        <span class="loc-name">${loc.name}</span>
                        <span class="loc-cat">${catData.name}</span>
                    </div>
                </div>
            `;
        }).join('');

        container.onclick = (e) => {
            const item = e.target.closest('.location-item');
            if (item) {
                const locId = item.dataset.id;
                const loc = this.locations.find(l => l.id == locId);
                if (loc) {
                    this.map.flyTo([loc.lat, loc.lng], 14, { duration: 1.5 });

                    setTimeout(() => {
                        if (this.markersCache[locId]) {
                            this.mainClusterGroup.zoomToShowLayer(this.markersCache[locId], () => {
                                this.markersCache[locId].openPopup();
                            });
                        }
                    }, 1500);

                    if (window.innerWidth <= 991) {
                        document.getElementById('filterSidebar').classList.remove('active');
                    }
                }
            }
        };

        if (window.lucide) {
            lucide.createIcons();
        }
    }

    attachEventListeners() {
        const searchInput = document.getElementById('mapSearch');
        const searchBtn = document.getElementById('searchGlobalBtn');

        if(searchInput) {
            searchInput.addEventListener('input', () => this.filterListBySearch());

            searchInput.addEventListener('keypress', (e) => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    this.searchGlobal(searchInput.value);
                }
            });
        }

        if(searchBtn) {
            searchBtn.addEventListener('click', (e) => {
                e.preventDefault();
                this.searchGlobal(searchInput.value);
            });
        }

        const btnOpen = document.getElementById('mobileFilterBtn');
        const btnClose = document.getElementById('closeSidebarBtn');
        const sidebar = document.getElementById('filterSidebar');

        if(btnOpen && sidebar) btnOpen.addEventListener('click', () => sidebar.classList.add('active'));
        if(btnClose && sidebar) btnClose.addEventListener('click', () => sidebar.classList.remove('active'));

        const layerButtons = document.querySelectorAll('.layer-btn');
        layerButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const targetBtn = e.currentTarget;
                const layerName = targetBtn.dataset.layer;

                if (this.currentBaseLayer === layerName) return;

                this.map.removeLayer(this.baseLayers[this.currentBaseLayer]);
                this.baseLayers[layerName].addTo(this.map);
                this.currentBaseLayer = layerName;

                layerButtons.forEach(b => b.classList.remove('active'));
                targetBtn.classList.add('active');
            });
        });
    }

    async searchGlobal(query) {
        if (query.length < 3) return;

        try {
            const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}`);
            const results = await response.json();

            if (results && results.length > 0) {
                const result = results[0];

                const bbox = result.boundingbox;
                const bounds = [
                    [bbox[0], bbox[2]],
                    [bbox[1], bbox[3]]
                ];

                this.map.fitBounds(bounds);
                this.updateMapMarkers();

                if (window.innerWidth <= 991) {
                    document.getElementById('filterSidebar').classList.remove('active');
                }
            } else {
                alert("Nie znaleziono takiej miejscowości na mapie.");
            }
        } catch (error) {
            console.error("Błąd wyszukiwania globalnego:", error);
        }
    }

    filterListBySearch() {
        const query = document.getElementById('mapSearch').value.toLowerCase();

        const filtered = this.locations.filter(loc => {
            const catData = this.categories[loc.cat];
            if (!catData) return false;

            const matchesSearch = loc.name.toLowerCase().includes(query);
            const isCategoryActive = this.activeCategories.has(loc.cat);

            return matchesSearch && isCategoryActive;
        });

        this.renderList(filtered);
        this.updateMapMarkers();
    }

    handleLocationFound(e) {
        if(this.gpsMarker) this.map.removeLayer(this.gpsMarker);

        const meCat = this.categories['me'];
        const htmlIcon = L.divIcon({
            className: 'custom-div-icon',
            html: `<div class="custom-map-marker" style="background-color: ${meCat.color}; animation: pulse 2s infinite;">
                       <i data-lucide="${meCat.icon}"></i>
                   </div>`,
            iconSize: [36, 36],
            iconAnchor: [18, 18]
        });

        this.gpsMarker = L.marker(e.latlng, { icon: htmlIcon }).addTo(this.map);
        this.gpsMarker.bindTooltip('<b>Tu jesteś</b>', { direction: 'top', className: 'custom-tooltip' }).openTooltip();
        if (window.lucide) lucide.createIcons();
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new MapApplication();
});