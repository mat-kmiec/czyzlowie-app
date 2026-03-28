
(function() {
    'use strict';

    if (performance.navigation && performance.navigation.type === 2) {
        location.reload(true);
    }

    if (document.currentScript) {
        const cacheControl = document.querySelector('meta[http-equiv="Cache-Control"]');
        if (!cacheControl) {
            const meta = document.createElement('meta');
            meta.httpEquiv = 'Cache-Control';
            meta.content = 'no-cache, no-store, must-revalidate';
            document.head.appendChild(meta);
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        const loginForm = document.querySelector('form[action*="/login"]');
        
        if (loginForm) {
            loginForm.addEventListener('submit', function(e) {
                setTimeout(() => {
                    if ('caches' in window) {
                        caches.keys().then(names => {
                            names.forEach(name => caches.delete(name));
                        });
                    }
                    location.reload(true);
                }, 500);
            });
        }
    });

    document.addEventListener('submit', function(e) {
        if (e.target.action && e.target.action.includes('/logout')) {
            e.preventDefault();

            const form = e.target;
            const formData = new FormData(form);
            
            fetch(form.action, {
                method: 'POST',
                body: formData,
                credentials: 'same-origin',
                cache: 'no-store',
                headers: {
                    'Cache-Control': 'no-cache, no-store, must-revalidate'
                }
            })
            .then(response => {
                if (response.ok || response.status === 302 || response.status === 303) {
                    if ('caches' in window) {
                        caches.keys().then(names => {
                            names.forEach(name => caches.delete(name));
                        });
                    }
                    setTimeout(() => {
                        location.href = '/login?logout=true';
                    }, 300);
                }
            })
            .catch(() => {
                setTimeout(() => {
                    location.href = '/login?logout=true';
                }, 300);
            });
        }
    });

    if (window.XMLHttpRequest) {
        const originalOpen = XMLHttpRequest.prototype.open;
        XMLHttpRequest.prototype.open = function(method, url, ...args) {
            this.setRequestHeader = (function(fn) {
                return function(header, value) {
                    if (header.toLowerCase() === 'cache-control') return;
                    return fn.apply(this, [header, value]);
                };
            })(this.setRequestHeader);
            
            return originalOpen.apply(this, [method, url, ...args]);
        };
    }

    setInterval(function() {
        fetch('/api/health', {
            method: 'GET',
            cache: 'no-store',
            credentials: 'same-origin',
            headers: {
                'Cache-Control': 'no-cache, no-store, must-revalidate'
            }
        }).catch(() => {
        });
    }, 10000);
})();


