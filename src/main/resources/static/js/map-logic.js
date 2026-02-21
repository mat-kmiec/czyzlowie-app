import { MAP_CONFIG, CATEGORIES } from './map-data.js';

class MapApplication {
    constructor() {
        this.map = null;
        this.categories = CATEGORIES;
        this.locations = [];
        this.markersCache = {};
        this.fetchTimeout = null;
        this.allDataLoaded = false;

        const defaultInactive = ['me', 'hydro', 'meteo'];
        this.activeCategories = new Set(Object.keys(this.categories).filter(k => !defaultInactive.includes(k)));

        this.baseLayers = {
            standard: L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenStreetMap',
                maxZoom: 19
            }),
            satellite: L.tileLayer('https://mt1.google.com/vt/lyrs=y&x={x}&y={y}&z={z}', {
                attribution: '&copy; Google',
                maxZoom: 20
            }),
            geoportal: L.tileLayer.wms('https://mapy.geoportal.gov.pl/wss/service/PZGIK/ORTO/WMS/StandardResolution', {
                layers: 'Raster',
                format: 'image/jpeg',
                maxZoom: 20,
                attribution: '&copy; Geoportal'
            }),
            terrain: L.tileLayer('https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenTopoMap',
                maxZoom: 17
            })
        };
        this.currentBaseLayer = 'standard';

        this.activeOverlays = new Set();
        this.overlays = {
            seamap: L.tileLayer('https://t1.openseamap.org/seamark/{z}/{x}/{y}.png', {
                attribution: '&copy; OpenSeaMap',
                maxZoom: 19
            }),
            nature: L.tileLayer.wms('https://sdi.gdos.gov.pl/wms', {
                layers: 'Rezerwaty,ParkiNarodowe',
                format: 'image/png',
                transparent: true,
                opacity: 0.5,
                attribution: '&copy; GDOŚ',
                maxZoom: 19
            })
        };

        this.init();
    }

    async init() {
        this.initMap();
        this.initCustomControls();
        this.setupClusterGroup();
        this.renderLegend();

        await this.setInitialView();

        this.map.on('moveend', () => {
            if (this.allDataLoaded) return;
            clearTimeout(this.fetchTimeout);
            this.fetchTimeout = setTimeout(() => {
                this.fetchLocationsForCurrentBounds();
            }, 300);
        });

        this.attachEventListeners();

        if (window.lucide) {
            lucide.createIcons();
        }
    }

    async setInitialView() {
        this.toggleLoader(true, 'Szukam Twojej pozycji...');
        return new Promise((resolve) => {
            if ("geolocation" in navigator) {
                navigator.geolocation.getCurrentPosition(
                    (position) => {
                        const lat = position.coords.latitude;
                        const lng = position.coords.longitude;
                        this.map.setView([lat, lng], 12);
                        this.handleLocationFound({ latlng: { lat, lng } });
                        resolve();
                    },
                    (error) => {
                        this.toggleLoader(false);
                        console.warn("Brak zgody na lokalizację. Ładuję Warszawę.");
                        this.map.setView([52.2297, 21.0122], 11);
                        resolve();
                    },
                    { timeout: 4000, maximumAge: 60000 }
                );
            } else {
                this.toggleLoader(false);
                this.map.setView([52.2297, 21.0122], 11);
                resolve();
            }
        }).then(() => {
            this.toggleLoader(false);
            return this.fetchLocationsForCurrentBounds();
        });
    }

    toggleLoader(show, text = 'Ładowanie...') {
        const loader = document.getElementById('map-loader');
        if (!loader) return;

        const textEl = loader.querySelector('.loader-text');
        if (textEl) textEl.innerText = text;

        if (show) {
            loader.classList.add('active');
        } else {
            loader.classList.remove('active');
        }
    }

    initMap() {
        this.map = L.map('map', {
            zoomControl: false,
            preferCanvas: true
        });
        this.baseLayers[this.currentBaseLayer].addTo(this.map);
    }

    async fetchLocationsForCurrentBounds() {
        if (!this.map || this.allDataLoaded) return;
        this.toggleLoader(true, 'Pobieram łowiska...');

        const bounds = this.map.getBounds();
        const north = bounds.getNorth();
        const south = bounds.getSouth();
        const east = bounds.getEast();
        const west = bounds.getWest();

        const latSpread = Math.abs(north - south);
        const lngSpread = Math.abs(east - west);
        let url = `/api/map/markers?north=${north}&south=${south}&east=${east}&west=${west}`;
        if (latSpread > 5.5 || lngSpread > 8) {
            url = `/api/map/markers`;

            this.allDataLoaded = true;
            console.log("Oddalono mapę. Pobrano pełną bazę (100% kraju) i wyłączono zapytania API.");
        }

        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error("Błąd pobierania danych");

            const data = await response.json();
            let addedNewLocations = false;

            data.forEach(marker => {
                const exists = this.locations.some(loc => loc.id === marker.id);

                if (!exists) {
                    const typeLower = marker.type ? marker.type.toLowerCase() : '';
                    let baseUrl = '/synop';

                    if (typeLower === 'hydro') baseUrl = '/hydro';
                    else if (typeLower === 'meteo') baseUrl = '/meteo';
                    else if (typeLower === 'slip') baseUrl = '/slip';
                    else if (['lake', 'reservoir', 'river', 'commercial', 'oxbow', 'specific_spot'].includes(typeLower)) {
                        baseUrl = '/lowisko';
                    }

                    this.locations.push({
                        id: marker.id,
                        name: marker.name,
                        cat: typeLower,
                        lat: marker.lat,
                        lng: marker.lng,
                        coords: marker.polygonCoordinates,
                        description: marker.description,
                        startDate: marker.startDate,
                        endDate: marker.endDate,
                        restrictionType: marker.restrictionType,
                        url: `${baseUrl}/${marker.slug}`
                    });
                    addedNewLocations = true;
                }
            });

            if (addedNewLocations) {
                this.loadMarkers();
                this.updateMapMarkers();
                this.renderList(this.locations);
            }

        } catch (error) {
            console.error('Błąd ładowania danych mapy:', error);
        } finally {
            this.toggleLoader(false);
        }
    }

    initCustomControls() {
        const container = L.DomUtil.create('div', 'map-controls-container');

        const zoomGroup = L.DomUtil.create('div', 'map-controls-group', container);
        this.createBtn(zoomGroup, 'plus', 'Przybliż', () => this.map.zoomIn());
        this.createBtn(zoomGroup, 'minus', 'Oddal', () => this.map.zoomOut());

        this.createBtn(container, 'navigation', 'Moja lokalizacja', () => {
            this.map.locate({setView: true, maxZoom: 14});
        });

        this.createBtn(container, 'expand', 'Widok całej Polski', () => {
            this.map.setView(MAP_CONFIG.initialCoords || [52.0, 19.0], 6);
        });

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
            if (this.markersCache[loc.id]) return;

            const catData = this.categories[loc.cat] || this.categories['restriction'];
            if (!catData && loc.cat !== 'restriction') return;

            if (loc.cat === 'restriction' && loc.coords) {
                try {
                    const coordsArr = JSON.parse(loc.coords);
                    const isTotal = (!loc.restrictionType || loc.restrictionType === 'TOTAL_BAN');
                    const color = isTotal ? '#ef4444' : '#f59e0b';
                    const iconName = isTotal ? 'shield-off' : 'info';

                    const area = L.polygon(coordsArr, {
                        color: color,
                        fillColor: color,
                        fillOpacity: 0.35,
                        weight: 2,
                        dashArray: isTotal ? '0' : '5, 10'
                    });

                    const center = area.getBounds().getCenter();
                    const htmlIcon = L.divIcon({
                        className: 'custom-div-icon',
                        html: `<div class="custom-map-marker restriction-marker" style="background-color: ${color}; border: 2px solid white; box-shadow: 0 0 10px rgba(0,0,0,0.3);">
                                   <i data-lucide="${iconName}"></i>
                               </div>`,
                        iconSize: [30, 30],
                        iconAnchor: [15, 15]
                    });

                    const marker = L.marker(center, { icon: htmlIcon });

                    const dateRange = (loc.startDate && loc.endDate)
                        ? `<div class="popup-date" style="font-size: 0.8rem; margin-bottom: 8px; color: #94a3b8;"><i data-lucide="calendar" style="width: 14px; height: 14px; vertical-align: middle;"></i> ${loc.startDate} — ${loc.endDate}</div>`
                        : '';

                    const popupContent = `
                        <div class="popup-header">
                            <div class="popup-title">${loc.name}</div>
                            <div class="popup-badge" style="background: ${color}25; color: ${color}; border: 1px solid ${color}40;">
                                <i data-lucide="${iconName}"></i> 
                                ${isTotal ? 'ZAKAZ CAŁKOWITY' : 'OGRANICZENIA'}
                            </div>
                        </div>
                        <div class="popup-body">
                            ${dateRange}
                            <p class="popup-desc">${loc.description || 'Brak dodatkowego opisu.'}</p>
                        </div>
                    `;

                    marker.bindPopup(popupContent, { className: 'custom-popup' });
                    area.bindPopup(popupContent, { className: 'custom-popup' });

                    marker.on('popupopen', () => { if (window.lucide) lucide.createIcons(); });
                    area.on('popupopen', () => { if (window.lucide) lucide.createIcons(); });

                    marker.on('add', () => { if (!this.map.hasLayer(area)) this.map.addLayer(area); });
                    marker.on('remove', () => { if (this.map.hasLayer(area)) this.map.removeLayer(area); });

                    this.markersCache[loc.id] = marker;

                } catch (e) {
                    console.error("Błąd parsowania obszaru:", e);
                }
            } else if (loc.lat && loc.lng) {
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

                marker.on('popupopen', () => { if (window.lucide) lucide.createIcons(); });

                this.markersCache[loc.id] = marker;
            }
        });
    }

    updateMapMarkers() {
        const query = document.getElementById('mapSearch')?.value.toLowerCase() || '';
        const clusterItems = [];

        this.mainClusterGroup.clearLayers();

        this.locations.forEach(loc => {
            if (!this.activeCategories.has(loc.cat)) return;
            if (!loc.name.toLowerCase().includes(query)) return;

            const item = this.markersCache[loc.id];
            if (item) {
                clusterItems.push(item);
            }
        });

        this.mainClusterGroup.addLayers(clusterItems);

        if (window.lucide) lucide.createIcons();
    }

    renderList(itemsToRender) {
        const container = document.getElementById('locationsList');
        const countEl = document.getElementById('locCount');

        if (countEl) countEl.innerText = itemsToRender.length;
        if (!container) return;

        container.innerHTML = itemsToRender.map(loc => {
            const catData = this.categories[loc.cat] || this.categories['restriction'];
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
                const loc = this.locations.find(l => l.id === locId);
                if (loc) {
                    const mapItem = this.markersCache[locId];

                    if (mapItem) {
                        this.map.flyTo(mapItem.getLatLng(), 14, { duration: 1.5 });
                        setTimeout(() => {
                            if (this.mainClusterGroup.hasLayer(mapItem)) {
                                this.mainClusterGroup.zoomToShowLayer(mapItem, () => {
                                    mapItem.openPopup();
                                });
                            }
                        }, 1500);
                    }

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

        const baseLayerButtons = document.querySelectorAll('#baseLayerControls .layer-btn');
        baseLayerButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const targetBtn = e.currentTarget;
                const layerName = targetBtn.dataset.layer;

                if (this.currentBaseLayer === layerName) return;

                this.map.removeLayer(this.baseLayers[this.currentBaseLayer]);
                this.baseLayers[layerName].addTo(this.map);
                this.currentBaseLayer = layerName;

                baseLayerButtons.forEach(b => b.classList.remove('active'));
                targetBtn.classList.add('active');
            });
        });

        const overlayButtons = document.querySelectorAll('#overlayControls .overlay-btn');
        overlayButtons.forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const targetBtn = e.currentTarget;
                const overlayName = targetBtn.dataset.overlay;

                if (this.activeOverlays.has(overlayName)) {
                    if (this.overlays[overlayName]) {
                        this.map.removeLayer(this.overlays[overlayName]);
                    }
                    this.activeOverlays.delete(overlayName);
                    targetBtn.classList.remove('active');
                    return;
                }

                if (this.overlays[overlayName]) {
                    this.overlays[overlayName].addTo(this.map);
                    this.activeOverlays.add(overlayName);
                    targetBtn.classList.add('active');
                }

            });
        });
    }

    async searchGlobal(query) {
        if (!query || query.length < 3) return;

        this.toggleLoader(true, `Szukam miejscowości: ${query}`);

        try {
            const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&countrycodes=pl`);
            const results = await response.json();

            if (results && results.length > 0) {
                const result = results[0];
                const bbox = result.boundingbox;
                const bounds = [
                    [bbox[0], bbox[2]],
                    [bbox[1], bbox[3]]
                ];

                this.map.fitBounds(bounds);
                document.getElementById('mapSearch').value = '';
                this.filterListBySearch();

            } else {
                alert("Nie znaleziono takiej miejscowości w Polsce.");
            }
        } catch (error) {
            console.error("Błąd wyszukiwania:", error);
        } finally {
            this.toggleLoader(false);
        }
    }

    filterListBySearch() {
        const query = document.getElementById('mapSearch').value.toLowerCase();

        const filtered = this.locations.filter(loc => {
            const catData = this.categories[loc.cat] || this.categories['restriction'];
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