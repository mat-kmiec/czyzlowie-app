
const CACHE_NAME = 'czyzlowie-cache-v3';
const ASSET_CACHE = 'czyzlowie-assets-v1';
const STATIC_ASSETS = ['/css/fragments/navbar.css', '/css/fragments/footer.css'];

self.addEventListener('install', (event) => {
    event.waitUntil(
        caches.open(ASSET_CACHE).then(cache => {
            return cache.addAll(STATIC_ASSETS).catch(() => {
            });
        }).then(() => self.skipWaiting())
    );
});

self.addEventListener('activate', (event) => {
    event.waitUntil(
        caches.keys().then(keys => {
            return Promise.all(
                keys.map(key => {
                    if (key !== CACHE_NAME && key !== ASSET_CACHE) {
                        return caches.delete(key);
                    }
                })
            );
        }).then(() => self.clients.claim())
    );
});

self.addEventListener('fetch', (event) => {
    const req = event.request;

    if (req.method !== 'GET') return;

    if (req.url.match(/\.(css|js|png|jpg|jpeg|svg|gif|woff2?|ttf|ico|webp)$/i) || 
        req.url.match(/\/assets\//)) {
        event.respondWith(
            caches.match(req).then(cachedRes => {
                if (cachedRes) return cachedRes;
                
                return fetch(req, { priority: 'low' }).then(networkRes => {
                    if (!networkRes || networkRes.status !== 200) {
                        return networkRes;
                    }
                    
                    const cacheToUse = req.url.match(/\/assets\//) ? ASSET_CACHE : CACHE_NAME;
                    const clonedRes = networkRes.clone();
                    caches.open(cacheToUse).then(cache => {
                        cache.put(req, clonedRes);
                    }).catch(() => {});
                    
                    return networkRes;
                }).catch(() => {
                    return caches.match(req);
                });
            })
        );
    }
    else {
        event.respondWith(
            fetch(req, { priority: 'high' }).then(networkRes => {
                if (!networkRes || networkRes.status !== 200) {
                    return networkRes;
                }
                const clonedRes = networkRes.clone();
                caches.open(CACHE_NAME).then(cache => {
                    cache.put(req, clonedRes);
                }).catch(() => {});
                return networkRes;
            }).catch(() => {
                return caches.match(req) || 
                       caches.match('/offline.html').catch(() => new Response('Offline'));
            })
        );
    }
});

