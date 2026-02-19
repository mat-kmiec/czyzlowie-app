class MapApplication {
    constructor() {
        this.map = null;
        this.initialCoords = [52.23, 21.01];
        this.initialZoom = 6;

        this.categories = {
            lake: { name: 'Jezioro', icon: 'droplet', color: '#3b82f6' },
            river: { name: 'Rzeka', icon: 'waves', color: '#0ea5e9' },
            synop: { name: 'Stacja Synop', icon: 'thermometer', color: '#f59e0b' },
            meteo: { name: 'Stacja Meteo', icon: 'cloud-sun-rain', color: '#8b5cf6' },
            hydro: { name: 'Stacja Hydro', icon: 'droplets', color: '#06b6d4' },
            oxbow: { name: 'Starorzecze', icon: 'undo-2', color: '#10b981' },
            launch: { name: 'Miejsce wodowania', icon: 'anchor', color: '#ef4444' },
            fishery: { name: 'Łowisko', icon: 'fish', color: '#6366f1' },
            commercial: { name: 'Łowisko komercyjne', icon: 'dollar-sign', color: '#ec4899' },
            me: { name: 'Moja lokalizacja', icon: 'circle-user', color: '#22c55e' }
        };

        this.layerGroups = {};
        this.activeCategories = new Set(Object.keys(this.categories).filter(k => k !== 'me'));

        this.locations = [
            { id: 1, name: 'Jezioro Śniardwy', cat: 'lake', lat: 53.75, lng: 21.73, url: '/lowisko/1' },
            { id: 2, name: 'Rzeka Bug (Włodawa)', cat: 'river', lat: 51.55, lng: 23.55, url: '/lowisko/2' },
            { id: 3, name: 'Łowisko Złota Rybka', cat: 'commercial', lat: 52.10, lng: 20.80, url: '/lowisko/3' },
            { id: 4, name: 'Stacja IMGW Warszawa', cat: 'synop', lat: 52.22, lng: 20.98, url: '/stacja/1' },
            { id: 6, name: 'Slip Zegrze1', cat: 'launch', lat: 52.46, lng: 22.03, url: '/slip/1' },
            { id: 7, name: 'Slip Zegrze2', cat: 'launch', lat: 52.46, lng: 23.03, url: '/slip/3' },
            { id: 8, name: 'Slip Zegrze3', cat: 'launch', lat: 52.46, lng: 24.03, url: '/slip/5' },
        ];

        this.markersCache = {};

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

    init() {
        this.initMap();
        this.initCustomControls();
        this.setupLayerGroups();
        this.renderLegend();
        this.loadMarkers();
        this.renderList(this.locations);
        this.attachEventListeners();
        lucide.createIcons();
    }

    initMap() {
        this.map = L.map('map', {
            zoomControl: false
        }).setView(this.initialCoords, this.initialZoom);

        this.baseLayers[this.currentBaseLayer].addTo(this.map);

        this.map.on('locationfound', (e) => this.handleLocationFound(e));
    }

    initCustomControls() {
        const container = L.DomUtil.create('div', 'map-controls-container');

        const zoomGroup = L.DomUtil.create('div', 'map-controls-group', container);
        this.createBtn(zoomGroup, 'plus', 'Przybliż', () => this.map.zoomIn());
        this.createBtn(zoomGroup, 'minus', 'Oddal', () => this.map.zoomOut());

        this.createBtn(container, 'navigation', 'Moja lokalizacja', () => this.map.locate({setView: true, maxZoom: 14}));
        this.createBtn(container, 'expand', 'Resetuj widok', () => this.map.setView(this.initialCoords, this.initialZoom));

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

    setupLayerGroups() {
        Object.keys(this.categories).forEach(cat => {
            if(cat !== 'me') {
                this.layerGroups[cat] = L.layerGroup().addTo(this.map);
            }
        });
    }

    renderLegend() {
        const container = document.getElementById('legendContainer');
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
            this.map.removeLayer(this.layerGroups[key]);
            btnElement.classList.remove('active');
        } else {
            this.activeCategories.add(key);
            this.map.addLayer(this.layerGroups[key]);
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

            marker.bindTooltip(`<b>${loc.name}</b>`, {
                direction: 'top', offset: [0, -15], className: 'custom-tooltip'
            });

            const popupContent = `
                <div class="popup-header">
                    <div class="popup-title">${loc.name}</div>
                    <div class="popup-badge" style="background: ${catData.color}25; color: ${catData.color}; border: 1px solid ${catData.color}40;">
                        <i data-lucide="${catData.icon}"></i> ${catData.name}
                    </div>
                </div>
                <div class="popup-body">
                    <a href="${loc.url}" class="popup-btn">
                        Szczegóły <i data-lucide="arrow-right"></i>
                    </a>
                </div>
            `;

            marker.bindPopup(popupContent, {
                className: 'custom-popup',
                autoPanPadding: [50, 50]
            });

            marker.on('popupopen', () => {
                lucide.createIcons();
            });

            marker.addTo(this.layerGroups[loc.cat]);
            this.markersCache[loc.id] = marker;
        });
    }

    renderList(itemsToRender) {
        const container = document.getElementById('locationsList');
        document.getElementById('locCount').innerText = itemsToRender.length;
        container.innerHTML = '';

        itemsToRender.forEach(loc => {
            const catData = this.categories[loc.cat];

            const div = document.createElement('div');
            div.className = 'location-item';
            div.innerHTML = `
                <div class="custom-map-marker" style="background-color: ${catData.color}; width: 36px; height: 36px; position: static;">
                    <i data-lucide="${catData.icon}"></i>
                </div>
                <div class="loc-details">
                    <span class="loc-name">${loc.name}</span>
                    <span class="loc-cat">${catData.name}</span>
                </div>
            `;

            div.onclick = () => {
                this.map.flyTo([loc.lat, loc.lng], 14, {
                    duration: 1.5,
                    easeLinearity: 0.25
                });

                setTimeout(() => {
                    this.markersCache[loc.id].openPopup();
                }, 1500);

                if(window.innerWidth <= 991) {
                    document.getElementById('filterSidebar').classList.remove('active');
                }
            };

            container.appendChild(div);
        });

        lucide.createIcons();
    }

    attachEventListeners() {
        const searchInput = document.getElementById('mapSearch');
        searchInput.addEventListener('input', () => this.filterListBySearch());

        const btnOpen = document.getElementById('mobileFilterBtn');
        const btnClose = document.getElementById('closeSidebarBtn');
        const sidebar = document.getElementById('filterSidebar');

        btnOpen.addEventListener('click', () => sidebar.classList.add('active'));
        btnClose.addEventListener('click', () => sidebar.classList.remove('active'));

        const layerButtons = document.querySelectorAll('.layer-btn');
        layerButtons.forEach(btn => {
            btn.addEventListener('click', (e) => {
                const targetBtn = e.target.closest('.layer-btn');
                const layerName = targetBtn.dataset.layer;

                this.map.removeLayer(this.baseLayers[this.currentBaseLayer]);

                this.baseLayers[layerName].addTo(this.map);
                this.currentBaseLayer = layerName;

                layerButtons.forEach(b => b.classList.remove('active'));
                targetBtn.classList.add('active');
            });
        });
    }

    filterListBySearch() {
        const query = document.getElementById('mapSearch').value.toLowerCase();

        const filtered = this.locations.filter(loc => {
            const matchesSearch = loc.name.toLowerCase().includes(query) ||
                this.categories[loc.cat].name.toLowerCase().includes(query);
            const isCategoryActive = this.activeCategories.has(loc.cat);
            return matchesSearch && isCategoryActive;
        });

        this.renderList(filtered);
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
        lucide.createIcons();
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new MapApplication();
});