// CONFIGURAÇÃO: COLE A URL DO SEU GOOGLE APPS SCRIPT AQUI
const API_URL = "https://script.google.com/macros/s/AKfycbxxkxzEU8hjAqDfDlbk1t5w6t_-2DqwvzEY8fmIEe9vSZfv81ctidf_pJ6EgbsH1J1Y/exec";

// Mock Data (Caso a API não esteja configurada ainda)
const MOCK_DATA = {
    banners: [
        { Titulo: "Culto de Celebração", Subtitulo: "Domingo às 19h", ImagemUrl: "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?q=80&w=1000" },
        { Titulo: "Escola Bíblica", Subtitulo: "Domingo às 9h", ImagemUrl: "https://images.unsplash.com/photo-1504052434569-70ad5836ab65?q=80&w=1000" }
    ],
    noticias: [
        { Titulo: "Conferência de Jovens 2026", Data: "25/10/2026 às 19h", Descricao: "Um tempo de renovo e propósito para a juventude.", ImagemUrl: "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?q=80&w=500" },
        { Titulo: "Novo Horário de Cultos", Data: "20/11/2026 às 18:30", Descricao: "Venha celebrar conosco em um culto de adoração profunda.", ImagemUrl: "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?q=80&w=500" }
    ],
    sermoes: [
        { Titulo: "Vivendo pela Fé", Pregador: "Pr. João Silva", Data: "15/04/2026", ImagemUrl: "https://images.unsplash.com/photo-1507679799987-c73779587ccf?q=80&w=500", VideoUrl: "https://www.youtube.com/watch?v=dQw4w9WgXcQ" },
        { Titulo: "O Poder da Oração", Pregador: "Pra. Maria", Data: "10/04/2026", ImagemUrl: "https://images.unsplash.com/photo-1499209974431-9dac3adaf471?q=80&w=500", VideoUrl: "" }
    ],
    ministerios: [
        { Nome: "Louvor", Lider: "Davi", Descricao: "Ministério de música e adoração.", ImagemUrl: "https://images.unsplash.com/photo-1510915361894-db8b60106cb1?q=80&w=500" },
        { Nome: "Infantil", Lider: "Sara", Descricao: "Ensino bíblico para crianças.", ImagemUrl: "https://images.unsplash.com/photo-1484662020986-75935d2ebc66?q=80&w=500" }
    ],
    mensagemLider: [
        { Nome: "Pr. Presidente", Cargo: "Pastor Sênior", Mensagem: "A Importância da Fé e da Perseverança", VideoUrl: "https://www.youtube.com/watch?v=dQw4w9WgXcQ", FotoUrl: "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?q=80&w=200" },
        { Nome: "Pra. Auxiliar", Cargo: "Pastora de Famílias", Mensagem: "Família sob a Graça: O Fundamento do Lar", VideoUrl: "https://drive.google.com/file/d/1_PLACEHOLDER_ID/view?usp=sharing", FotoUrl: "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=200" }
    ],
    eventos: [
        { Titulo: "Culto da Família", Data: "Domingo", Horario: "19:00", Local: "Sede" },
        { Titulo: "Escola Bíblica", Data: "Domingo", Horario: "09:00", Local: "Sede" },
        { Titulo: "Células", Data: "Quarta-feira", Horario: "20:00", Local: "Casas" },
        { Titulo: "Culto de Jovens", Data: "Sábado", Horario: "19:30", Local: "Anexo" }
    ],
    galeria: [
        { Titulo: "Batismo 2025", ImagemUrl: "https://images.unsplash.com/photo-1511795409834-ef04bbd61622?q=80&w=1000" },
        { Titulo: "Conferência", ImagemUrl: "https://images.unsplash.com/photo-1438232992991-995b7058bbb3?q=80&w=1000" },
        { Titulo: "Ação Social", ImagemUrl: "https://images.unsplash.com/photo-1499209974431-9dac3adaf471?q=80&w=1000" }
    ]
};

let currentGivingType = 'dizimo';
let bannerInterval = null;

// Converte URLs do YouTube e Google Drive para o formato de incorporação (Embed/Preview)
function getEmbedUrl(url) {
    if (!url) return '';

    // Suporte para Google Drive
    if (url.includes('drive.google.com')) {
        let fileId = '';
        if (url.includes('/d/')) {
            fileId = url.split('/d/')[1].split('/')[0].split('?')[0];
        } else if (url.includes('id=')) {
            fileId = new URLSearchParams(new URL(url).search).get('id');
        }
        return fileId ? `https://drive.google.com/file/d/${fileId}/preview` : url;
    }

    // Suporte para YouTube
    if (url.includes('youtube.com/embed/')) return url;
    let videoId = '';
    if (url.includes('youtu.be/')) {
        videoId = url.split('youtu.be/')[1].split('?')[0];
    } else if (url.includes('youtube.com/watch')) {
        videoId = new URLSearchParams(new URL(url).search).get('v');
    } else if (url.includes('youtube.com/shorts/')) {
        videoId = url.split('shorts/')[1].split('?')[0];
    }

    return videoId ? `https://www.youtube.com/embed/${videoId}` : url;
}

// Função global para formatar data e hora no padrão brasileiro (GMT-3)
const formatDateBR = (dateValue, compact = false) => {
    if (!dateValue || dateValue.toString().toLowerCase() === 'undefined') return '';

    const dateStr = dateValue.toString().trim();

    // Se já estiver formatado como BR (xx/xx) ou hora (xx:xx), mantém original
    if (dateStr.includes('/') && dateStr.length <= 10) return dateStr;
    if (dateStr.includes(':') && !dateStr.includes('T') && dateStr.length <= 5) return dateStr;

    const dateObj = new Date(dateValue);

    if (!isNaN(dateObj.getTime()) && (dateStr.includes('-') || typeof dateValue === 'number' || dateStr.includes('T'))) {
        // Forçar GMT-3 usando Intl.DateTimeFormat
        const options = {
            timeZone: 'America/Sao_Paulo',
            day: '2-digit',
            month: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            hour12: false
        };

        const formatter = new Intl.DateTimeFormat('pt-BR', options);
        const parts = formatter.formatToParts(dateObj);
        const find = (type) => parts.find(p => p.type === type)?.value || '';

        if (compact) return `${find('day')}/${find('month')}`;
        return `${find('day')}/${find('month')} às ${find('hour')}:${find('minute')}`;
    }

    return dateStr;
};

document.addEventListener('DOMContentLoaded', () => {
    setTimeout(() => {
        const splash = document.getElementById('splash');
        if (splash) {
            splash.style.opacity = '0';
            setTimeout(() => {
                splash.style.display = 'none';
            }, 500);
        }
    }, 2000);

    fetchData();
    initBible();
    setupBranding();
    registerServiceWorker();
    setupInstallPrompt();
});

let deferredPrompt;
function setupInstallPrompt() {
    const installContainer = document.getElementById('install-container');
    const installBtn = document.getElementById('install-button');

    window.addEventListener('beforeinstallprompt', (e) => {
        // Previne que o Chrome mostre o prompt automático
        e.preventDefault();
        // Guarda o evento para disparar depois
        deferredPrompt = e;
        // Mostra o botão de instalação
        if (installContainer) installContainer.style.display = 'block';
    });

    if (installBtn) {
        installBtn.addEventListener('click', async () => {
            if (!deferredPrompt) return;
            // Mostra o prompt
            deferredPrompt.prompt();
            // Espera pela escolha do usuário
            const { outcome } = await deferredPrompt.userChoice;
            console.log(`User response to the install prompt: ${outcome}`);
            // Limpa o prompt
            deferredPrompt = null;
            // Esconde o botão
            if (installContainer) installContainer.style.display = 'none';
        });
    }

    window.addEventListener('appinstalled', () => {
        // Esconde o botão após instalação
        if (installContainer) installContainer.style.display = 'none';
        deferredPrompt = null;
        console.log('PWA was installed');
    });
}

function registerServiceWorker() {
    if ('serviceWorker' in navigator) {
        window.addEventListener('load', () => {
            navigator.serviceWorker.register('./sw.js').then(reg => {
                console.log('Service Worker registrado!');

                // Se houver um worker esperando, mostra a notificação
                if (reg.waiting) {
                    showUpdateNotification(reg.waiting);
                }

                // Escuta por novos workers instalando
                reg.addEventListener('updatefound', () => {
                    const newWorker = reg.installing;
                    newWorker.addEventListener('statechange', () => {
                        if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
                            showUpdateNotification(newWorker);
                        }
                    });
                });
            }).catch(err => console.log('Falha ao registrar Service Worker', err));
        });

        // Recarrega a página quando o novo Service Worker assume o controle
        let refreshing = false;
        navigator.serviceWorker.addEventListener('controllerchange', () => {
            if (!refreshing) {
                window.location.reload();
                refreshing = true;
            }
        });
    }
}

function showUpdateNotification(worker) {
    const notification = document.getElementById('update-notification');
    const btn = document.getElementById('update-button');

    if (notification && btn) {
        notification.style.display = 'block';
        btn.onclick = () => {
            worker.postMessage('SKIP_WAITING');
            notification.style.display = 'none';
        };
    }
}

function setupBranding(data = null) {
    const defaultName = "Nome da Sua Igreja Aqui";
    const defaultLogo = "https://i.ibb.co/tMg1KQyD/Logo-dourado.png";

    let config = data?.config || data?.configuracoes;

    if (Array.isArray(config)) {
        config = config[0];
    }

    const findKey = (obj, ...keys) => {
        if (!obj) return null;
        for (let key of keys) {
            if (obj[key] && typeof obj[key] === 'string' && obj[key].toLowerCase() !== 'undefined') return obj[key].trim();
            let lowerKey = key.toLowerCase();
            if (obj[lowerKey] && typeof obj[lowerKey] === 'string' && obj[lowerKey].toLowerCase() !== 'undefined') return obj[lowerKey].trim();
            let found = Object.keys(obj).find(k => k.toLowerCase() === lowerKey);
            if (found && obj[found] && typeof obj[found] === 'string' && obj[found].toLowerCase() !== 'undefined') return obj[found].trim();
        }
        return null;
    };

    const appName = findKey(config, "NomeIgreja", "Nome", "Igreja") || defaultName;
    const logoUrl = findKey(config, "LogoUrl", "Logo", "Imagem", "Url") || defaultLogo;

    const splashName = document.getElementById('splash-app-name');
    if (splashName) splashName.innerText = appName;
    if (document.title) document.title = appName;

    if (logoUrl) {
        const splashLogo = document.getElementById('splash-logo-placeholder');
        const headerLogo = document.getElementById('header-logo-placeholder');

        if (splashLogo) {
            splashLogo.innerHTML = `<img src="${logoUrl}" style="width:120px; height:120px; object-fit:contain; margin-bottom:20px; animation: pulse 2s infinite">`;
        }
        if (headerLogo) {
            headerLogo.innerHTML = `<img src="${logoUrl}" alt="Logo" style="height: 100%; width: 100%; object-fit: contain">`;
            headerLogo.style.height = "90px";
            headerLogo.style.width = "90px";
            headerLogo.style.background = "none";
            headerLogo.style.border = "none";
        }
    }
}

function openLiveStream() {
    const liveUrl = window.appData?.config?.LinkAoVivo || "https://www.youtube.com/results?search_query=igreja+ao+vivo";
    window.open(liveUrl, '_blank');
}

function selectMood(mood) {
    // Navega para a tela da Comunidade (índice 3 na nav)
    navigateTo('community', 3);

    // Carrega o conteúdo baseado no humor
    showCommunitySection('devotional', null);
    loadDevotional(mood);
}

function navigateTo(targetId, elementOrIndex) {
    document.querySelectorAll('.nav-item').forEach(item => item.classList.remove('active'));

    if (elementOrIndex instanceof HTMLElement) {
        elementOrIndex.classList.add('active');
    } else if (typeof elementOrIndex === 'number') {
        const navItems = document.querySelectorAll('.nav-item');
        if (navItems[elementOrIndex]) navItems[elementOrIndex].classList.add('active');
    }

    document.querySelectorAll('.view').forEach(view => view.classList.remove('active'));
    const target = document.getElementById(targetId);
    if (target) target.classList.add('active');

    if (targetId === 'community') {
        showCommunitySection('hub');
    }

    // Garantir que ao navegar para Bíblia, mostre os controles
    if (targetId === 'bible') {
        const bibleContent = document.getElementById('bible-content');
        if (bibleContent) bibleContent.classList.add('active');
        const bibleDevotional = document.getElementById('bible-devotional');
        if (bibleDevotional) bibleDevotional.classList.remove('active');
    }
}

function switchSubTab(showId, hideId, btn) {
    document.getElementById(showId).classList.add('active');
    document.getElementById(hideId).classList.remove('active');

    btn.parentNode.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');

    if (showId === 'bible-devotional') {
        loadDevotional();
    }
}

async function fetchData() {
    try {
        const response = await fetch(`${API_URL}?action=all`);
        const data = await response.json();
        window.appData = data;

        // Verifica novos conteúdos no Mural
        checkNewMuralContent(data);

        setupBranding(data);
        renderContent(data);
    } catch (error) {
        console.warn("Usando dados de simulação (API não encontrada ou URL placeholder).");
        window.appData = MOCK_DATA;
        renderContent(MOCK_DATA);
    }
}

function renderContent(data) {
    const bannerCarousel = document.getElementById('banner-carousel');

    const banners = data.banners || MOCK_DATA.banners;
    if (bannerCarousel) {
        bannerCarousel.innerHTML = banners.map(item => `
            <div class="carousel-item">
                <img src="${item.ImagemUrl}" alt="${item.Titulo}" onclick="applyZoomEffect(this)">
                <div class="carousel-caption">
                    <h2>${item.Titulo}</h2>
                    <p>${item.Subtitulo || ''}</p>
                </div>
            </div>
        `).join('');
        startAutoCycle(bannerCarousel);
    }

    const newsGrid = document.getElementById('news-grid');
    const noticias = data.noticias || MOCK_DATA.noticias;
    if (newsGrid) {
        newsGrid.innerHTML = noticias.map(item => `
            <div class="news-card">
                <img src="${item.ImagemUrl || 'https://via.placeholder.com/240x140/1A1A1A/FFD700?text=Igreja'}" alt="" style="width:100%; height:140px; object-fit:cover; border-radius:12px 12px 0 0">
                <div class="news-info" style="padding:12px">
                    <small class="gold-text" style="font-weight:bold">${formatDateBR(item.Data, true)}</small>
                    <h4 style="font-size:15px; margin:5px 0; color:var(--white)">${item.Titulo}</h4>
                    <p style="font-size:12px; opacity:0.8; line-height:1.4; color:var(--white)">${item.Descricao || ''}</p>
                </div>
            </div>
        `).join('');
    }

    // Ticker de Agenda Semanal
    const tickerContent = document.getElementById('agenda-ticker-content');
    if (tickerContent) {
        const eventos = (data.eventos && data.eventos.length > 0) ? data.eventos : (data.agenda || MOCK_DATA.eventos);

        const tickerText = eventos.map(ev => {
            const titulo = (ev.Titulo || ev.Evento || "").toUpperCase();

            // Lógica de formatação melhorada
            const diaRaw = ev.Data || ev.Dia || "";
            const horaRaw = ev.Horario || ev.Hora || "";

            let dia = formatDateBR(diaRaw, true) || diaRaw;
            let hora = (horaRaw && horaRaw.includes(':')) ? horaRaw : (formatDateBR(horaRaw) ? formatDateBR(horaRaw).split(' às ')[1] : horaRaw);

            const info = [dia, hora].filter(Boolean).join(' às ');
            return `${titulo} ${info ? '(' + info + ')' : ''}`;
        }).join('   ✦   ');

        tickerContent.innerText = tickerText + "   ✦   " + tickerText; // Duplicado para loop infinito suave
    }

    const sermonsList = document.getElementById('sermons-list');
    const sermoes = data.sermoes || MOCK_DATA.sermoes;
    if (sermonsList) {
        sermonsList.innerHTML = sermoes.map(item => {
            const embedUrl = getEmbedUrl(item.VideoUrl);
            return `
                <div class="sermon-item" style="cursor:pointer" onclick="${embedUrl ? `window.open('${embedUrl}', '_blank')` : ''}">
                    <div class="sermon-thumb">
                        <img src="${item.ImagemUrl || 'https://via.placeholder.com/100/1A1A1A/FFD700?text=Play'}" alt="">
                        <i class="fas fa-play play-overlay"></i>
                    </div>
                    <div class="sermon-info">
                        <h4>${item.Titulo}</h4>
                        <p>${item.Pregador}</p>
                        <p style="font-size: 11px; opacity: 0.6">${formatDateBR(item.Data, true)}</p>
                    </div>
                </div>
            `;
        }).join('');
    }

    // Galeria de Fotos
    if (data.galeria) {
        window.appData.galeria = data.galeria;
    }
}

function showCommunitySection(section, btn, isMural = false) {
    const content = document.getElementById('com-content');

    // Se for 'hub', mostramos a grade de ícones
    if (section === 'hub') {
        content.innerHTML = `
            <div class="section-container" style="padding-top:20px">
                <div class="hub-grid">
                    <div class="hub-item" onclick="showCommunitySection('events', null)">
                        <div class="hub-icon-circle"><i class="fas fa-calendar-day"></i></div>
                        <span>Agenda Semanal</span>
                    </div>
                    <div class="hub-item" onclick="showCommunitySection('devotional', null)">
                        <div class="hub-icon-circle"><i class="fas fa-file-alt"></i></div>
                        <span>Devocional</span>
                    </div>
                    <div class="hub-item" onclick="showCommunitySection('gallery', null)">
                        <div class="hub-icon-circle"><i class="fas fa-images"></i></div>
                        <span>Galeria</span>
                    </div>
                    <div class="hub-item" onclick="showCommunitySection('ministries', null)">
                        <div class="hub-icon-circle"><i class="fas fa-users"></i></div>
                        <span>Ministérios</span>
                    </div>
                    <div class="hub-item" onclick="showCommunitySection('leadership', null, true)">
                        <div class="hub-icon-circle"><i class="fas fa-bullhorn"></i></div>
                        <span>Mural</span>
                    </div>
                    <div class="hub-item" onclick="showCommunitySection('locations', null)">
                        <div class="hub-icon-circle"><i class="fas fa-map-marker-alt"></i></div>
                        <span>Nossas Igrejas</span>
                    </div>
                    <div class="hub-item" onclick="navigateTo('prayer-view', 4)">
                        <div class="hub-icon-circle"><i class="fas fa-heart"></i></div>
                        <span>Pedidos de Oração</span>
                    </div>
                </div>
            </div>
        `;
        return;
    }

    // Botão de Voltar para o Hub
    const backBtn = `<button class="btn-outline" style="width: auto; padding: 5px 15px; margin-bottom: 20px; font-size: 12px" onclick="showCommunitySection('hub')"><i class="fas fa-arrow-left"></i> Voltar ao Menu</button>`;

    // Limpar alerta do Mural ao visualizar
    if (isMural || section === 'leadership') {
        const badge = document.getElementById('mural-badge');
        if (badge) badge.remove();

        const list = window.appData?.mensagemLider || [];
        if (list.length > 0) {
            const latest = list[0];
            localStorage.setItem('last_mural_id', latest.Nome + latest.Mensagem);
        }
    }

    if (btn) {
        btn.parentNode.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
    }

    if (section === 'devotional') {
        content.innerHTML = `<div class="section-container" style="padding-top:20px">${backBtn}<div id="bible-devotional" class="sub-view active"><div class="full-devotional" id="devotional-detail"></div></div></div>`;
        loadDevotional();
    } else if (section === 'gallery') {
        const galeria = window.appData?.galeria || MOCK_DATA.galeria;

        if (galeria && galeria.length > 0) {
            content.innerHTML = `
                <div class="section-container" style="padding-top:20px">
                    ${backBtn}
                    <h4 style="color:var(--gold); margin-bottom:20px; text-align:center">Galeria de Fotos</h4>
                    <div style="display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px;">
                        ${galeria.map(img => `
                            <div class="gallery-item" onclick="openZoomImage('${img.ImagemUrl}')" style="aspect-ratio: 1; border-radius: 8px; overflow: hidden; background: #1A1A1A; cursor: pointer">
                                <img src="${img.ImagemUrl}" style="width: 100%; height: 100%; object-fit: cover">
                            </div>
                        `).join('')}
                    </div>
                </div>
            `;
        } else {
            content.innerHTML = `<div class="section-container" style="padding-top:20px">${backBtn}<h4 style="color:var(--gold)">Galeria de Fotos</h4><p style="margin-top:20px; opacity:0.7">Em breve: Momentos especiais da nossa igreja.</p></div>`;
        }
    } else if (section === 'locations') {
        content.innerHTML = `<div class="section-container" style="padding-top:20px">${backBtn}
            <h4 style="color:var(--gold); margin-bottom:20px">Nossas Igrejas</h4>
            <div class="devotional-card" style="margin-bottom:16px; border: 2px solid var(--gold)">
                <div style="flex: 1">
                    <h4 style="color:var(--gold)">Igreja Sede</h4>
                    <p style="font-size:13px; margin-top:5px">Av. Principal, 1000 - Centro, SP</p>
                    <small style="opacity:0.7">(11) 99999-9999</small>
                    <button class="btn-gold" style="padding: 8px; margin-top: 15px" onclick="window.open('https://maps.google.com')">Ver no Mapa</button>
                </div>
            </div>
            <div class="devotional-card" style="margin-bottom:16px">
                <div style="flex: 1">
                    <h4>Filial Zona Norte</h4>
                    <p style="font-size:13px; margin-top:5px">Rua das Flores, 500 - Santana, SP</p>
                    <button class="btn-outline" style="padding: 8px; margin-top: 15px" onclick="window.open('https://maps.google.com')">Ver no Mapa</button>
                </div>
            </div>
        </div>`;
    } else if (section === 'ministries') {
        content.innerHTML = `<div class="section-container" style="padding-top:20px">${backBtn}` + (window.appData?.ministerios || MOCK_DATA.ministerios).map(m => `
            <div class="devotional-card" style="margin-bottom:16px; display:block; padding:0; overflow:hidden">
                <img src="${m.ImagemUrl || 'https://via.placeholder.com/400x120/1A1A1A/FFD700?text=Ministerio'}"
                     style="width: 100%; height: 120px; object-fit: cover; border-bottom: 1px solid rgba(255, 215, 0, 0.2);">
                <div style="padding:16px">
                    <h4 style="color:var(--gold); margin-bottom:8px">${m.Nome}</h4>
                    <p style="color:#eee; font-size:14px; margin-bottom:10px">${m.Descricao}</p>
                    <small style="opacity:0.8">Líder: ${m.Lider}</small>
                </div>
            </div>
        `).join('') + `</div>`;
    } else if (section === 'leadership') {
        content.innerHTML = `
            <div class="section-container" style="padding-top:20px; text-align:center">
                ${backBtn}
                ${isMural ? '<h2 style="color:var(--gold); margin-bottom:20px">Mural da Liderança</h2>' : '<i class="fas fa-user-tie" style="font-size:50px; color:var(--gold); margin-bottom:20px"></i>'}
                <div style="text-align:left">
                    ${(window.appData?.mensagemLider || MOCK_DATA.mensagemLider).map(msg => {
                        const embedUrl = getEmbedUrl(msg.VideoUrl);
                        return `
                            <div class="devotional-card" style="margin-bottom:24px; display:block; padding:0; overflow:hidden">
                                ${embedUrl ? `
                                    <iframe width="100%" style="aspect-ratio: 16/9; border:none"
                                        src="${embedUrl}"
                                        allowfullscreen>
                                    </iframe>
                                ` : ''}
                                <div style="padding:15px; display:flex; align-items:center; gap:15px">
                                    ${msg.FotoUrl ? `<img src="${msg.FotoUrl}" style="width:50px; height:50px; border-radius:50%; object-fit:cover; border:2px solid var(--gold)">` : ''}
                                    <div>
                                        <h4 style="color:var(--gold); margin-bottom:2px">${msg.Mensagem}</h4>
                                        <p style="font-size:13px; font-weight:bold; margin-bottom:2px">${msg.Nome}</p>
                                        <small style="opacity:0.7; display:block">${msg.Cargo}</small>
                                    </div>
                                </div>
                            </div>
                        `;
                    }).join('')}
                </div>
            </div>
        `;
    } else if (section === 'events') {
        const list = (window.appData?.eventos && window.appData.eventos.length > 0)
                    ? window.appData.eventos
                    : (window.appData?.agenda || MOCK_DATA.eventos);

        content.innerHTML = `
            <div class="section-container" style="padding-top:20px">
                ${backBtn}
                <h4 style="color:var(--gold); margin-bottom:20px; text-align:center">Agenda Semanal</h4>
                ${list.map(ev => {
                    const diaRaw = ev.Data || ev.Dia || "";
                    const titulo = ev.Titulo || ev.Evento || "";
                    const horaRaw = ev.Horario || ev.Hora || "";
                    const local = ev.Local || "";
                    const dia = formatDateBR(diaRaw, true) || diaRaw;
                    const hora = (horaRaw && horaRaw.includes(':')) ? horaRaw : (formatDateBR(horaRaw) ? formatDateBR(horaRaw).split(' às ')[1] : horaRaw);

                    return `
                        <div class="devotional-card" style="margin-bottom:16px; border-left: 4px solid var(--gold); padding: 16px; display: flex; align-items: center; gap: 15px">
                            <div style="background: rgba(255, 215, 0, 0.1); width: 45px; height: 45px; border-radius: 10px; display: flex; align-items: center; justify-content: center">
                                <i class="fas fa-calendar-day" style="color:var(--gold); font-size: 20px"></i>
                            </div>
                            <div style="flex: 1">
                                <h4 style="margin: 0; color: var(--gold); font-size: 16px; font-weight: 700">${titulo}</h4>
                                <div style="display: flex; align-items: center; gap: 8px; margin-top: 4px; flex-wrap: wrap">
                                    <span style="color: #fff; font-size: 13px; font-weight: 500">${dia}</span>
                                    ${dia && (hora || local) ? `<span style="color: var(--gold); opacity: 0.5">•</span>` : ''}
                                    <span style="color: #fff; font-size: 13px">${hora}</span>
                                    ${hora && local ? `<span style="color: var(--gold); opacity: 0.5">•</span>` : ''}
                                    <span style="color: #ccc; font-size: 13px">${local}</span>
                                </div>
                            </div>
                        </div>
                    `;
                }).join('')}
            </div>
        `;
    }
}

let bibleBooksData = [];
// Mapeamento para API Bolls.life (ID do livro e total de capítulos)
const BIBLE_BOOKS_FALLBACK = [
    { id: 1, name: "Gênesis", abbrev: "gn", chapters: 50 },
    { id: 2, name: "Êxodo", abbrev: "ex", chapters: 40 },
    { id: 3, name: "Levítico", abbrev: "lv", chapters: 27 },
    { id: 4, name: "Números", abbrev: "nm", chapters: 36 },
    { id: 5, name: "Deuteronômio", abbrev: "dt", chapters: 34 },
    { id: 6, name: "Josué", abbrev: "js", chapters: 24 },
    { id: 7, name: "Juízes", abbrev: "jz", chapters: 21 },
    { id: 8, name: "Rute", abbrev: "rt", chapters: 4 },
    { id: 9, name: "1 Samuel", abbrev: "1sm", chapters: 31 },
    { id: 10, name: "2 Samuel", abbrev: "2sm", chapters: 24 },
    { id: 11, name: "1 Reis", abbrev: "1rs", chapters: 22 },
    { id: 12, name: "2 Reis", abbrev: "2rs", chapters: 25 },
    { id: 13, name: "1 Crônicas", abbrev: "1cr", chapters: 29 },
    { id: 14, name: "2 Crônicas", abbrev: "2cr", chapters: 36 },
    { id: 15, name: "Esdras", abbrev: "ezr", chapters: 10 },
    { id: 16, name: "Neemias", abbrev: "ne", chapters: 13 },
    { id: 17, name: "Ester", abbrev: "et", chapters: 10 },
    { id: 18, name: "Jó", abbrev: "job", chapters: 42 },
    { id: 19, name: "Salmos", abbrev: "ps", chapters: 150 },
    { id: 20, name: "Provérbios", abbrev: "pr", chapters: 31 },
    { id: 21, name: "Eclesiastes", abbrev: "ec", chapters: 12 },
    { id: 22, name: "Cânticos", abbrev: "ct", chapters: 8 },
    { id: 23, name: "Isaías", abbrev: "is", chapters: 66 },
    { id: 24, name: "Jeremias", abbrev: "jr", chapters: 52 },
    { id: 25, name: "Lamentações", abbrev: "lm", chapters: 5 },
    { id: 26, name: "Ezequiel", abbrev: { pt: "ez" }, chapters: 48 },
    { id: 27, name: "Daniel", abbrev: "dn", chapters: 12 },
    { id: 28, name: "Oseias", abbrev: "os", chapters: 14 },
    { id: 29, name: "Joel", abbrev: "jl", chapters: 3 },
    { id: 30, name: "Amós", abbrev: "am", chapters: 9 },
    { id: 31, name: "Obadias", abbrev: "ob", chapters: 1 },
    { id: 32, name: "Jonas", abbrev: "jn", chapters: 4 },
    { id: 33, name: "Miqueias", abbrev: "mi", chapters: 7 },
    { id: 34, name: "Naum", abbrev: "na", chapters: 3 },
    { id: 35, name: "Habacuque", abbrev: "hb", chapters: 3 },
    { id: 36, name: "Sofonias", abbrev: "sf", chapters: 3 },
    { id: 37, name: "Ageu", abbrev: "ag", chapters: 2 },
    { id: 38, name: "Zacarias", abbrev: "zc", chapters: 14 },
    { id: 39, name: "Malaquias", abbrev: "ml", chapters: 4 },
    { id: 40, name: "Mateus", abbrev: "mt", chapters: 28 },
    { id: 41, name: "Marcos", abbrev: "mk", chapters: 16 },
    { id: 42, name: "Lucas", abbrev: "lk", chapters: 24 },
    { id: 43, name: "João", abbrev: "jn", chapters: 21 },
    { id: 44, name: "Atos", abbrev: "act", chapters: 28 },
    { id: 45, name: "Romanos", abbrev: "rm", chapters: 16 },
    { id: 46, name: "1 Coríntios", abbrev: "1co", chapters: 16 },
    { id: 47, name: "2 Coríntios", abbrev: "2co", chapters: 13 },
    { id: 48, name: "Gálatas", abbrev: "gl", chapters: 6 },
    { id: 49, name: "Efésios", abbrev: "ep", chapters: 6 },
    { id: 50, name: "Filipenses", abbrev: "ph", chapters: 4 },
    { id: 51, name: "Colossenses", abbrev: "cl", chapters: 4 },
    { id: 52, name: "1 Tessalonicenses", abbrev: "1ts", chapters: 5 },
    { id: 53, name: "2 Tessalonicenses", abbrev: "2ts", chapters: 3 },
    { id: 54, name: "1 Timóteo", abbrev: "1tm", chapters: 6 },
    { id: 55, name: "2 Timóteo", abbrev: "2tm", chapters: 4 },
    { id: 56, name: "Tito", abbrev: "tt", chapters: 3 },
    { id: 57, name: "Filemom", abbrev: "phm", chapters: 1 },
    { id: 58, name: "Hebreus", abbrev: "hb", chapters: 13 },
    { id: 59, name: "Tiago", abbrev: "tg", chapters: 5 },
    { id: 60, name: "1 Pedro", abbrev: "1pe", chapters: 5 },
    { id: 61, name: "2 Pedro", abbrev: "2pe", chapters: 3 },
    { id: 62, name: "1 João", abbrev: "1jn", chapters: 5 },
    { id: 63, name: "2 João", abbrev: "2jn", chapters: 1 },
    { id: 64, name: "3 João", abbrev: "3jn", chapters: 1 },
    { id: 65, name: "Judas", abbrev: "jd", chapters: 1 },
    { id: 66, name: "Apocalipse", abbrev: "re", chapters: 22 }
];

async function initBible() {
    const versionSelector = document.getElementById('version-selector');
    const bookSelector = document.getElementById('book-selector');
    const chapterSelector = document.getElementById('chapter-selector');
    const textContainer = document.getElementById('bible-text');

    if (!bookSelector || !chapterSelector || !textContainer) return;

    // Atualiza versões para os códigos da Bolls.life
    versionSelector.innerHTML = `
        <option value="NVIPT">NVI</option>
        <option value="ARAV">ARA</option>
        <option value="ARC">ARC</option>
        <option value="KJV">KJV</option>
    `;

    bibleBooksData = BIBLE_BOOKS_FALLBACK;

    // Popula Livros
    bookSelector.innerHTML = bibleBooksData.map(book =>
        `<option value="${book.id}">${book.name}</option>`
    ).join('');

    // Listeners
    versionSelector.addEventListener('change', () => loadVerses());
    bookSelector.addEventListener('change', () => updateChapters());
    chapterSelector.addEventListener('change', () => loadVerses());

    updateChapters();
}

function updateChapters() {
    const bookSelector = document.getElementById('book-selector');
    const chapterSelector = document.getElementById('chapter-selector');
    const selectedId = parseInt(bookSelector.value);

    const book = bibleBooksData.find(b => b.id === selectedId);
    if (book) {
        let options = "";
        for (let i = 1; i <= book.chapters; i++) {
            options += `<option value="${i}">${i}</option>`;
        }
        chapterSelector.innerHTML = options;
        loadVerses();
    }
}

async function loadVerses() {
    const textContainer = document.getElementById('bible-text');
    const version = document.getElementById('version-selector').value;
    const bookId = document.getElementById('book-selector').value;
    const chapter = document.getElementById('chapter-selector').value;

    textContainer.innerHTML = "<p style='text-align:center; opacity:0.5; padding: 20px;'>Buscando palavra sagrada...</p>";

    try {
        // API Bolls.life: Versão / ID Livro / Capítulo
        const response = await fetch(`https://bolls.life/get-chapter/${version}/${bookId}/${chapter}/`);

        if (!response.ok) throw new Error("Erro na conexão com o servidor da Bíblia.");

        const verses = await response.json();

        if (verses && verses.length > 0) {
            textContainer.innerHTML = verses.map(v => `
                <p style="margin-bottom:15px; line-height:1.6">
                    <span style="color:var(--gold); font-weight:bold; margin-right:10px; font-size:0.9em">${v.verse}</span>
                    ${v.text}
                </p>
            `).join('');
        } else {
            textContainer.innerHTML = "<p style='padding:20px; text-align:center'>Nenhum versículo encontrado.</p>";
        }
    } catch (error) {
        console.error("Erro Bíblia:", error);
        textContainer.innerHTML = `
            <div style="text-align:center; padding:20px">
                <p style="color:var(--gold)">Não foi possível carregar este capítulo.</p>
                <p style="font-size:12px; opacity:0.7; margin-top:10px">Tente trocar a versão ou verifique sua internet.</p>
                <button class="btn-gold" style="margin-top:20px; padding:8px 15px; font-size:12px" onclick="loadVerses()">Tentar Novamente</button>
            </div>
        `;
    }
    textContainer.scrollTop = 0;
}

const DEVOTIONAL_VERSES = {
    'Triste': [
        { title: "O Consolo que Vem do Alto", ref: "Salmos 34:18", text: "Perto está o Senhor dos que têm o coração quebrantado e salva os de espírito oprimido." },
        { title: "Deus Enxugará suas Lágrimas", ref: "Apocalipse 21:4", text: "Ele enxugará dos seus olhos toda lágrima. Não haverá mais morte, nem tristeza, nem choro, nem dor." },
        { title: "Refúgio e Fortaleza", ref: "Salmos 46:1", text: "Deus é o nosso refúgio e a nossa fortaleza, auxílio sempre presente na adversidade." },
        { title: "Não Temas", ref: "Isaías 41:10", text: "Não temas, porque eu sou contigo; não te assombres, porque eu sou o teu Deus; eu te esforço, e te ajudo, e te sustento com a destra da minha justiça." },
        { title: "Esperança em Deus", ref: "Salmos 42:11", text: "Por que estás abatida, ó minha alma? Por que te perturbas dentro de mim? Espera em Deus, pois ainda o louvarei." }
    ],
    'Cansado': [
        { title: "Descanso para a Alma", ref: "Mateus 11:28", text: "Vinde a mim, todos os que estais cansados e oprimidos, e eu vos aliviarei." },
        { title: "Forças Renovadas", ref: "Isaías 40:31", text: "Mas aqueles que esperam no Senhor renovam as suas forças. Voam alto como águias; correm e não se fatigam." },
        { title: "Socorro Bem Presente", ref: "Salmos 121:1-2", text: "Elevo os meus olhos para os montes; de onde vem o meu socorro? O meu socorro vem do Senhor, que fez o céu e a terra." },
        { title: "O Senhor é meu Pastor", ref: "Salmos 23:1-3", text: "O Senhor é o meu pastor; nada me faltará. Deitar-me faz em verdes pastos, guia-me mansamente a águas tranquilas. Refrigera a minha alma." },
        { title: "Paz que Excede Entendimento", ref: "Filipenses 4:7", text: "E a paz de Deus, que excede todo o entendimento, guardará os vossos corações e os vossos sentimentos em Cristo Jesus." }
    ],
    'Grato': [
        { title: "O Sacrifício de Louvor", ref: "1 Tessalonicenses 5:18", text: "Em tudo dai graças, porque esta é a vontade de Deus em Cristo Jesus para convosco." },
        { title: "A Bondade do Senhor", ref: "Salmos 107:1", text: "Deem graças ao Senhor, porque ele é bom; o seu amor dura para sempre." },
        { title: "Louvor de Coração", ref: "Salmos 103:1", text: "Bendiga ao Senhor a minha alma! Bendiga ao seu santo nome todo o meu ser!" },
        { title: "Grandes Coisas fez o Senhor", ref: "Salmos 126:3", text: "Grandes coisas fez o Senhor por nós, pelas quais estamos alegres." },
        { title: "Cantarei ao Senhor", ref: "Salmos 13:6", text: "Cantarei ao Senhor, porquanto me tem feito muito bem." }
    ],
    'Feliz': [
        { title: "A Alegria do Senhor", ref: "Neemias 8:10", text: "Não vos entristeçais, porque a alegria do Senhor é a vossa força." },
        { title: "Coração Alegre", ref: "Provérbios 15:13", text: "O coração alegre aformoseia o rosto, mas pela dor do coração o espírito se abate." },
        { title: "Regozijo Constante", ref: "Filipenses 4:4", text: "Alegrem-se sempre no Senhor. Novamente direi: Alegrem-se!" },
        { title: "Felicidade na Palavra", ref: "Salmos 1:1-2", text: "Bem-aventurado o homem que tem o seu prazer na lei do Senhor, e na sua lei medita de dia e de noite." },
        { title: "Transbordando de Alegria", ref: "Salmos 16:11", text: "Tu me farás conhecer a vereda da vida, a alegria plena da tua presença." }
    ],
    'default': [
        { title: "O Renovo das Misericórdias", ref: "Lamentações 3:22-23", text: "As misericórdias do Senhor são a causa de não sermos consumidos, porque as suas compaixões não têm fim; renovam-se cada manhã." }
    ]
};

function loadIndependentDevotional(mood = null) {
    loadDevotional(mood);
}

function loadIndependentMinistries() {
    const content = document.getElementById('ministries-content');
    if (!content) return;
    const list = window.appData?.ministerios || MOCK_DATA.ministerios;
    content.innerHTML = list.map(m => `
        <div class="devotional-card" style="margin-bottom:16px; display:block; padding:0; overflow:hidden">
            <img src="${m.ImagemUrl || 'https://via.placeholder.com/400x120/1A1A1A/FFD700?text=Ministerio'}"
                 style="width: 100%; height: 120px; object-fit: cover; border-bottom: 1px solid rgba(255, 215, 0, 0.2);">
            <div style="padding:16px">
                <h4 style="color:var(--gold); margin-bottom:8px">${m.Nome}</h4>
                <p style="color:#eee; font-size:14px; margin-bottom:10px">${m.Descricao}</p>
                <small style="opacity:0.8">Líder: ${m.Lider}</small>
            </div>
        </div>
    `).join('');
}

function loadIndependentAgenda() {
    const content = document.getElementById('agenda-content');
    if (!content) return;
    const list = (window.appData?.eventos && window.appData.eventos.length > 0)
                ? window.appData.eventos
                : (window.appData?.agenda || MOCK_DATA.eventos);

    content.innerHTML = list.map(ev => {
        const diaRaw = ev.Data || ev.Dia || "";
        const titulo = ev.Titulo || ev.Evento || "";
        const horaRaw = ev.Horario || ev.Hora || "";
        const local = ev.Local || "";
        const dia = formatDateBR(diaRaw, true) || diaRaw;
        const hora = (horaRaw && horaRaw.includes(':')) ? horaRaw : (formatDateBR(horaRaw) ? formatDateBR(horaRaw).split(' às ')[1] : horaRaw);

        return `
            <div class="devotional-card" style="margin-bottom:16px; border-left: 4px solid var(--gold); padding: 16px; display: flex; align-items: center; gap: 15px">
                <div style="background: rgba(255, 215, 0, 0.1); width: 45px; height: 45px; border-radius: 10px; display: flex; align-items: center; justify-content: center">
                    <i class="fas fa-calendar-day" style="color:var(--gold); font-size: 20px"></i>
                </div>
                <div style="flex: 1">
                    <h4 style="margin: 0; color: var(--gold); font-size: 16px; font-weight: 700">${titulo}</h4>
                    <div style="display: flex; align-items: center; gap: 8px; margin-top: 4px; flex-wrap: wrap">
                        <span style="color: #fff; font-size: 13px; font-weight: 500">${dia}</span>
                        ${dia && (hora || local) ? `<span style="color: var(--gold); opacity: 0.5">•</span>` : ''}
                        <span style="color: #fff; font-size: 13px">${hora}</span>
                        ${hora && local ? `<span style="color: var(--gold); opacity: 0.5">•</span>` : ''}
                        <span style="color: #ccc; font-size: 13px">${local}</span>
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

async function loadDevotional(mood = null) {
    const detail = document.getElementById('devotional-detail') || document.getElementById('devotional-content');
    if (!detail) return;

    detail.innerHTML = `<div style="text-align:center; padding:40px;"><i class="fas fa-spinner fa-spin" style="font-size:30px; color:var(--gold)"></i><p style="margin-top:15px">Buscando Palavra de Deus...</p></div>`;

    // Referências curadas para o Devocional
    const references = [
        "john+3:16", "psalms+23:1", "1+corinthians+13:4", "philippians+4:13", "matthew+11:28",
        "isaiah+41:10", "jeremiah+29:11", "romans+8:28", "proverbs+3:5", "joshua+1:9",
        "psalms+46:1", "matthew+6:33", "galatians+5:22", "ephesians+2:8", "lamentations+3:22"
    ];
    const randomRef = references[Math.floor(Math.random() * references.length)];

    try {
        // Tenta buscar da API
        const response = await fetch(`https://bible-api.com/${randomRef}?translation=almeida`);
        if (!response.ok) throw new Error("Erro na API");
        const data = await response.json();

        detail.innerHTML = `
            <div class="section-container" style="padding-top:20px; display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 50vh; text-align: center;">
                <div class="devotional-card" style="display:block; text-align:center; margin-bottom: 30px; border-color: rgba(255,215,0,0.4); max-width: 500px; width: 100%; padding: 40px; background: #1A1A1A;">
                    <small class="gold-text" style="font-weight:bold; letter-spacing:2px; font-size: 1.1em; display: block; margin-bottom: 20px;">DEVOCIONAL</small>
                    <p style="font-style:italic; margin:25px 0; font-size:1.5em; line-height: 1.6; color: white;">"${data.text.trim()}"</p>
                    <strong class="gold-text" style="font-size: 1.2em; display: block; margin-top: 15px;">${data.reference}</strong>

                    <button class="btn-outline" style="margin-top:30px" onclick="shareDevotional('Palavra de Deus', '${data.reference}', '${data.text.trim().replace(/'/g, "\\'")}')">
                        <i class="fas fa-share-alt"></i> Compartilhar
                    </button>
                </div>
                <button class="btn-outline" style="width:auto; padding:10px 20px" onclick="loadDevotional()">
                    <i class="fas fa-redo"></i> Ver outro versículo
                </button>
            </div>
        `;
    } catch (error) {
        console.error("Erro ao carregar devocional:", error);
        // Fallback local se a API falhar
        const fallback = DEVOTIONAL_VERSES['default'][0];
        detail.innerHTML = `
            <div class="section-container" style="padding-top:20px; display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 50vh; text-align: center;">
                <div class="devotional-card" style="display:block; text-align:center; margin-bottom: 30px; border-color: rgba(255,215,0,0.4); max-width: 500px; width: 100%; padding: 40px; background: #1A1A1A;">
                    <small class="gold-text" style="font-weight:bold; letter-spacing:2px; font-size: 1.1em; display: block; margin-bottom: 20px;">DEVOCIONAL</small>
                    <p style="font-style:italic; margin:25px 0; font-size:1.5em; line-height: 1.6; color: white;">"${fallback.text}"</p>
                    <strong class="gold-text" style="font-size: 1.2em; display: block; margin-top: 15px;">${fallback.ref}</strong>
                </div>
                <p style="color:var(--light-grey); font-size:12px">Exibindo conteúdo offline. Verifique sua conexão para novos versículos.</p>
            </div>
        `;
    }
}

function shareDevotional(title, reference, text) {
    const shareText = `${title}\n\n"${text}"\n\n${reference}`;

    if (navigator.share) {
        navigator.share({
            title: title,
            text: shareText
        }).catch(err => console.log('Erro ao compartilhar:', err));
    } else {
        navigator.clipboard.writeText(shareText).then(() => {
            alert("Devocional copiado para a área de transferência!");
        });
    }
}

function openGivingModal() {
    document.getElementById('giving-modal').style.display = 'block';
    switchGivingTab('dizimo');
}
function closeGivingModal() { document.getElementById('giving-modal').style.display = 'none'; }

function switchGivingTab(type) {
    currentGivingType = type;
    const content = document.getElementById('giving-form-content');
    const tabDizimo = document.getElementById('tab-dizimo');
    const tabOferta = document.getElementById('tab-oferta');

    tabDizimo.classList.remove('active');
    tabOferta.classList.remove('active');

    if (type === 'dizimo') {
        tabDizimo.classList.add('active');
        content.innerHTML = `
            <h4>Informar Dízimo</h4>
            <input type="text" placeholder="Nome Completo" id="give-name">
            <input type="text" placeholder="CPF ou Telefone" id="give-ident">
            <input type="number" placeholder="Valor R$" id="give-value">
            <div style="margin-top:10px; text-align:left">
                <label style="font-size:12px; color:var(--gold); display:block; margin-bottom:5px">Anexar Comprovante (Opcional)</label>
                <input type="file" id="give-receipt" accept="image/*,application/pdf" style="font-size:12px; border:none; padding:0">
            </div>
            <button class="btn-outline" style="margin-top:15px" onclick="submitGiving()">Informar Envio</button>
        `;
    } else {
        tabOferta.classList.add('active');
        content.innerHTML = `
            <h4>Informar Oferta</h4>
            <input type="text" placeholder="Nome (Opcional)" id="give-name">
            <input type="number" placeholder="Valor R$" id="give-value">
            <button class="btn-outline" onclick="submitGiving()">Informar Envio</button>
        `;
    }
}

function copyPix() {
    const key = document.getElementById('pix-key').innerText;
    navigator.clipboard.writeText(key);
    alert("Chave PIX copiada!");
}

async function submitGiving() {
    const name = document.getElementById('give-name').value;
    const value = document.getElementById('give-value').value;
    const ident = document.getElementById('give-ident') ? document.getElementById('give-ident').value : "N/A";
    const fileInput = document.getElementById('give-receipt');

    if(currentGivingType === 'dizimo' && (!name || !value || !ident)) {
        return alert("Preencha todos os campos do Dízimo");
    }
    if(currentGivingType === 'oferta' && !value) {
        return alert("Informe o valor da oferta");
    }

    const payload = {
        type: 'giving',
        subtipo: currentGivingType === 'dizimo' ? 'Dízimo' : 'Oferta',
        nome: name || "Anônimo",
        identificacao: ident,
        valor: value,
        data: new Date().toLocaleDateString(),
        fileData: null,
        fileName: null
    };

    if (fileInput && fileInput.files && fileInput.files[0]) {
        const file = fileInput.files[0];
        try {
            const base64 = await new Promise((resolve, reject) => {
                const reader = new FileReader();
                reader.onload = () => resolve(reader.result.split(',')[1]);
                reader.onerror = error => reject(error);
                reader.readAsDataURL(file);
            });
            payload.fileData = base64;
            payload.fileName = file.name;
        } catch (e) {
            console.error("Erro ao ler arquivo:", e);
            return alert("Erro ao processar o arquivo. Tente novamente.");
        }
    }

    try {
        await fetch(API_URL, {
            method: 'POST',
            mode: 'no-cors',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        alert("Sua contribuição de " + payload.subtipo + " foi enviada!");
        closeGivingModal();
    } catch(e) {
        console.error(e);
        alert("Erro ao enviar. Tente novamente.");
    }
}

async function submitPrayer() {
    const name = document.getElementById('p-name').value;
    const reason = document.getElementById('p-reason').value;
    const msg = document.getElementById('p-msg').value;

    if(!name || !reason || !msg) return alert("Por favor, preencha seu nome, selecione o motivo e escreva seu pedido.");

    const payload = {
        type: 'prayer',
        nome: name,
        motivo: reason,
        mensagem: msg,
        data: new Date().toLocaleString()
    };

    try {
        await fetch(API_URL, {
            method: 'POST',
            mode: 'no-cors',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        alert("Pedido de oração enviado com sucesso!");
        document.getElementById('p-name').value = "";
        document.getElementById('p-reason').value = "";
        document.getElementById('p-msg').value = "";
    } catch (e) {
        console.error(e);
        alert("Erro ao enviar pedido de oração.");
    }
}

function checkNewMuralContent(data) {
    const list = data.mensagemLider || [];
    if (list.length === 0) return;

    const latest = list[0];
    const latestId = latest.Nome + latest.Mensagem;
    const lastSeenId = localStorage.getItem('last_mural_id');

    if (latestId !== lastSeenId) {
        // Alerta visual no botão Mural
        const muralBtn = document.querySelector('.action-btn[onclick*="leadership"]');
        if (muralBtn) {
            const badge = document.createElement('div');
            badge.style.cssText = "position:absolute; top:-5px; right:15px; background:red; color:white; border-radius:50%; width:18px; height:18px; font-size:12px; display:flex; align-items:center; justify-content:center; font-weight:bold; border:2px solid black";
            badge.innerText = "!";
            badge.id = "mural-badge";
            muralBtn.style.position = "relative";
            muralBtn.appendChild(badge);
        }

        // Alerta sonoro ou notificação visual ativa (Toast personalizado)
        showNotification("Nova mensagem da Liderança no Mural!");
    }
}

function showNotification(msg) {
    const toast = document.createElement('div');
    toast.style.cssText = "position:fixed; top:20px; left:50%; transform:translateX(-50%); background:var(--gold); color:black; padding:12px 24px; border-radius:30px; font-weight:bold; z-index:2000; box-shadow:0 4px 15px rgba(0,0,0,0.5); animation: slideDown 0.5s ease";
    toast.innerText = msg;
    document.body.appendChild(toast);
    setTimeout(() => {
        toast.style.animation = "fadeOut 0.5s ease";
        setTimeout(() => toast.remove(), 500);
    }, 4000);
}

function openZoomImage(url) {
    const overlay = document.createElement('div');
    overlay.style.cssText = "position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.9); z-index:3000; display:flex; align-items:center; justify-content:center; cursor:pointer";
    overlay.onclick = () => overlay.remove();

    const img = document.createElement('img');
    img.src = url;
    img.style.cssText = "max-width:95%; max-height:90%; object-fit:contain; border-radius:8px; box-shadow:0 0 20px rgba(0,0,0,0.5)";

    const closeBtn = document.createElement('div');
    closeBtn.innerHTML = '<i class="fas fa-times"></i>';
    closeBtn.style.cssText = "position:absolute; top:20px; right:20px; color:white; font-size:24px";

    overlay.appendChild(img);
    overlay.appendChild(closeBtn);
    document.body.appendChild(overlay);
}

function applyZoomEffect(img) {
    img.classList.add('zoomed');
    setTimeout(() => {
        img.classList.remove('zoomed');
    }, 2000);
}

function startAutoCycle(carousel) {
    if (bannerInterval) clearInterval(bannerInterval);
    bannerInterval = setInterval(() => {
        const itemWidth = carousel.querySelector('.carousel-item').offsetWidth + 10;
        const maxScroll = carousel.scrollWidth - carousel.offsetWidth;

        if (carousel.scrollLeft >= maxScroll - 5) {
            carousel.scrollTo({ left: 0, behavior: 'smooth' });
        } else {
            carousel.scrollBy({ left: itemWidth, behavior: 'smooth' });
        }
    }, 5000);
}

if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        // navigator.serviceWorker.register('/sw.js');
    });
}
