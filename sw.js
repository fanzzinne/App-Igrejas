// Versão do Cache: v2.1 (Alterar este valor força a atualização)
const CACHE_NAME = 'app-igrejas-v2.1';

const ASSETS = [
  './',
  './index.html',
  './style.css?v=2.1',
  './app.js?v=2.1',
  './manifest.json?v=2.1',
  'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'
];

// Instalação: Limpa cache antigo e baixa novos assets
self.addEventListener('install', event => {
  self.skipWaiting(); // Força o SW a assumir o controle imediatamente
  event.waitUntil(
    caches.open(CACHE_NAME)
      .then(cache => cache.addAll(ASSETS))
  );
});

self.addEventListener('message', event => {
  if (event.data === 'SKIP_WAITING') {
    self.skipWaiting();
  }
});

// Ativação: Remove caches obsoletos
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys => {
      return Promise.all(
        keys.filter(key => key !== CACHE_NAME).map(key => caches.delete(key))
      );
    }).then(() => self.clients.claim())
  );
});

// Busca: Estratégia de Cache First com Fallback para Rede
self.addEventListener('fetch', event => {
  event.respondWith(
    caches.match(event.request)
      .then(response => {
        return response || fetch(event.request).then(fetchRes => {
            return caches.open(CACHE_NAME).then(cache => {
                // Não cachear recursos de terceiros ou APIs dinâmicas aqui se preferir
                if (event.request.url.includes(location.origin)) {
                    cache.put(event.request.url, fetchRes.clone());
                }
                return fetchRes;
            });
        });
      })
  );
});
