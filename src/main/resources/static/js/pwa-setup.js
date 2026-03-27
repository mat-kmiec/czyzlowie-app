
function hideSplashScreen() {
    const splash = document.getElementById('custom-splash-screen');
    if (splash) {
        splash.classList.add('splash-hidden');
        setTimeout(() => {
            try { splash.remove(); } catch(e) {}
        }, 300);
    }
}

if (document.readyState === 'complete' || document.readyState === 'interactive') {
    hideSplashScreen();
} else {
    document.addEventListener('DOMContentLoaded', hideSplashScreen, { once: true });
}

let deferredPrompt;

document.addEventListener('DOMContentLoaded', () => {
    if ('serviceWorker' in navigator) {
        navigator.serviceWorker.register('/sw.js').catch(() => {});
    }

    const isMobile = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
    const isIOS = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream;
    const isStandalone = window.matchMedia('(display-mode: standalone)').matches || 
                         window.navigator.standalone === true;

    if (!isMobile || isStandalone || localStorage.getItem('pwa-install-declined')) {
        return;
    }

    window.addEventListener('beforeinstallprompt', (e) => {
        e.preventDefault();
        deferredPrompt = e;
        showMyInstallBanner(false);
    }, { once: true });

    if (isIOS) {
        showMyInstallBanner(true);
    }
}, { once: true });

function showMyInstallBanner(isIOS) {
    if (document.getElementById('pwa-install-banner')) return;

    const banner = document.createElement('div');
    banner.id = 'pwa-install-banner';
    banner.style.cssText = "position: fixed; bottom: 20px; left: 50%; transform: translateX(-50%); width: 90%; max-width: 400px; background: rgba(15, 23, 42, 0.95); backdrop-filter: blur(10px); border: 1px solid rgba(46, 204, 113, 0.3); border-radius: 16px; padding: 20px; z-index: 10000; box-shadow: 0 10px 30px rgba(0,0,0,0.5); text-align: center; color: white; font-family: sans-serif;";

    banner.innerHTML = `
        <img src="/assets/logo.png" alt="" style="width: 50px; margin-bottom: 10px;">
        <h4 style="margin: 0 0 5px 0;">Zainstaluj CzyZlowie</h4>
        <p style="color: #cbd5e1; font-size: 0.85rem; margin-bottom: 15px;">Dostęp do prognoz prosto z pulpitu telefonu.</p>
        
        <div id="pwa-buttons-container" style="display: flex; gap: 10px;">
            <button id="btn-pwa-decline" style="flex: 1; padding: 12px; background: transparent; border: 1px solid rgba(255,255,255,0.2); color: white; border-radius: 10px; cursor: pointer;">Później</button>
            <button id="btn-pwa-install" style="flex: 1; padding: 12px; background: #2ecc71; border: none; color: #020617; font-weight: 900; border-radius: 10px; cursor: pointer;">Instaluj</button>
        </div>

        <div id="ios-steps" style="display: none; margin-top: 15px; text-align: left; background: rgba(0,0,0,0.4); padding: 15px; border-radius: 12px; border: 1px solid rgba(255,255,255,0.05);">
            <p style="color: #fff; font-size: 0.9rem; margin-top: 0; margin-bottom: 12px; font-weight: bold; text-align: center;">Apple wymaga 3 szybkich kroków:</p>
            <ol style="color: #cbd5e1; font-size: 0.85rem; padding-left: 20px; line-height: 1.6; margin: 0;">
                <li style="margin-bottom: 10px;">Stuknij ikonę <strong>Udostępnij</strong> <img src="https://developer.apple.com/design/human-interface-guidelines/foundations/images/icons/sharing_share_icon_2x.png" alt="" style="width:16px; vertical-align:middle; filter: invert(1); margin: 0 2px;"> na dolnym pasku Safari.</li>
                <li style="margin-bottom: 10px;">Przewiń menu w dół i wybierz <strong>Do ekranu początkowego</strong> <span style="font-size: 1.2em; vertical-align: middle;">➕</span></li>
                <li>Stuknij <strong>Dodaj</strong> w prawym górnym rogu ekranu.</li>
            </ol>
            <p style="text-align: center; margin-top: 15px; margin-bottom: 0; color: #2ecc71; font-weight: bold; font-size: 0.85rem;">Gotowe! Apka jest na pulpicie. 🎣</p>
        </div>
    `;

    document.body.appendChild(banner);

    const declineBtn = document.getElementById('btn-pwa-decline');
    const installBtn = document.getElementById('btn-pwa-install');
    
    if (declineBtn) {
        declineBtn.addEventListener('click', () => {
            localStorage.setItem('pwa-install-declined', 'true');
            banner.remove();
        }, { once: true });
    }

    if (installBtn) {
        installBtn.addEventListener('click', async () => {
            if (isIOS) {
                const buttonsContainer = document.getElementById('pwa-buttons-container');
                const iosSteps = document.getElementById('ios-steps');
                if (buttonsContainer && iosSteps) {
                    buttonsContainer.style.display = 'none';
                    iosSteps.style.display = 'block';
                }
                return;
            }

            if (!deferredPrompt) return;
            deferredPrompt.prompt();

            const { outcome } = await deferredPrompt.userChoice;
            deferredPrompt = null;
            banner.remove();
        }, { once: true });
    }
}
