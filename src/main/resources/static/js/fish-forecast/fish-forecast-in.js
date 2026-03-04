document.addEventListener('DOMContentLoaded', () => {
    if (typeof lucide !== 'undefined') lucide.createIcons();

    document.getElementById('targetTimeInput').value = new Date().toISOString();

    const locationInput = document.getElementById('locationInput');
    const latInput = document.getElementById('latInput');
    const lonInput = document.getElementById('lonInput');
    const statusBox = document.getElementById('geo-status');

    const btnGeocode = document.getElementById('btn-geocode');
    const btnGeocodeIcon = document.getElementById('btn-geocode-icon');
    const btnGeocodeText = document.getElementById('btn-geocode-text');

    const btnGpsLive = document.getElementById('btn-gps-live');

    function showStatus(type, text, iconName) {
        statusBox.className = `geo-status ${type}`;
        statusBox.innerHTML = `<i data-lucide="${iconName}" style="width: 14px; margin-right: 4px; vertical-align: text-bottom;"></i> <span>${text}</span>`;
        lucide.createIcons();
    }

    const fishSelectors = document.querySelectorAll('.fish-selector');
    fishSelectors.forEach(checkbox => {
        checkbox.addEventListener('change', function() {
            if (this.checked) {
                // Jeśli ten jest zaznaczony, odznacz wszystkie inne
                fishSelectors.forEach(other => {
                    if (other !== this) {
                        other.checked = false;
                    }
                });
            }
        });
    });

    btnGeocode.addEventListener('click', async () => {
        const query = locationInput.value.trim();
        if (!query) {
            showStatus('error', 'Wpisz nazwę łowiska przed zatwierdzeniem!', 'alert-circle');
            return;
        }

        const originalText = btnGeocodeText.textContent;
        btnGeocodeText.textContent = 'Szukam w satelicie...';
        btnGeocode.style.opacity = '0.7';

        try {
            const response = await fetch(`https://nominatim.openstreetmap.org/search?format=json&q=${encodeURIComponent(query)}&countrycodes=pl&limit=1`);
            const data = await response.json();

            if (data && data.length > 0) {
                latInput.value = data[0].lat;
                lonInput.value = data[0].lon;

                const cleanName = data[0].name.split(',')[0].trim();
                locationInput.value = cleanName;

                showStatus('success', `Zlokalizowano: ${cleanName} (Lat: ${parseFloat(data[0].lat).toFixed(2)}, Lon: ${parseFloat(data[0].lon).toFixed(2)})`, 'check-circle');
            } else {
                showStatus('error', 'Nie znaleziono miejsca na mapie Polski.', 'alert-triangle');
                latInput.value = '';
                lonInput.value = '';
            }
        } catch (error) {
            console.error("Błąd Geocodingu:", error);
            showStatus('error', 'Problem z połączeniem z satelitą.', 'wifi-off');
        } finally {
            btnGeocodeText.textContent = originalText;
            btnGeocode.style.opacity = '1';
        }
    });

    btnGpsLive.addEventListener('click', () => {
        if (!navigator.geolocation) {
            showStatus('error', 'Twoja przeglądarka nie wspiera GPS.', 'alert-circle');
            return;
        }

        showStatus('success', 'Pobieranie koordynatów GPS...', 'satellite');

        const originalIcon = btnGpsLive.innerHTML;
        btnGpsLive.innerHTML = '<div class="spinner-border spinner-border-sm text-brand-green" role="status"></div>';

        navigator.geolocation.getCurrentPosition(
            async (position) => {
                const lat = position.coords.latitude;
                const lon = position.coords.longitude;

                latInput.value = lat;
                lonInput.value = lon;

                try {
                    const response = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&zoom=10&addressdetails=1`);
                    const data = await response.json();

                    if (data && data.address) {
                        const city = data.address.city || data.address.town || data.address.village || data.address.municipality || data.name;

                        if (city) {
                            locationInput.value = `${city}`;
                            showStatus('success', `Namierzono pomyślnie: ${city}!`, 'check-circle');
                        } else {
                            locationInput.value = `Twój GPS [${lat.toFixed(4)}, ${lon.toFixed(4)}]`;
                            showStatus('success', 'Namierzono pomyślnie!', 'check-circle');
                        }
                    } else {
                        locationInput.value = `📍 Twój GPS [${lat.toFixed(4)}, ${lon.toFixed(4)}]`;
                        showStatus('success', 'Namierzono pomyślnie!', 'check-circle');
                    }
                } catch (error) {
                    console.error("Błąd Reverse Geocodingu:", error);
                    locationInput.value = `Twój GPS [${lat.toFixed(4)}, ${lon.toFixed(4)}]`;
                    showStatus('success', 'Namierzono GPS (brak nazwy z satelity).', 'check-circle');
                } finally {
                    btnGpsLive.innerHTML = originalIcon;
                    lucide.createIcons();
                }
            },
            (error) => {
                showStatus('error', 'Odmowa dostępu do GPS lub błąd sygnału.', 'alert-triangle');
                btnGpsLive.innerHTML = originalIcon;
                lucide.createIcons();
            }
        );
    });

    const algoForm = document.getElementById('algoForm');
    const loader = document.getElementById('fishing-loader');
    const loaderDesc = document.getElementById('loading-desc');
    const loadingBar = document.getElementById('loading-bar');
    const loadingHook = document.getElementById('loading-hook');

    const logTexts = [
        "Połączenie z bazą IMGW Synop & Hydro...",
        "Skanowanie stref przydennych i termokliny...",
        "Zderzanie modeli pogodowych (OpenMeteo)...",
        "Obliczanie wektorów i predykcja brań..."
    ];

    algoForm.addEventListener('submit', (e) => {
        if(!latInput.value || !lonInput.value) {
            e.preventDefault();
            showStatus('error', 'Brak współrzędnych! Wpisz lokację i kliknij "Zatwierdź współrzędne".', 'alert-circle');
            locationInput.style.boxShadow = '0 0 15px var(--predator-red)';
            setTimeout(() => locationInput.style.boxShadow = 'none', 1000);
            return;
        }

        e.preventDefault();
        window.scrollTo(0,0);
        document.body.style.overflow = 'hidden';
        loader.classList.add('active');

        let progress = 0; let textIndex = 0;
        const progressInt = setInterval(() => {
            progress += Math.random() * 8 + 2;
            if(progress > 100) progress = 100;
            loadingBar.style.width = `${progress}%`;
            loadingHook.style.left = `calc(${progress}% - 8px)`;

            const expectedIndex = Math.floor((progress / 100) * logTexts.length);
            if(expectedIndex > textIndex && expectedIndex < logTexts.length) {
                textIndex = expectedIndex;
                loaderDesc.innerText = logTexts[textIndex];
            }
        }, 300);

        setTimeout(() => {
            clearInterval(progressInt);
            loadingBar.style.width = '100%';
            loadingHook.style.left = 'calc(100% - 8px)';
            loaderDesc.innerText = "ZAKOŃCZONO SKANOWANIE. GENEROWANIE RAPORTU...";
            loaderDesc.style.color = "var(--brand-green)";
            setTimeout(() => { algoForm.submit(); }, 600);
        }, 4000);
    });
});