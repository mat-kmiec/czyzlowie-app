
const CACHE_NAME = 'czyzlowie-cache-v2';

self.addEventListener('install', (event) => {
    self.skipWaiting();
});

self.addEventListener('activate', (event) => {
    event.waitUntil(
        caches.keys().then(keys => {
            return Promise.all(
                keys.map(key => {
                    if (key !== CACHE_NAME) {
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

    if (req.url.match(/\.(css|js|png|jpg|jpeg|svg|gif|woff2?|ttf|ico)$/)) {
        event.respondWith(
            caches.match(req).then(cachedRes => {
                return cachedRes || fetch(req).then(networkRes => {
                    return caches.open(CACHE_NAME).then(cache => {
                        cache.put(req, networkRes.clone());
                        return networkRes;
                    });
                });
            })
        );
    }
    else {
        event.respondWith(
            fetch(req).then(networkRes => {
                return caches.open(CACHE_NAME).then(cache => {
                    cache.put(req, networkRes.clone());
                    return networkRes;
                });
            }).catch(() => {
                return caches.match(req);
            })
        );
    }
});

